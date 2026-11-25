package notificacoes;

import static notificacoes.ProtocoloNotificacoes.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import model.AlertaEstoque;

public final class ServidorNotificacoes {
    private final ThreadPoolExecutor conexoes = new ThreadPoolExecutor(0, 32, 30, TimeUnit.SECONDS,
            new SynchronousQueue<>());
    private final Set<Assinante> assinantes = ConcurrentHashMap.newKeySet();
    // Reserva espaço no pool para os caixas publicarem mesmo com vários painéis abertos.
    private final Semaphore vagasAssinantes = new Semaphore(24);

    public static void main(String[] args) throws IOException {
        new ServidorNotificacoes().executar();
    }

    private void executar() throws IOException {
        try (ServerSocket servidor = new ServerSocket()) {
            servidor.bind(endereco());
            System.out.println("Servidor de notificações ouvindo em " + servidor.getLocalSocketAddress());
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = servidor.accept();
                try {
                    conexoes.execute(() -> atender(socket));
                } catch (RejectedExecutionException e) {
                    fechar(socket);
                }
            }
        } finally {
            conexoes.shutdownNow();
        }
    }

    private void atender(Socket socket) {
        try (socket;
             DataInputStream entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
            socket.setSoTimeout(TIMEOUT_CONEXAO);
            int tipo = entrada.readUnsignedByte();
            if (tipo == INSCREVER) {
                acompanhar(socket, saida);
            } else if (tipo == PUBLICAR) {
                distribuir(lerAlerta(entrada));
                saida.writeByte(PUBLICADO);
                saida.flush();
            }
        } catch (IOException e) {
            // Quedas e mensagens inválidas encerram somente esta conexão.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void acompanhar(Socket socket, DataOutputStream saida) throws IOException, InterruptedException {
        if (!vagasAssinantes.tryAcquire()) return;

        Assinante assinante = new Assinante(socket);
        assinantes.add(assinante);
        try {
            saida.writeByte(INSCRITO);
            saida.flush();
            while (!socket.isClosed()) {
                AlertaEstoque alerta = assinante.fila.poll(INTERVALO_PULSO, TimeUnit.MILLISECONDS);
                if (alerta == null) {
                    saida.writeByte(PULSO);
                    saida.flush();
                } else {
                    escreverAlerta(saida, ALERTA, alerta);
                }
            }
        } finally {
            assinantes.remove(assinante);
            vagasAssinantes.release();
        }
    }

    private void distribuir(AlertaEstoque alerta) {
        for (Assinante assinante : assinantes) {
            if (!assinante.fila.offer(alerta)) fechar(assinante.socket);
        }
    }

    private static void fechar(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private static final class Assinante {
        private final Socket socket;
        private final ArrayBlockingQueue<AlertaEstoque> fila = new ArrayBlockingQueue<>(64);

        private Assinante(Socket socket) {
            this.socket = socket;
        }
    }
}

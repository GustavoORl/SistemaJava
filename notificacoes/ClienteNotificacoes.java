package notificacoes;

import static notificacoes.ProtocoloNotificacoes.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AlertaEstoque;

public final class ClienteNotificacoes implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ClienteNotificacoes.class.getName());
    private static final ThreadPoolExecutor ENVIOS = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(64), tarefa -> {
                Thread thread = new Thread(tarefa, "notificacoes-envio");
                thread.setDaemon(true);
                return thread;
            });

    private final Consumer<AlertaEstoque> aoReceber;
    private final Consumer<Boolean> aoMudarConexao;
    private final Thread escuta = new Thread(this::escutar, "notificacoes-escuta");
    private volatile Socket socketAberto;
    private volatile boolean encerrado;

    public ClienteNotificacoes(Consumer<AlertaEstoque> aoReceber, Consumer<Boolean> aoMudarConexao) {
        this.aoReceber = aoReceber;
        this.aoMudarConexao = aoMudarConexao;
        escuta.setDaemon(true);
    }

    public void iniciar() {
        escuta.start();
    }

    public static void publicar(List<AlertaEstoque> alertas) {
        if (alertas.isEmpty()) return;

        try {
            ENVIOS.execute(() -> enviar(alertas));
        } catch (RejectedExecutionException e) {
            LOG.warning("Fila de notificações cheia. Os avisos estão disponíveis no histórico de estoque.");
        }
    }

    private static void enviar(List<AlertaEstoque> alertas) {
        try {
            for (AlertaEstoque alerta : alertas) {
                try (Socket socket = new Socket()) {
                    socket.connect(endereco(), TIMEOUT_CONEXAO);
                    socket.setSoTimeout(TIMEOUT_CONEXAO);
                    try (DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                         DataInputStream entrada = new DataInputStream(socket.getInputStream())) {
                        escreverAlerta(saida, PUBLICAR, alerta);
                        if (entrada.readUnsignedByte() != PUBLICADO) throw new IOException("Publicação recusada.");
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Não foi possível transmitir todos os avisos de estoque.", e);
        }
    }

    private void escutar() {
        while (!encerrado) {
            try (Socket socket = new Socket()) {
                socketAberto = socket;
                if (encerrado) return;
                socket.connect(endereco(), TIMEOUT_CONEXAO);
                socket.setSoTimeout(TIMEOUT_CONEXAO);
                try (DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                     DataInputStream entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
                    saida.writeByte(INSCREVER);
                    saida.flush();
                    if (entrada.readUnsignedByte() != INSCRITO) throw new IOException("Inscrição recusada.");
                    socket.setSoTimeout(3 * INTERVALO_PULSO);
                    aoMudarConexao.accept(true);
                    while (!encerrado) {
                        int tipo = entrada.readUnsignedByte();
                        if (tipo == ALERTA) {
                            aoReceber.accept(lerAlerta(entrada));
                        } else if (tipo != PULSO) {
                            throw new IOException("Mensagem de notificação desconhecida.");
                        }
                    }
                }
            } catch (IOException e) {
                if (!encerrado) aoMudarConexao.accept(false);
            } finally {
                socketAberto = null;
            }

            try {
                Thread.sleep(TIMEOUT_CONEXAO);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public void close() {
        encerrado = true;
        escuta.interrupt();
        Socket socket = socketAberto;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}

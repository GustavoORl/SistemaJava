package notificacoes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import model.AlertaEstoque;

final class ProtocoloNotificacoes {
    // Os comandos antigos usavam mensagens sem o tipo da movimentação.
    static final int INSCREVER = 7;
    static final int PUBLICAR = 8;
    static final int INSCRITO = 3;
    static final int ALERTA = 4;
    static final int PULSO = 5;
    static final int PUBLICADO = 6;
    static final int TIMEOUT_CONEXAO = 2_000;
    static final int INTERVALO_PULSO = 5_000;

    private ProtocoloNotificacoes() { }

    static InetSocketAddress endereco() {
        return new InetSocketAddress(
                System.getProperty("costock.notificacoes.host", "127.0.0.1"),
                Integer.getInteger("costock.notificacoes.porta", 5050));
    }

    static AlertaEstoque lerAlerta(DataInputStream entrada) throws IOException {
        String produto = entrada.readUTF();
        int quantidade = entrada.readInt();
        int minimo = entrada.readInt();
        String tipo = entrada.readUTF();
        try {
            return new AlertaEstoque(produto, quantidade, minimo, AlertaEstoque.Tipo.valueOf(tipo));
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    static void escreverAlerta(DataOutputStream saida, int tipo, AlertaEstoque alerta) throws IOException {
        saida.writeByte(tipo);
        saida.writeUTF(alerta.produto());
        saida.writeInt(alerta.quantidade());
        saida.writeInt(alerta.minimo());
        saida.writeUTF(alerta.tipo().name());
        saida.flush();
    }
}

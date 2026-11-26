package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.AlertaEstoque;
import model.AlertaEstoque.Tipo;

public class AlertaEstoqueDAO {
    public AlertaEstoque registrarMovimentacao(Connection conn, Integer idVenda, int idProduto,
            Tipo tipo, int quantidadeMovimentada) throws SQLException {
        String sql = """
                SELECT p.nome, e.quantidade, p.estoque_minimo
                FROM estoque e JOIN produtos p ON p.id_produto = e.id_produto
                WHERE e.id_produto = ?
                FOR SHARE
                """;
        AlertaEstoque alerta;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Estoque não encontrado para o produto com ID: " + idProduto);
                }
                int saldo = rs.getInt("quantidade");
                int minimo = rs.getInt("estoque_minimo");
                if (tipo == Tipo.SAIDA && saldo < minimo && saldo + (long) quantidadeMovimentada >= minimo) {
                    tipo = Tipo.ESTOQUE_BAIXO;
                }
                alerta = new AlertaEstoque(rs.getString("nome"), saldo, minimo, tipo);
            }
        }
        registrar(conn, idVenda, idProduto, alerta);
        return alerta;
    }

    private void registrar(Connection conn, Integer idVenda, int idProduto, AlertaEstoque alerta)
            throws SQLException {
        String sql = """
                INSERT INTO alertas_estoque (id_venda, id_produto, produto, quantidade, estoque_minimo, tipo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, idVenda, Types.INTEGER);
            stmt.setInt(2, idProduto);
            stmt.setString(3, alerta.produto());
            stmt.setInt(4, alerta.quantidade());
            stmt.setInt(5, alerta.minimo());
            stmt.setString(6, alerta.tipo().name());
            stmt.executeUpdate();
        }
    }

    public List<AlertaEstoque> listarRecentes() throws SQLException {
        String sql = """
                SELECT produto, quantidade, estoque_minimo, tipo
                FROM alertas_estoque
                ORDER BY criado_em DESC, id_alerta DESC
                LIMIT 30
                """;
        List<AlertaEstoque> alertas = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertas.add(new AlertaEstoque(rs.getString("produto"),
                        rs.getInt("quantidade"), rs.getInt("estoque_minimo"),
                        Tipo.valueOf(rs.getString("tipo"))));
            }
        }
        return alertas;
    }
}

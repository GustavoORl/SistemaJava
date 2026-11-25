package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.AlertaEstoque;

public class AlertaEstoqueDAO {
    public void registrar(Connection conn, int idVenda, int idProduto, AlertaEstoque alerta)
            throws SQLException {
        String sql = """
                INSERT INTO alertas_estoque (id_venda, id_produto, produto, quantidade, estoque_minimo)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            stmt.setInt(2, idProduto);
            stmt.setString(3, alerta.produto());
            stmt.setInt(4, alerta.quantidade());
            stmt.setInt(5, alerta.minimo());
            stmt.executeUpdate();
        }
    }

    public List<AlertaEstoque> listarRecentes() throws SQLException {
        String sql = """
                SELECT produto, quantidade, estoque_minimo
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
                        rs.getInt("quantidade"), rs.getInt("estoque_minimo")));
            }
        }
        return alertas;
    }
}

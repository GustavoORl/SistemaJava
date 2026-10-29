package dao;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

/** Consultas somente de leitura para as listagens administrativas. */
public class AdministradorConsultaDAO {
    public DefaultTableModel listar(String tabela) throws SQLException {
        if (!tabela.equals("usuarios") && !tabela.equals("cargos") &&
            !tabela.equals("produtos") && !tabela.equals("categorias") &&
            !tabela.equals("estoque") && !tabela.equals("caixas")) {
            throw new IllegalArgumentException("Tabela não autorizada: " + tabela);
        }
        String sql = "SELECT * FROM " + tabela;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int colunas = meta.getColumnCount();
            String[] nomes = new String[colunas];
            for (int i = 1; i <= colunas; i++) {
                nomes[i - 1] = meta.getColumnLabel(i).replace('_', ' ').toUpperCase();
            }
            DefaultTableModel modelo = new DefaultTableModel(nomes, 0) {
                @Override public boolean isCellEditable(int linha, int coluna) { return false; }
            };
            while (rs.next()) {
                Object[] linha = new Object[colunas];
                for (int i = 1; i <= colunas; i++) linha[i - 1] = rs.getObject(i);
                modelo.addRow(linha);
            }
            return modelo;
        }
    }

    public int contar(String tabela) throws SQLException {
        if (!tabela.equals("usuarios") && !tabela.equals("produtos") &&
            !tabela.equals("caixas")) {
            throw new IllegalArgumentException("Tabela não autorizada: " + tabela);
        }
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + tabela);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int contarAbaixoMinimo() throws SQLException {
        String sql = "SELECT COUNT(*) FROM estoque e " +
                     "JOIN produtos p ON p.id_produto = e.id_produto " +
                     "WHERE e.quantidade < p.estoque_minimo";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}

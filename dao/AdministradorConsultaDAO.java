package dao;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

/** Consultas somente de leitura para as listagens administrativas. */
public class AdministradorConsultaDAO {
    public DefaultTableModel listar(String tabela) throws SQLException {
        if (!tabela.equals("usuarios") && !tabela.equals("cargos") &&
            !tabela.equals("produtos") && !tabela.equals("categorias") &&
            !tabela.equals("estoque") && !tabela.equals("caixas") &&
            !tabela.equals("vendas") && !tabela.equals("movimentacoes_estoque")) {
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

    public int contarCaixasAbertos() throws SQLException {
        try (Connection c = Conexao.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT COUNT(*) FROM caixas WHERE status = 'ABERTO'");
             ResultSet r = s.executeQuery()) {
            r.next();
            return r.getInt(1);
        }
    }

    public String[] resumoFaturamento() throws SQLException {
        String sql = "SELECT " +
            "COALESCE(SUM(CASE WHEN DATE(data_venda) = CURDATE() THEN valor_total ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN data_venda >= CURDATE() - INTERVAL 6 DAY THEN valor_total ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN YEAR(data_venda) = YEAR(CURDATE()) AND MONTH(data_venda) = MONTH(CURDATE()) THEN valor_total ELSE 0 END), 0) " +
            "FROM vendas WHERE status = 'FINALIZADA'";
        try (Connection c = Conexao.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) {
            r.next();
            java.text.NumberFormat fmt = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
            return new String[]{fmt.format(r.getBigDecimal(1)), fmt.format(r.getBigDecimal(2)), fmt.format(r.getBigDecimal(3))};
        }
    }
}

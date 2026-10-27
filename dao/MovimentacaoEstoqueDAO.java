package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.MovimentacaoEstoque;
import model.Produto;
import model.Usuario;

public class MovimentacaoEstoqueDAO {
    public void cadastrar(MovimentacaoEstoque movimentacao) {
        String sql = """
                    INSERT INTO movimentacoes_estoque
                    (id_produto, id_usuario, tipo, quantidade, motivo)
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movimentacao.getProduto().getIdProduto());
            stmt.setInt(2, movimentacao.getUsuario().getId());
            stmt.setString(3, movimentacao.getTipo());
            stmt.setInt(4, movimentacao.getQuantidade());
            stmt.setString(5, movimentacao.getMotivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MovimentacaoEstoque> listar() {
        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

        String sql = "SELECT m.id_movimentacao, "
                + "m.id_produto, "
                + "p.nome AS produto_nome, "
                + "m.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "m.tipo, "
                + "m.quantidade, "
                + "m.motivo, "
                + "m.data_movimentacao "
                + "FROM movimentacoes_estoque m "
                + "INNER JOIN produtos p ON m.id_produto = p.id_produto "
                + "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario "
                + "ORDER BY m.data_movimentacao DESC, m.id_movimentacao DESC";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setNome(rs.getString("usuario_nome"));

                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setNome(rs.getString("produto_nome"));

                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                movimentacao.setIdMovimentacao(rs.getInt("id_movimentacao"));
                movimentacao.setMotivo(rs.getString("motivo"));
                movimentacao.setQuantidade(rs.getInt("quantidade"));
                movimentacao.setTipo(rs.getString("tipo"));
                movimentacao.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                movimentacao.setProduto(produto);
                movimentacao.setUsuario(usuario);

                movimentacoes.add(movimentacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movimentacoes;
    }

    public List<MovimentacaoEstoque> buscarPorProduto(int idProduto) {

        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

        String sql = "SELECT m.id_movimentacao, "
                + "m.id_produto, "
                + "p.nome AS produto_nome, "
                + "m.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "m.tipo, "
                + "m.quantidade, "
                + "m.motivo, "
                + "m.data_movimentacao "
                + "FROM movimentacoes_estoque m "
                + "INNER JOIN produtos p ON m.id_produto = p.id_produto "
                + "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario "
                + "WHERE m.id_produto = ? "
                + "ORDER BY m.data_movimentacao DESC, m.id_movimentacao DESC";

        
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);

            try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setNome(rs.getString("produto_nome"));

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario")); 
                usuario.setNome(rs.getString("usuario_nome"));

                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                movimentacao.setIdMovimentacao(rs.getInt("id_movimentacao"));   
                movimentacao.setMotivo(rs.getString("motivo"));
                movimentacao.setQuantidade(rs.getInt("quantidade"));
                movimentacao.setTipo(rs.getString("tipo")); 
                movimentacao.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                movimentacao.setProduto(produto);
                movimentacao.setUsuario(usuario);

                movimentacoes.add(movimentacao);
        }
    }

} catch (SQLException e) {
    System.out.println("Erro ao buscar movimentações por produto!");
    e.printStackTrace();
}

        return movimentacoes;
    }
    
    public List<MovimentacaoEstoque> buscarPorCodigoOuNome(String busca) {
        
        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

        String sql = "SELECT m.id_movimentacao, "
                    + "m.id_produto, "
                    + "p.nome AS produto_nome, "
                    + "m.id_usuario, "
                    + "u.nome AS usuario_nome, "
                    + "m.tipo, "
                    + "m.quantidade, "
                    + "m.motivo, "
                    + "m.data_movimentacao "
                    + "FROM movimentacoes_estoque m "
                    + "INNER JOIN produtos p ON m.id_produto = p.id_produto "
                    + "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario "
                    + "WHERE p.nome LIKE ? OR CAST(p.id_produto AS CHAR) = ? "
                    + "ORDER BY m.data_movimentacao DESC, m.id_movimentacao DESC";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, "%" + busca + "%");
                stmt.setString(2, busca);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));

                    MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                    movimentacao.setIdMovimentacao(rs.getInt("id_movimentacao"));
                    movimentacao.setMotivo(rs.getString("motivo"));
                    movimentacao.setQuantidade(rs.getInt("quantidade"));
                    movimentacao.setTipo(rs.getString("tipo"));
                    movimentacao.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                    movimentacao.setProduto(produto);
                    movimentacao.setUsuario(usuario);

                    movimentacoes.add(movimentacao);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erro ao buscar movimentações por código ou nome!");
            e.printStackTrace();
        }
        return movimentacoes;
    }

    public List<MovimentacaoEstoque> buscarPorUsuario(int idUsuario) {
        List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

        String sql = "SELECT m.id_movimentacao, "
                    + "m.id_produto, "
                    + "p.nome AS produto_nome, "
                    + "m.id_usuario, "
                    + "u.nome AS usuario_nome, "
                    + "m.tipo, "
                    + "m.quantidade, "
                    + "m.motivo, "
                    + "m.data_movimentacao "
                    + "FROM movimentacoes_estoque m "
                    + "INNER JOIN produtos p ON m.id_produto = p.id_produto "
                    + "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario "
                    + "WHERE m.id_usuario = ? "
                    + "ORDER BY m.data_movimentacao DESC, m.id_movimentacao DESC";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));

                    MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                    movimentacao.setIdMovimentacao(rs.getInt("id_movimentacao"));
                    movimentacao.setMotivo(rs.getString("motivo"));
                    movimentacao.setQuantidade(rs.getInt("quantidade"));
                    movimentacao.setTipo(rs.getString("tipo"));
                    movimentacao.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                    movimentacao.setProduto(produto);
                    movimentacao.setUsuario(usuario);

                    movimentacoes.add(movimentacao);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erro ao buscar movimentações por usuário!");
            e.printStackTrace();
        }
        return movimentacoes;
    }
}
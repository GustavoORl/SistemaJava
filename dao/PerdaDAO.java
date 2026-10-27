package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;    
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Produto;
import model.Usuario;
import model.Perda;

public class PerdaDAO {
    public void cadastrar(Perda perda) {
        String sql = """
                    INSERT INTO perdas
                    (id_produto, id_usuario, tipo, quantidade, motivo)
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, perda.getProduto().getIdProduto());
            stmt.setInt(2, perda.getUsuario().getId());
            stmt.setString(3, perda.getTipo());     
            stmt.setInt(4, perda.getQuantidade());
            stmt.setString(5, perda.getMotivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Perda> listar() {
        List<Perda> perdas = new ArrayList<>();

        String sql = "SELECT p.id_perda, "
                + "p.id_produto, "
                + "pr.nome AS produto_nome, "
                + "p.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "p.tipo, "
                + "p.quantidade, "
                + "p.motivo, "
                + "p.data_perda "
                + "FROM perdas p "
                + "INNER JOIN produtos pr ON p.id_produto = pr.id_produto "
                + "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "ORDER BY p.data_perda DESC, p.id_perda DESC";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Perda perda = new Perda();
                perda.setIdPerda(rs.getInt("id_perda"));
                perda.setTipo(rs.getString("tipo"));
                perda.setQuantidade(rs.getInt("quantidade"));
                perda.setMotivo(rs.getString("motivo"));
                perda.setDataPerda(rs.getTimestamp("data_perda").toLocalDateTime());

                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setNome(rs.getString("produto_nome"));
                perda.setProduto(produto);

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setNome(rs.getString("usuario_nome"));
                perda.setUsuario(usuario);

                perdas.add(perda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perdas;
    }

    public List<Perda> listarPorProduto(int idProduto) {
        List<Perda> perdas = new ArrayList<>();

        String sql = "SELECT p.id_perda, "
                + "p.id_produto, "
                + "pr.nome AS produto_nome, "
                + "p.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "p.tipo, "
                + "p.quantidade, "
                + "p.motivo, "
                + "p.data_perda "
                + "FROM perdas p "
                + "INNER JOIN produtos pr ON p.id_produto = pr.id_produto "
                + "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "WHERE p.id_produto = ? "
                + "ORDER BY p.data_perda DESC, p.id_perda DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Perda perda = new Perda();
                    perda.setIdPerda(rs.getInt("id_perda"));
                    perda.setTipo(rs.getString("tipo"));
                    perda.setQuantidade(rs.getInt("quantidade"));
                    perda.setMotivo(rs.getString("motivo"));
                    perda.setDataPerda(rs.getTimestamp("data_perda").toLocalDateTime());

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));
                    perda.setProduto(produto);

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));
                    perda.setUsuario(usuario);

                    perdas.add(perda);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perdas;
    }   

    public List<Perda> listarPorUsuario(int idUsuario) {
        List<Perda> perdas = new ArrayList<>();

        String sql = "SELECT p.id_perda, "
                + "p.id_produto, "
                + "pr.nome AS produto_nome, "
                + "p.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "p.tipo, "
                + "p.quantidade, "
                + "p.motivo, "
                + "p.data_perda "
                + "FROM perdas p "
                + "INNER JOIN produtos pr ON p.id_produto = pr.id_produto "
                + "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "WHERE p.id_usuario = ? "
                + "ORDER BY p.data_perda DESC, p.id_perda DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Perda perda = new Perda();
                    perda.setIdPerda(rs.getInt("id_perda"));
                    perda.setTipo(rs.getString("tipo"));
                    perda.setQuantidade(rs.getInt("quantidade"));
                    perda.setMotivo(rs.getString("motivo"));
                    perda.setDataPerda(rs.getTimestamp("data_perda").toLocalDateTime());

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));
                    perda.setProduto(produto);

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));
                    perda.setUsuario(usuario);

                    perdas.add(perda);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perdas;
    }

    public List<Perda> buscarPorCodigoOuNome(String busca){
        List<Perda> perdas = new ArrayList<>();

        String sql = "SELECT p.id_perda, "
                + "p.id_produto, "
                + "pr.nome AS produto_nome, "
                + "p.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "p.tipo, "
                + "p.quantidade, "
                + "p.motivo, "
                + "p.data_perda "
                + "FROM perdas p "
                + "INNER JOIN produtos pr ON p.id_produto = pr.id_produto "
                + "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "WHERE pr.nome LIKE ? OR CAST(pr.id_produto AS CHAR) LIKE ? "
                + "ORDER BY p.data_perda DESC, p.id_perda DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String buscaFormatada = "%" + busca + "%";
            stmt.setString(1, buscaFormatada);
            stmt.setString(2, buscaFormatada);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Perda perda = new Perda();
                    perda.setIdPerda(rs.getInt("id_perda"));
                    perda.setTipo(rs.getString("tipo"));
                    perda.setQuantidade(rs.getInt("quantidade"));
                    perda.setMotivo(rs.getString("motivo"));
                    perda.setDataPerda(rs.getTimestamp("data_perda").toLocalDateTime());

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));
                    perda.setProduto(produto);

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));
                    perda.setUsuario(usuario);

                    perdas.add(perda);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perdas;
    }

    public List <Perda> buscarPorTipo(String tipo){
        List<Perda> perdas = new ArrayList<>();

        String sql = "SELECT p.id_perda, "
                + "p.id_produto, "
                + "pr.nome AS produto_nome, "
                + "p.id_usuario, "
                + "u.nome AS usuario_nome, "
                + "p.tipo, "
                + "p.quantidade, "
                + "p.motivo, "
                + "p.data_perda "
                + "FROM perdas p "
                + "INNER JOIN produtos pr ON p.id_produto = pr.id_produto "
                + "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "WHERE p.tipo = ? "
                + "ORDER BY p.data_perda DESC, p.id_perda DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Perda perda = new Perda();
                    perda.setIdPerda(rs.getInt("id_perda"));
                    perda.setTipo(rs.getString("tipo"));
                    perda.setQuantidade(rs.getInt("quantidade"));
                    perda.setMotivo(rs.getString("motivo"));
                    perda.setDataPerda(rs.getTimestamp("data_perda").toLocalDateTime());

                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("produto_nome"));
                    perda.setProduto(produto);

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("usuario_nome"));
                    perda.setUsuario(usuario);

                    perdas.add(perda);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perdas;
    }
}
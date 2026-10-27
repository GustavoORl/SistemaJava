package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import exception.EstoqueInsuficienteException;
import  model.Estoque;
import model.Produto;

public class EstoqueDAO {
    /*  
            EstoqueDAO
            movimentacao_estoqueDAO
            PerdaDAO*/

    public void cadastrar(Estoque estoque){
        String sql = "INSERT INTO estoque (id_produto, quantidade) "
                   + "VALUES (?, ?)"; 

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            
            stmt.setInt(1, estoque.getProduto().getIdProduto());
            stmt.setInt(2, estoque.getQuantidade());
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Estoque> listar() {

            List<Estoque> estoques = new ArrayList<>();

            String sql = "SELECT e.id_estoque, "
                            + "e.quantidade, "
                            + "e.data_atualizacao, "
                            + "p.id_produto, "
                            + "p.nome, "                            
                            + "p.descricao, "
                            + "p.codigo_barras, "
                            + "p.preco_custo, "
                            + "p.preco_venda, "
                            + "p.estoque_minimo, "
                            + "p.ativo, "
                            + "p.id_categoria "
                            + "FROM estoque e "
                            + "INNER JOIN produtos p ON e.id_produto = p.id_produto "
                            +"ORDER BY p.nome";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Produto produto = new Produto();

                    produto.setIdProduto(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setCodigoBarras(rs.getString("codigo_barras"));
                    produto.setPrecoCusto(rs.getBigDecimal("preco_custo"));
                    produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
                    produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                    produto.setAtivo(rs.getBoolean("ativo"));
            

                    Estoque estoque = new Estoque();
                    estoque.setIdEstoque(rs.getInt("id_estoque"));
                    estoque.setProduto(produto);
                    estoque.setQuantidade(rs.getInt("quantidade"));
                    estoque.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());

                    estoques.add(estoque);
                }

            } catch (SQLException e) {
                System.out.println("Erro ao listar estoque!");
                e.printStackTrace();
            }

            return estoques;
        }

        public Estoque buscarPorCodigoOuNome(String busca) {

            String sql = "SELECT e.id_estoque, "
                            + "e.quantidade, "
                            + "e.data_atualizacao, "
                            + "p.id_produto, "
                            + "p.nome, "
                            + "p.descricao, "
                            + "p.codigo_barras, "
                            + "p.preco_custo, "
                            + "p.preco_venda, "
                            + "p.estoque_minimo, "
                            + "p.ativo, "
                            + "p.id_categoria "
                            + "FROM estoque e "
                            + "INNER JOIN produtos p ON e.id_produto = p.id_produto "
                            + "WHERE p.ativo = TRUE "
                            + "AND (p.codigo_barras = ? OR p.nome LIKE ?) "
                            + "LIMIT 1";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, busca);
                stmt.setString(2, "%" + busca + "%");

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        Produto produto = new Produto();

                        produto.setIdProduto(rs.getInt("id_produto"));
                        produto.setNome(rs.getString("nome"));
                        produto.setDescricao(rs.getString("descricao"));
                        produto.setCodigoBarras(rs.getString("codigo_barras"));
                        produto.setPrecoCusto(rs.getBigDecimal("preco_custo"));
                        produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
                        produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                        produto.setAtivo(rs.getBoolean("ativo"));

                        Estoque estoque = new Estoque();

                        estoque.setIdEstoque(rs.getInt("id_estoque"));
                        estoque.setProduto(produto);
                        estoque.setQuantidade(rs.getInt("quantidade"));
                        estoque.setDataAtualizacao(
                            rs.getTimestamp("data_atualizacao").toLocalDateTime()
                        );

                        return (estoque);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar estoque!");
                e.printStackTrace();
            }

            return null;
        }

        public void adicionarQuantidade(int idProduto, int quantidade) {

            if (quantidade <= 0) {
                throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
                );
            }

            String sql = "UPDATE estoque "
                    + "SET quantidade = quantidade + ?, "
                    + "data_atualizacao = ? "
                    + "WHERE id_produto = ?";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, quantidade);
                stmt.setTimestamp(
                    2,
                    Timestamp.valueOf(java.time.LocalDateTime.now())
                );
                stmt.setInt(3, idProduto);

                stmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Erro ao adicionar quantidade ao estoque!");
                e.printStackTrace();
            }
        }

        public void adicionarQuantidade(Connection conn, int idProduto, int quantidade)
                    throws SQLException {

                if (quantidade <= 0) {
                    throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero."
                    );
                }

                String sql = "UPDATE estoque "
                        + "SET quantidade = quantidade + ?, "
                        + "data_atualizacao = ? "
                        + "WHERE id_produto = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, quantidade);
                    stmt.setTimestamp(
                        2,
                        Timestamp.valueOf(java.time.LocalDateTime.now())
                    );
                    stmt.setInt(3, idProduto);

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas == 0) {
                        throw new SQLException(
                            "Nenhum estoque encontrado para o produto: " + idProduto
                        );
                    }
                }
        }

        public void retirarQuantidade(int idProduto, int quantidade) throws EstoqueInsuficienteException {
            if (quantidade <= 0) {
                throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
                );
            }

            String sql = "UPDATE estoque "
                    + "SET quantidade = quantidade - ?, "
                    + "data_atualizacao = ? "
                    + "WHERE id_produto = ? "
                    + "AND quantidade >= ?";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, quantidade);
                stmt.setTimestamp(
                    2,
                    Timestamp.valueOf(java.time.LocalDateTime.now())
                );
                stmt.setInt(3, idProduto);
                stmt.setInt(4, quantidade);

                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas == 0) {
                    throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para realizar a saída."
                    );
                }

            } catch (SQLException e) {
                System.out.println("Erro ao retirar quantidade do estoque!");
                e.printStackTrace();
            }
        }

            public Estoque buscarPorIdProduto(int idProduto){
                
                String sql = "SELECT e.id_estoque, "
                                + "e.quantidade, "
                                + "e.data_atualizacao, "
                                + "p.id_produto, "
                                + "p.nome, "
                                + "p.descricao, "
                                + "p.codigo_barras, "
                                + "p.preco_custo, "
                                + "p.preco_venda, "
                                + "p.estoque_minimo, "
                                + "p.ativo, "
                                + "p.id_categoria "
                                + "FROM estoque e "
                                + "INNER JOIN produtos p ON e.id_produto = p.id_produto "
                                + "WHERE p.id_produto = ?";

                try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, idProduto);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (rs.next()) {

                            Produto produto = new Produto();

                            produto.setIdProduto(rs.getInt("id_produto"));
                            produto.setNome(rs.getString("nome"));
                            produto.setDescricao(rs.getString("descricao"));
                            produto.setCodigoBarras(rs.getString("codigo_barras"));
                            produto.setPrecoCusto(rs.getBigDecimal("preco_custo"));
                            produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
                            produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                            produto.setAtivo(rs.getBoolean("ativo"));

                            Estoque estoque = new Estoque();

                            estoque.setIdEstoque(rs.getInt("id_estoque"));
                            estoque.setProduto(produto);
                            estoque.setQuantidade(rs.getInt("quantidade"));
                            estoque.setDataAtualizacao(
                                rs.getTimestamp("data_atualizacao").toLocalDateTime()
                            );

                            return (estoque);
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Erro ao buscar estoque por ID do produto!");
                    e.printStackTrace();
                }

                return null;
            }
        
            public void ajustarQuantidade(Connection conn, int idProduto, int novaQuantidade)
                    throws SQLException {

                if (novaQuantidade < 0) {
                    throw new IllegalArgumentException(
                        "A nova quantidade não pode ser negativa."
                    );
                }

                String sql = "UPDATE estoque "
                        + "SET quantidade = ?, "
                        + "data_atualizacao = ? "
                        + "WHERE id_produto = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, novaQuantidade);
                    stmt.setTimestamp(
                        2,
                        Timestamp.valueOf(java.time.LocalDateTime.now())
                    );
                    stmt.setInt(3, idProduto);

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas == 0) {
                        throw new SQLException(
                            "Estoque não encontrado para o produto com ID: " + idProduto
                        );
                    }
                }
            }
            

}

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Produto;
import model.Categoria;

public class ProdutoDAO {
    public void cadastrar(Produto produto) {

            String sql = "INSERT INTO produtos (nome, descricao, codigo_Barras, preco_Custo, preco_Venda, estoque_Minimo, id_categoria, ativo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setString(1, produto.getNome());
                        stmt.setString(2, produto.getDescricao());
                        stmt.setString(3, produto.getCodigoBarras());
                        stmt.setBigDecimal(4, produto.getPrecoCusto());
                        stmt.setBigDecimal(5, produto.getPrecoVenda());
                        stmt.setInt(6, produto.getEstoqueMinimo());
                        stmt.setInt(7,produto.getCategoria().getId());
                        stmt.setBoolean(8, produto.isAtivo());
                        
 
                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public List<Produto> listar() {
            List<Produto> produtos = new ArrayList<>();

            String sql = "SELECT p.*, "
                       + "c.nome AS categoria_nome, "
                       + "c.descricao AS categoria_descricao "
                       + "FROM produtos p "
                       + "INNER JOIN categorias c ON p.id_categoria = c.id_categoria";

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("categoria_nome"));
                    categoria.setDescricao(rs.getString("categoria_descricao"));


                    Produto produto = new Produto();
                        produto.setIdProduto(rs.getInt("id_produto"));
                        produto.setNome(rs.getString("nome"));
                        produto.setDescricao(rs.getString("descricao"));
                        produto.setCodigoBarras(rs.getString("codigo_barras"));
                        produto.setPrecoCusto(rs.getBigDecimal("preco_custo"));
                        produto.setPrecoVenda(rs.getBigDecimal("preco_venda"));
                        produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                        produto.setAtivo(rs.getBoolean("ativo"));
                        produto.setCategoria(categoria);
                
                        produtos.add(produto);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return produtos;
        }
}

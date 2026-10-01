package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

public class CategoriaDAO {

    public void cadastrar(Categoria categoria){
        String sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)){

                    stmt.setString(1, categoria.getNome());
                    stmt.setString(2, categoria.getDescricao());

                    stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> listar(){
        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT * FROM categorias";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            
                while (rs.next()){
                    Categoria categoria = new Categoria();

                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("nome"));
                    categoria.setDescricao(rs.getString("descricao"));
                    
                    categorias.add(categoria);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }

    public Categoria buscarPorId(int id){
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("nome"));
                    categoria.setDescricao(rs.getString("descricao"));

                    return categoria;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void atualizar(Categoria categoria){
        String sql = "UPDATE categorias SET nome = ?, descricao = ? WHERE id_categoria = ?";
        
        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getId());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id){
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

                stmt.setInt(1, id);

                stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

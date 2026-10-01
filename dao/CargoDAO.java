package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Cargo;

public class CargoDAO {
    
    public int cadastrar(Cargo cargo) {

    String sql = "INSERT INTO cargos (nome) VALUES (?)";

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
                 sql,
                 PreparedStatement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, cargo.getNome());

        stmt.executeUpdate();

        try (ResultSet rs = stmt.getGeneratedKeys()) {

            if (rs.next()) {

                int idGerado = rs.getInt(1);

                cargo.setIdCargo(idGerado);

                return idGerado;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return -1;
}

    public List<Cargo> listar(){
        List<Cargo> cargos = new ArrayList<>();

        String sql = "SELECT * FROM cargos";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            
                while (rs.next()){
                    Cargo cargo = new Cargo();

                    cargo.setIdCargo(rs.getInt("id_cargo"));
                    cargo.setNome(rs.getString("nome"));

                    cargos.add(cargo);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cargos;
    }

    public Cargo buscarPorId(int id){
        String sql = "SELECT * FROM cargos WHERE id_cargo = ?";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){

                    Cargo cargo = new Cargo();
                    cargo.setIdCargo(rs.getInt("id_cargo"));
                    cargo.setNome(rs.getString("nome"));

                    return cargo;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void atualizar(Cargo cargo){
        String sql = "UPDATE cargos SET nome = ? WHERE id_cargo = ?";
        
        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, cargo.getNome());
            stmt.setInt(2, cargo.getId());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id){
        String sql = "DELETE FROM cargos WHERE id_cargo = ?";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

                stmt.setInt(1, id);

                stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

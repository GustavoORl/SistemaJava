package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import model.Permissao;

public class PermissaoDAO {

    public void adicionarPermissaoAoCargo(int idCargo, Permissao permissao) {

        String sql = "INSERT INTO cargo_permissao (id_cargo, id_permissao) "
                + "SELECT ?, id_permissao "
                + "FROM permissoes "
                + "WHERE nome = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCargo);
            stmt.setString(2, permissao.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removerPermissaoDoCargo(int idCargo, Permissao permissao) {

        String sql = "DELETE FROM cargo_permissao "
                + "WHERE id_cargo = ? "
                + "AND id_permissao = ("
                + "SELECT id_permissao FROM permissoes WHERE nome = ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCargo);
            stmt.setString(2, permissao.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<Permissao> listarPermissoesDoCargo(int idCargo) {

        Set<Permissao> permissoes = new HashSet<>();

        String sql = "SELECT p.nome "
                + "FROM permissoes p "
                + "INNER JOIN cargo_permissao cp "
                + "ON p.id_permissao = cp.id_permissao "
                + "WHERE cp.id_cargo = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCargo);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    String nome = rs.getString("nome");

                    Permissao permissao = Permissao.valueOf(nome);

                    permissoes.add(permissao);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissoes;
    }
}
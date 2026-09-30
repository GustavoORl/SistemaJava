    package dao;

    import model.Cargo;
    import model.Usuario;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.List;

    public class UsuarioDAO {

        public void cadastrar(Usuario usuario) {

            String sql = "INSERT INTO usuarios (nome, telefone, email, endereco, senha, id_cargo, ativo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, usuario.getNome());
                    stmt.setString(2, usuario.getTelefone());
                    stmt.setString(3, usuario.getEmail());
                    stmt.setString(4, usuario.getEndereco());
                    stmt.setString(5, usuario.getSenha());
                    stmt.setInt(6, usuario.getCargo().getId());
                    stmt.setBoolean(7, usuario.isAtivo());

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public List<Usuario> listar() {
            List<Usuario> usuarios = new ArrayList<>();

            String sql = "SELECT u.*, c.nome AS nome_cargo "  
                       + "FROM usuarios u " 
                       + "INNER JOIN cargos c ON u.id_cargo = c.id_cargo";

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Cargo cargo = new Cargo();
                    cargo.setIdCargo(rs.getInt("id_cargo"));
                    cargo.setNome(rs.getString("nome_cargo"));


                    Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("senha"),
                        cargo,
                        rs.getBoolean("ativo")
                    );
                    
                    usuarios.add(usuario);
                        

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return usuarios;
        }

        public Usuario buscarPorId(int id){
            String sql = "SELECT u.*, c.nome AS nome_cargo "
                       + "FROM usuarios u "
                       + "INNER JOIN cargos c ON u.id_cargo = c.id_cargo "
                       + "WHERE u.id_usuario = ?";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        Cargo cargo = new Cargo();
                        cargo.setIdCargo(rs.getInt("id_cargo"));
                        cargo.setNome(rs.getString("nome_cargo"));

                        return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("email"),
                            rs.getString("endereco"),
                            rs.getString("senha"),
                            cargo,
                            rs.getBoolean("ativo")
                        );
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public void atualizar(Usuario usuario){
            String sql = "UPDATE usuarios SET "
                       + "nome = ?, "
                       + "telefone = ?, "
                       + "email = ?, "
                       + "endereco = ?, "
                       + "senha = ?, "
                       + "id_cargo = ?, "
                       + "ativo = ? "
                       + "WHERE id_usuario = ?";

            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getTelefone());
                stmt.setString(3, usuario.getEmail());
                stmt.setString(4, usuario.getEndereco());
                stmt.setString(5, usuario.getSenha());
                stmt.setInt(6, usuario.getCargo().getId());
                stmt.setBoolean(7, usuario.isAtivo());
                stmt.setInt(8, usuario.getId());

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void excluir(int id) {
            String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
            try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            
                stmt.setInt(1, id);

                stmt.executeUpdate();
            
            } catch (SQLException e) {
                    e.printStackTrace();
            }
        }
    }
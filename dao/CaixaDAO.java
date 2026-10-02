package dao;

import model.Caixa;
import model.StatusCaixa;
import model.Usuario;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CaixaDAO {

    // ============================================================
    // CADASTRAR / ABRIR CAIXA
    // ============================================================

    public boolean cadastrar(Caixa caixa) {

        String sql = """
            INSERT INTO caixas
            (id_caixa, id_usuario, data_abertura, valor_abertura, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            // id_caixa é STRING
            stmt.setString(
                    1,
                    caixa.getIdCaixa()
            );

            stmt.setInt(
                    2,
                    caixa.getUsuario().getId()
            );

            stmt.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            caixa.getDataAbertura()
                    )
            );

            stmt.setBigDecimal(
                    4,
                    caixa.getValorAbertura()
            );

            stmt.setString(
                    5,
                    caixa.getStatus().name()
            );

            stmt.executeUpdate();

            // ====================================================
            // RECUPERA O id_abertura GERADO PELO BANCO
            // ====================================================

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {

                    caixa.setIdAbertura(
                            rs.getInt(1)
                    );
                }
            }

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // LISTAR TODOS OS CAIXAS
    // ============================================================

    public List<Caixa> listar() {

        List<Caixa> caixas =
                new ArrayList<>();

        String sql = """
            SELECT
                c.id_abertura,
                c.id_caixa,
                c.id_usuario,
                c.data_abertura,
                c.data_fechamento,
                c.valor_abertura,
                c.valor_fechamento,
                c.status,
                u.nome AS nome_usuario
            FROM caixas c
            INNER JOIN usuarios u
                ON c.id_usuario = u.id_usuario
            ORDER BY c.id_abertura DESC
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                caixas.add(
                        mapearCaixa(rs)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return caixas;
    }


    // ============================================================
    // BUSCAR ABERTURA POR ID
    // ============================================================
    // Aqui usamos id_abertura, pois ele identifica
    // unicamente uma abertura de caixa.
    // ============================================================

    public Caixa buscarPorId(int idAbertura) {

        String sql = """
            SELECT
                c.id_abertura,
                c.id_caixa,
                c.id_usuario,
                c.data_abertura,
                c.data_fechamento,
                c.valor_abertura,
                c.valor_fechamento,
                c.status,
                u.nome AS nome_usuario
            FROM caixas c
            INNER JOIN usuarios u
                ON c.id_usuario = u.id_usuario
            WHERE c.id_abertura = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idAbertura
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                if (rs.next()) {

                    return mapearCaixa(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // BUSCAR CAIXA ABERTO
    // ============================================================

    public Caixa buscarCaixaAberto() {

        String sql = """
            SELECT
                c.id_abertura,
                c.id_caixa,
                c.id_usuario,
                c.data_abertura,
                c.data_fechamento,
                c.valor_abertura,
                c.valor_fechamento,
                c.status,
                u.nome AS nome_usuario
            FROM caixas c
            INNER JOIN usuarios u
                ON c.id_usuario = u.id_usuario
            WHERE c.status = 'ABERTO'
            LIMIT 1
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {

                return mapearCaixa(rs);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // BUSCAR ABERTURA PELO IDENTIFICADOR DO CAIXA
    // ============================================================

    public Caixa buscarPorCaixa(String idCaixa) {

        String sql = """
            SELECT
                c.id_abertura,
                c.id_caixa,
                c.id_usuario,
                c.data_abertura,
                c.data_fechamento,
                c.valor_abertura,
                c.valor_fechamento,
                c.status,
                u.nome AS nome_usuario
            FROM caixas c
            INNER JOIN usuarios u
                ON c.id_usuario = u.id_usuario
            WHERE c.id_caixa = ?
            ORDER BY c.id_abertura DESC
            LIMIT 1
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(
                    1,
                    idCaixa
            );

            try (ResultSet rs =
                         stmt.executeQuery()) {

                if (rs.next()) {

                    return mapearCaixa(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // FECHAR CAIXA
    // ============================================================

    public boolean fechar(
            int idAbertura,
            LocalDateTime dataFechamento,
            BigDecimal valorFechamento) {

        String sql = """
            UPDATE caixas
            SET
                data_fechamento = ?,
                valor_fechamento = ?,
                status = 'FECHADO'
            WHERE id_abertura = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            dataFechamento
                    )
            );

            stmt.setBigDecimal(
                    2,
                    valorFechamento
            );

            stmt.setInt(
                    3,
                    idAbertura
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // ATUALIZAR CAIXA
    // ============================================================

    public boolean atualizar(Caixa caixa) {

        String sql = """
            UPDATE caixas
            SET
                id_caixa = ?,
                id_usuario = ?,
                data_abertura = ?,
                data_fechamento = ?,
                valor_abertura = ?,
                valor_fechamento = ?,
                status = ?
            WHERE id_abertura = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // id_caixa é STRING
            stmt.setString(
                    1,
                    caixa.getIdCaixa()
            );

            stmt.setInt(
                    2,
                    caixa.getUsuario().getId()
            );

            stmt.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            caixa.getDataAbertura()
                    )
            );

            // Data de fechamento
            if (caixa.getDataFechamento() != null) {

                stmt.setTimestamp(
                        4,
                        Timestamp.valueOf(
                                caixa.getDataFechamento()
                        )
                );

            } else {

                stmt.setNull(
                        4,
                        Types.TIMESTAMP
                );
            }

            // Valor de abertura
            stmt.setBigDecimal(
                    5,
                    caixa.getValorAbertura()
            );

            // Valor de fechamento
            if (caixa.getValorFechamento() != null) {

                stmt.setBigDecimal(
                        6,
                        caixa.getValorFechamento()
                );

            } else {

                stmt.setNull(
                        6,
                        Types.DECIMAL
                );
            }

            // Status
            stmt.setString(
                    7,
                    caixa.getStatus().name()
            );

            // id_abertura
            stmt.setInt(
                    8,
                    caixa.getIdAbertura()
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // EXCLUIR ABERTURA
    // ============================================================

    public boolean excluir(int idAbertura) {

        String sql = """
            DELETE FROM caixas
            WHERE id_abertura = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idAbertura
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // MAPEAR RESULTSET -> OBJETO CAIXA
    // ============================================================

    private Caixa mapearCaixa(
            ResultSet rs
    ) throws SQLException {

        Caixa caixa =
                new Caixa();

        // ========================================================
        // ID DA ABERTURA
        // ========================================================

        caixa.setIdAbertura(
                rs.getInt("id_abertura")
        );

        // ========================================================
        // IDENTIFICADOR DO CAIXA
        // ========================================================

        caixa.setIdCaixa(
                rs.getString("id_caixa")
        );

        // ========================================================
        // USUÁRIO RESPONSÁVEL
        // ========================================================

        Usuario usuario =
                new Usuario();

        usuario.setId(
                rs.getInt("id_usuario")
        );

        usuario.setNome(
                rs.getString("nome_usuario")
        );

        caixa.setUsuario(
                usuario
        );

        // ========================================================
        // DATA DE ABERTURA
        // ========================================================

        Timestamp dataAbertura =
                rs.getTimestamp(
                        "data_abertura"
                );

        if (dataAbertura != null) {

            caixa.setDataAbertura(
                    dataAbertura.toLocalDateTime()
            );
        }

        // ========================================================
        // DATA DE FECHAMENTO
        // ========================================================

        Timestamp dataFechamento =
                rs.getTimestamp(
                        "data_fechamento"
                );

        if (dataFechamento != null) {

            caixa.setDataFechamento(
                    dataFechamento.toLocalDateTime()
            );
        }

        // ========================================================
        // VALOR DE ABERTURA
        // ========================================================

        caixa.setValorAbertura(
                rs.getBigDecimal(
                        "valor_abertura"
                )
        );

        // ========================================================
        // VALOR DE FECHAMENTO
        // ========================================================

        caixa.setValorFechamento(
                rs.getBigDecimal(
                        "valor_fechamento"
                )
        );

        // ========================================================
        // STATUS
        // ========================================================

        caixa.setStatus(
                StatusCaixa.valueOf(
                        rs.getString("status")
                )
        );

        return caixa;
    }
}
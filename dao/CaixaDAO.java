package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Caixa;
import model.ResumoFechamentoCaixa;
import model.StatusCaixa;
import model.Usuario;

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
            // RECUPERA O id_tabela_caixa GERADO PELO BANCO
            // ====================================================

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {

                    caixa.setIdTabelaCaixa(
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
                c.id_tabela_caixa,
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
            ORDER BY c.id_tabela_caixa DESC
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
    // Aqui usamos id_tabela_caixa, pois ele identifica
    // unicamente uma abertura de caixa.
    // ============================================================

    public Caixa buscarPorId(int idTabelaCaixa) {

        String sql = """
            SELECT
                c.id_tabela_caixa,
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
            WHERE c.id_tabela_caixa = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idTabelaCaixa
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

    public Caixa buscarCaixaAberto(String idCaixa) {

    String sql = """
        SELECT
            c.id_tabela_caixa,
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
          AND c.status = 'ABERTO'
        LIMIT 1
        """;

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, idCaixa);

        try (ResultSet rs = stmt.executeQuery()) {

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
            int idTabelaCaixa,
            LocalDateTime dataFechamento,
            BigDecimal valorFechamento) {

        String sql = """
            UPDATE caixas
            SET
                data_fechamento = ?,
                valor_fechamento = ?,
                status = 'FECHADO'
            WHERE id_tabela_caixa = ?
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
                    idTabelaCaixa
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
            WHERE id_tabela_caixa = ?
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

            // id_tabela_caixa
            stmt.setInt(
                    8,
                    caixa.getIdTabelaCaixa()
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

    public boolean excluir(int idTabelaCaixa) {

        String sql = """
            DELETE FROM caixas
            WHERE id_tabela_caixa = ?
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    idTabelaCaixa
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

        caixa.setIdTabelaCaixa(
                rs.getInt("id_tabela_caixa")
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


public ResumoFechamentoCaixa buscarResumoFechamento(
        int idTabelaCaixa
) throws SQLException {

    if (idTabelaCaixa <= 0) {
        throw new SQLException("Sessão de caixa inválida.");
    }

    ResumoFechamentoCaixa resumo =
            new ResumoFechamentoCaixa();

    resumo.setIdTabelaCaixa(idTabelaCaixa);

    String sqlCaixa = """
        SELECT valor_abertura
        FROM caixas
        WHERE id_tabela_caixa = ?
        """;

    String sqlPagamentos = """
        SELECT
            fp.nome AS forma_pagamento,
            COALESCE(SUM(p.valor), 0) AS total
        FROM pagamentos p
        INNER JOIN formas_pagamento fp
            ON fp.id_forma_pagamento = p.id_forma_pagamento
        INNER JOIN vendas v
            ON v.id_venda = p.id_venda
        WHERE p.id_tabela_caixa = ?
          AND v.id_tabela_caixa = p.id_tabela_caixa
          AND v.status = 'FINALIZADA'
        GROUP BY fp.id_forma_pagamento, fp.nome
        """;

    String sqlQuantidade = """
        SELECT COUNT(*) AS quantidade
        FROM vendas
        WHERE id_tabela_caixa = ?
          AND status = 'FINALIZADA'
        """;

    try (Connection conn = Conexao.getConnection()) {

        try (PreparedStatement stmt =
                     conn.prepareStatement(sqlCaixa)) {

            stmt.setInt(1, idTabelaCaixa);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Sessão de caixa não encontrada."
                    );
                }

                resumo.setValorAbertura(
                        rs.getBigDecimal("valor_abertura")
                );
            }
        }

        try (PreparedStatement stmt =
                     conn.prepareStatement(sqlPagamentos)) {

            stmt.setInt(1, idTabelaCaixa);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    String forma = rs.getString(
                            "forma_pagamento"
                    );

                    BigDecimal total = rs.getBigDecimal(
                            "total"
                    );

                    switch (forma) {

                        case "DINHEIRO":
                            resumo.setTotalDinheiro(total);
                            break;

                        case "PIX":
                            resumo.setTotalPix(total);
                            break;

                        case "CARTAO_CREDITO":
                            resumo.setTotalCredito(total);
                            break;

                        case "CARTAO_DEBITO":
                            resumo.setTotalDebito(total);
                            break;

                        case "PRAZO":
                            resumo.setTotalPrazo(total);
                            break;

                        default:
                            throw new SQLException(
                                    "Forma de pagamento desconhecida: "
                                    + forma
                            );
                    }
                }
            }
        }

        try (PreparedStatement stmt =
                     conn.prepareStatement(sqlQuantidade)) {

            stmt.setInt(1, idTabelaCaixa);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    resumo.setQuantidadeVendas(
                            rs.getInt("quantidade")
                    );
                }
            }
        }
    }

    return resumo;
}


public ResumoFechamentoCaixa fecharComConferencia(
        int idTabelaCaixa,
        BigDecimal valorContado,
        String observacao
) throws SQLException {

    if (idTabelaCaixa <= 0) {
        throw new SQLException(
                "Sessão de caixa inválida."
        );
    }

    if (valorContado == null
            || valorContado.signum() < 0
            || valorContado.scale() > 2) {

        throw new SQLException(
                "Valor contado inválido."
        );
    }

    if (observacao == null) {
        observacao = "";
    }

    if (observacao.length() > 500) {
        throw new SQLException(
                "A observação excede 500 caracteres."
        );
    }

    String sqlBloqueio = """
        SELECT status
        FROM caixas
        WHERE id_tabela_caixa = ?
        FOR UPDATE
        """;

    String sqlFechamento = """
        UPDATE caixas
        SET
            data_fechamento = CURRENT_TIMESTAMP,
            valor_fechamento = ?,
            valor_esperado = ?,
            diferenca = ?,
            observacao_fechamento = ?,
            status = 'FECHADO'
        WHERE id_tabela_caixa = ?
          AND status = 'ABERTO'
        """;

    try (Connection conn = Conexao.getConnection()) {

        conn.setAutoCommit(false);

        try {

            // Bloqueia a sessão, impedindo que uma
            // venda seja finalizada simultaneamente.

            try (PreparedStatement stmt =
                    conn.prepareStatement(sqlBloqueio)) {

                stmt.setInt(1, idTabelaCaixa);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "Sessão não encontrada."
                        );
                    }

                    if (!"ABERTO".equals(
                            rs.getString("status"))) {

                        throw new SQLException(
                                "Este caixa já está fechado."
                        );
                    }
                }
            }

            // Recalcula os valores usando a mesma
            // conexão e dentro da transação.

            ResumoFechamentoCaixa resumo =
                    consultarResumoNaConexao(
                            conn,
                            idTabelaCaixa
                    );

            BigDecimal esperado =
                    resumo.getDinheiroEsperado();

            BigDecimal diferenca =
                    valorContado.subtract(esperado);

            try (PreparedStatement stmt =
                    conn.prepareStatement(sqlFechamento)) {

                stmt.setBigDecimal(
                        1,
                        valorContado
                );

                stmt.setBigDecimal(
                        2,
                        esperado
                );

                stmt.setBigDecimal(
                        3,
                        diferenca
                );

                stmt.setString(
                        4,
                        observacao
                );

                stmt.setInt(
                        5,
                        idTabelaCaixa
                );

                if (stmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Não foi possível encerrar a sessão."
                    );
                }
            }

            conn.commit();

            return resumo;

        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (SQLException rollbackErro) {
                e.addSuppressed(rollbackErro);
            }

            if (e instanceof SQLException) {
                throw (SQLException) e;
            }

            throw new SQLException(
                    "Erro no fechamento: "
                            + e.getMessage(),
                    e
            );

        } finally {
            conn.setAutoCommit(true);
        }
    }
}

// =====================================================
// RESUMO FINANCEIRO NA MESMA CONEXÃO
// =====================================================

private ResumoFechamentoCaixa consultarResumoNaConexao(
        Connection conn,
        int idTabelaCaixa
) throws SQLException {

    ResumoFechamentoCaixa resumo =
            new ResumoFechamentoCaixa();

    resumo.setIdTabelaCaixa(idTabelaCaixa);

    String sqlCaixa = """
        SELECT valor_abertura
        FROM caixas
        WHERE id_tabela_caixa = ?
        """;

    String sqlPagamentos = """
        SELECT
            fp.nome AS forma_pagamento,
            COALESCE(SUM(p.valor), 0) AS total
        FROM pagamentos p
        INNER JOIN formas_pagamento fp
            ON fp.id_forma_pagamento = p.id_forma_pagamento
        INNER JOIN vendas v
            ON v.id_venda = p.id_venda
        WHERE p.id_tabela_caixa = ?
          AND v.id_tabela_caixa = p.id_tabela_caixa
          AND v.status = 'FINALIZADA'
        GROUP BY fp.id_forma_pagamento, fp.nome
        """;

    String sqlVendas = """
        SELECT
            COUNT(*) AS quantidade,
            COALESCE(SUM(valor_total), 0) AS total
        FROM vendas
        WHERE id_tabela_caixa = ?
          AND status = 'FINALIZADA'
        """;

    try (PreparedStatement stmt =
            conn.prepareStatement(sqlCaixa)) {

        stmt.setInt(1, idTabelaCaixa);

        try (ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) {
                throw new SQLException(
                        "Sessão não encontrada."
                );
            }

            resumo.setValorAbertura(
                    rs.getBigDecimal("valor_abertura")
            );
        }
    }

    try (PreparedStatement stmt =
            conn.prepareStatement(sqlPagamentos)) {

        stmt.setInt(1, idTabelaCaixa);

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String forma =
                        rs.getString("forma_pagamento");

                BigDecimal total =
                        rs.getBigDecimal("total");

                switch (forma) {

                    case "DINHEIRO":
                        resumo.setTotalDinheiro(total);
                        break;

                    case "PIX":
                        resumo.setTotalPix(total);
                        break;

                    case "CARTAO_CREDITO":
                        resumo.setTotalCredito(total);
                        break;

                    case "CARTAO_DEBITO":
                        resumo.setTotalDebito(total);
                        break;

                    case "PRAZO":
                        resumo.setTotalPrazo(total);
                        break;

                    default:
                        throw new SQLException(
                                "Forma de pagamento desconhecida: "
                                        + forma
                        );
                }
            }
        }
    }

    BigDecimal totalRegistrado;

    try (PreparedStatement stmt =
            conn.prepareStatement(sqlVendas)) {

        stmt.setInt(1, idTabelaCaixa);

        try (ResultSet rs = stmt.executeQuery()) {

            rs.next();

            resumo.setQuantidadeVendas(
                    rs.getInt("quantidade")
            );

            totalRegistrado =
                    rs.getBigDecimal("total");
        }
    }

    // Verifica se os pagamentos contabilizados
    // correspondem ao valor das vendas finalizadas.

    if (resumo.getTotalVendas()
            .compareTo(totalRegistrado) != 0) {

        throw new SQLException(
                "Divergência entre vendas e pagamentos. "
                + "Total das vendas: "
                + totalRegistrado
                + " | Total dos pagamentos: "
                + resumo.getTotalVendas()
        );
    }

    return resumo;
}


}
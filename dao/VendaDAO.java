package dao;

import model.AlertaEstoque;
import model.AlertaEstoque.Tipo;
import model.Venda;
import model.itemVenda;
import model.Pagamento;
import model.StatusVenda;
import model.StatusPagamento;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendaDAO {

    private final AlertaEstoqueDAO alertaEstoqueDAO = new AlertaEstoqueDAO();

    public record ResultadoVenda(int idVenda, List<AlertaEstoque> alertas) { }

    public int finalizarVenda(Venda venda) throws SQLException {
        return finalizarVendaComAlertas(venda).idVenda();
    }

    public ResultadoVenda finalizarVendaComAlertas(Venda venda) throws SQLException {

        validarVenda(venda);

        try (Connection conn = Conexao.getConnection()) {

            conn.setAutoCommit(false);

            try {
                validarCaixaAberto(conn, venda.getCaixa().getIdTabelaCaixa());

                int idVenda = inserirVenda(conn, venda);
                Map<Integer, Integer> quantidadesVendidas = new LinkedHashMap<>();

                for (itemVenda item : venda.getItens()) {
                    inserirItem(conn, idVenda, item);
                    int quantidade = baixarEstoque(conn, item);
                    quantidadesVendidas.merge(item.getProduto().getIdProduto(), quantidade, Integer::sum);
                    registrarMovimentacao(
                            conn,
                            item,
                            venda.getUsuario().getId(),
                            idVenda
                    );
                }

                for (Pagamento pagamento : venda.getPagamentos()) {
                    inserirPagamento(
                            conn,
                            idVenda,
                            venda.getCaixa().getIdTabelaCaixa(),
                            pagamento
                    );
                }

                List<AlertaEstoque> alertas = registrarAlertasEstoque(conn, idVenda, quantidadesVendidas);
                conn.commit();

                venda.setIdVenda(idVenda);
                venda.setStatus(StatusVenda.FINALIZADA);

                return new ResultadoVenda(idVenda, alertas);

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
                        "Erro ao finalizar a venda: " + e.getMessage(),
                        e
                );

            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void validarVenda(Venda venda) throws SQLException {

        if (venda == null
                || venda.getUsuario() == null
                || venda.getUsuario().getId() <= 0
                || venda.getCaixa() == null
                || venda.getCaixa().getIdTabelaCaixa() <= 0) {

            throw new SQLException(
                    "Usuário ou sessão de caixa inválidos."
            );
        }

        List<itemVenda> itens = venda.getItens();
        List<Pagamento> pagamentos = venda.getPagamentos();

        if (itens == null || itens.isEmpty()) {
            throw new SQLException("A venda não possui itens.");
        }

        if (pagamentos == null || pagamentos.isEmpty()) {
            throw new SQLException("A venda não possui pagamentos.");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (itemVenda item : itens) {

            if (item == null
                    || item.getProduto() == null
                    || item.getProduto().getIdProduto() <= 0
                    || item.getQuantidade() == null
                    || item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0
                    || item.getQuantidade().scale() > 0
                    || item.getPrecoUnitario() == null
                    || item.getPrecoUnitario().signum() < 0) {

                throw new SQLException("Item da venda inválido.");
            }

            BigDecimal desconto = item.getDesconto() == null
                    ? BigDecimal.ZERO
                    : item.getDesconto();

            BigDecimal bruto = item.getPrecoUnitario()
                    .multiply(item.getQuantidade());

            if (desconto.signum() < 0
                    || desconto.compareTo(bruto) > 0) {

                throw new SQLException("Desconto inválido.");
            }

            BigDecimal subtotal = bruto.subtract(desconto);

            item.setSubtotal(subtotal);

            totalCalculado = totalCalculado.add(subtotal);
        }

        if (totalCalculado.signum() <= 0) {
            throw new SQLException("O total da venda deve ser positivo.");
        }

        BigDecimal totalPagamentos = BigDecimal.ZERO;

        for (Pagamento pagamento : pagamentos) {

            if (pagamento == null
                    || pagamento.getTipoFormaPagamento() == null
                    || pagamento.getStatus() != StatusPagamento.APROVADO
                    || pagamento.getValor() == null
                    || pagamento.getValor().signum() <= 0) {

                throw new SQLException("Pagamento inválido.");
            }

            totalPagamentos = totalPagamentos.add(
                    pagamento.getValor()
            );
        }

        if (totalPagamentos.compareTo(totalCalculado) != 0) {
            throw new SQLException(
                    "A soma dos pagamentos não corresponde ao total."
            );
        }

        venda.setValorTotal(totalCalculado);
    }

    private void validarCaixaAberto(
            Connection conn,
            int idTabelaCaixa
    ) throws SQLException {

        String sql = """
            SELECT status
            FROM caixas
            WHERE id_tabela_caixa = ?
            FOR UPDATE
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTabelaCaixa);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Sessão de caixa não encontrada."
                    );
                }

                if (!"ABERTO".equals(rs.getString("status"))) {
                    throw new SQLException(
                            "Não é possível vender com o caixa fechado."
                    );
                }
            }
        }
    }

    private int inserirVenda(
            Connection conn,
            Venda venda
    ) throws SQLException {

        String sql = """
            INSERT INTO vendas (
                id_usuario,
                id_tabela_caixa,
                data_venda,
                valor_total,
                status
            )
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            stmt.setInt(1, venda.getUsuario().getId());

            stmt.setInt(2, venda.getCaixa().getIdTabelaCaixa());

            LocalDateTime data = venda.getDataVenda() != null
                    ? venda.getDataVenda()
                    : LocalDateTime.now();

            stmt.setTimestamp(3, Timestamp.valueOf(data));

            stmt.setBigDecimal(4, venda.getValorTotal());

            stmt.setString(5, StatusVenda.FINALIZADA.name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Não foi possível obter o ID da venda.");
    }

    private void inserirItem(
            Connection conn,
            int idVenda,
            itemVenda item
    ) throws SQLException {

        String sql = """
            INSERT INTO itens_venda (
                id_venda,
                id_produto,
                quantidade,
                preco_unitario,
                desconto,
                subtotal
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);

            stmt.setInt(2, item.getProduto().getIdProduto());

            stmt.setInt(3, item.getQuantidade().intValueExact());

            stmt.setBigDecimal(4, item.getPrecoUnitario());

            stmt.setBigDecimal(
                    5,
                    item.getDesconto() == null
                            ? BigDecimal.ZERO
                            : item.getDesconto()
            );

            stmt.setBigDecimal(6, item.getSubtotal());

            stmt.executeUpdate();
        }
    }

    private int baixarEstoque(
            Connection conn,
            itemVenda item
    ) throws SQLException {

        String sql = """
            UPDATE estoque
            SET quantidade = quantidade - ?,
                data_atualizacao = CURRENT_TIMESTAMP
            WHERE id_produto = ?
              AND quantidade >= ?
            """;

        int quantidade = item.getQuantidade().intValueExact();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, item.getProduto().getIdProduto());
            stmt.setInt(3, quantidade);

            int linhas = stmt.executeUpdate();

            if (linhas != 1) {
                throw new SQLException(
                        "Estoque insuficiente ou não cadastrado para: "
                        + item.getProduto().getNome()
                );
            }
        }
        return quantidade;
    }

    private List<AlertaEstoque> registrarAlertasEstoque(Connection conn, int idVenda,
            Map<Integer, Integer> quantidadesVendidas) throws SQLException {
        List<AlertaEstoque> alertas = new ArrayList<>();
        for (var vendido : quantidadesVendidas.entrySet()) {
            alertas.add(alertaEstoqueDAO.registrarMovimentacao(
                    conn, idVenda, vendido.getKey(), Tipo.SAIDA, vendido.getValue()));
        }
        return List.copyOf(alertas);
    }

    private void registrarMovimentacao(
            Connection conn,
            itemVenda item,
            int idUsuario,
            int idVenda
    ) throws SQLException {

        String sql = """
            INSERT INTO movimentacoes_estoque (
                id_produto,
                id_usuario,
                tipo,
                quantidade,
                motivo
            )
            VALUES (?, ?, 'SAIDA', ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getProduto().getIdProduto());

            stmt.setInt(2, idUsuario);

            stmt.setInt(3, item.getQuantidade().intValueExact());

            stmt.setString(4, "Venda #" + idVenda);

            stmt.executeUpdate();
        }
    }

    private void inserirPagamento(
            Connection conn,
            int idVenda,
            int idTabelaCaixa,
            Pagamento pagamento
    ) throws SQLException {

        int idFormaPagamento = buscarFormaPagamento(
                conn,
                pagamento.getTipoFormaPagamento().name()
        );

        String sql = """
            INSERT INTO pagamentos (
                id_venda,
                id_tabela_caixa,
                id_forma_pagamento,
                valor,
                data_pagamento
            )
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);

            stmt.setInt(2, idTabelaCaixa);

            stmt.setInt(3, idFormaPagamento);

            stmt.setBigDecimal(4, pagamento.getValor());

            LocalDateTime data = pagamento.getDataPagamento() != null
                    ? pagamento.getDataPagamento()
                    : LocalDateTime.now();

            stmt.setTimestamp(5, Timestamp.valueOf(data));

            stmt.executeUpdate();
        }
    }

    private int buscarFormaPagamento(
            Connection conn,
            String nome
    ) throws SQLException {

        String sql = """
            SELECT id_forma_pagamento
            FROM formas_pagamento
            WHERE nome = ?
              AND ativo = 1
            LIMIT 1
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id_forma_pagamento");
                }
            }
        }

        throw new SQLException(
                "Forma de pagamento não cadastrada ou inativa: " + nome
        );
    }
}

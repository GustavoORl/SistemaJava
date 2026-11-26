package services;

import dao.AlertaEstoqueDAO;
import dao.EstoqueDAO;
import exception.EstoqueInsuficienteException;
import exception.ProdutoNaoEncontradoException;

import java.sql.Connection;
import java.sql.SQLException;

import dao.Conexao;
import dao.MovimentacaoEstoqueDAO;
import model.AlertaEstoque;
import model.AlertaEstoque.Tipo;
import model.MovimentacaoEstoque;
import model.Produto;
import model.Estoque;
import model.Usuario;

public class EstoqueService {

    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private final MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
    private final AlertaEstoqueDAO alertaEstoqueDAO = new AlertaEstoqueDAO();

    public AlertaEstoque adicionarEstoque(int idProduto, int idUsuario,
        int quantidade, String motivo) throws ProdutoNaoEncontradoException {

    if (quantidade <= 0) {
        throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
    }

    if (idProduto <= 0 || idUsuario <= 0) {
        throw new IllegalArgumentException("Os IDs devem ser maiores que zero.");
    }

    if (motivo == null || motivo.isBlank()) {
        throw new IllegalArgumentException("O motivo não pode ser nulo ou vazio.");
    }

    try (Connection conn = Conexao.getConnection()) {

        conn.setAutoCommit(false);

        try {
            Estoque estoque = estoqueDAO.buscarPorIdProduto(idProduto);

            if (estoque == null) {
                throw new ProdutoNaoEncontradoException(
                    "Estoque não encontrado para o produto com ID: " + idProduto
                );
            }

            Produto produto = estoque.getProduto();

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException("O produto está inativo.");
            }

            Usuario usuario = new Usuario();
            usuario.setId(idUsuario);

            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(usuario);
            movimentacao.setTipo("ENTRADA");
            movimentacao.setQuantidade(quantidade);
            movimentacao.setMotivo(motivo);

            estoqueDAO.adicionarQuantidade(conn, idProduto, quantidade);

            return confirmarMovimentacao(conn, movimentacao, Tipo.ENTRADA);

        } catch (SQLException | ProdutoNaoEncontradoException | RuntimeException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackErro) {
                e.addSuppressed(rollbackErro);
            }
            throw e;
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
            "Erro ao adicionar estoque e registrar movimentação.", e
        );
    }
}

    public AlertaEstoque retirarEstoque(int idProduto, int idUsuario,
                              int quantidade, String motivo)
            throws ProdutoNaoEncontradoException, EstoqueInsuficienteException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (idProduto <= 0 || idUsuario <= 0) {
            throw new IllegalArgumentException("Os IDs devem ser maiores que zero.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo não pode ser nulo ou vazio.");
        }

        try (Connection conn = Conexao.getConnection()) {

            conn.setAutoCommit(false);

            try {
                Estoque estoque = estoqueDAO.buscarPorIdProduto(idProduto);

                if (estoque == null) {
                    throw new ProdutoNaoEncontradoException(
                        "Estoque não encontrado para o produto com ID: " + idProduto
                    );
                }

                Produto produto = estoque.getProduto();

                if (!produto.isAtivo()) {
                    throw new IllegalArgumentException("O produto está inativo.");
                }

                Usuario usuario = new Usuario();
                usuario.setId(idUsuario);

                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                movimentacao.setProduto(produto);
                movimentacao.setUsuario(usuario);
                movimentacao.setTipo("SAIDA");
                movimentacao.setQuantidade(quantidade);
                movimentacao.setMotivo(motivo);

                estoqueDAO.retirarQuantidade(conn, idProduto, quantidade);

                return confirmarMovimentacao(conn, movimentacao, Tipo.SAIDA);

            } catch (SQLException | ProdutoNaoEncontradoException
                     | EstoqueInsuficienteException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackErro) {
                    e.addSuppressed(rollbackErro);
                }
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "Erro ao retirar estoque e registrar movimentação.", e
            );
        }
    }

    public AlertaEstoque ajustarEstoque(int idProduto, int idUsuario,
        int novaQuantidade, String motivo) throws ProdutoNaoEncontradoException {

    if (novaQuantidade < 0) {
        throw new IllegalArgumentException(
            "A nova quantidade não pode ser negativa."
        );
    }

    if (idProduto <= 0 || idUsuario <= 0) {
        throw new IllegalArgumentException(
            "Os IDs devem ser maiores que zero."
        );
    }

    if (motivo == null || motivo.isBlank()) {
        throw new IllegalArgumentException(
            "O motivo não pode ser nulo ou vazio."
        );
    }

    try (Connection conn = Conexao.getConnection()) {

        conn.setAutoCommit(false);

        try {
            Estoque estoque = estoqueDAO.buscarPorIdProduto(idProduto);

            if (estoque == null) {
                throw new ProdutoNaoEncontradoException(
                    "Estoque não encontrado para o produto com ID: " + idProduto
                );
            }

            Produto produto = estoque.getProduto();

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException(
                    "Não é possível ajustar o estoque de um produto inativo."
                );
            }

            Usuario usuario = new Usuario();
            usuario.setId(idUsuario);

            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(usuario);
            movimentacao.setTipo("AJUSTE");
            movimentacao.setQuantidade(novaQuantidade);
            movimentacao.setMotivo(motivo);

            estoqueDAO.ajustarQuantidade(conn, idProduto, novaQuantidade);

            return confirmarMovimentacao(conn, movimentacao, Tipo.AJUSTE);

        } catch (SQLException | ProdutoNaoEncontradoException | RuntimeException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackErro) {
                e.addSuppressed(rollbackErro);
            }
            throw e;
        }

    } catch (SQLException e) {
        throw new IllegalStateException(
            "Erro ao ajustar o estoque e registrar a movimentação.", e
        );
    }
}

    private AlertaEstoque confirmarMovimentacao(Connection conn, MovimentacaoEstoque movimentacao,
            Tipo tipo) throws SQLException {
        movimentacaoDAO.cadastrar(conn, movimentacao);
        AlertaEstoque alerta = alertaEstoqueDAO.registrarMovimentacao(conn, null,
                movimentacao.getProduto().getIdProduto(), tipo, movimentacao.getQuantidade());
        conn.commit();
        return alerta;
    }

    public int consultarQuantidade(int idProduto) throws ProdutoNaoEncontradoException {

        Estoque estoque = estoqueDAO.buscarPorIdProduto(idProduto);

        if (estoque == null) {
            throw new ProdutoNaoEncontradoException( "Estoque não encontrado para o produto com ID: " + idProduto );
        }

        return estoque.getQuantidade();
    }

    public boolean verificarDisponibilidade(int idProduto, int quantidade) throws ProdutoNaoEncontradoException {

        if (quantidade<= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        int quantidadeDisponivel = consultarQuantidade(idProduto);

        return quantidadeDisponivel >= quantidade;
    }
}

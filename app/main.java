package app;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Caixa;
import model.Cargo;
import model.Categoria;
import model.Estoque;
import model.Produto;
import model.StatusCaixa;
import model.StatusVenda;
import model.Usuario;
import model.Venda;
import model.itemVenda;


public class Main {
    public static void main(String[] args) {

        // =====================================
        // 1. CRIAR UMA CATEGORIA
        // =====================================

        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNome("Alimentos");
        categoria.setDescricao("Produtos alimentícios");

        // =====================================
        // 2. CRIAR UM PRODUTO
        // =====================================

        Produto produto = new Produto();

        produto.setIdProduto(1);
        produto.setNome("Arroz 5kg");
        produto.setDescricao("Arroz tipo 1");
        produto.setCodigoBarras("7891234567890");
        produto.setPrecoCusto(new BigDecimal("20.00"));
        produto.setPrecoVenda(new BigDecimal("30.00"));
        produto.setEstoqueMinimo(10);
        produto.setCategoria(categoria);
        produto.setAtivo(true);

        System.out.println("Produto criado: "
                + produto.getNome());

        // =====================================
        // 3. ADICIONAR O PRODUTO AO ESTOQUE
        // =====================================

        Estoque estoque = new Estoque();

        estoque.setIdEstoque(1);
        estoque.setProduto(produto);
        estoque.setQuantidade(new BigDecimal("50"));
        estoque.setDataAtualizacao(LocalDateTime.now());

        System.out.println("Quantidade em estoque: "
                + estoque.getQuantidade());

        // =====================================
        // 4. CRIAR UM FUNCIONÁRIO
        // =====================================

        Cargo cargo = new Cargo();

        cargo.setIdCargo(1);
        cargo.setNome("Caixa");

        Usuario funcionario = new Usuario();

        funcionario.setId(1);
        funcionario.setNome("João da Silva");
        funcionario.setEmail("joao@mercado.com");
        funcionario.setCargo(cargo);
        funcionario.setAtivo(true);

        System.out.println("Funcionário: "
                + funcionario.getNome());

        // =====================================
        // 5. ABRIR UM CAIXA
        // =====================================

        Caixa caixa = new Caixa();

        caixa.setIdCaixa(1);
        caixa.setUsuarioAbertura(funcionario);
        caixa.setDataAbertura(LocalDateTime.now());
        caixa.setValorAbertura(new BigDecimal("100.00"));
        caixa.setStatus(StatusCaixa.ABERTO);

        System.out.println("Caixa aberto!");

        // =====================================
        // 6. CRIAR UMA VENDA
        // =====================================

        Venda venda = new Venda();

        venda.setIdVenda(1);
        venda.setDataVenda(LocalDateTime.now());
        venda.setUsuario(funcionario);
        venda.setCaixa(caixa);
        venda.setStatus(StatusVenda.ABERTA);

        List<itemVenda> itens = new ArrayList<>();

        // Criar um item para a venda
        itemVenda item = new itemVenda();

        item.setIdItem(1);
        item.setVenda(venda);
        item.setProduto(produto);

        // Vender 3 unidades
        item.setQuantidade(new BigDecimal("3"));
        item.setPrecoUnitario(produto.getPrecoVenda());
        item.setDesconto(BigDecimal.ZERO);

        // Calcular subtotal
        BigDecimal subtotal = item.getQuantidade()
                .multiply(item.getPrecoUnitario())
                .subtract(item.getDesconto());

        item.setSubtotal(subtotal);

        itens.add(item);

        venda.setItens(itens);

        // Calcular o valor total da venda
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (itemVenda itemVenda : venda.getItens()) {
            valorTotal = valorTotal.add(
                    itemVenda.getSubtotal()
            );
        }

        venda.setValorTotal(valorTotal);

        // =====================================
        // 7. FINALIZAR A VENDA E BAIXAR ESTOQUE
        // =====================================

        if (estoque.getQuantidade()
                .compareTo(item.getQuantidade()) >= 0) {

            estoque.setQuantidade(
                    estoque.getQuantidade()
                            .subtract(item.getQuantidade())
            );

            venda.setStatus(StatusVenda.FINALIZADA);

            System.out.println("Venda finalizada!");

        } else {
            System.out.println("Estoque insuficiente!");
        }

        // =====================================
        // 8. EXIBIR O RESUMO
        // =====================================

        System.out.println("\n===== RESUMO DA VENDA =====");

        System.out.println("Funcionário: "
                + funcionario.getNome());

        System.out.println("Caixa: "
                + caixa.getIdCaixa());

        System.out.println("Produto: "
                + produto.getNome());

        System.out.println("Quantidade vendida: "
                + item.getQuantidade());

        System.out.println("Valor total: R$ "
                + venda.getValorTotal());

        System.out.println("Estoque restante: "
                + estoque.getQuantidade());

        System.out.println("Status da venda: "
                + venda.getStatus());
    }
}

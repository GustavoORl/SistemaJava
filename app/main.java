package app;

import dao.CargoDAO;
import dao.CategoriaDAO;
import dao.ProdutoDAO;
import dao.UsuarioDAO;
import java.math.BigDecimal;
import model.Cargo;
import model.Categoria;
import model.Produto;
import model.Usuario;

public class main {

    public static void main(String[] args) {

        CargoDAO cargoDAO = new CargoDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        // =====================================================
        // 1. CRIAR CARGOS
        // =====================================================

        Cargo administrador = new Cargo();
        administrador.setNome("Administrador");

        cargoDAO.cadastrar(administrador);

        Cargo caixa = new Cargo();
        caixa.setNome("Caixa");

        cargoDAO.cadastrar(caixa);

        System.out.println("Cargos cadastrados!");
        System.out.println("Administrador ID: " + administrador.getId());
        System.out.println("Caixa ID: " + caixa.getId());


        // =====================================================
        // 2. CRIAR CATEGORIAS
        // =====================================================

        Categoria alimentos = new Categoria();
        alimentos.setNome("Alimentos");
        alimentos.setDescricao("Produtos alimentícios");

        categoriaDAO.cadastrar(alimentos);


        Categoria bebidas = new Categoria();
        bebidas.setNome("Bebidas");
        bebidas.setDescricao("Bebidas em geral");

        categoriaDAO.cadastrar(bebidas);


        Categoria higiene = new Categoria();
        higiene.setNome("Higiene");
        higiene.setDescricao("Produtos de higiene pessoal");

        categoriaDAO.cadastrar(higiene);

        System.out.println("Categorias cadastradas!");

        System.out.println("Alimentos ID: " + alimentos.getId());
        System.out.println("Bebidas ID: " + bebidas.getId());
        System.out.println("Higiene ID: " + higiene.getId());


        // =====================================================
        // 3. CRIAR ADMINISTRADOR
        // =====================================================

        Usuario admin = new Usuario();

        admin.setNome("João Silva");
        admin.setTelefone("(12) 99999-1111");
        admin.setEmail("admin@costock.com");
        admin.setEndereco("Rua Principal, 100");
        admin.setSenha("123456");
        admin.setCargo(administrador);
        admin.setAtivo(true);

        usuarioDAO.cadastrar(admin);

        System.out.println("Administrador cadastrado!");


        // =====================================================
        // 4. CRIAR CAIXA
        // =====================================================

        Usuario funcionarioCaixa = new Usuario();

        funcionarioCaixa.setNome("Carlos Oliveira");
        funcionarioCaixa.setTelefone("(12) 99999-2222");
        funcionarioCaixa.setEmail("caixa@costock.com");
        funcionarioCaixa.setEndereco("Rua das Flores, 200");
        funcionarioCaixa.setSenha("123456");
        funcionarioCaixa.setCargo(caixa);
        funcionarioCaixa.setAtivo(true);

        usuarioDAO.cadastrar(funcionarioCaixa);

        System.out.println("Caixa cadastrado!");


        // =====================================================
        // 5. CRIAR PRODUTOS
        // =====================================================

        cadastrarProduto(
                produtoDAO,
                "Arroz Branco 5kg",
                "Arroz branco tipo 1",
                "7891000000001",
                "25.90",
                "32.90",
                10,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Feijão Carioca 1kg",
                "Feijão carioca tipo 1",
                "7891000000002",
                "7.50",
                "10.90",
                15,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Macarrão Espaguete 500g",
                "Macarrão de trigo",
                "7891000000003",
                "3.50",
                "5.99",
                20,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Açúcar Cristal 1kg",
                "Açúcar cristal refinado",
                "7891000000004",
                "3.20",
                "4.99",
                15,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Óleo de Soja 900ml",
                "Óleo de soja vegetal",
                "7891000000005",
                "5.20",
                "7.49",
                10,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Café Torrado 500g",
                "Café torrado e moído",
                "7891000000006",
                "12.50",
                "18.90",
                10,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Leite Integral 1L",
                "Leite integral UHT",
                "7891000000007",
                "4.20",
                "6.49",
                20,
                alimentos
        );

        cadastrarProduto(
                produtoDAO,
                "Refrigerante Cola 2L",
                "Refrigerante sabor cola",
                "7891000000008",
                "6.50",
                "9.99",
                15,
                bebidas
        );

        cadastrarProduto(
                produtoDAO,
                "Suco de Laranja 1L",
                "Suco de laranja integral",
                "7891000000009",
                "5.50",
                "8.99",
                10,
                bebidas
        );

        cadastrarProduto(
                produtoDAO,
                "Água Mineral 500ml",
                "Água mineral sem gás",
                "7891000000010",
                "1.00",
                "2.49",
                30,
                bebidas
        );

        cadastrarProduto(
                produtoDAO,
                "Sabonete 90g",
                "Sabonete perfumado",
                "7891000000011",
                "1.80",
                "3.49",
                20,
                higiene
        );

        cadastrarProduto(
                produtoDAO,
                "Shampoo 350ml",
                "Shampoo para cabelos",
                "7891000000012",
                "8.50",
                "14.90",
                10,
                higiene
        );

        cadastrarProduto(
                produtoDAO,
                "Pasta de Dente 90g",
                "Creme dental com flúor",
                "7891000000013",
                "4.50",
                "7.99",
                15,
                higiene
        );


        // =====================================================
        // FINAL
        // =====================================================

        System.out.println();
        System.out.println("======================================");
        System.out.println("      BANCO POPULADO COM SUCESSO!");
        System.out.println("======================================");
    }


    // =========================================================
    // MÉTODO PARA CRIAR PRODUTO
    // =========================================================

    private static void cadastrarProduto(
            ProdutoDAO produtoDAO,
            String nome,
            String descricao,
            String codigoBarras,
            String precoCusto,
            String precoVenda,
            int estoqueMinimo,
            Categoria categoria) {

        Produto produto = new Produto();

        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setCodigoBarras(codigoBarras);

        produto.setPrecoCusto(
                new BigDecimal(precoCusto)
        );

        produto.setPrecoVenda(
                new BigDecimal(precoVenda)
        );

        produto.setEstoqueMinimo(estoqueMinimo);

        produto.setCategoria(categoria);

        produto.setAtivo(true);

        produtoDAO.cadastrar(produto);
    }
}
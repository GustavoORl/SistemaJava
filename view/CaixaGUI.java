
package view;

import dao.ProdutoDAO;
import model.Produto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class CaixaGUI extends JFrame {

    // =====================================================
    // COMPONENTES DA TELA
    // =====================================================

    private JTextField txtBusca;
    private JTextField txtQuantidade;

    private JTable tabelaCarrinho;
    private DefaultTableModel modeloTabela;

    private JLabel lblTotal;
    private JLabel lblOperador;

    private JButton btnAdicionar;
    private JButton btnRemover;
    private JButton btnLimpar;
    private JButton btnFinalizar;
    private JButton btnSair;

    // =====================================================
    // FORMATAÇÃO DE MOEDA
    // =====================================================

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                    new Locale("pt", "BR")
            );

    // =====================================================
    // DAO E CARRINHO
    // =====================================================

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private final List<ItemCarrinho> carrinho =
            new ArrayList<>();

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public CaixaGUI() {

        configurarJanela();

        inicializarComponentes();
    }

    // =====================================================
    // CONFIGURAÇÃO DA JANELA
    // =====================================================

    private void configurarJanela() {

        setTitle("CoStock - Frente de Caixa");

        setSize(1100, 700);

        setMinimumSize(new Dimension(900, 600));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // =====================================================
    // INICIALIZAÇÃO DOS COMPONENTES
    // =====================================================

    private void inicializarComponentes() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout());

        painelPrincipal.setBackground(
                new Color(245, 247, 248)
        );

        // Cabeçalho
        JPanel painelCabecalho = criarCabecalho();

        // Área central
        JPanel painelCentral =
                new JPanel(new BorderLayout(10, 10));

        painelCentral.setBackground(
                new Color(245, 247, 248)
        );

        painelCentral.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        JPanel painelBusca = criarPainelBusca();

        JPanel painelCarrinho = criarPainelCarrinho();

        painelCentral.add(
                painelBusca,
                BorderLayout.NORTH
        );

        painelCentral.add(
                painelCarrinho,
                BorderLayout.CENTER
        );

        // Rodapé
        JPanel painelRodape = criarRodape();

        painelPrincipal.add(
                painelCabecalho,
                BorderLayout.NORTH
        );

        painelPrincipal.add(
                painelCentral,
                BorderLayout.CENTER
        );

        painelPrincipal.add(
                painelRodape,
                BorderLayout.SOUTH
        );

        setContentPane(painelPrincipal);
    }

    // =====================================================
    // CABEÇALHO
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBackground(
                new Color(23, 60, 56)
        );

        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel lblTitulo =
                new JLabel("CoStock | Frente de Caixa");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        lblTitulo.setForeground(Color.WHITE);

        lblOperador = new JLabel("Operador: Caixa");

        lblOperador.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        lblOperador.setForeground(Color.WHITE);

        painel.add(
                lblTitulo,
                BorderLayout.WEST
        );

        painel.add(
                lblOperador,
                BorderLayout.EAST
        );

        return painel;
    }

    // =====================================================
    // BUSCA E ADIÇÃO DE PRODUTOS
    // =====================================================

    private JPanel criarPainelBusca() {

        JPanel painel =
                new JPanel(new GridBagLayout());

        painel.setBackground(Color.WHITE);

        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 228)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Rótulo da busca
        JLabel lblBusca =
                new JLabel("Código, ID ou nome do produto");

        lblBusca.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        // Campo de busca
        txtBusca = new JTextField();

        txtBusca.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtBusca.setPreferredSize(
                new Dimension(300, 40)
        );

        // Rótulo da quantidade
        JLabel lblQuantidade =
                new JLabel("Quantidade");

        lblQuantidade.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        // Campo da quantidade
        txtQuantidade = new JTextField("1");

        txtQuantidade.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtQuantidade.setPreferredSize(
                new Dimension(100, 40)
        );

        // Botão adicionar
        btnAdicionar = criarBotao(
                "Adicionar produto",
                new Color(20, 125, 100)
        );

        // Primeira linha: rótulos
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        painel.add(lblBusca, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;

        painel.add(lblQuantidade, gbc);

        // Segunda linha: campos
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;

        painel.add(txtBusca, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;

        painel.add(txtQuantidade, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;

        painel.add(btnAdicionar, gbc);

        // Eventos
        btnAdicionar.addActionListener(
                e -> adicionarProduto()
        );

        txtBusca.addActionListener(
                e -> adicionarProduto()
        );

        txtQuantidade.addActionListener(
                e -> adicionarProduto()
        );

        return painel;
    }

    // =====================================================
    // CARRINHO DE COMPRAS
    // =====================================================

    private JPanel criarPainelCarrinho() {

        JPanel painel =
                new JPanel(new BorderLayout(0, 10));

        painel.setBackground(Color.WHITE);

        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 228)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        JLabel lblTitulo =
                new JLabel("Produtos da venda");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        // Colunas da tabela
        String[] colunas = {
            "Código",
            "Produto",
            "Quantidade",
            "Preço unitário",
            "Subtotal"
        };

        modeloTabela = new DefaultTableModel(
                colunas,
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        tabelaCarrinho =
                new JTable(modeloTabela);

        tabelaCarrinho.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        tabelaCarrinho.setRowHeight(30);

        tabelaCarrinho.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabelaCarrinho.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        tabelaCarrinho.getTableHeader().setBackground(
                new Color(230, 235, 235)
        );

        JScrollPane scroll =
                new JScrollPane(tabelaCarrinho);

        // Total
        lblTotal = new JLabel(
                formatoMoeda.format(BigDecimal.ZERO)
        );

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 30)
        );

        lblTotal.setForeground(
                new Color(20, 125, 100)
        );

        JLabel lblTotalTitulo =
                new JLabel("TOTAL DA COMPRA");

        lblTotalTitulo.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JPanel painelTotal =
                new JPanel(new BorderLayout());

        painelTotal.setBackground(Color.WHITE);

        painelTotal.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 0, 0, 0
                )
        );

        painelTotal.add(
                lblTotalTitulo,
                BorderLayout.NORTH
        );

        painelTotal.add(
                lblTotal,
                BorderLayout.CENTER
        );

        painel.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        painel.add(
                scroll,
                BorderLayout.CENTER
        );

        painel.add(
                painelTotal,
                BorderLayout.SOUTH
        );

        return painel;
    }

    // =====================================================
    // RODAPÉ E BOTÕES
    // =====================================================

    private JPanel criarRodape() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBackground(
                new Color(245, 247, 248)
        );

        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 20, 20, 20
                )
        );

        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        painelBotoes.setBackground(
                new Color(245, 247, 248)
        );

        btnRemover = criarBotao(
                "Remover item",
                new Color(190, 60, 60)
        );

        btnLimpar = criarBotao(
                "Limpar venda",
                new Color(100, 100, 100)
        );

        btnFinalizar = criarBotao(
                "Finalizar venda",
                new Color(20, 125, 100)
        );

        btnSair = criarBotao(
                "Sair",
                new Color(70, 80, 90)
        );

        // Eventos dos botões
        btnRemover.addActionListener(
                e -> removerItem()
        );

        btnLimpar.addActionListener(
                e -> limparVenda()
        );

        btnFinalizar.addActionListener(
                e -> finalizarVenda()
        );

        btnSair.addActionListener(
                e -> sair()
        );

        painelBotoes.add(btnRemover);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnFinalizar);
        painelBotoes.add(btnSair);

        painel.add(
                painelBotoes,
                BorderLayout.EAST
        );

        return painel;
    }

    // =====================================================
    // CRIAR BOTÕES
    // =====================================================

    private JButton criarBotao(
            String texto,
            Color cor
    ) {

        JButton botao = new JButton(texto);

        botao.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        botao.setForeground(Color.WHITE);

        botao.setBackground(cor);

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        botao.setPreferredSize(
                new Dimension(150, 42)
        );

        return botao;
    }

    // =====================================================
    // ADICIONAR PRODUTO AO CARRINHO
    // =====================================================

    private void adicionarProduto() {

        String busca =
                txtBusca.getText().trim();

        if (busca.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite o código, ID ou nome do produto."
            );

            txtBusca.requestFocus();

            return;
        }

        int quantidade;

        try {

            quantidade = Integer.parseInt(
                    txtQuantidade.getText().trim()
            );

            if (quantidade <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "A quantidade deve ser maior que zero."
                );

                return;
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite uma quantidade inteira válida."
            );

            return;
        }

        // Consulta o produto no banco
        Produto produtoEncontrado =
                buscarProduto(busca);

        if (produtoEncontrado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Produto não encontrado ou inativo.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            txtBusca.requestFocus();

            return;
        }

        // Verifica se o produto já está no carrinho
        ItemCarrinho itemExistente = null;

        for (ItemCarrinho item : carrinho) {

            if (item.getProduto().getIdProduto()
                    == produtoEncontrado.getIdProduto()) {

                itemExistente = item;

                break;
            }
        }

        if (itemExistente != null) {

            // Soma a quantidade
            itemExistente.setQuantidade(
                    itemExistente.getQuantidade()
                    + quantidade
            );

        } else {

            // Cria um novo item
            ItemCarrinho novoItem =
                    new ItemCarrinho(
                            produtoEncontrado,
                            quantidade
                    );

            carrinho.add(novoItem);
        }

        atualizarTabela();

        // Limpa os campos
        txtBusca.setText("");

        txtQuantidade.setText("1");

        txtBusca.requestFocus();
    }

    // =====================================================
    // BUSCAR PRODUTO NO BANCO
    // =====================================================

    private Produto buscarProduto(String busca) {

        // Primeiro tenta localizar pelo ID
        try {

            int id = Integer.parseInt(busca);

            Produto produto =
                    produtoDAO.buscarPorCodigoOuNome(busca);

            if (produto != null && produto.isAtivo()) {
                return produto;
            }

        } catch (NumberFormatException e) {

            // Se não for um número inteiro,
            // continua a busca por código ou nome.
        }

        // Busca pelo código de barras ou nome
        return produtoDAO.buscarPorCodigoOuNome(busca);
    }

    // =====================================================
    // ATUALIZAR TABELA E TOTAL
    // =====================================================

    private void atualizarTabela() {

        modeloTabela.setRowCount(0);

        for (ItemCarrinho item : carrinho) {

            Produto produto =
                    item.getProduto();

            BigDecimal subtotal =
                    item.getSubtotal();

            Object[] linha = {
                produto.getIdProduto(),
                produto.getNome(),
                item.getQuantidade(),
                formatoMoeda.format(
                        produto.getPrecoVenda()
                ),
                formatoMoeda.format(subtotal)
            };

            modeloTabela.addRow(linha);
        }

        atualizarTotal();
    }

    // =====================================================
    // CALCULAR TOTAL
    // =====================================================

    private BigDecimal calcularTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrinho item : carrinho) {

            total = total.add(
                    item.getSubtotal()
            );
        }

        return total;
    }

    // =====================================================
    // ATUALIZAR TOTAL
    // =====================================================

    private void atualizarTotal() {

        BigDecimal total = calcularTotal();

        lblTotal.setText(
                formatoMoeda.format(total)
        );
    }

    // =====================================================
    // REMOVER ITEM
    // =====================================================

    private void removerItem() {

        int linhaSelecionada =
                tabelaCarrinho.getSelectedRow();

        if (linhaSelecionada == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um produto para remover."
            );

            return;
        }

        // Converte o índice da tabela para o modelo
        int linhaModelo =
                tabelaCarrinho.convertRowIndexToModel(
                        linhaSelecionada
                );

        carrinho.remove(linhaModelo);

        atualizarTabela();
    }

    // =====================================================
    // LIMPAR VENDA
    // =====================================================

    private void limparVenda() {

        if (carrinho.isEmpty()) {
            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente limpar todos os itens?",
                        "Limpar venda",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta == JOptionPane.YES_OPTION) {

            carrinho.clear();

            atualizarTabela();

            txtBusca.setText("");

            txtQuantidade.setText("1");

            txtBusca.requestFocus();
        }
    }

    // =====================================================
    // FINALIZAR VENDA
    // =====================================================

    private void finalizarVenda() {

        if (carrinho.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Adicione pelo menos um produto à venda."
            );

            return;
        }

        BigDecimal total = calcularTotal();

        String[] formasPagamento = {
            "Dinheiro",
            "Pix",
            "Cartão de débito",
            "Cartão de crédito"
        };

        String formaPagamento =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Total: "
                        + formatoMoeda.format(total)
                        + "\n\nSelecione a forma de pagamento:",
                        "Finalizar venda",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        formasPagamento,
                        formasPagamento[0]
                );

        if (formaPagamento == null) {
            return;
        }

        // Nesta etapa, apenas simulamos a confirmação.
        // Posteriormente será conectado ao VendaDAO
        // e ao PagamentoDAO.

        JOptionPane.showMessageDialog(
                this,
                "Venda registrada na simulação!\n\n"
                + "Total: "
                + formatoMoeda.format(total)
                + "\nForma de pagamento: "
                + formaPagamento,
                "Venda concluída",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Limpa o carrinho após a simulação
        carrinho.clear();

        atualizarTabela();

        txtBusca.setText("");

        txtQuantidade.setText("1");

        txtBusca.requestFocus();
    }

    // =====================================================
    // SAIR
    // =====================================================

    private void sair() {

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja encerrar a sessão do caixa?",
                        "Encerrar sessão",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta == JOptionPane.YES_OPTION) {

            LoginGUI login = new LoginGUI();

            login.setVisible(true);

            dispose();
        }
    }

    // =====================================================
    // CLASSE AUXILIAR: ITEM DO CARRINHO
    // =====================================================

    private static class ItemCarrinho {

        private final Produto produto;

        private int quantidade;

        public ItemCarrinho(
                Produto produto,
                int quantidade
        ) {

            this.produto = produto;

            this.quantidade = quantidade;
        }

        public Produto getProduto() {
            return produto;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public BigDecimal getSubtotal() {

            return produto.getPrecoVenda()
                    .multiply(
                            BigDecimal.valueOf(quantidade)
                    );
        }
    }

    // =====================================================
    // MAIN PARA TESTAR A TELA
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            CaixaGUI tela = new CaixaGUI();

            tela.setVisible(true);
        });
    }
}
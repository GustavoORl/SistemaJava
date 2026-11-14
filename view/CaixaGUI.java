
package view;

import dao.ProdutoDAO;
import dao.VendaDAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;

import model.Caixa;
import model.Pagamento;
import model.Produto;
import model.StatusPagamento;
import model.StatusVenda;
import model.TipoFormaPagamento;
import model.Usuario;
import model.Venda;
import model.itemVenda;

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
    // COMPONENTES DA PESQUISA
    // =====================================================

    private JPopupMenu popupSugestoes;
    private JList<Produto> listaSugestoes;
    private DefaultListModel<Produto> modeloSugestoes;

    private Timer timerPesquisa;

    private int versaoPesquisa = 0;

    // =====================================================
    // FORMATAÇÃO DE MOEDA
    // =====================================================

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                    new Locale("pt", "BR")
            );

    // =====================================================
    // USUÁRIO E CAIXA
    // =====================================================

    private Usuario usuario;
    private Caixa caixa;

    // =====================================================
    // DAO E ITENS DA VENDA
    // =====================================================

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final VendaDAO vendaDAO = new VendaDAO();

    private final List<ItemCarrinho> carrinho =
            new ArrayList<>();

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public CaixaGUI(Usuario usuario, Caixa caixa) {

        this.usuario = usuario;
        this.caixa = caixa;

        configurarJanela();

        inicializarComponentes();

        configurarPesquisaProdutos();

        configurarTela();
    }

    // =====================================================
    // CONFIGURAÇÃO DA TELA
    // =====================================================

    private void configurarTela() {

        if (usuario != null) {

            lblOperador.setText(
                    "Operador: " + usuario.getNome()
            );
        }
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

        JPanel painelCabecalho = criarCabecalho();

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

        JLabel lblBusca =
                new JLabel("Código, ID ou nome do produto");

        lblBusca.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtBusca = new JTextField();

        txtBusca.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtBusca.setPreferredSize(
                new Dimension(300, 40)
        );

        JLabel lblQuantidade =
                new JLabel("Quantidade");

        lblQuantidade.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtQuantidade = new JTextField("1");

        txtQuantidade.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtQuantidade.setPreferredSize(
                new Dimension(100, 40)
        );

        btnAdicionar = criarBotao(
                "Adicionar produto",
                new Color(20, 125, 100)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        painel.add(lblBusca, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;

        painel.add(lblQuantidade, gbc);

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

        btnAdicionar.addActionListener(
                e -> adicionarProduto()
        );

        txtBusca.addActionListener(
                e -> confirmarPesquisa()
        );

        txtQuantidade.addActionListener(
                e -> adicionarProduto()
        );

        return painel;
    }

    // =====================================================
    // CONFIGURAR SUGESTÕES DE PESQUISA
    // =====================================================

    private void configurarPesquisaProdutos() {

        modeloSugestoes = new DefaultListModel<>();

        listaSugestoes = new JList<>(modeloSugestoes);

        listaSugestoes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        listaSugestoes.setFixedCellHeight(42);

        listaSugestoes.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        listaSugestoes.setCellRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label = (JLabel) super
                                .getListCellRendererComponent(
                                        list,
                                        value,
                                        index,
                                        isSelected,
                                        cellHasFocus
                                );

                        if (value instanceof Produto) {

                            Produto produto = (Produto) value;

                            String codigo =
                                    produto.getCodigoBarras();

                            if (codigo == null
                                    || codigo.isBlank()) {

                                codigo = String.valueOf(
                                        produto.getIdProduto()
                                );
                            }

                            label.setText(
                                    produto.getNome()
                                    + "  |  Código: "
                                    + codigo
                                    + "  |  "
                                    + formatoMoeda.format(
                                            produto.getPrecoVenda()
                                    )
                            );
                        }

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 10, 0, 10
                                )
                        );

                        if (isSelected) {

                            label.setBackground(
                                    new Color(220, 240, 233)
                            );

                            label.setForeground(
                                    new Color(23, 60, 56)
                            );
                        }

                        return label;
                    }
                }
        );

        // O popup não poderá receber foco automaticamente.
        popupSugestoes = new JPopupMenu();

        popupSugestoes.setFocusable(false);

        popupSugestoes.setRequestFocusEnabled(false);

        popupSugestoes.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 220, 215)
                )
        );

        // A lista também não recebe foco ao aparecer.
        listaSugestoes.setFocusable(false);

        listaSugestoes.setRequestFocusEnabled(false);

        JScrollPane scrollSugestoes =
                new JScrollPane(listaSugestoes);

        scrollSugestoes.setFocusable(false);

        scrollSugestoes.setRequestFocusEnabled(false);

        scrollSugestoes.setBorder(null);

        scrollSugestoes.setPreferredSize(
                new Dimension(500, 210)
        );

        popupSugestoes.add(scrollSugestoes);

        timerPesquisa = new Timer(
                250,
                e -> carregarSugestoes()
        );

        timerPesquisa.setRepeats(false);

        txtBusca.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        agendarPesquisa();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        agendarPesquisa();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        agendarPesquisa();
                    }
                }
        );

        // O foco permanece sempre no campo de pesquisa.
        txtBusca.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {

                        if (e.getKeyCode() == KeyEvent.VK_DOWN
                                && popupSugestoes.isVisible()) {

                            moverSelecaoSugestao(1);

                            e.consume();
                        }

                        if (e.getKeyCode() == KeyEvent.VK_UP
                                && popupSugestoes.isVisible()) {

                            moverSelecaoSugestao(-1);

                            e.consume();
                        }

                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

                            cancelarSugestoes();

                            e.consume();
                        }
                    }
                }
        );

        // Seleção por clique duplo.
        listaSugestoes.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2) {

                            int indice =
                                    listaSugestoes.locationToIndex(
                                            e.getPoint()
                                    );

                            if (indice >= 0) {

                                listaSugestoes.setSelectedIndex(
                                        indice
                                );

                                selecionarSugestao();
                            }
                        }
                    }
                }
        );
    }

    // =====================================================
    // MOVER SELEÇÃO SEM TIRAR O FOCO DA PESQUISA
    // =====================================================

    private void moverSelecaoSugestao(int direcao) {

        if (modeloSugestoes.isEmpty()) {
            return;
        }

        int indiceAtual =
                listaSugestoes.getSelectedIndex();

        int novoIndice;

        if (indiceAtual == -1) {

            novoIndice = direcao > 0
                    ? 0
                    : modeloSugestoes.size() - 1;

        } else {

            novoIndice = indiceAtual + direcao;

            if (novoIndice < 0) {
                novoIndice = 0;
            }

            if (novoIndice >= modeloSugestoes.size()) {
                novoIndice = modeloSugestoes.size() - 1;
            }
        }

        listaSugestoes.setSelectedIndex(novoIndice);

        listaSugestoes.ensureIndexIsVisible(novoIndice);

        // Não chamamos requestFocus na lista.
    }

    // =====================================================
    // AGENDAR PESQUISA
    // =====================================================

    private void agendarPesquisa() {

        versaoPesquisa++;

        timerPesquisa.stop();

        String texto = txtBusca.getText().trim();

        if (texto.isEmpty()) {

            modeloSugestoes.clear();

            popupSugestoes.setVisible(false);

            return;
        }

        timerPesquisa.restart();
    }

    // =====================================================
    // CARREGAR SUGESTÕES
    // =====================================================

    private void carregarSugestoes() {

        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            return;
        }

        final int versaoAtual = versaoPesquisa;

        SwingWorker<List<Produto>, Void> worker =
                new SwingWorker<List<Produto>, Void>() {

                    @Override
                    protected List<Produto> doInBackground() {

                        return produtoDAO.buscarSugestoes(termo);
                    }

                    @Override
                    protected void done() {

                        try {

                            if (versaoAtual != versaoPesquisa) {
                                return;
                            }

                            if (!termo.equals(
                                    txtBusca.getText().trim()
                            )) {
                                return;
                            }

                            List<Produto> produtos = get();

                            modeloSugestoes.clear();

                            for (Produto produto : produtos) {

                                modeloSugestoes.addElement(produto);
                            }

                            if (modeloSugestoes.isEmpty()) {

                                popupSugestoes.setVisible(false);

                                return;
                            }

                            int largura = Math.max(
                                    txtBusca.getWidth(),
                                    500
                            );

                            popupSugestoes.setPopupSize(
                                    largura,
                                    Math.min(
                                            230,
                                            produtos.size() * 42 + 12
                                    )
                            );

                            if (txtBusca.isShowing()
                                    && txtBusca.hasFocus()) {

                                if (!popupSugestoes.isVisible()) {

                                    popupSugestoes.show(
                                            txtBusca,
                                            0,
                                            txtBusca.getHeight()
                                    );
                                }
                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                            popupSugestoes.setVisible(false);
                        }
                    }
                };

        worker.execute();
    }

    // =====================================================
    // SELECIONAR SUGESTÃO
    // =====================================================

    private void selecionarSugestao() {

        Produto selecionado =
                listaSugestoes.getSelectedValue();

        if (selecionado == null) {
            return;
        }

        cancelarSugestoes();

        adicionarProdutoEncontrado(selecionado);
    }

    // =====================================================
    // CANCELAR SUGESTÕES
    // =====================================================

    private void cancelarSugestoes() {

        versaoPesquisa++;

        timerPesquisa.stop();

        popupSugestoes.setVisible(false);

        modeloSugestoes.clear();
    }

    // =====================================================
    // CONFIRMAR PESQUISA
    // =====================================================

    private void confirmarPesquisa() {

        String busca = txtBusca.getText().trim();

        if (busca.isEmpty()) {
            return;
        }

        // Um código exato sempre tem prioridade.
        Produto produtoExato =
                buscarProdutoExato(busca);

        if (produtoExato != null) {

            cancelarSugestoes();

            adicionarProdutoEncontrado(produtoExato);

            return;
        }

        // Se o operador navegou pelas sugestões
        // usando as setas, Enter seleciona o item.
        if (popupSugestoes.isVisible()
                && listaSugestoes.getSelectedIndex() >= 0) {

            selecionarSugestao();

            return;
        }

        adicionarProduto();
    }

    // =====================================================
    // BUSCAR PRODUTO EXATO
    // =====================================================

    private Produto buscarProdutoExato(String busca) {

        if (busca == null || busca.trim().isEmpty()) {
            return null;
        }

        String termo = busca.trim();

        Produto produto =
                produtoDAO.buscarPorCodigoExato(termo);

        if (produto != null && produto.isAtivo()) {
            return produto;
        }

        try {

            int id = Integer.parseInt(termo);

            produto = produtoDAO.buscarPorIdExato(id);

            if (produto != null && produto.isAtivo()) {
                return produto;
            }

        } catch (NumberFormatException e) {

            // O texto não é um ID numérico.
        }

        return null;
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
                "Fechar caixa",
                new Color(190, 60, 60)
        );

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
                e -> abrirFechamento()
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
    // ADICIONAR PRODUTO
    // =====================================================

    private void adicionarProduto() {

        String busca = txtBusca.getText().trim();

        if (busca.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite o código, ID ou nome do produto.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            txtBusca.requestFocusInWindow();

            return;
        }

        cancelarSugestoes();

        Produto produtoEncontrado =
                buscarProduto(busca);

        if (produtoEncontrado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Produto não encontrado.\n"
                    + "Verifique o código ou selecione "
                    + "uma sugestão pelo nome.",
                    "Produto não encontrado",
                    JOptionPane.WARNING_MESSAGE
            );

            txtBusca.requestFocusInWindow();

            return;
        }

        adicionarProdutoEncontrado(produtoEncontrado);
    }

    // =====================================================
    // ADICIONAR PRODUTO IDENTIFICADO
    // =====================================================

    private void adicionarProdutoEncontrado(Produto produto) {

        if (produto == null || !produto.isAtivo()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Produto não encontrado ou inativo.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

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
                        "A quantidade deve ser maior que zero.",
                        "Quantidade inválida",
                        JOptionPane.WARNING_MESSAGE
                );

                txtQuantidade.requestFocusInWindow();

                return;
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite uma quantidade inteira válida.",
                    "Quantidade inválida",
                    JOptionPane.WARNING_MESSAGE
            );

            txtQuantidade.requestFocusInWindow();

            return;
        }

        ItemCarrinho itemExistente = null;

        for (ItemCarrinho item : carrinho) {

            if (item.getProduto().getIdProduto()
                    == produto.getIdProduto()) {

                itemExistente = item;

                break;
            }
        }

        if (itemExistente != null) {

            itemExistente.setQuantidade(
                    itemExistente.getQuantidade()
                    + quantidade
            );

        } else {

            carrinho.add(
                    new ItemCarrinho(
                            produto,
                            quantidade
                    )
            );
        }

        atualizarTabela();

        cancelarSugestoes();

        txtBusca.setText("");

        txtQuantidade.setText("1");

        txtBusca.requestFocusInWindow();
    }

    // =====================================================
    // BUSCAR PRODUTO
    // =====================================================

    private Produto buscarProduto(String busca) {

        if (busca == null || busca.trim().isEmpty()) {
            return null;
        }

        String termo = busca.trim();

        // Código de barras ou ID exato.
        Produto produto = buscarProdutoExato(termo);

        if (produto != null) {
            return produto;
        }

        // Busca parcial: só retorna automaticamente
        // se houver um único produto correspondente.
        List<Produto> encontrados =
                produtoDAO.buscarSugestoes(termo);

        if (encontrados.size() == 1) {

            Produto encontrado = encontrados.get(0);

            if (encontrado.isAtivo()) {
                return encontrado;
            }
        }

        return null;
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

            String codigo =
                    produto.getCodigoBarras();

            if (codigo == null || codigo.isBlank()) {

                codigo = String.valueOf(
                        produto.getIdProduto()
                );
            }

            Object[] linha = {
                codigo,
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

            cancelarSugestoes();

            txtBusca.setText("");

            txtQuantidade.setText("1");

            txtBusca.requestFocusInWindow();
        }
    }

    // =====================================================
    // FINALIZAR VENDA
    // =====================================================

    private void finalizarVenda() {

        if (carrinho.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Adicione pelo menos um produto à venda.",
                    "Carrinho vazio",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (usuario == null || caixa == null
                || caixa.getIdTabelaCaixa() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Usuário ou sessão de caixa inválidos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
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

        String formaSelecionada =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Total: " + formatoMoeda.format(total)
                        + "\n\nSelecione a forma de pagamento:",
                        "Finalizar venda",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        formasPagamento,
                        formasPagamento[0]
                );

        if (formaSelecionada == null) {
            return;
        }

        TipoFormaPagamento tipoPagamento;

        switch (formaSelecionada) {

            case "Dinheiro":
                tipoPagamento = TipoFormaPagamento.DINHEIRO;
                break;

            case "Pix":
                tipoPagamento = TipoFormaPagamento.PIX;
                break;

            case "Cartão de débito":
                tipoPagamento = TipoFormaPagamento.CARTAO_DEBITO;
                break;

            case "Cartão de crédito":
                tipoPagamento = TipoFormaPagamento.CARTAO_CREDITO;
                break;

            default:

                JOptionPane.showMessageDialog(
                        this,
                        "Forma de pagamento inválida."
                );

                return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Confirma a venda?\n\n"
                + "Total: " + formatoMoeda.format(total)
                + "\nPagamento: " + formaSelecionada
                + "\nCaixa: " + caixa.getIdCaixa(),
                "Confirmar venda",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        Venda venda = new Venda();

        venda.setUsuario(usuario);
        venda.setCaixa(caixa);
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus(StatusVenda.FINALIZADA);
        venda.setValorTotal(total);

        List<itemVenda> itensVenda = new ArrayList<>();

        for (ItemCarrinho itemCarrinho : carrinho) {

            itemVenda item = new itemVenda();

            item.setVenda(venda);

            item.setProduto(
                    itemCarrinho.getProduto()
            );

            item.setQuantidade(
                    BigDecimal.valueOf(
                            itemCarrinho.getQuantidade()
                    )
            );

            item.setPrecoUnitario(
                    itemCarrinho.getProduto().getPrecoVenda()
            );

            item.setDesconto(BigDecimal.ZERO);

            item.setSubtotal(
                    itemCarrinho.getSubtotal()
            );

            itensVenda.add(item);
        }

        venda.setItens(itensVenda);

        Pagamento pagamento = new Pagamento();

        pagamento.setVenda(venda);

        pagamento.setValor(total);

        pagamento.setDataPagamento(
                LocalDateTime.now()
        );

        pagamento.setTipoFormaPagamento(
                tipoPagamento
        );

        pagamento.setStatus(
                StatusPagamento.APROVADO
        );

        List<Pagamento> pagamentos =
                new ArrayList<>();

        pagamentos.add(pagamento);

        venda.setPagamentos(pagamentos);

        btnFinalizar.setEnabled(false);

        try {

            int idVenda =
                    vendaDAO.finalizarVenda(venda);

            JOptionPane.showMessageDialog(
                    this,
                    "Venda finalizada com sucesso!\n\n"
                    + "Número da venda: " + idVenda
                    + "\nTotal: " + formatoMoeda.format(total)
                    + "\nPagamento: " + formaSelecionada
                    + "\nCaixa: " + caixa.getIdCaixa(),
                    "Venda concluída",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carrinho.clear();

            atualizarTabela();

            cancelarSugestoes();

            txtBusca.setText("");

            txtQuantidade.setText("1");

            txtBusca.requestFocusInWindow();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível finalizar a venda.\n\n"
                    + e.getMessage(),
                    "Erro na venda",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();

        } finally {

            btnFinalizar.setEnabled(true);
        }
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
    // ABRIR FECHAMENTO DO CAIXA
    // =====================================================

    private void abrirFechamento() {

        if (caixa == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum caixa está associado a esta sessão.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        CaixaFechamentoGUI tela =
                new CaixaFechamentoGUI(caixa);

        tela.setVisible(true);

        dispose();
    }
}

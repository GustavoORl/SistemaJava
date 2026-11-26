
package view;

import dao.ProdutoDAO;
import dao.VendaDAO;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;

import model.AlertaEstoque;
import model.Caixa;
import model.Pagamento;
import model.Produto;
import model.StatusPagamento;
import model.StatusVenda;
import model.TipoFormaPagamento;
import model.Usuario;
import model.Venda;
import model.itemVenda;
import notificacoes.ClienteNotificacoes;

public class CaixaGUI extends JFrame {

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

    private JPopupMenu popupSugestoes;
    private JList<Produto> listaSugestoes;
    private DefaultListModel<Produto> modeloSugestoes;

    private Timer timerPesquisa;

    private int versaoPesquisa = 0;

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                    new Locale("pt", "BR")
            );

    private Usuario usuario;
    private Caixa caixa;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final VendaDAO vendaDAO = new VendaDAO();

    private final List<ItemCarrinho> carrinho =
            new ArrayList<>();

    public CaixaGUI(Usuario usuario, Caixa caixa) {

        this.usuario = usuario;
        this.caixa = caixa;

        configurarJanela();

        inicializarComponentes();

        configurarPesquisaProdutos();

        configurarTela();
    }

    private void configurarTela() {

        if (usuario != null) {

            lblOperador.setText(
                    "Operador: " + usuario.getNome()
            );
        }
    }

    private void configurarJanela() {

        setTitle("CoStock - Frente de Caixa");

        setSize(1100, 700);

        setMinimumSize(new Dimension(900, 600));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

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

        popupSugestoes = new JPopupMenu();

        popupSugestoes.setFocusable(false);

        popupSugestoes.setRequestFocusEnabled(false);

        popupSugestoes.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 220, 215)
                )
        );

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

    }

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

    private void selecionarSugestao() {

        Produto selecionado =
                listaSugestoes.getSelectedValue();

        if (selecionado == null) {
            return;
        }

        cancelarSugestoes();

        adicionarProdutoEncontrado(selecionado);
    }

    private void cancelarSugestoes() {

        versaoPesquisa++;

        timerPesquisa.stop();

        popupSugestoes.setVisible(false);

        modeloSugestoes.clear();
    }

    private void confirmarPesquisa() {

        String busca = txtBusca.getText().trim();

        if (busca.isEmpty()) {
            return;
        }

        Produto produtoExato =
                buscarProdutoExato(busca);

        if (produtoExato != null) {

            cancelarSugestoes();

            adicionarProdutoEncontrado(produtoExato);

            return;
        }

        if (popupSugestoes.isVisible()
                && listaSugestoes.getSelectedIndex() >= 0) {

            selecionarSugestao();

            return;
        }

        adicionarProduto();
    }

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

        }

        return null;
    }

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

    private Produto buscarProduto(String busca) {

        if (busca == null || busca.trim().isEmpty()) {
            return null;
        }

        String termo = busca.trim();

        Produto produto = buscarProdutoExato(termo);

        if (produto != null) {
            return produto;
        }

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

    private BigDecimal calcularTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrinho item : carrinho) {

            total = total.add(
                    item.getSubtotal()
            );
        }

        return total;
    }

    private void atualizarTotal() {

        BigDecimal total = calcularTotal();

        lblTotal.setText(
                formatoMoeda.format(total)
        );
    }

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

        String formaSelecionada =
                exibirModalFinalizarVenda(total);

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

            VendaDAO.ResultadoVenda resultado = vendaDAO.finalizarVendaComAlertas(venda);
            ClienteNotificacoes.publicar(resultado.alertas());

            exibirModalVendaConcluida(
                    resultado.idVenda(),
                    total,
                    formaSelecionada,
                    venda.getDataVenda(),
                    resultado.alertas()
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

    private String exibirModalFinalizarVenda(BigDecimal total) {

        final Color VERDE_ESCURO = new Color(23, 60, 56);
        final Color VERDE = new Color(20, 125, 100);
        final Color VERDE_CLARO = new Color(220, 240, 233);
        final Color FUNDO = new Color(245, 247, 248);
        final Color BORDA = new Color(220, 225, 228);
        final Color TEXTO_SECUNDARIO = new Color(100, 100, 100);

        final String[] resultado = { null };

        JDialog dialog = new JDialog(
                this,
                "Finalizar venda",
                true
        );

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        JPanel cabecalho =
                new JPanel(new BorderLayout(0, 4));

        cabecalho.setBackground(VERDE_ESCURO);

        cabecalho.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 24, 18, 24
                )
        );

        JLabel lblTitulo =
                new JLabel("Finalizar venda");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        lblTitulo.setForeground(Color.WHITE);

        int qtdItens = carrinho.size();

        JLabel lblSubtitulo = new JLabel(
                "Caixa " + caixa.getIdCaixa()
                + "  •  " + qtdItens
                + (qtdItens == 1 ? " item" : " itens")
        );

        lblSubtitulo.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        lblSubtitulo.setForeground(
                new Color(200, 220, 215)
        );

        cabecalho.add(lblTitulo, BorderLayout.NORTH);
        cabecalho.add(lblSubtitulo, BorderLayout.SOUTH);

        JPanel corpo =
                new JPanel(new GridBagLayout());

        corpo.setBackground(Color.WHITE);

        corpo.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 24, 20, 24
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTotalTitulo =
                new JLabel("TOTAL DA COMPRA");

        lblTotalTitulo.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        lblTotalTitulo.setForeground(TEXTO_SECUNDARIO);

        JLabel lblTotalValor =
                new JLabel(formatoMoeda.format(total));

        lblTotalValor.setFont(
                new Font("Arial", Font.BOLD, 32)
        );

        lblTotalValor.setForeground(VERDE);

        JLabel lblPagamento =
                new JLabel("FORMA DE PAGAMENTO");

        lblPagamento.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        lblPagamento.setForeground(TEXTO_SECUNDARIO);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        corpo.add(lblTotalTitulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        corpo.add(lblTotalValor, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        corpo.add(lblPagamento, gbc);

        String[] formasPagamento = {
            "Dinheiro",
            "Pix",
            "Cartão de débito",
            "Cartão de crédito"
        };

        JPanel painelOpcoes =
                new JPanel(new GridLayout(2, 2, 10, 10));

        painelOpcoes.setBackground(Color.WHITE);

        ButtonGroup grupo = new ButtonGroup();

        JToggleButton[] opcoes =
                new JToggleButton[formasPagamento.length];

        for (int i = 0; i < formasPagamento.length; i++) {

            String forma = formasPagamento[i];

            JToggleButton opcao = new JToggleButton(
                    (i + 1) + "  " + forma
            );

            opcao.setActionCommand(forma);

            opcao.setFont(
                    new Font("Arial", Font.BOLD, 14)
            );

            opcao.setFocusPainted(false);
            opcao.setContentAreaFilled(false);
            opcao.setOpaque(true);

            opcao.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            opcao.setPreferredSize(
                    new Dimension(190, 54)
            );

            Runnable aplicarEstilo = () -> {

                if (opcao.isSelected()) {

                    opcao.setBackground(VERDE_CLARO);
                    opcao.setForeground(VERDE);

                    opcao.setBorder(
                            BorderFactory.createLineBorder(
                                    VERDE, 2
                            )
                    );

                } else {

                    opcao.setBackground(Color.WHITE);
                    opcao.setForeground(VERDE_ESCURO);

                    opcao.setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(
                                            BORDA, 1
                                    ),
                                    BorderFactory.createEmptyBorder(
                                            1, 1, 1, 1
                                    )
                            )
                    );
                }
            };

            opcao.addItemListener(e -> aplicarEstilo.run());

            aplicarEstilo.run();

            opcao.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() == 2) {

                        resultado[0] = forma;
                        dialog.dispose();
                    }
                }
            });

            grupo.add(opcao);
            painelOpcoes.add(opcao);
            opcoes[i] = opcao;
        }

        opcoes[0].setSelected(true);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        corpo.add(painelOpcoes, gbc);

        JPanel rodape = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        rodape.setBackground(FUNDO);

        rodape.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0, BORDA
                        ),
                        BorderFactory.createEmptyBorder(
                                14, 24, 14, 24
                        )
                )
        );

        JButton btnCancelar =
                criarBotao("Cancelar", TEXTO_SECUNDARIO);

        JButton btnConfirmar =
                criarBotao("Confirmar venda", VERDE);

        for (JButton b : new JButton[] { btnCancelar, btnConfirmar }) {
            b.setContentAreaFilled(false);
            b.setOpaque(true);
        }

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnConfirmar.addActionListener(e -> {

            if (grupo.getSelection() != null) {

                resultado[0] =
                        grupo.getSelection().getActionCommand();
            }

            dialog.dispose();
        });

        rodape.add(btnCancelar);
        rodape.add(btnConfirmar);

        JRootPane root = dialog.getRootPane();

        root.setDefaultButton(btnConfirmar);

        root.registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        for (int i = 0; i < opcoes.length; i++) {

            JToggleButton opcao = opcoes[i];

            root.registerKeyboardAction(
                    e -> opcao.setSelected(true),
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_1 + i, 0
                    ),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            root.registerKeyboardAction(
                    e -> opcao.setSelected(true),
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_NUMPAD1 + i, 0
                    ),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );
        }

        JPanel conteudo =
                new JPanel(new BorderLayout());

        conteudo.setBackground(Color.WHITE);

        conteudo.add(cabecalho, BorderLayout.NORTH);
        conteudo.add(corpo, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);

        dialog.setContentPane(conteudo);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        SwingUtilities.invokeLater(
                btnConfirmar::requestFocusInWindow
        );

        dialog.setVisible(true);

        return resultado[0];
    }

    private void exibirModalVendaConcluida(
            int idVenda,
            BigDecimal total,
            String formaPagamento,
            LocalDateTime dataVenda,
            List<AlertaEstoque> alertas
    ) {

        final Color VERDE_ESCURO = new Color(23, 60, 56);
        final Color VERDE = new Color(20, 125, 100);
        final Color VERDE_CLARO = new Color(220, 240, 233);
        final Color FUNDO = new Color(245, 247, 248);
        final Color BORDA = new Color(220, 225, 228);
        final Color TEXTO_SECUNDARIO = new Color(100, 100, 100);

        JDialog dialog = new JDialog(
                this,
                "Venda concluída",
                true
        );

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        JPanel cabecalho =
                new JPanel(new BorderLayout(0, 4));

        cabecalho.setBackground(VERDE_ESCURO);

        cabecalho.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 24, 18, 24
                )
        );

        JLabel lblTitulo =
                new JLabel("Venda concluída");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel(
                "Venda nº " + idVenda
                + "  •  Caixa " + caixa.getIdCaixa()
        );

        lblSubtitulo.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        lblSubtitulo.setForeground(
                new Color(200, 220, 215)
        );

        cabecalho.add(lblTitulo, BorderLayout.NORTH);
        cabecalho.add(lblSubtitulo, BorderLayout.SOUTH);

        JPanel corpo =
                new JPanel(new GridBagLayout());

        corpo.setBackground(Color.WHITE);

        corpo.setBorder(
                BorderFactory.createEmptyBorder(
                        22, 24, 20, 24
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComponent icone = new JComponent() {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int d = Math.min(getWidth(), getHeight());
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;

                g2.setColor(VERDE_CLARO);
                g2.fillOval(x, y, d, d);

                g2.setColor(VERDE);
                g2.setStroke(new BasicStroke(
                        d / 11f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                ));

                g2.drawPolyline(
                        new int[] {
                            x + d * 28 / 100,
                            x + d * 44 / 100,
                            x + d * 72 / 100
                        },
                        new int[] {
                            y + d * 52 / 100,
                            y + d * 67 / 100,
                            y + d * 36 / 100
                        },
                        3
                );

                g2.dispose();
            }
        };

        icone.setPreferredSize(new Dimension(64, 64));

        JLabel lblMensagem = new JLabel(
                "Venda finalizada com sucesso!",
                JLabel.CENTER
        );

        lblMensagem.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        lblMensagem.setForeground(VERDE_ESCURO);

        JLabel lblTotalTitulo = new JLabel(
                "TOTAL DA COMPRA",
                JLabel.CENTER
        );

        lblTotalTitulo.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        lblTotalTitulo.setForeground(TEXTO_SECUNDARIO);

        JLabel lblTotalValor = new JLabel(
                formatoMoeda.format(total),
                JLabel.CENTER
        );

        lblTotalValor.setFont(
                new Font("Arial", Font.BOLD, 32)
        );

        lblTotalValor.setForeground(VERDE);

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 12, 0);
        corpo.add(icone, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        corpo.add(lblMensagem, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 2, 0);
        corpo.add(lblTotalTitulo, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        corpo.add(lblTotalValor, gbc);

        JPanel painelDetalhes =
                new JPanel(new GridBagLayout());

        painelDetalhes.setBackground(FUNDO);

        painelDetalhes.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(
                                12, 16, 12, 16
                        )
                )
        );

        String dataFormatada = dataVenda == null
                ? "-"
                : dataVenda.format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        )
                );

        String[][] detalhes = {
            { "Número da venda", String.valueOf(idVenda) },
            { "Pagamento", formaPagamento },
            { "Caixa", String.valueOf(caixa.getIdCaixa()) },
            { "Data", dataFormatada }
        };

        GridBagConstraints gd =
                new GridBagConstraints();

        gd.insets = new Insets(4, 0, 4, 0);

        for (int i = 0; i < detalhes.length; i++) {

            JLabel rotulo = new JLabel(detalhes[i][0]);

            rotulo.setFont(
                    new Font("Arial", Font.PLAIN, 13)
            );

            rotulo.setForeground(TEXTO_SECUNDARIO);

            JLabel valor = new JLabel(detalhes[i][1]);

            valor.setFont(
                    new Font("Arial", Font.BOLD, 14)
            );

            valor.setForeground(VERDE_ESCURO);

            gd.gridy = i;

            gd.gridx = 0;
            gd.weightx = 1;
            gd.anchor = GridBagConstraints.WEST;
            painelDetalhes.add(rotulo, gd);

            gd.gridx = 1;
            gd.weightx = 0;
            gd.anchor = GridBagConstraints.EAST;
            painelDetalhes.add(valor, gd);
        }

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        corpo.add(painelDetalhes, gbc);

        JPanel painelEstoque = new JPanel(new BorderLayout(0, 6));
        painelEstoque.setOpaque(false);
        JLabel tituloEstoque = new JLabel("Estoque após a venda");
        tituloEstoque.setForeground(VERDE_ESCURO);
        tituloEstoque.setFont(new Font("Arial", Font.BOLD, 13));
        painelEstoque.add(tituloEstoque, BorderLayout.NORTH);

        String[] saldos = alertas.stream()
                .map(alerta -> "%s — %d em estoque".formatted(alerta.produto(), alerta.quantidade()))
                .toArray(String[]::new);
        JList<String> listaSaldos = new JList<>(saldos);
        listaSaldos.setFixedCellHeight(23);
        JScrollPane scrollSaldos = new JScrollPane(listaSaldos);
        scrollSaldos.setPreferredSize(new Dimension(300, Math.min(saldos.length, 4) * 23 + 20));
        painelEstoque.add(scrollSaldos, BorderLayout.CENTER);

        gbc.gridy = 5;
        gbc.insets = new Insets(12, 0, 0, 0);
        corpo.add(painelEstoque, gbc);

        JPanel rodape = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        rodape.setBackground(FUNDO);

        rodape.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0, BORDA
                        ),
                        BorderFactory.createEmptyBorder(
                                14, 24, 14, 24
                        )
                )
        );

        JButton btnNovaVenda =
                criarBotao("Nova venda", VERDE);

        btnNovaVenda.setContentAreaFilled(false);
        btnNovaVenda.setOpaque(true);

        btnNovaVenda.addActionListener(e -> dialog.dispose());

        rodape.add(btnNovaVenda);

        JRootPane root = dialog.getRootPane();

        root.setDefaultButton(btnNovaVenda);

        root.registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel conteudo =
                new JPanel(new BorderLayout());

        conteudo.setBackground(Color.WHITE);

        conteudo.add(cabecalho, BorderLayout.NORTH);
        conteudo.add(corpo, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);

        dialog.setContentPane(conteudo);
        dialog.setResizable(false);
        dialog.pack();

        dialog.setSize(
                Math.max(dialog.getWidth(), 420),
                dialog.getHeight()
        );

        dialog.setLocationRelativeTo(this);

        SwingUtilities.invokeLater(
                btnNovaVenda::requestFocusInWindow
        );

        dialog.setVisible(true);
    }

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

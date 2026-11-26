package view;

import dao.AdministradorConsultaDAO;
import dao.AlertaEstoqueDAO;
import dao.CargoDAO;
import dao.CategoriaDAO;
import dao.EstoqueDAO;
import dao.ProdutoDAO;
import dao.UsuarioDAO;
import model.AlertaEstoque;
import model.Cargo;
import model.Categoria;
import model.Estoque;
import services.EstoqueService;
import model.Produto;
import model.Usuario;
import notificacoes.ClienteNotificacoes;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class AdministradorGUI extends JFrame {

    private static final Color PRIMARY = Color.decode("#173C38");
    private static final Color PRIMARY_HOVER = Color.decode("#24544E");
    private static final Color PRIMARY_LIGHT = Color.decode("#E5F0ED");
    private static final Color CANVAS = Color.decode("#F5F7F6");
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = Color.decode("#20312E");
    private static final Color MUTED = Color.decode("#64746F");
    private static final Color BORDER = Color.decode("#DFE7E3");
    private static final Color SIDEBAR_TEXT = Color.decode("#E8F2EF");

    private final Usuario usuarioLogado;
    private final boolean gerente;
    private final AdministradorConsultaDAO consultas = new AdministradorConsultaDAO();
    private final AlertaEstoqueDAO alertaEstoqueDAO = new AlertaEstoqueDAO();
    private final Map<String, NavButton> menuButtons = new LinkedHashMap<>();
    private final JPanel content = new JPanel(new BorderLayout());
    private final DefaultListModel<String> alertasEstoque = new DefaultListModel<>();
    private final JLabel statusNotificacoes = label("Conectando ao servidor de alertas…", 12, false, MUTED);
    private final JLabel ultimoAlerta = label("Carregando histórico de alertas…", 13, true, TEXT);
    private final JButton botaoAlertas = new ActionButton("Alertas de estoque", false);
    private final ClienteNotificacoes clienteNotificacoes;
    private JLabel pageTitle;
    private String currentPage = "Início";
    private JTable currentTable;
    private JTextField searchField;
    private JLabel tableStatus;
    private TableRowSorter<DefaultTableModel> currentSorter;
    private long requestVersion = 0;
    private long versaoHistoricoAlertas;

    public AdministradorGUI() {
        this(null, false);
    }

    public AdministradorGUI(Usuario usuario) {
        this(usuario, false);
    }

    protected AdministradorGUI(Usuario usuario, boolean acessoGerente) {
        super(acessoGerente ? "CoStock | Gerência" : "CoStock | Administração");
        this.usuarioLogado = usuario;
        this.gerente = acessoGerente;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1040, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CANVAS);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildWorkspace(), BorderLayout.CENTER);
        navigate("Início");
        clienteNotificacoes = usuario == null ? null : new ClienteNotificacoes(
                alerta -> SwingUtilities.invokeLater(this::carregarHistoricoAlertas),
                conectado -> SwingUtilities.invokeLater(() -> atualizarStatusNotificacoes(conectado)));
        if (clienteNotificacoes != null) {
            carregarHistoricoAlertas();
            clienteNotificacoes.iniciar();
        }
    }

    @Override
    public void dispose() {
        if (clienteNotificacoes != null) clienteNotificacoes.close();
        super.dispose();
    }

    private static Font font(int size, boolean bold) {
        return new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size);
    }

    private static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(font(size, bold));
        result.setForeground(color);
        return result;
    }

    private static JPanel transparent() { return transparent(new FlowLayout()); }

    private static JPanel transparent(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(PRIMARY);
        sidebar.setPreferredSize(new Dimension(248, 0));

        JPanel brand = transparent(new GridLayout(2, 1, 0, 2));
        brand.setBorder(new EmptyBorder(30, 24, 25, 20));
        brand.add(label("CoStock", 29, true, WHITE));
        brand.add(label(gerente ? "PAINEL GERENCIAL" : "PAINEL ADMINISTRATIVO", 10, true, SIDEBAR_TEXT));
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = transparent();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] secoes = gerente
                ? new String[]{"Início", "Produtos", "Abrir Caixa", "Caixas", "Estoque", "Movimentações de Estoque"}
                : new String[]{"Início", "Vendas", "Faturamento", "Boletos", "Usuários",
                        "Cargos e Permissões", "Produtos", "Categorias", "Estoque",
                        "Movimentações de Estoque", "Caixas", "Abrir Caixa", "Relatórios", "Configurações"};
        for (String page : secoes) {
            NavButton button = new NavButton(page);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            button.addActionListener(e -> navigate(page));
            menuButtons.put(page, button);
            menu.add(button);
            menu.add(Box.createVerticalStrut(5));
        }
        sidebar.add(menu, BorderLayout.CENTER);

        JPanel footer = transparent(new BorderLayout());
        footer.setBorder(new EmptyBorder(8, 12, 20, 12));
        NavButton exit = new NavButton("Sair do sistema");
        exit.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?",
                    "CoStock", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) dispose();
        });
        footer.add(exit, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(CANVAS);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(18, 32, 18, 32)));
        pageTitle = label("Visão geral", 20, true, TEXT);
        header.add(pageTitle, BorderLayout.WEST);
        header.add(label("ADMINISTRADOR", 11, true, MUTED), BorderLayout.EAST);
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(header, BorderLayout.NORTH);
        if (usuarioLogado != null) topo.add(criarBarraDeAlertas(), BorderLayout.CENTER);
        workspace.add(topo, BorderLayout.NORTH);

        content.setBackground(CANVAS);
        content.setBorder(new EmptyBorder(26, 32, 26, 32));
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel criarBarraDeAlertas() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setBackground(PRIMARY_LIGHT);
        barra.setBorder(new EmptyBorder(8, 32, 8, 32));
        barra.add(ultimoAlerta, BorderLayout.CENTER);

        JList<String> lista = new JList<>(alertasEstoque);
        lista.setFont(font(13, false));
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(480, 140));
        JPopupMenu popup = new JPopupMenu();
        popup.add(scroll);
        botaoAlertas.setEnabled(false);
        botaoAlertas.addActionListener(e -> popup.show(botaoAlertas,
                botaoAlertas.getWidth() - popup.getPreferredSize().width, botaoAlertas.getHeight()));

        JPanel controles = transparent(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        controles.add(statusNotificacoes);
        controles.add(botaoAlertas);
        barra.add(controles, BorderLayout.EAST);
        return barra;
    }

    private void atualizarStatusNotificacoes(boolean conectado) {
        if (isDisplayable()) {
            statusNotificacoes.setText(conectado ? "Alertas conectados" : "Servidor de alertas indisponível");
            if (conectado) carregarHistoricoAlertas();
        }
    }

    private void carregarHistoricoAlertas() {
        long versao = ++versaoHistoricoAlertas;
        new SwingWorker<List<AlertaEstoque>, Void>() {
            @Override protected List<AlertaEstoque> doInBackground() throws SQLException {
                return alertaEstoqueDAO.listarRecentes();
            }

            @Override protected void done() {
                if (!isDisplayable() || versao != versaoHistoricoAlertas) return;
                try {
                    atualizarHistoricoAlertas(get());
                } catch (Exception e) {
                    ultimoAlerta.setText("Não foi possível consultar o histórico de alertas");
                    ultimoAlerta.setToolTipText(null);
                    System.getLogger(AdministradorGUI.class.getName())
                            .log(System.Logger.Level.WARNING, "Falha ao carregar alertas de estoque", e);
                }
            }
        }.execute();
    }

    private void atualizarHistoricoAlertas(List<AlertaEstoque> alertas) {
        alertasEstoque.clear();
        for (AlertaEstoque alerta : alertas) {
            alertasEstoque.addElement(formatarAlertaEstoque(alerta));
        }
        boolean temAlertas = !alertasEstoque.isEmpty();
        ultimoAlerta.setText(temAlertas ? alertasEstoque.firstElement() : "Nenhum alerta de estoque registrado");
        ultimoAlerta.setToolTipText(temAlertas ? ultimoAlerta.getText() : null);
        botaoAlertas.setText("Alertas de estoque (" + alertasEstoque.size() + ")");
        botaoAlertas.setEnabled(temAlertas);
    }

    private String formatarAlertaEstoque(AlertaEstoque alerta) {
        return "%s — %s (%d em estoque, mínimo de %d)"
                .formatted(alerta.tipo().rotulo(), alerta.produto(), alerta.quantidade(), alerta.minimo());
    }

    private void confirmarAlteracaoEstoque(AlertaEstoque alerta) {
        ClienteNotificacoes.publicar(List.of(alerta));
        carregarHistoricoAlertas();
        JOptionPane.showMessageDialog(this, formatarAlertaEstoque(alerta),
                "Estoque atualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void navigate(String page) {
        if (gerente && !java.util.Arrays.asList("Início", "Produtos", "Abrir Caixa", "Caixas",
                "Estoque", "Movimentações de Estoque").contains(page)) return;
        if ("Abrir Caixa".equals(page)) {
            if (usuarioLogado == null) {
                JOptionPane.showMessageDialog(this, "Entre pelo login para abrir o caixa.");
            } else {
                new CaixaAberturaGUI(usuarioLogado).setVisible(true);
            }
            return;
        }
        currentPage = page;
        requestVersion++;
        for (Map.Entry<String, NavButton> entry : menuButtons.entrySet()) {
            entry.getValue().setSelected(entry.getKey().equals(page));
        }
        pageTitle.setText(page.equals("Início") ? "Visão geral" : page);
        content.removeAll();
        currentTable = null;
        searchField = null;
        tableStatus = null;
        currentSorter = null;

        if (page.equals("Início")) buildDashboard();
        else if (page.equals("Boletos"))
            buildPlaceholder("Boletos", "Módulo de boletos ainda não implementado no banco/sistema.");
        else if (page.equals("Faturamento")) buildFinancialPanel();
        else if (page.equals("Relatórios"))
            buildPlaceholder("Relatórios", "Os relatórios detalhados ainda não foram implementados.");
        else if (page.equals("Configurações"))
            buildPlaceholder("Configurações", "As configurações serão disponibilizadas em uma próxima etapa.");
        else buildListing(page);

        content.revalidate();
        content.repaint();
    }

    private JPanel metric(String title, String number, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(18, 20, 18, 20)));
        card.add(label(title, 11, true, MUTED), BorderLayout.NORTH);
        card.add(label(number, 31, true, PRIMARY), BorderLayout.CENTER);
        card.add(label(description, 11, false, MUTED), BorderLayout.SOUTH);
        return card;
    }

    private void buildDashboard() {
        JPanel page = transparent(new BorderLayout(0, 24));
        JPanel heading = transparent();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = label("Bem-vindo ao CoStock", 27, true, TEXT);
        JLabel subtitle = label("Acompanhe seus registros e acesse os módulos do sistema.", 13, false, MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(subtitle);
        page.add(heading, BorderLayout.NORTH);

        JPanel body = transparent(new BorderLayout(0, 28));
        JPanel metrics = transparent(new GridLayout(1, 4, 14, 0));
        metrics.setPreferredSize(new Dimension(0, 136));
        metrics.add(metric(gerente ? "CAIXAS ABERTOS" : "USUÁRIOS", "—", "Cadastrados / abertos"));
        metrics.add(metric("PRODUTOS", "—", "Cadastrados"));
        metrics.add(metric("ESTOQUE", "—", "Abaixo do mínimo"));
        metrics.add(metric("CAIXAS", "—", "Total cadastrado"));
        body.add(metrics, BorderLayout.NORTH);

        JPanel shortcuts = transparent(new BorderLayout(0, 15));
        shortcuts.add(label("Acesso rápido", 19, true, TEXT), BorderLayout.NORTH);
        JPanel grid = transparent(new GridLayout(2, 3, 14, 14));
        for (String section : (gerente
                ? new String[]{"Produtos", "Abrir Caixa", "Caixas", "Estoque", "Movimentações de Estoque"}
                : new String[]{"Vendas", "Faturamento", "Estoque", "Abrir Caixa", "Caixas", "Relatórios"})) {
            JPanel tile = new JPanel(new BorderLayout(0, 8));
            tile.setBackground(WHITE);
            tile.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER), new EmptyBorder(18, 20, 18, 20)));
            JLabel tileTitle = label(section, 16, true, TEXT);
            JLabel tileSubtitle = label(shortcutDescription(section), 11, false, MUTED);
            JPanel tileText = transparent();
            tileText.setLayout(new BoxLayout(tileText, BoxLayout.Y_AXIS));
            tileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            tileSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            tileText.add(tileTitle);
            tileText.add(Box.createVerticalStrut(7));
            tileText.add(tileSubtitle);
            tile.add(tileText, BorderLayout.CENTER);
            ActionButton action = new ActionButton("Abrir  →", false);
            action.addActionListener(e -> navigate(section));
            tile.add(action, BorderLayout.SOUTH);
            grid.add(tile);
        }
        shortcuts.add(grid, BorderLayout.CENTER);
        body.add(shortcuts, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        content.add(scroll, BorderLayout.CENTER);

        final long version = requestVersion;
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() {
                String[] values = new String[4];
                try { values[0] = Integer.toString(gerente ? consultas.contarCaixasAbertos() : consultas.contar("usuarios")); }
                catch (SQLException e) { values[0] = "Erro"; }
                try { values[1] = Integer.toString(consultas.contar("produtos")); }
                catch (SQLException e) { values[1] = "Erro"; }
                try { values[2] = Integer.toString(consultas.contarAbaixoMinimo()); }
                catch (SQLException e) { values[2] = "Erro"; }
                try { values[3] = Integer.toString(consultas.contar("caixas")); }
                catch (SQLException e) { values[3] = "Erro"; }
                return values;
            }
            @Override protected void done() {
                if (version != requestVersion || !"Início".equals(currentPage)) return;
                try {
                    String[] values = get();
                    metrics.removeAll();
                    metrics.add(metric(gerente ? "CAIXAS ABERTOS" : "USUÁRIOS", values[0], gerente ? "Em operação" : "Cadastrados"));
                    metrics.add(metric("PRODUTOS", values[1], "Cadastrados"));
                    metrics.add(metric("ESTOQUE", values[2], "Abaixo do mínimo"));
                    metrics.add(metric("CAIXAS", values[3], "Total cadastrado"));
                    metrics.revalidate();
                    metrics.repaint();
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private String shortcutDescription(String section) {
        switch (section) {
            case "Usuários": return "Consultar funcionários";
            case "Produtos": return "Visualizar catálogo";
            case "Categorias": return "Organizar categorias";
            case "Estoque": return "Consultar quantidades";
            case "Caixas": return "Acompanhar caixas";
            case "Abrir Caixa": return "Iniciar operação";
            case "Vendas": return "Consultar vendas";
            case "Faturamento": return "Diário, semanal e mensal";
            case "Movimentações de Estoque": return "Entradas e saídas";
            default: return "Indicadores e análises";
        }
    }

    private String tableFor(String section) {
        switch (section) {
            case "Usuários": return "usuarios";
            case "Cargos e Permissões": return "cargos";
            case "Produtos": return "produtos";
            case "Categorias": return "categorias";
            case "Estoque": return "estoque";
            case "Caixas": return "caixas";
            case "Vendas": return "vendas";
            case "Movimentações de Estoque": return "movimentacoes_estoque";
            default: throw new IllegalArgumentException("Seção desconhecida: " + section);
        }
    }

    private void buildListing(String section) {
        JPanel toolbar = transparent(new BorderLayout(0, 18));
        JPanel heading = transparent();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = label("Listagem de " + section.toLowerCase(), 24, true, TEXT);
        JLabel subtitle = label("Consulte e filtre os registros do banco de dados.", 12, false, MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);
        toolbar.add(heading, BorderLayout.NORTH);

        JPanel tools = transparent(new BorderLayout(12, 0));
        searchField = new JTextField();
        searchField.setFont(font(13, false));
        searchField.setForeground(TEXT);
        searchField.setBackground(WHITE);
        searchField.setCaretColor(PRIMARY);
        searchField.setToolTipText("Pesquisar em todas as colunas");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(11, 14, 11, 14)));
        tools.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });
        ActionButton refresh = new ActionButton("Atualizar listagem", true);
        refresh.addActionListener(e -> loadListing(section));
        if (section.equals("Produtos")) {
            JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            ActionButton edit = new ActionButton("Atualizar produto", false);
            edit.addActionListener(e -> editSelectedProduct());
            ActionButton delete = new ActionButton("Excluir produto", false);
            delete.addActionListener(e -> deleteSelectedProduct());
            actions.add(edit);
            actions.add(delete);
            actions.add(refresh);
            tools.add(actions, BorderLayout.EAST);
        } else if (section.equals("Usuários")) {
            JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            ActionButton create = new ActionButton("Novo usuário", false);
            create.addActionListener(e -> createUser());
            ActionButton edit = new ActionButton("Atualizar usuário", false);
            edit.addActionListener(e -> editSelectedUser());
            ActionButton delete = new ActionButton("Excluir usuário", false);
            delete.addActionListener(e -> deleteSelectedUser());
            actions.add(create);
            actions.add(edit);
            actions.add(delete);
            actions.add(refresh);
            tools.add(actions, BorderLayout.EAST);
        } else if (section.equals("Estoque")) {
            JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            ActionButton create = new ActionButton("Novo estoque", false);
            create.addActionListener(e -> createStock());
            ActionButton edit = new ActionButton("Editar quantidade", false);
            edit.addActionListener(e -> editSelectedStock());
            ActionButton delete = new ActionButton("Excluir estoque", false);
            delete.addActionListener(e -> deleteSelectedStock());
            actions.add(create);
            actions.add(edit);
            actions.add(delete);
            actions.add(refresh);
            tools.add(actions, BorderLayout.EAST);
        } else if (section.equals("Movimentações de Estoque")) {
            JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            ActionButton create = new ActionButton("Nova movimentação", false);
            create.addActionListener(e -> createMovement());
            actions.add(create);
            actions.add(refresh);
            tools.add(actions, BorderLayout.EAST);
        } else {
            tools.add(refresh, BorderLayout.EAST);
        }
        toolbar.add(tools, BorderLayout.SOUTH);
        content.add(toolbar, BorderLayout.NORTH);

        currentTable = new JTable();
        currentTable.setFont(font(12, false));
        currentTable.setForeground(TEXT);
        currentTable.setBackground(WHITE);
        currentTable.setRowHeight(40);
        currentTable.setGridColor(BORDER);
        currentTable.setShowVerticalLines(false);
        currentTable.setSelectionBackground(PRIMARY_LIGHT);
        currentTable.setSelectionForeground(TEXT);
        currentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        currentTable.setFillsViewportHeight(true);
        currentTable.getTableHeader().setFont(font(12, true));
        currentTable.getTableHeader().setForeground(WHITE);
        currentTable.getTableHeader().setBackground(PRIMARY);
        currentTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        currentTable.getTableHeader().setOpaque(true);
        currentTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        currentTable.setDefaultRenderer(Object.class, new BodyRenderer());
        JScrollPane scroll = new JScrollPane(currentTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);

        JPanel tableArea = transparent(new BorderLayout());
        tableArea.setBorder(new EmptyBorder(22, 0, 0, 0));
        tableArea.add(scroll, BorderLayout.CENTER);
        content.add(tableArea, BorderLayout.CENTER);

        tableStatus = label("Carregando registros...", 12, false, MUTED);
        tableStatus.setBorder(new EmptyBorder(12, 0, 0, 0));
        content.add(tableStatus, BorderLayout.SOUTH);
        loadListing(section);
    }

    private void loadListing(String section) {
        final JTable target = currentTable;
        final JLabel footer = tableStatus;
        final JTextField field = searchField;
        final long version = ++requestVersion;
        footer.setText("Consultando banco de dados...");

        new SwingWorker<DefaultTableModel, Void>() {
            @Override protected DefaultTableModel doInBackground() throws SQLException {
                return consultas.listar(tableFor(section));
            }
            @Override protected void done() {
                if (version != requestVersion || target != currentTable || !section.equals(currentPage)) return;
                try {
                    DefaultTableModel model = get();
                    target.setModel(model);
                    for (String entity : new String[] {"USUARIO", "CARGO", "CATEGORIA", "PRODUTO"}) {
                        int idColumn = target.convertColumnIndexToView(model.findColumn("ID " + entity));
                        int nameColumn = target.convertColumnIndexToView(model.findColumn("NOME " + entity));
                        if (idColumn >= 0 && nameColumn >= 0) {
                            int destination = nameColumn < idColumn ? idColumn : idColumn + 1;
                            target.moveColumn(nameColumn, destination);
                        }
                    }
                    target.setDefaultRenderer(Object.class, new BodyRenderer());
                    for (int i = 0; i < target.getColumnCount(); i++) {
                        target.getColumnModel().getColumn(i).setPreferredWidth(165);
                    }
                    currentSorter = new TableRowSorter<>(model);
                    target.setRowSorter(currentSorter);
                    applySearchFilter();
                } catch (Exception e) {
                    footer.setText("Falha ao consultar registros.");
                    showError(e);
                }
            }
        }.execute();
    }

    private void applySearchFilter() {
        if (currentSorter == null || searchField == null || currentTable == null) return;

        String term = searchField.getText().trim();
        currentSorter.setRowFilter(term.isEmpty() ? null
                : RowFilter.regexFilter("(?iu)" + Pattern.quote(term)));

        if (tableStatus != null) {
            tableStatus.setText(currentTable.getRowCount() + " registro(s) exibido(s)"
                    + (term.isEmpty() ? "" : " para: " + term));
        }
    }

    private Integer selectedId(String idColumnName, String entityName) {
        if (currentTable == null || currentTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um " + entityName + " na tabela.",
                    "CoStock", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int row = currentTable.convertRowIndexToModel(currentTable.getSelectedRow());
        TableModel model = currentTable.getModel();
        int idColumn = 0;
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (model.getColumnName(i).equals(idColumnName)) { idColumn = i; break; }
        }
        Object value = model.getValueAt(row, idColumn);
        return value == null ? null : Integer.valueOf(value.toString());
    }

    private void reloadIfVisible(String section) {
        if (section.equals(currentPage) && currentTable != null && tableStatus != null) {
            loadListing(section);
        }
    }

    private void deleteSelectedProduct() {
        Integer id = selectedId("ID PRODUTO", "produto");
        if (id == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o produto #" + id + "?\nEsta ação não pode ser desfeita.",
                "Excluir produto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return new ProdutoDAO().excluir(id);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Produto excluído com sucesso.");
                        reloadIfVisible("Produtos");
                    } else {
                        JOptionPane.showMessageDialog(AdministradorGUI.this,
                                "Não foi possível excluir o produto.\n"
                                        + "Ele pode estar vinculado a estoque, vendas ou movimentações.\n"
                                        + "Nesse caso, use \"Atualizar produto\" e desmarque \"Produto ativo\".",
                                "Excluir produto", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void editSelectedProduct() {
        Integer id = selectedId("ID PRODUTO", "produto");
        if (id == null) return;

        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() {
                return new Object[]{new ProdutoDAO().buscarPorId(id), new CategoriaDAO().listar()};
            }
            @SuppressWarnings("unchecked")
            @Override protected void done() {
                try {
                    Object[] result = get();
                    if (result[0] == null) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Produto não encontrado.",
                                "Atualizar produto", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    showProductDialog((Produto) result[0], (java.util.List<Categoria>) result[1]);
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private JTextField formField(String value) {
        JTextField field = new JTextField(value == null ? "" : value);
        field.setFont(font(13, false));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(9, 12, 9, 12)));
        return field;
    }

    private void showProductDialog(Produto produto, java.util.List<Categoria> categorias) {
        JDialog dialog = new JDialog(this, "Atualizar produto", true);
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(WHITE);
        root.setBorder(new EmptyBorder(24, 28, 22, 28));
        dialog.setContentPane(root);

        JPanel heading = transparent();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = label("Atualizar produto #" + produto.getIdProduto(), 20, true, TEXT);
        JLabel subtitle = label("Altere os dados e clique em salvar.", 12, false, MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JTextField nome = formField(produto.getNome());
        JTextField descricao = formField(produto.getDescricao());
        JTextField codigo = formField(produto.getCodigoBarras());
        JTextField custo = formField(produto.getPrecoCusto() == null ? "" : produto.getPrecoCusto().toPlainString());
        JTextField venda = formField(produto.getPrecoVenda() == null ? "" : produto.getPrecoVenda().toPlainString());
        JTextField minimo = formField(Integer.toString(produto.getEstoqueMinimo()));

        JComboBox<Categoria> categoria = new JComboBox<>(categorias.toArray(new Categoria[0]));
        categoria.setFont(font(13, false));
        categoria.setBackground(WHITE);
        categoria.setForeground(TEXT);
        categoria.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Categoria) setText(((Categoria) value).getNome());
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        for (Categoria c : categorias) {
            if (produto.getCategoria() != null && c.getId() == produto.getCategoria().getId()) {
                categoria.setSelectedItem(c);
            }
        }

        JCheckBox ativo = new JCheckBox("Produto ativo", produto.isAtivo());
        ativo.setFont(font(13, false));
        ativo.setForeground(TEXT);
        ativo.setOpaque(false);
        ativo.setFocusPainted(false);

        JPanel form = transparent(new GridLayout(0, 2, 14, 12));
        Object[][] rows = {
                {"NOME", nome}, {"DESCRIÇÃO", descricao}, {"CÓDIGO DE BARRAS", codigo},
                {"CATEGORIA", categoria}, {"PREÇO DE CUSTO", custo}, {"PREÇO DE VENDA", venda},
                {"ESTOQUE MÍNIMO", minimo}, {"SITUAÇÃO", ativo}};
        for (Object[] r : rows) {
            JPanel cell = transparent(new BorderLayout(0, 6));
            cell.add(label((String) r[0], 11, true, MUTED), BorderLayout.NORTH);
            cell.add((Component) r[1], BorderLayout.CENTER);
            form.add(cell);
        }
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        ActionButton cancel = new ActionButton("Cancelar", false);
        cancel.addActionListener(e -> dialog.dispose());
        ActionButton save = new ActionButton("Salvar alterações", true);
        save.addActionListener(e -> {
            try {
                if (nome.getText().trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do produto.");
                if (nome.getText().trim().length() > 150) throw new IllegalArgumentException("O nome pode ter no máximo 150 caracteres.");
                if (descricao.getText().trim().length() > 255) throw new IllegalArgumentException("A descrição pode ter no máximo 255 caracteres.");
                if (codigo.getText().trim().length() > 50) throw new IllegalArgumentException("O código de barras pode ter no máximo 50 caracteres.");
                if (categoria.getSelectedItem() == null) throw new IllegalArgumentException("Selecione uma categoria.");
                java.math.BigDecimal precoCusto = new java.math.BigDecimal(custo.getText().trim().replace(",", "."));
                java.math.BigDecimal precoVenda = new java.math.BigDecimal(venda.getText().trim().replace(",", "."));
                int estoqueMinimo = Integer.parseInt(minimo.getText().trim());
                if (precoCusto.signum() < 0 || precoVenda.signum() < 0 || estoqueMinimo < 0)
                    throw new IllegalArgumentException("Preços e estoque mínimo não podem ser negativos.");

                produto.setNome(nome.getText().trim());
                String textoDescricao = descricao.getText().trim();
                produto.setDescricao(textoDescricao.isEmpty() ? null : textoDescricao);
                String codigoBarras = codigo.getText().trim();
                produto.setCodigoBarras(codigoBarras.isEmpty() ? null : codigoBarras);
                produto.setPrecoCusto(precoCusto);
                produto.setPrecoVenda(precoVenda);
                produto.setEstoqueMinimo(estoqueMinimo);
                produto.setCategoria((Categoria) categoria.getSelectedItem());
                produto.setAtivo(ativo.isSelected());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Preços e estoque mínimo devem ser números válidos.",
                        "Atualizar produto", JOptionPane.WARNING_MESSAGE);
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Atualizar produto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            save.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() {
                    return new ProdutoDAO().atualizar(produto);
                }
                @Override protected void done() {
                    save.setEnabled(true);
                    try {
                        if (get()) {
                            dialog.dispose();
                            JOptionPane.showMessageDialog(AdministradorGUI.this, "Produto atualizado com sucesso.");
                            reloadIfVisible("Produtos");
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Não foi possível atualizar o produto.\n"
                                            + "Verifique os dados informados (por exemplo, se o código\n"
                                            + "de barras já pertence a outro produto).",
                                    "Atualizar produto", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { showError(ex); }
                }
            }.execute();
        });
        buttons.add(cancel);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 620), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void createUser() {
        new SwingWorker<java.util.List<Cargo>, Void>() {
            @Override protected java.util.List<Cargo> doInBackground() {
                return new CargoDAO().listar();
            }
            @Override protected void done() {
                try { showUserDialog(null, get()); }
                catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void editSelectedUser() {
        Integer id = selectedId("ID USUARIO", "usuário");
        if (id == null) return;

        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() {
                return new Object[]{new UsuarioDAO().buscarPorId(id), new CargoDAO().listar()};
            }
            @SuppressWarnings("unchecked")
            @Override protected void done() {
                try {
                    Object[] result = get();
                    if (result[0] == null) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Usuário não encontrado.",
                                "Atualizar usuário", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    showUserDialog((Usuario) result[0], (java.util.List<Cargo>) result[1]);
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void deleteSelectedUser() {
        Integer id = selectedId("ID USUARIO", "usuário");
        if (id == null) return;
        if (usuarioLogado != null && usuarioLogado.getId() == id) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir o usuário com o qual está conectado.",
                    "Excluir usuário", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o usuário #" + id + "?\nEsta ação não pode ser desfeita.",
                "Excluir usuário", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return new UsuarioDAO().excluir(id);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Usuário excluído com sucesso.");
                        reloadIfVisible("Usuários");
                    } else {
                        JOptionPane.showMessageDialog(AdministradorGUI.this,
                                "Não foi possível excluir o usuário.\n"
                                        + "Ele pode estar vinculado a caixas, vendas ou movimentações.\n"
                                        + "Nesse caso, use \"Atualizar usuário\" e desmarque \"Usuário ativo\".",
                                "Excluir usuário", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void showUserDialog(Usuario usuario, java.util.List<Cargo> cargos) {
        final boolean novo = usuario == null;
        final Usuario alvo = novo ? new Usuario() : usuario;
        final String dialogTitle = novo ? "Novo usuário" : "Atualizar usuário";

        JDialog dialog = new JDialog(this, dialogTitle, true);
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(WHITE);
        root.setBorder(new EmptyBorder(24, 28, 22, 28));
        dialog.setContentPane(root);

        JPanel heading = transparent();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = label(novo ? "Novo usuário" : "Atualizar usuário #" + alvo.getId(), 20, true, TEXT);
        JLabel subtitle = label(novo ? "Preencha os dados e clique em salvar."
                : "Altere os dados e clique em salvar. Deixe a senha em branco para mantê-la.",
                12, false, MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JTextField nome = formField(alvo.getNome());
        JTextField email = formField(alvo.getEmail());
        JTextField telefone = formField(alvo.getTelefone());
        JTextField endereco = formField(alvo.getEndereco());
        JPasswordField senha = new JPasswordField();
        senha.setFont(font(13, false));
        senha.setForeground(TEXT);
        senha.setBackground(WHITE);
        senha.setCaretColor(PRIMARY);
        senha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(9, 12, 9, 12)));

        JComboBox<Cargo> cargo = new JComboBox<>(cargos.toArray(new Cargo[0]));
        cargo.setFont(font(13, false));
        cargo.setBackground(WHITE);
        cargo.setForeground(TEXT);
        cargo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Cargo) setText(((Cargo) value).getNome());
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        for (Cargo c : cargos) {
            if (alvo.getCargo() != null && c.getId() == alvo.getCargo().getId()) cargo.setSelectedItem(c);
        }

        JCheckBox ativo = new JCheckBox("Usuário ativo", novo || alvo.isAtivo());
        ativo.setFont(font(13, false));
        ativo.setForeground(TEXT);
        ativo.setOpaque(false);
        ativo.setFocusPainted(false);

        JPanel form = transparent(new GridLayout(0, 2, 14, 12));
        Object[][] rows = {
                {"NOME", nome}, {"E-MAIL", email}, {"TELEFONE", telefone}, {"ENDEREÇO", endereco},
                {novo ? "SENHA" : "NOVA SENHA (OPCIONAL)", senha}, {"CARGO", cargo}, {"SITUAÇÃO", ativo}};
        for (Object[] r : rows) {
            JPanel cell = transparent(new BorderLayout(0, 6));
            cell.add(label((String) r[0], 11, true, MUTED), BorderLayout.NORTH);
            cell.add((Component) r[1], BorderLayout.CENTER);
            form.add(cell);
        }
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        ActionButton cancel = new ActionButton("Cancelar", false);
        cancel.addActionListener(e -> dialog.dispose());
        ActionButton save = new ActionButton(novo ? "Cadastrar usuário" : "Salvar alterações", true);
        save.addActionListener(e -> {
            String textoNome = nome.getText().trim();
            String textoEmail = email.getText().trim();
            String textoTelefone = telefone.getText().trim();
            String textoEndereco = endereco.getText().trim();
            String textoSenha = new String(senha.getPassword());
            String erro = null;
            if (textoNome.isEmpty()) erro = "Informe o nome do usuário.";
            else if (textoNome.length() > 100) erro = "O nome pode ter no máximo 100 caracteres.";
            else if (!textoEmail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) erro = "Informe um e-mail válido.";
            else if (textoEmail.length() > 100) erro = "O e-mail pode ter no máximo 100 caracteres.";
            else if (textoTelefone.length() > 20) erro = "O telefone pode ter no máximo 20 caracteres.";
            else if (textoEndereco.length() > 255) erro = "O endereço pode ter no máximo 255 caracteres.";
            else if (novo && textoSenha.isEmpty()) erro = "Informe uma senha.";
            else if (textoSenha.length() > 255) erro = "A senha pode ter no máximo 255 caracteres.";
            else if (cargo.getSelectedItem() == null) erro = "Selecione um cargo.";
            else if (!novo && usuarioLogado != null && usuarioLogado.getId() == alvo.getId() && !ativo.isSelected())
                erro = "Você não pode desativar o usuário com o qual está conectado.";
            if (erro != null) {
                JOptionPane.showMessageDialog(dialog, erro, dialogTitle, JOptionPane.WARNING_MESSAGE);
                return;
            }

            alvo.setNome(textoNome);
            alvo.setEmail(textoEmail);
            alvo.setTelefone(textoTelefone.isEmpty() ? null : textoTelefone);
            alvo.setEndereco(textoEndereco.isEmpty() ? null : textoEndereco);
            if (!textoSenha.isEmpty()) alvo.setSenha(textoSenha);
            alvo.setCargo((Cargo) cargo.getSelectedItem());
            alvo.setAtivo(ativo.isSelected());

            save.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() {
                    UsuarioDAO dao = new UsuarioDAO();
                    return novo ? dao.cadastrar(alvo) : dao.atualizar(alvo);
                }
                @Override protected void done() {
                    save.setEnabled(true);
                    try {
                        if (get()) {
                            dialog.dispose();
                            JOptionPane.showMessageDialog(AdministradorGUI.this,
                                    novo ? "Usuário cadastrado com sucesso." : "Usuário atualizado com sucesso.");
                            reloadIfVisible("Usuários");
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Não foi possível salvar o usuário.\n"
                                            + "Verifique os dados informados (por exemplo, se o e-mail\n"
                                            + "já pertence a outro usuário).",
                                    dialogTitle, JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { showError(ex); }
                }
            }.execute();
        });
        buttons.add(cancel);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 620), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static String serviceErrorMessage(java.util.concurrent.ExecutionException ex) {
        Throwable cause = ex.getCause();
        return cause instanceof IllegalStateException && cause.getCause() != null
                ? cause.getMessage() + "\n" + cause.getCause().getMessage()
                : cause.getMessage();
    }

    private JDialog stockDialog(String dialogTitle, String heading, String subtitle,
            Object[][] rows, ActionButton save) {
        JDialog dialog = new JDialog(this, dialogTitle, true);
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(WHITE);
        root.setBorder(new EmptyBorder(24, 28, 22, 28));
        dialog.setContentPane(root);

        JPanel head = transparent();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = label(heading, 20, true, TEXT);
        JLabel sub = label(subtitle, 12, false, MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(Box.createVerticalStrut(5));
        head.add(sub);
        root.add(head, BorderLayout.NORTH);

        JPanel form = transparent(new GridLayout(0, 2, 14, 12));
        for (Object[] r : rows) {
            JPanel cell = transparent(new BorderLayout(0, 6));
            cell.add(label((String) r[0], 11, true, MUTED), BorderLayout.NORTH);
            cell.add((Component) r[1], BorderLayout.CENTER);
            form.add(cell);
        }
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        ActionButton cancel = new ActionButton("Cancelar", false);
        cancel.addActionListener(e -> dialog.dispose());
        buttons.add(cancel);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 620), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void createStock() {
        new SwingWorker<java.util.List<Produto>, Void>() {
            @Override protected java.util.List<Produto> doInBackground() {
                Set<Integer> comEstoque = new HashSet<>();
                for (Estoque e : new EstoqueDAO().listar()) comEstoque.add(e.getProduto().getIdProduto());
                java.util.List<Produto> disponiveis = new ArrayList<>();
                for (Produto p : new ProdutoDAO().listar()) {
                    if (!comEstoque.contains(p.getIdProduto())) disponiveis.add(p);
                }
                return disponiveis;
            }
            @Override protected void done() {
                try {
                    java.util.List<Produto> produtos = get();
                    if (produtos.isEmpty()) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this,
                                "Todos os produtos cadastrados já possuem estoque.",
                                "Novo estoque", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    showCreateStockDialog(produtos);
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void showCreateStockDialog(java.util.List<Produto> produtos) {
        JComboBox<Produto> produto = new JComboBox<>(produtos.toArray(new Produto[0]));
        produto.setFont(font(13, false));
        produto.setBackground(WHITE);
        produto.setForeground(TEXT);
        produto.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Produto) {
                    Produto p = (Produto) value;
                    setText("#" + p.getIdProduto() + " — " + p.getNome());
                }
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        JTextField quantidade = formField("0");

        ActionButton save = new ActionButton("Cadastrar estoque", true);
        JDialog dialog = stockDialog("Novo estoque", "Novo estoque",
                "Escolha um produto sem estoque e informe a quantidade inicial.",
                new Object[][]{{"PRODUTO", produto}, {"QUANTIDADE INICIAL", quantidade}}, save);

        save.addActionListener(e -> {
            int qtd;
            try {
                qtd = Integer.parseInt(quantidade.getText().trim());
                if (qtd < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Informe uma quantidade inteira maior ou igual a zero.",
                        "Novo estoque", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Estoque estoque = new Estoque();
            estoque.setProduto((Produto) produto.getSelectedItem());
            estoque.setQuantidade(qtd);

            save.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() {
                    return new EstoqueDAO().cadastrar(estoque);
                }
                @Override protected void done() {
                    save.setEnabled(true);
                    try {
                        if (get()) {
                            dialog.dispose();
                            JOptionPane.showMessageDialog(AdministradorGUI.this, "Estoque cadastrado com sucesso.");
                            reloadIfVisible("Estoque");
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Não foi possível cadastrar o estoque.\n"
                                            + "Verifique se esse produto já não possui estoque.",
                                    "Novo estoque", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { showError(ex); }
                }
            }.execute();
        });
        dialog.setVisible(true);
    }

    private void editSelectedStock() {
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this,
                    "Entre pelo login para ajustar o estoque.\nO ajuste é registrado nas movimentações com o seu usuário.",
                    "Editar quantidade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Integer idProduto = selectedId("ID PRODUTO", "estoque");
        if (idProduto == null) return;

        new SwingWorker<Estoque, Void>() {
            @Override protected Estoque doInBackground() {
                return new EstoqueDAO().buscarPorIdProduto(idProduto);
            }
            @Override protected void done() {
                try {
                    Estoque estoque = get();
                    if (estoque == null) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Estoque não encontrado.",
                                "Editar quantidade", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    showEditStockDialog(estoque);
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void showEditStockDialog(Estoque estoque) {
        Produto produto = estoque.getProduto();
        JTextField nomeProduto = formField("#" + produto.getIdProduto() + " — " + produto.getNome());
        nomeProduto.setEditable(false);
        nomeProduto.setBackground(CANVAS);
        JTextField atual = formField(Integer.toString(estoque.getQuantidade()));
        atual.setEditable(false);
        atual.setBackground(CANVAS);
        JTextField nova = formField(Integer.toString(estoque.getQuantidade()));
        JTextField motivo = formField("");

        ActionButton save = new ActionButton("Salvar ajuste", true);
        JDialog dialog = stockDialog("Editar quantidade", "Editar quantidade do estoque #" + estoque.getId(),
                "O ajuste é registrado em Movimentações de Estoque como AJUSTE.",
                new Object[][]{{"PRODUTO", nomeProduto}, {"QUANTIDADE ATUAL", atual},
                        {"NOVA QUANTIDADE", nova}, {"MOTIVO", motivo}}, save);

        save.addActionListener(e -> {
            int qtd;
            try {
                qtd = Integer.parseInt(nova.getText().trim());
                if (qtd < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Informe uma quantidade inteira maior ou igual a zero.",
                        "Editar quantidade", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String textoMotivo = motivo.getText().trim();
            if (textoMotivo.isEmpty() || textoMotivo.length() > 255) {
                JOptionPane.showMessageDialog(dialog, "Informe o motivo do ajuste (até 255 caracteres).",
                        "Editar quantidade", JOptionPane.WARNING_MESSAGE);
                return;
            }

            save.setEnabled(false);
            new SwingWorker<AlertaEstoque, Void>() {
                @Override protected AlertaEstoque doInBackground() throws Exception {
                    return new EstoqueService().ajustarEstoque(produto.getIdProduto(),
                            usuarioLogado.getId(), qtd, textoMotivo);
                }
                @Override protected void done() {
                    save.setEnabled(true);
                    try {
                        AlertaEstoque alerta = get();
                        dialog.dispose();
                        confirmarAlteracaoEstoque(alerta);
                        reloadIfVisible("Estoque");
                    } catch (java.util.concurrent.ExecutionException ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Não foi possível ajustar o estoque.\n" + serviceErrorMessage(ex),
                                "Editar quantidade", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) { showError(ex); }
                }
            }.execute();
        });
        dialog.setVisible(true);
    }

    private void deleteSelectedStock() {
        Integer idEstoque = selectedId("ID ESTOQUE", "estoque");
        if (idEstoque == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o estoque #" + idEstoque + "?\n"
                        + "O produto continuará cadastrado, mas ficará sem registro de estoque.\n"
                        + "Esta ação não pode ser desfeita.",
                "Excluir estoque", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return new EstoqueDAO().excluir(idEstoque);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Estoque excluído com sucesso.");
                        reloadIfVisible("Estoque");
                    } else {
                        JOptionPane.showMessageDialog(AdministradorGUI.this, "Não foi possível excluir o estoque.",
                                "Excluir estoque", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void createMovement() {
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this,
                    "Entre pelo login para registrar movimentações.\nA movimentação é registrada com o seu usuário.",
                    "Nova movimentação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new SwingWorker<java.util.List<Estoque>, Void>() {
            @Override protected java.util.List<Estoque> doInBackground() {
                java.util.List<Estoque> ativos = new ArrayList<>();
                for (Estoque e : new EstoqueDAO().listar()) {
                    if (e.getProduto().isAtivo()) ativos.add(e);
                }
                return ativos;
            }
            @Override protected void done() {
                try {
                    java.util.List<Estoque> estoques = get();
                    if (estoques.isEmpty()) {
                        JOptionPane.showMessageDialog(AdministradorGUI.this,
                                "Nenhum produto ativo possui estoque.\nCadastre um estoque na aba Estoque.",
                                "Nova movimentação", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    showMovementDialog(estoques);
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void showMovementDialog(java.util.List<Estoque> estoques) {
        JComboBox<Estoque> produto = new JComboBox<>(estoques.toArray(new Estoque[0]));
        produto.setFont(font(13, false));
        produto.setBackground(WHITE);
        produto.setForeground(TEXT);
        produto.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Estoque) {
                    Estoque e = (Estoque) value;
                    setText("#" + e.getProduto().getIdProduto() + " — " + e.getProduto().getNome()
                            + "  (atual: " + e.getQuantidade() + ")");
                }
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });

        String[] tipos = {"ENTRADA", "SAIDA"};
        JComboBox<String> tipo = new JComboBox<>(tipos);
        tipo.setFont(font(13, false));
        tipo.setBackground(WHITE);
        tipo.setForeground(TEXT);
        tipo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if ("ENTRADA".equals(value)) setText("Entrada (soma ao estoque)");
                else if ("SAIDA".equals(value)) setText("Saída (subtrai do estoque)");
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });

        JTextField quantidade = formField("");
        JTextField motivo = formField("");

        ActionButton save = new ActionButton("Registrar movimentação", true);
        JDialog dialog = stockDialog("Nova movimentação", "Nova movimentação de estoque",
                "A quantidade do estoque é atualizada junto com o registro da movimentação.",
                new Object[][]{{"PRODUTO", produto}, {"TIPO", tipo},
                        {"QUANTIDADE", quantidade}, {"MOTIVO", motivo}}, save);

        save.addActionListener(e -> {
            Estoque estoque = (Estoque) produto.getSelectedItem();
            String tipoSelecionado = (String) tipo.getSelectedItem();
            int qtd;
            try {
                qtd = Integer.parseInt(quantidade.getText().trim());
                if (qtd <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Informe uma quantidade inteira maior que zero.",
                        "Nova movimentação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String textoMotivo = motivo.getText().trim();
            if (textoMotivo.isEmpty() || textoMotivo.length() > 255) {
                JOptionPane.showMessageDialog(dialog, "Informe o motivo da movimentação (até 255 caracteres).",
                        "Nova movimentação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idProduto = estoque.getProduto().getIdProduto();
            int idUsuario = usuarioLogado.getId();
            save.setEnabled(false);
            new SwingWorker<AlertaEstoque, Void>() {
                @Override protected AlertaEstoque doInBackground() throws Exception {
                    EstoqueService service = new EstoqueService();
                    return "ENTRADA".equals(tipoSelecionado)
                            ? service.adicionarEstoque(idProduto, idUsuario, qtd, textoMotivo)
                            : service.retirarEstoque(idProduto, idUsuario, qtd, textoMotivo);
                }
                @Override protected void done() {
                    save.setEnabled(true);
                    try {
                        AlertaEstoque alerta = get();
                        dialog.dispose();
                        confirmarAlteracaoEstoque(alerta);
                        reloadIfVisible("Movimentações de Estoque");
                    } catch (java.util.concurrent.ExecutionException ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Não foi possível registrar a movimentação.\n" + serviceErrorMessage(ex),
                                "Nova movimentação", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) { showError(ex); }
                }
            }.execute();
        });
        dialog.setVisible(true);
    }

    private void buildFinancialPanel() {
        JPanel panel = transparent(new BorderLayout(0, 18));
        panel.add(label("Faturamento de vendas finalizadas", 22, true, TEXT), BorderLayout.NORTH);
        JPanel cards = transparent(new GridLayout(1, 3, 14, 0));
        cards.add(metric("HOJE", "—", "Vendas finalizadas"));
        cards.add(metric("ÚLTIMOS 7 DIAS", "—", "Vendas finalizadas"));
        cards.add(metric("MÊS ATUAL", "—", "Vendas finalizadas"));
        panel.add(cards, BorderLayout.CENTER);
        panel.add(label("Valores brutos; não incluem despesas ou boletos.", 12, false, MUTED), BorderLayout.SOUTH);
        content.add(panel, BorderLayout.NORTH);
        final long version = requestVersion;
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() throws Exception {
                return consultas.resumoFaturamento();
            }
            @Override protected void done() {
                if (version != requestVersion || !"Faturamento".equals(currentPage)) return;
                try {
                    String[] v = get();
                    cards.removeAll();
                    cards.add(metric("HOJE", v[0], "Vendas finalizadas"));
                    cards.add(metric("ÚLTIMOS 7 DIAS", v[1], "Vendas finalizadas"));
                    cards.add(metric("MÊS ATUAL", v[2], "Vendas finalizadas"));
                    cards.revalidate();
                    cards.repaint();
                } catch (Exception e) { showError(e); }
            }
        }.execute();
    }

    private void buildPlaceholder(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(28, 28, 28, 28)));
        panel.add(label(title, 24, true, PRIMARY), BorderLayout.NORTH);
        panel.add(label(message, 13, false, MUTED), BorderLayout.CENTER);
        content.add(panel, BorderLayout.NORTH);
    }

    private void showError(Exception error) {
        Throwable cause = error;
        while (cause.getCause() != null) cause = cause.getCause();
        JOptionPane.showMessageDialog(this,
                "Não foi possível consultar o banco de dados.\n" + cause.getMessage(),
                "Erro de consulta", JOptionPane.ERROR_MESSAGE);
    }

    private static class NavButton extends JButton {
        private boolean active;
        private boolean hovered;
        NavButton(String text) {
            super(text);
            setFont(font(13, false));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 16, 0, 10));
            setForeground(SIDEBAR_TEXT);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }
        @Override public void setSelected(boolean selected) {
            active = selected;
            setForeground(active ? PRIMARY : SIDEBAR_TEXT);
            setFont(font(13, active));
            repaint();
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(active ? WHITE : hovered ? PRIMARY_HOVER : PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ActionButton extends JButton {
        private final boolean filled;
        private boolean hovered;
        ActionButton(String text, boolean filled) {
            super(text);
            this.filled = filled;
            setFont(font(12, true));
            setForeground(filled ? WHITE : PRIMARY);
            setBorder(new EmptyBorder(11, 17, 11, 17));
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(filled ? (hovered ? PRIMARY_HOVER : PRIMARY)
                    : (hovered ? PRIMARY_LIGHT : WHITE));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setOpaque(true);
            setBackground(PRIMARY);
            setForeground(WHITE);
            setFont(font(12, true));
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setBackground(PRIMARY);
            setForeground(WHITE);
            return this;
        }
    }

    private static class BodyRenderer extends DefaultTableCellRenderer {
        BodyRenderer() { setBorder(new EmptyBorder(0, 12, 0, 12)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setForeground(TEXT);
            setBackground(selected ? PRIMARY_LIGHT : row % 2 == 0 ? WHITE : CANVAS);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new AdministradorGUI().setVisible(true);
        });
    }
}

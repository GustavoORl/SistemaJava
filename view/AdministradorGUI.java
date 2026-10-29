package view;

import dao.AdministradorConsultaDAO;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Painel administrativo CoStock.
 *
 * Responsabilidades:
 * - Apresentar a identidade visual e a navegação;
 * - Solicitar dados ao AdministradorConsultaDAO, sem SQL na interface;
 * - Exibir indicadores, tabelas, filtros e mensagens de erro.
 *
 * As consultas são executadas em SwingWorker para não travar a janela.
 */
public class AdministradorGUI extends JFrame {

    // Paleta: verde principal solicitado e tons neutros com contraste.
    private static final Color PRIMARY = Color.decode("#173C38");
    private static final Color PRIMARY_HOVER = Color.decode("#24544E");
    private static final Color PRIMARY_LIGHT = Color.decode("#E5F0ED");
    private static final Color CANVAS = Color.decode("#F5F7F6");
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = Color.decode("#20312E");
    private static final Color MUTED = Color.decode("#64746F");
    private static final Color BORDER = Color.decode("#DFE7E3");
    private static final Color SIDEBAR_TEXT = Color.decode("#E8F2EF");

    private final AdministradorConsultaDAO consultas = new AdministradorConsultaDAO();
    private final Map<String, NavButton> menuButtons = new LinkedHashMap<>();
    private final JPanel content = new JPanel(new BorderLayout());
    private JLabel pageTitle;
    private String currentPage = "Início";
    private JTable currentTable;
    private JTextField searchField;
    private JLabel tableStatus;
    private TableRowSorter<DefaultTableModel> currentSorter;
    private long requestVersion = 0;

    public AdministradorGUI() {
        super("CoStock | Administração");
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
    }

    // Tipografia centralizada para preservar a hierarquia visual.
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
        brand.add(label("PAINEL ADMINISTRATIVO", 10, true, SIDEBAR_TEXT));
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = transparent();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(12, 12, 12, 12));

        for (String page : new String[]{"Início", "Usuários", "Cargos e Permissões",
                "Produtos", "Categorias", "Estoque", "Caixas", "Relatórios", "Configurações"}) {
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
        workspace.add(header, BorderLayout.NORTH);

        content.setBackground(CANVAS);
        content.setBorder(new EmptyBorder(26, 32, 26, 32));
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private void navigate(String page) {
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
        metrics.add(metric("USUÁRIOS", "—", "Cadastrados"));
        metrics.add(metric("PRODUTOS", "—", "Cadastrados"));
        metrics.add(metric("ESTOQUE", "—", "Abaixo do mínimo"));
        metrics.add(metric("CAIXAS", "—", "Total cadastrado"));
        body.add(metrics, BorderLayout.NORTH);

        JPanel shortcuts = transparent(new BorderLayout(0, 15));
        shortcuts.add(label("Acesso rápido", 19, true, TEXT), BorderLayout.NORTH);
        JPanel grid = transparent(new GridLayout(2, 3, 14, 14));
        for (String section : new String[]{"Usuários", "Produtos", "Categorias", "Estoque", "Caixas", "Relatórios"}) {
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

        // Busca os totais reais sem bloquear a interface gráfica.
        final long version = requestVersion;
        new SwingWorker<String[], Void>() {
            @Override protected String[] doInBackground() {
                String[] values = new String[4];
                try { values[0] = Integer.toString(consultas.contar("usuarios")); }
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
                    metrics.add(metric("USUÁRIOS", values[0], "Cadastrados"));
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

        // Um único listener acompanha a pesquisa durante toda a vida desta tela.
        // O filtro é reaplicado mesmo depois de atualizar os dados do banco.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });
        ActionButton refresh = new ActionButton("Atualizar listagem", true);
        refresh.addActionListener(e -> loadListing(section));
        tools.add(refresh, BorderLayout.EAST);
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
                    target.setDefaultRenderer(Object.class, new BodyRenderer());
                    for (int i = 0; i < target.getColumnCount(); i++) {
                        target.getColumnModel().getColumn(i).setPreferredWidth(165);
                    }
                    // Substitui o sorter da listagem após carregar o modelo atualizado.
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

    // Pesquisa por qualquer coluna, sem distinguir maiúsculas/minúsculas.
    // Pattern.quote impede que caracteres como +, (, [, * sejam tratados como regex.
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

    // Botão pintado manualmente: evita o tema do Windows apagar texto ou cores.
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

    // Botões de ação: fundo e texto com contraste explícito em todos os temas.
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

    // Cabeçalho com cores próprias, independente do Look and Feel.
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

    // Linhas alternadas e seleção visível melhoram a leitura de tabelas grandes.
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

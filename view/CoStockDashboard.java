package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * CoStock Dashboard
 *
 * Interface Swing baseada na referência visual fornecida.
 * - Header fixo: logo (esq.) + barra de pesquisa (centro) + botão de conta/login (dir.)
 * - Navbar lateral: botões de navegação reais entre as telas da plataforma
 * - Cards do painel: apenas para visualização (dados estáticos)
 */
public class CoStockDashboard extends JFrame {

    // =========================================================
    // CORES
    // =========================================================

    private static final Color ORANGE = Color.decode("#FF921C");
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.decode("#111111");

    private static final Color GREEN = Color.decode("#10B981");
    private static final Color GREEN_DARK = Color.decode("#0A9E70");

    private static final Color BLUE = Color.decode("#6366F1");
    private static final Color BLUE_DARK = Color.decode("#5558D1");

    private static final Color RED = Color.decode("#F43F5E");
    private static final Color RED_DARK = Color.decode("#D93652");

    private static final Color NAVY = Color.decode("#263D6E");
    private static final Color NAVY_DARK = Color.decode("#21345E");

    private static final Color PURPLE = Color.decode("#B838B6");
    private static final Color PURPLE_DARK = Color.decode("#9F319E");

    private static final Color YELLOW = Color.decode("#BDA20C");
    private static final Color YELLOW_DARK = Color.decode("#A48D08");

    private static final Color TEXT_GRAY = Color.decode("#777777");
    private static final Color BORDER_GRAY = Color.decode("#E5E5E5");

    // =========================================================
    // DADOS (estáticos, apenas para visualização)
    // =========================================================

    private static final String VENDAS_HOJE = "1.500,00";
    private static final String VENDAS_PERIODO = "5.500,00";
    private static final String CANCELAMENTOS = "300,00";

    private static final String FATURAMENTO = "11.500,00";
    private static final String QUANTIDADE_VENDAS = "4.500";
    private static final String TICKET_MEDIO = "320,10";

    // =========================================================
    // FONTES
    // =========================================================

    private static Font POPPINS;
    private static Font POPPINS_MEDIUM;
    private static Font POPPINS_SEMIBOLD;

    // =========================================================
    // DIMENSÕES
    // =========================================================

    private static final int SIDEBAR_WIDTH = 235;
    private static final int HEADER_HEIGHT = 82;

    // =========================================================
    // NAVEGAÇÃO
    // =========================================================

    private record MenuEntry(String key, String label) {
    }

    private static final MenuEntry[] MENU_ENTRIES = {
            new MenuEntry("painel", "Painel"),
            new MenuEntry("clientes", "Clientes"),
            new MenuEntry("frente_caixa", "Frente de caixa"),
            new MenuEntry("produtos", "Produtos"),
            new MenuEntry("servicos", "Servi\u00e7os"),
            new MenuEntry("compras", "Compras"),
            new MenuEntry("financeiro", "Financeiro"),
            new MenuEntry("estoque", "Estoque"),
            new MenuEntry("relatorios", "Relat\u00f3rios")
    };

    private static final String DEFAULT_PAGE = "painel";

    private final List<MenuButton> menuButtons = new ArrayList<>();
    private CardLayout contentLayout;
    private JPanel contentPanel;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public CoStockDashboard() {

        loadFonts();

        setTitle("CoStock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1280, 720);
        setMinimumSize(new Dimension(980, 620));

        setLocationRelativeTo(null);

        buildInterface();
    }

    // =========================================================
    // CONSTRUÇÃO
    // =========================================================

    private void buildInterface() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // =========================================================
    // SIDEBAR (navegação real entre telas)
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ORANGE);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(Box.createVerticalStrut(15));

        for (MenuEntry entry : MENU_ENTRIES) {

            boolean selected = entry.key().equals(DEFAULT_PAGE);

            MenuButton button = new MenuButton(
                    entry.key(),
                    entry.label(),
                    selected
            );

            menuButtons.add(button);
            menu.add(button);
        }

        sidebar.add(menu, BorderLayout.NORTH);

        JPanel logout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logout.setOpaque(false);
        logout.setBorder(new EmptyBorder(0, 42, 30, 0));

        JLabel sair = new JLabel("<html><u>Sair da Conta</u></html>");
        sair.setForeground(WHITE);
        sair.setFont(poppinsMedium(14));
        sair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sair.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                confirmLogout();
            }
        });

        logout.add(sair);
        sidebar.add(logout, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Atualiza o botão selecionado e troca a tela exibida no conteúdo.
     */
    private void selectPage(String key) {

        for (MenuButton button : menuButtons) {
            button.setSelectedState(button.getKey().equals(key));
        }

        if (contentLayout != null && contentPanel != null) {
            contentLayout.show(contentPanel, key);
        }
    }

    private void confirmLogout() {

        int result = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair da conta?",
                "Sair da conta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // =========================================================
    // BOTÃO DE MENU (sidebar)
    // =========================================================

    private class MenuButton extends JPanel {

        private final String key;
        private boolean selected;
        private final JLabel textLabel;

        MenuButton(String key, String label, boolean selected) {

            this.key = key;
            this.selected = selected;

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 28, 0, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(SIDEBAR_WIDTH, 53));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 53));

            textLabel = new JLabel(label);
            textLabel.setFont(poppinsMedium(15));
            textLabel.setForeground(selected ? ORANGE : WHITE);
            add(textLabel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectPage(MenuButton.this.key);
                }
            });
        }

        String getKey() {
            return key;
        }

        void setSelectedState(boolean selected) {
            this.selected = selected;
            textLabel.setForeground(selected ? ORANGE : WHITE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            if (selected) {

                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = getHeight();
                g.setColor(WHITE);
                g.fillRoundRect(0, 0, getWidth(), h, h, h);

                g.dispose();
            }

            super.paintComponent(graphics);
        }
    }

    // =========================================================
    // ÁREA PRINCIPAL
    // =========================================================

    private JPanel createMainArea() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(WHITE);

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createContentContainer(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createContentContainer() {

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(WHITE);

        for (MenuEntry entry : MENU_ENTRIES) {

            if (entry.key().equals(DEFAULT_PAGE)) {
                contentPanel.add(createDashboardScroll(), entry.key());
            } else {
                contentPanel.add(createPlaceholderPage(entry.label()), entry.key());
            }
        }

        contentLayout.show(contentPanel, DEFAULT_PAGE);

        return contentPanel;
    }

    private JPanel createPlaceholderPage(String title) {

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(WHITE);

        JPanel textWrapper = new JPanel();
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);
        heading.setFont(poppinsSemibold(22));
        heading.setForeground(BLACK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Esta se\u00e7\u00e3o ainda est\u00e1 em constru\u00e7\u00e3o.");
        subtitle.setFont(poppins(13));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        textWrapper.add(heading);
        textWrapper.add(Box.createVerticalStrut(8));
        textWrapper.add(subtitle);

        page.add(textWrapper);

        return page;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        // -----------------------------------------------------
        // LOGO (canto superior esquerdo)
        // -----------------------------------------------------

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(10, 20, 0, 0));

        LogoView logo = new LogoView();
        logo.setPreferredSize(new Dimension(46, 46));
        logoPanel.add(logo);

        JPanel logoText = new JPanel();
        logoText.setOpaque(false);
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CoStock");
        title.setFont(poppinsSemibold(21));
        title.setForeground(ORANGE);

        JLabel subtitle = new JLabel("Sistema Inteligente de Gest\u00e3o");
        subtitle.setFont(poppins(9));
        subtitle.setForeground(TEXT_GRAY);

        logoText.add(Box.createVerticalStrut(6));
        logoText.add(title);
        logoText.add(subtitle);

        logoPanel.add(logoText);
        header.add(logoPanel, BorderLayout.WEST);

        // -----------------------------------------------------
        // PESQUISA (centro)
        // -----------------------------------------------------

        JPanel searchArea = new JPanel(new GridBagLayout());
        searchArea.setOpaque(false);

        SearchField search = new SearchField();
        search.setPreferredSize(new Dimension(350, 37));

        searchArea.add(search);
        header.add(searchArea, BorderLayout.CENTER);

        // -----------------------------------------------------
        // CONTA / LOGIN (canto superior direito)
        // -----------------------------------------------------

        header.add(new UserAccountButton(), BorderLayout.EAST);

        JPanel line = new JPanel();
        line.setBackground(BORDER_GRAY);
        line.setPreferredSize(new Dimension(0, 1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(WHITE);
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(line, BorderLayout.SOUTH);

        return wrapper;
    }

    /**
     * Bloco de conta do usuário no header — funciona como botão de
     * login/conta: ao clicar, abre um menu com as ações disponíveis.
     */
    private class UserAccountButton extends JPanel {

        UserAccountButton() {

            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            setBorder(new EmptyBorder(17, 0, 0, 25));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            UserIcon userIcon = new UserIcon();
            userIcon.setPreferredSize(new Dimension(32, 32));
            add(userIcon);

            JPanel userText = new JPanel();
            userText.setOpaque(false);
            userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));

            JLabel name = new JLabel("Isabel Lopes");
            name.setFont(poppinsSemibold(12));
            name.setForeground(BLACK);

            JLabel role = new JLabel("Operador de Caixa");
            role.setFont(poppins(9));
            role.setForeground(Color.decode("#999999"));

            userText.add(name);
            userText.add(role);

            add(Box.createHorizontalStrut(6));
            add(userText);

            JPopupMenu menu = new JPopupMenu();
            menu.add(buildMenuItem("Meu perfil", e -> selectPage("clientes")));
            menu.add(buildMenuItem("Configura\u00e7\u00f5es", e -> {
            }));
            menu.addSeparator();
            menu.add(buildMenuItem("Sair da conta", e -> confirmLogout()));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    menu.show(UserAccountButton.this, getWidth() - 160, getHeight() + 5);
                }
            });
        }

        private JMenuItem buildMenuItem(String text, ActionListener listener) {
            JMenuItem item = new JMenuItem(text);
            item.setFont(poppinsMedium(12));
            item.addActionListener(listener);
            return item;
        }
    }

    // =========================================================
    // DASHBOARD COM SCROLL
    // =========================================================

    private JScrollPane createDashboardScroll() {

        JPanel dashboard = new JPanel(new GridBagLayout());
        dashboard.setBackground(WHITE);
        dashboard.setBorder(new EmptyBorder(27, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // -----------------------------------------------------
        // GESTÃO DE VENDAS
        // -----------------------------------------------------

        JLabel vendas = new JLabel("Gest\u00e3o de vendas");
        vendas.setFont(poppinsSemibold(25));
        vendas.setForeground(BLACK);
        dashboard.add(vendas, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 0, 0);

        JPanel salesCards = createCardsPanel();

        salesCards.add(new DashboardCard(
                GREEN, GREEN_DARK,
                "Vendas de hoje", VENDAS_HOJE, "R$",
                "Ticket m\u00e9dio: R$500,00 \u2013 Total de vendas: 3",
                CardIcon.CART
        ));

        salesCards.add(new DashboardCard(
                BLUE, BLUE_DARK,
                "Vendas per\u00edodo", VENDAS_PERIODO, "R$",
                "Ticket m\u00e9dio: R$500,00 \u2013 Total de vendas: 11",
                CardIcon.BARS
        ));

        salesCards.add(new DashboardCard(
                RED, RED_DARK,
                "Cancelamentos", CANCELAMENTOS, "R$",
                "Ticket m\u00e9dio: R$150,00 \u2013 Total de vendas: 2",
                CardIcon.CLOSE
        ));

        dashboard.add(salesCards, gbc);

        // -----------------------------------------------------
        // GESTÃO FINANCEIRA
        // -----------------------------------------------------

        gbc.gridy++;
        gbc.insets = new Insets(27, 0, 0, 0);

        JLabel financeiro = new JLabel("Gest\u00e3o Financeira");
        financeiro.setFont(poppinsSemibold(25));
        financeiro.setForeground(BLACK);
        dashboard.add(financeiro, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 0, 0);

        JPanel financeCards = createCardsPanel();

        financeCards.add(new DashboardCard(
                NAVY, NAVY_DARK,
                "Faturamento", FATURAMENTO, "R$",
                "60% maior em rela\u00e7\u00e3o ao m\u00eas anterior",
                CardIcon.CART
        ));

        financeCards.add(new DashboardCard(
                PURPLE, PURPLE_DARK,
                "Qtd Vendas", QUANTIDADE_VENDAS, "UN",
                "32% maior em rela\u00e7\u00e3o ao m\u00eas anterior",
                CardIcon.BARS
        ));

        financeCards.add(new DashboardCard(
                YELLOW, YELLOW_DARK,
                "Ticket m\u00e9dio", TICKET_MEDIO, "R$",
                "16% menor em rela\u00e7\u00e3o ao m\u00eas anterior",
                CardIcon.COINS
        ));

        dashboard.add(financeCards, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        dashboard.add(Box.createVerticalGlue(), gbc);

        JScrollPane scroll = new JScrollPane(dashboard);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    private JPanel createCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        return panel;
    }

    // =========================================================
    // CARD (somente visualização)
    // =========================================================

    private static class DashboardCard extends JPanel {

        private final Color background;
        private final Color footerColor;

        private final String title;
        private final String value;
        private final String unit;
        private final String footer;

        private final CardIcon icon;

        DashboardCard(Color background, Color footerColor, String title, String value,
                      String unit, String footer, CardIcon icon) {

            this.background = background;
            this.footerColor = footerColor;
            this.title = title;
            this.value = value;
            this.unit = unit;
            this.footer = footer;
            this.icon = icon;

            setOpaque(false);
            setPreferredSize(new Dimension(300, 145));
            setMinimumSize(new Dimension(200, 130));
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int radius = 15;

            // CARD
            g.setColor(background);
            g.fillRoundRect(0, 0, w, h, radius, radius);

            // TÍTULO
            g.setColor(WHITE);
            g.setFont(poppinsMedium(15));
            g.drawString(title, 16, 30);

            // UNIDADE + VALOR (mesma linha, como no protótipo)
            g.setFont(poppins(11));
            FontMetrics unitFm = g.getFontMetrics();
            int unitWidth = unitFm.stringWidth(unit);
            g.drawString(unit, 16, 78);

            g.setFont(poppinsSemibold(28));
            g.drawString(value, 16 + unitWidth + 4, 78);

            // ÍCONE (marca d'água no canto superior direito)
            int iconSize = Math.min(62, Math.max(45, w / 6));
            int iconX = w - iconSize - 22;
            int iconY = 19;

            paintCardIcon(g, icon, iconX, iconY, iconSize);

            // FOOTER
            int footerHeight = 31;

            Path2D footerShape = new Path2D.Double();
            footerShape.moveTo(0, h - footerHeight);
            footerShape.lineTo(w, h - footerHeight);
            footerShape.lineTo(w, h - radius);
            footerShape.quadTo(w, h, w - radius, h);
            footerShape.lineTo(radius, h);
            footerShape.quadTo(0, h, 0, h - radius);
            footerShape.closePath();

            g.setColor(footerColor);
            g.fill(footerShape);

            g.setColor(WHITE);
            g.setFont(poppinsMedium(8.5f));
            FontMetrics fm = g.getFontMetrics();

            String footerText = fitText(g, footer, w - 18);
            int textWidth = fm.stringWidth(footerText);
            int x = Math.max(9, (w - textWidth) / 2);

            g.drawString(footerText, x, h - 11);

            g.dispose();
        }

        private static String fitText(Graphics2D g, String text, int maxWidth) {

            if (g.getFontMetrics().stringWidth(text) <= maxWidth) {
                return text;
            }

            String result = text;

            while (result.length() > 3
                    && g.getFontMetrics().stringWidth(result + "...") > maxWidth) {
                result = result.substring(0, result.length() - 1);
            }

            return result + "...";
        }
    }

    // =========================================================
    // ÍCONES DOS CARDS
    // =========================================================

    private enum CardIcon {
        CART, BARS, CLOSE, COINS
    }

    private static void paintCardIcon(Graphics2D g, CardIcon icon, int x, int y, int size) {

        Graphics2D copy = (Graphics2D) g.create();

        // ícone em marca d'água clara sobre o card (branco translúcido)
        copy.setColor(new Color(255, 255, 255, 45));

        copy.setStroke(new BasicStroke(
                Math.max(4f, size / 14f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        double scale = size / 100.0;
        copy.translate(x, y);
        copy.scale(scale, scale);

        switch (icon) {
            case CART -> drawCart(copy);
            case BARS -> drawBars(copy);
            case CLOSE -> drawClose(copy);
            case COINS -> drawCoins(copy);
        }

        copy.dispose();
    }

    private static void drawCart(Graphics2D g) {
        g.drawLine(4, 12, 25, 12);
        g.drawLine(25, 12, 34, 51);
        g.drawLine(34, 51, 90, 51);
        g.drawLine(90, 51, 103, 20);
        g.drawLine(22, 20, 103, 20);
        g.fillOval(37, 58, 12, 12);
        g.fillOval(78, 58, 12, 12);
    }

    private static void drawBars(Graphics2D g) {
        g.fillRoundRect(4, 34, 20, 56, 4, 4);
        g.fillRoundRect(40, 19, 20, 71, 4, 4);
        g.fillRoundRect(76, 4, 20, 86, 4, 4);
    }

    private static void drawClose(Graphics2D g) {
        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(25, 25, 75, 75);
        g.drawLine(75, 25, 25, 75);
    }

    private static void drawCoins(Graphics2D g) {
        g.setStroke(new BasicStroke(5));
        g.drawOval(12, 8, 40, 19);
        g.drawOval(12, 19, 40, 19);
        g.drawOval(12, 30, 40, 19);
        g.drawOval(46, 34, 40, 19);
        g.drawOval(46, 45, 40, 19);
        g.drawOval(46, 56, 40, 19);
    }

    // =========================================================
    // LOGO
    // =========================================================

    private static class LogoView extends JPanel {

        LogoView() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Círculo de fundo (engrenagem estilizada)
            g.setColor(ORANGE);
            g.fillOval(4, 4, 38, 38);

            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 8; i++) {
                double angle = Math.toRadians(i * 45);
                int cx = 23, cy = 23, r1 = 19, r2 = 24;
                int x1 = cx + (int) (Math.cos(angle) * r1);
                int y1 = cy + (int) (Math.sin(angle) * r1);
                int x2 = cx + (int) (Math.cos(angle) * r2);
                int y2 = cy + (int) (Math.sin(angle) * r2);
                g.drawLine(x1, y1, x2, y2);
            }

            // Lâmpada (ideia / inteligência do sistema)
            g.setColor(WHITE);
            g.fillOval(13, 10, 20, 20);

            g.setColor(ORANGE);
            g.fillRoundRect(18, 26, 10, 4, 2, 2);
            g.fillRoundRect(19, 31, 8, 4, 2, 2);

            g.dispose();
        }
    }

    // =========================================================
    // USUÁRIO
    // =========================================================

    private static class UserIcon extends JPanel {

        UserIcon() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(ORANGE);
            g.fillOval(10, 2, 13, 13);
            g.fillRoundRect(4, 16, 25, 13, 10, 10);

            g.dispose();
        }
    }

    // =========================================================
    // PESQUISA
    // =========================================================

    private static class SearchField extends JPanel {

        private static final String PLACEHOLDER = "Pesquisar...";

        SearchField() {

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new RoundedBorder(ORANGE, 1, 20));

            SearchIcon icon = new SearchIcon();
            icon.setPreferredSize(new Dimension(37, 35));
            add(icon, BorderLayout.WEST);

            JTextField field = new JTextField();
            field.setText(PLACEHOLDER);
            field.setFont(poppins(11));
            field.setForeground(TEXT_GRAY);
            field.setOpaque(false);
            field.setBorder(null);

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(PLACEHOLDER)) {
                        field.setText("");
                        field.setForeground(BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isBlank()) {
                        field.setText(PLACEHOLDER);
                        field.setForeground(TEXT_GRAY);
                    }
                }
            });

            add(field, BorderLayout.CENTER);
        }
    }

    private static class SearchIcon extends JPanel {

        SearchIcon() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(TEXT_GRAY);
            g.setStroke(new BasicStroke(2));
            g.drawOval(9, 8, 12, 12);
            g.drawLine(19, 19, 25, 25);

            g.dispose();
        }
    }

    // =========================================================
    // COMPONENTES AUXILIARES
    // =========================================================

    private static class RoundedBorder implements javax.swing.border.Border {

        private final Color color;
        private final int thickness;
        private final int radius;

        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(3, 5, 3, 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(color);
            g.setStroke(new BasicStroke(thickness));
            g.drawRoundRect(x + 1, y + 1, width - 2, height - 2, radius, radius);

            g.dispose();
        }
    }

    // =========================================================
    // FONTES
    // =========================================================

    private static void loadFonts() {
        POPPINS = loadFont("/fonts/Poppins-Regular.ttf", Font.PLAIN);
        POPPINS_MEDIUM = loadFont("/fonts/Poppins-Medium.ttf", Font.PLAIN);
        POPPINS_SEMIBOLD = loadFont("/fonts/Poppins-SemiBold.ttf", Font.BOLD);
    }

    private static Font loadFont(String path, int style) {

        try {
            InputStream stream = CoStockDashboard.class.getResourceAsStream(path);

            if (stream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                return font.deriveFont(style, 14f);
            }

        } catch (Exception ignored) {
        }

        // Sem os arquivos Poppins-*.ttf no classpath, cai para a melhor
        // fonte do sistema disponível (evita fontes decorativas estranhas
        // que o "SansSerif" genérico pode mapear em algumas máquinas).
        return new Font(fallbackFamily(), style, 14);
    }

    private static String fallbackFamily() {

        String[] preferred = {
                "Segoe UI", "Helvetica Neue", "Arial",
                "Liberation Sans", "Noto Sans", "Verdana"
        };

        java.util.Set<String> available = new java.util.HashSet<>(
                java.util.Arrays.asList(
                        GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getAvailableFontFamilyNames()
                )
        );

        for (String family : preferred) {
            if (available.contains(family)) {
                return family;
            }
        }

        return Font.SANS_SERIF;
    }

    private static Font poppins(float size) {
        return POPPINS.deriveFont(Font.PLAIN, size);
    }

    private static Font poppinsMedium(float size) {
        return POPPINS_MEDIUM.deriveFont(Font.PLAIN, size);
    }

    private static Font poppinsSemibold(float size) {
        return POPPINS_SEMIBOLD.deriveFont(Font.BOLD, size);
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            CoStockDashboard dashboard = new CoStockDashboard();
            dashboard.setVisible(true);
        });
    }
}
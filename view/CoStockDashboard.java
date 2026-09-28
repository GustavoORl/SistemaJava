package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * CoStock Dashboard
 *
 * Estrutura:
 *  - Header de ponta a ponta: logo (esquerda), pesquisa (centro), conta/login (direita)
 *  - Navbar lateral: botoes de navegacao da plataforma (sem icones)
 *  - Cards: apenas visualizacao
 *
 * Recursos esperados no classpath (pasta resources/):
 *  - /images/logo.png
 *  - /fonts/Poppins-Regular.ttf, Poppins-Medium.ttf, Poppins-SemiBold.ttf
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

    private static final Color TEXT_GRAY = Color.decode("#666666");
    private static final Color PLACEHOLDER_GRAY = Color.decode("#8A8A8A");
    private static final Color BORDER_GRAY = Color.decode("#E3E3E3");
    private static final Color SEARCH_BG = Color.decode("#FFF7EE");
    private static final Color HOVER_GRAY = Color.decode("#F4F4F4");

    // =========================================================
    // DADOS (estaticos, apenas para visualizacao)
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
    // DIMENSOES
    // =========================================================

    private static final int SIDEBAR_WIDTH = 250;
    private static final int HEADER_HEIGHT = 100;
    private static final int HEADER_SIDE_WIDTH = 350;
    private static final int LOGO_HEIGHT = 70;
    private static final int MENU_ITEM_HEIGHT = 62;

    // =========================================================
    // NAVEGACAO
    // =========================================================

    private static final class MenuEntry {

        private final String key;
        private final String label;

        MenuEntry(String key, String label) {
            this.key = key;
            this.label = label;
        }

        String key() {
            return key;
        }

        String label() {
            return label;
        }
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

        setSize(1366, 768);
        setMinimumSize(new Dimension(1140, 680));

        setLocationRelativeTo(null);

        buildInterface();

        // Abre ocupando a tela inteira (mais espaco para fontes maiores)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // =========================================================
    // CONSTRUCAO
    // =========================================================

    private void buildInterface() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);

        // Header ocupa a largura toda, por cima da navbar
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContentContainer(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // =========================================================
    // HEADER (de ponta a ponta)
    // =========================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        // ---- Logo (canto superior esquerdo) ----
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(HEADER_SIDE_WIDTH, HEADER_HEIGHT));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.weightx = 1;
        lc.insets = new Insets(0, 26, 0, 0);
        left.add(new LogoView(), lc);

        // ---- Pesquisa (centro) ----
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(new SearchField(this::searchAndNavigate));

        // ---- Conta / login (canto superior direito) ----
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(HEADER_SIDE_WIDTH, HEADER_HEIGHT));

        GridBagConstraints rc = new GridBagConstraints();
        rc.anchor = GridBagConstraints.EAST;
        rc.weightx = 1;
        rc.insets = new Insets(0, 0, 0, 22);
        right.add(new UserAccountButton(), rc);

        header.add(left, BorderLayout.WEST);
        header.add(center, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);

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
     * Pesquisa simples: leva para a secao da navbar cujo nome combina
     * com o texto digitado (sem diferenciar maiusculas nem acentos).
     */
    private void searchAndNavigate(String query) {

        String q = normalize(query);
        if (q.isEmpty()) {
            return;
        }

        for (MenuEntry entry : MENU_ENTRIES) {
            if (normalize(entry.label()).contains(q)) {
                selectPage(entry.key());
                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Nenhum resultado para \"" + query + "\".",
                "Pesquisar",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String normalize(String text) {
        String n = Normalizer.normalize(text, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").trim().toLowerCase();
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ORANGE);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(Box.createVerticalStrut(18));

        for (MenuEntry entry : MENU_ENTRIES) {

            MenuButton button = new MenuButton(
                    entry.key(),
                    entry.label(),
                    entry.key().equals(DEFAULT_PAGE)
            );

            menuButtons.add(button);
            menu.add(button);
        }

        sidebar.add(menu, BorderLayout.NORTH);

        JPanel logout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logout.setOpaque(false);
        logout.setBorder(new EmptyBorder(0, 32, 30, 0));

        JLabel sair = new JLabel("<html><u>Sair da Conta</u></html>");
        sair.setForeground(WHITE);
        sair.setFont(poppinsMedium(17));
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

    private void showComingSoon(String what) {
        JOptionPane.showMessageDialog(
                this,
                what + " ainda est\u00e1 em constru\u00e7\u00e3o.",
                "CoStock",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // BOTAO DE MENU (sidebar) - sem icones, com hover e teclado
    // =========================================================

    private class MenuButton extends JPanel {

        private static final int RIGHT_MARGIN = 18;

        private final String key;
        private boolean selected;
        private boolean hover;
        private final JLabel textLabel;

        MenuButton(String key, String label, boolean selected) {

            this.key = key;
            this.selected = selected;

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 32, 0, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(SIDEBAR_WIDTH, MENU_ITEM_HEIGHT));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, MENU_ITEM_HEIGHT));
            setFocusable(true);

            textLabel = new JLabel(label);
            textLabel.setFont(poppinsMedium(19));
            textLabel.setForeground(selected ? ORANGE : WHITE);
            add(textLabel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectPage(MenuButton.this.key);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });

            Action activate = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectPage(MenuButton.this.key);
                }
            };
            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "activate");
            getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "activate");
            getActionMap().put("activate", activate);
        }

        String getKey() {
            return key;
        }

        void setSelectedState(boolean selected) {
            this.selected = selected;
            textLabel.setForeground(selected ? ORANGE : WHITE);
            repaint();
        }

        /** Pilula encostada na borda esquerda, arredondada so na direita. */
        private Shape pill() {
            int h = getHeight() - 8;
            return new RoundRectangle2D.Double(
                    -h, 4, getWidth() + h - RIGHT_MARGIN, h, h, h
            );
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g.setColor(WHITE);
                g.fill(pill());
            } else if (hover) {
                g.setColor(new Color(255, 255, 255, 55));
                g.fill(pill());
            }

            if (isFocusOwner()) {
                g.setColor(selected ? ORANGE : WHITE);
                g.setStroke(new BasicStroke(2f));
                g.draw(pill());
            }

            g.dispose();
            super.paintComponent(graphics);
        }
    }

    // =========================================================
    // CONTEUDO
    // =========================================================

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
        heading.setFont(poppinsSemibold(32));
        heading.setForeground(BLACK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Esta se\u00e7\u00e3o ainda est\u00e1 em constru\u00e7\u00e3o.");
        subtitle.setFont(poppins(18));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        textWrapper.add(heading);
        textWrapper.add(Box.createVerticalStrut(10));
        textWrapper.add(subtitle);

        page.add(textWrapper);

        return page;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private JScrollPane createDashboardScroll() {

        JPanel dashboard = new ScrollableDashboardPanel(new GridBagLayout());
        dashboard.setBackground(WHITE);
        dashboard.setBorder(new EmptyBorder(28, 32, 30, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // ---- Gestao de vendas ----
        dashboard.add(sectionTitle("Gest\u00e3o de vendas"), gbc);

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

        // ---- Gestao financeira ----
        gbc.gridy++;
        gbc.insets = new Insets(32, 0, 0, 0);
        dashboard.add(sectionTitle("Gest\u00e3o Financeira"), gbc);

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
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        return scroll;
    }

    /** Painel que acompanha a largura da janela (cards encolhem em vez de gerar rolagem lateral). */
    private static class ScrollableDashboardPanel extends JPanel implements Scrollable {

        ScrollableDashboardPanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(40, visibleRect.height - 40);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            Container parent = getParent();
            return parent instanceof JViewport
                    && parent.getHeight() > getPreferredSize().height;
        }
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(poppinsSemibold(32));
        label.setForeground(BLACK);
        return label;
    }

    private JPanel createCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        return panel;
    }

    // =========================================================
    // CARD (somente visualizacao)
    // =========================================================

    private static class DashboardCard extends JPanel {

        private static final int CORNER = 16;
        private static final int FOOTER_HEIGHT = 46;
        private static final int PAD = 24;

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
            // Largura preferida baixa de proposito: o GridLayout estica os cards.
            // (Se a soma passar da largura da janela, o GridBagLayout achata a altura.)
            setPreferredSize(new Dimension(240, 190));
            setMinimumSize(new Dimension(220, 190));
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            int w = getWidth();
            int h = getHeight();

            RoundRectangle2D cardShape = new RoundRectangle2D.Double(0, 0, w, h, CORNER * 2, CORNER * 2);

            // Fundo do card
            g.setColor(background);
            g.fill(cardShape);

            // Icone (marca d'agua, desenhado ANTES do texto)
            int iconSize = Math.min(64, Math.max(48, w / 5));
            paintCardIcon(g, icon, w - iconSize - 22, 18, iconSize);

            // Titulo
            g.setColor(WHITE);
            g.setFont(poppinsMedium(21));
            g.drawString(title, PAD, 42);

            // Unidade + valor na mesma linha
            int contentBottom = h - FOOTER_HEIGHT;
            int baseline = contentBottom - 30;

            Font unitFont = poppinsMedium(19);
            Font valueFont = poppinsSemibold(46);
            int maxWidth = w - PAD * 2;

            for (float size = 46f; size >= 30f; size -= 1f) {
                valueFont = poppinsSemibold(size);
                int total = g.getFontMetrics(unitFont).stringWidth(unit) + 6
                        + g.getFontMetrics(valueFont).stringWidth(value);
                if (total <= maxWidth) {
                    break;
                }
            }

            FontRenderContext frc = g.getFontRenderContext();
            double valueH = new TextLayout("0", valueFont, frc).getBounds().getHeight();
            double unitH = new TextLayout("R", unitFont, frc).getBounds().getHeight();

            int unitWidth = g.getFontMetrics(unitFont).stringWidth(unit);

            g.setFont(unitFont);
            g.drawString(unit, PAD, (int) Math.round(baseline - (valueH - unitH)));

            g.setFont(valueFont);
            g.drawString(value, PAD + unitWidth + 6, baseline);

            // Rodape (mesma curvatura do card, via intersecao)
            Area footerArea = new Area(cardShape);
            footerArea.intersect(new Area(new Rectangle2D.Double(0, h - FOOTER_HEIGHT, w, FOOTER_HEIGHT)));
            g.setColor(footerColor);
            g.fill(footerArea);

            // Texto do rodape: reduz a fonte antes de cortar com "..."
            g.setColor(WHITE);
            Font footerFont = poppinsMedium(14.5f);
            for (float size = 14.5f; size >= 11f; size -= 0.5f) {
                footerFont = poppinsMedium(size);
                if (g.getFontMetrics(footerFont).stringWidth(footer) <= w - 24) {
                    break;
                }
            }
            g.setFont(footerFont);

            FontMetrics fm = g.getFontMetrics();
            String footerText = fitText(fm, footer, w - 24);
            int textX = (w - fm.stringWidth(footerText)) / 2;
            int textY = h - FOOTER_HEIGHT + (FOOTER_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(footerText, textX, textY);

            g.dispose();
        }

        private static String fitText(FontMetrics fm, String text, int maxWidth) {

            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }

            String result = text;

            while (result.length() > 3 && fm.stringWidth(result + "...") > maxWidth) {
                result = result.substring(0, result.length() - 1);
            }

            return result + "...";
        }
    }

    // =========================================================
    // ICONES DOS CARDS (marca d'agua escura, como no prototipo)
    // =========================================================

    private enum CardIcon {
        CART, BARS, CLOSE, COINS
    }

    private static void paintCardIcon(Graphics2D g, CardIcon icon, int x, int y, int size) {

        Graphics2D copy = (Graphics2D) g.create();

        double scale = size / 100.0;
        copy.translate(x, y);
        copy.scale(scale, scale);

        switch (icon) {
            case CART:
                drawCart(copy);
                break;
            case BARS:
                drawBars(copy);
                break;
            case CLOSE:
                drawClose(copy);
                break;
            case COINS:
                drawCoins(copy);
                break;
            default:
                break;
        }

        copy.dispose();
    }

    private static final Color ICON_DARK = new Color(0, 0, 0, 42);
    private static final Color ICON_LIGHT = new Color(255, 255, 255, 70);

    private static void drawCart(Graphics2D g) {

        Stroke stroke = new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        Path2D basket = new Path2D.Double();
        basket.moveTo(28, 20);
        basket.lineTo(100, 20);
        basket.lineTo(89, 54);
        basket.lineTo(36, 54);
        basket.closePath();

        Area area = new Area(basket);
        area.add(new Area(stroke.createStrokedShape(new Line2D.Double(4, 8, 22, 8))));
        area.add(new Area(stroke.createStrokedShape(new Line2D.Double(22, 8, 36, 54))));
        area.add(new Area(new Ellipse2D.Double(38, 63, 15, 15)));
        area.add(new Area(new Ellipse2D.Double(78, 63, 15, 15)));

        g.setColor(ICON_DARK);
        g.fill(area);
    }

    private static void drawBars(Graphics2D g) {
        g.setColor(ICON_DARK);
        g.fillRoundRect(2, 38, 26, 62, 5, 5);
        g.fillRoundRect(37, 22, 26, 78, 5, 5);
        g.fillRoundRect(72, 0, 26, 100, 5, 5);
    }

    private static void drawClose(Graphics2D g) {

        g.setColor(ICON_DARK);
        g.fill(new Ellipse2D.Double(0, 0, 100, 100));

        Stroke stroke = new BasicStroke(11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Area cross = new Area(stroke.createStrokedShape(new Line2D.Double(30, 30, 70, 70)));
        cross.add(new Area(stroke.createStrokedShape(new Line2D.Double(70, 30, 30, 70))));

        g.setColor(ICON_LIGHT);
        g.fill(cross);
    }

    private static void drawCoins(Graphics2D g) {

        Area coins = new Area();

        for (int i = 0; i < 4; i++) {
            coins.add(new Area(new Ellipse2D.Double(4, 4 + i * 15, 46, 24)));
        }

        for (int i = 0; i < 3; i++) {
            coins.add(new Area(new Ellipse2D.Double(52, 38 + i * 15, 46, 24)));
        }

        g.setColor(ICON_DARK);
        g.fill(coins);
    }

    // =========================================================
    // LOGO (imagem anexada ao projeto)
    // =========================================================

    private static class LogoView extends JPanel {

        private final BufferedImage image;
        private final int drawWidth;

        LogoView() {

            setOpaque(false);

            image = loadImage("/images/logo.png");

            if (image != null) {
                drawWidth = Math.round(image.getWidth() * (float) LOGO_HEIGHT / image.getHeight());
            } else {
                drawWidth = 300;
            }

            setPreferredSize(new Dimension(drawWidth, LOGO_HEIGHT));
            setMinimumSize(new Dimension(drawWidth, LOGO_HEIGHT));
            setToolTipText("CoStock - Sistema Inteligente de Gest\u00e3o");
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
                g.drawImage(image, 0, 0, drawWidth, LOGO_HEIGHT, null);
            } else {
                // Fallback caso a imagem nao seja encontrada
                g.setColor(ORANGE);
                g.setFont(poppinsSemibold(34));
                g.drawString("CoStock", 4, 40);
                g.setColor(TEXT_GRAY);
                g.setFont(poppinsMedium(14));
                g.drawString("Sistema Inteligente de Gest\u00e3o", 4, 62);
            }

            g.dispose();
        }
    }

    // =========================================================
    // CONTA / LOGIN
    // =========================================================

    private class UserAccountButton extends JPanel {

        private boolean hover;

        UserAccountButton() {

            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
            setBorder(new EmptyBorder(10, 8, 10, 8));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Abrir menu da conta");

            UserIcon userIcon = new UserIcon();
            userIcon.setPreferredSize(new Dimension(44, 44));
            add(userIcon);

            JPanel userText = new JPanel();
            userText.setOpaque(false);
            userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));

            JLabel name = new JLabel("Isabel Lopes");
            name.setFont(poppinsSemibold(17));
            name.setForeground(BLACK);

            JLabel role = new JLabel("Operador de Caixa");
            role.setFont(poppins(14));
            role.setForeground(TEXT_GRAY);

            userText.add(name);
            userText.add(role);
            add(userText);

            ChevronIcon chevron = new ChevronIcon();
            chevron.setPreferredSize(new Dimension(16, 44));
            add(chevron);

            JPopupMenu menu = new JPopupMenu();
            menu.add(buildMenuItem("Meu perfil", e -> showComingSoon("Meu perfil")));
            menu.add(buildMenuItem("Configura\u00e7\u00f5es", e -> showComingSoon("Configura\u00e7\u00f5es")));
            menu.addSeparator();
            menu.add(buildMenuItem("Sair da conta", e -> confirmLogout()));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    menu.show(
                            UserAccountButton.this,
                            getWidth() - menu.getPreferredSize().width,
                            getHeight() + 2
                    );
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        private JMenuItem buildMenuItem(String text, ActionListener listener) {
            JMenuItem item = new JMenuItem(text);
            item.setFont(poppinsMedium(16));
            item.setPreferredSize(new Dimension(220, 42));
            item.addActionListener(listener);
            return item;
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            if (hover) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(HOVER_GRAY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g.dispose();
            }

            super.paintComponent(graphics);
        }
    }

    private static class UserIcon extends JPanel {

        UserIcon() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g.setColor(ORANGE);
            g.fillOval(w / 2 - 9, 5, 18, 18);
            g.fillRoundRect(w / 2 - 17, 26, 34, 15, 15, 15);

            g.dispose();
        }
    }

    private static class ChevronIcon extends JPanel {

        ChevronIcon() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            g.setColor(TEXT_GRAY);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(cx - 5, cy - 2, cx, cy + 3);
            g.drawLine(cx, cy + 3, cx + 5, cy - 2);

            g.dispose();
        }
    }

    // =========================================================
    // PESQUISA
    // =========================================================

    private static class SearchField extends JPanel {

        private static final String PLACEHOLDER = "Pesquisar...";

        private boolean focused;

        SearchField(java.util.function.Consumer<String> onSearch) {

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 0, 0, 20));
            setPreferredSize(new Dimension(460, 50));
            setMinimumSize(new Dimension(280, 50));

            SearchIcon icon = new SearchIcon();
            icon.setPreferredSize(new Dimension(56, 50));
            add(icon, BorderLayout.WEST);

            JTextField field = new JTextField() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    super.paintComponent(graphics);

                    if (getText().isEmpty()) {
                        Graphics2D g = (Graphics2D) graphics.create();
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g.setColor(PLACEHOLDER_GRAY);
                        g.setFont(getFont());
                        FontMetrics fm = g.getFontMetrics();
                        Insets in = getInsets();
                        g.drawString(PLACEHOLDER, in.left,
                                (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                        g.dispose();
                    }
                }
            };

            field.setFont(poppins(17));
            field.setForeground(BLACK);
            field.setCaretColor(ORANGE);
            field.setOpaque(false);
            field.setBorder(null);
            field.setToolTipText("Digite o nome de uma se\u00e7\u00e3o e pressione Enter");

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });

            field.addActionListener(e -> onSearch.accept(field.getText()));

            add(field, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            float stroke = focused ? 3f : 2f;

            g.setColor(SEARCH_BG);
            g.fillRoundRect(0, 0, w, h, h, h);

            g.setColor(ORANGE);
            g.setStroke(new BasicStroke(stroke));
            g.drawRoundRect(1, 1, w - 3, h - 3, h, h);

            g.dispose();
            super.paintComponent(graphics);
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

            int cx = getWidth() / 2 + 2;
            int cy = getHeight() / 2;

            g.setColor(TEXT_GRAY);
            g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawOval(cx - 11, cy - 11, 17, 17);
            g.drawLine(cx + 4, cy + 4, cx + 10, cy + 10);

            g.dispose();
        }
    }

    // =========================================================
    // RECURSOS (imagem e fontes)
    // =========================================================

    private static BufferedImage loadImage(String resourcePath) {

        try (InputStream stream = CoStockDashboard.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
        } catch (IOException ignored) {
        }

        // Plano B: procura a pasta resources/ ao lado de onde o programa foi iniciado
        String[] candidates = {
                "resources" + resourcePath,
                "src" + resourcePath,
                resourcePath.substring(1)
        };

        for (String candidate : candidates) {
            try {
                File file = new File(candidate);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    private static void loadFonts() {
        POPPINS = loadFont("/fonts/Poppins-Regular.ttf", Font.PLAIN);
        POPPINS_MEDIUM = loadFont("/fonts/Poppins-Medium.ttf", Font.PLAIN);
        POPPINS_SEMIBOLD = loadFont("/fonts/Poppins-SemiBold.ttf", Font.BOLD);
    }

    private static Font loadFont(String path, int style) {

        try (InputStream stream = CoStockDashboard.class.getResourceAsStream(path)) {
            if (stream != null) {
                return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(style, 14f);
            }
        } catch (Exception ignored) {
        }

        try {
            File file = new File("resources" + path);
            if (file.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(style, 14f);
            }
        } catch (Exception ignored) {
        }

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

        // Texto suavizado (mais legivel) em todo o Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

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
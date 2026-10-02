package view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Tela principal do Administrador do sistema CoStock.
 *
 * Nesta versão a tela possui apenas o design.
 * As funções serão conectadas posteriormente.
 */
public class AdministradorGUI extends JFrame {

    // =========================================================
    // CORES
    // =========================================================

    private static final Color GREEN = new Color(39, 174, 96);
    private static final Color GREEN_DARK = new Color(30, 145, 78);

    private static final Color BACKGROUND = new Color(247, 247, 247);
    private static final Color WHITE = Color.WHITE;

    private static final Color DARK = new Color(56, 56, 56);
    private static final Color TEXT_GRAY = new Color(110, 110, 110);
    private static final Color LIGHT_GRAY = new Color(235, 235, 235);

    // Cores secundárias
    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(142, 68, 173);
    private static final Color RED = new Color(231, 76, 60);

    // =========================================================
    // COMPONENTES
    // =========================================================

    private JPanel painelPrincipal;
    private JPanel painelConteudo;

    private JButton btnInicio;
    private JButton btnUsuarios;
    private JButton btnCargos;
    private JButton btnProdutos;
    private JButton btnCategorias;
    private JButton btnEstoque;
    private JButton btnCaixas;
    private JButton btnRelatorios;
    private JButton btnConfiguracoes;
    private JButton btnSair;

    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public AdministradorGUI() {

        configurarJanela();
        criarInterface();
    }

    // =========================================================
    // CONFIGURAÇÃO DA JANELA
    // =========================================================

    private void configurarJanela() {

        setTitle("CoStock - Administrador");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1280, 720);

        setMinimumSize(
                new Dimension(1050, 650)
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                BACKGROUND
        );
    }

    // =========================================================
    // INTERFACE PRINCIPAL
    // =========================================================

    private void criarInterface() {

        painelPrincipal = new JPanel(
                new BorderLayout()
        );

        painelPrincipal.setBackground(
                BACKGROUND
        );

        setContentPane(
                painelPrincipal
        );

        // Sidebar
        painelPrincipal.add(
                criarSidebar(),
                BorderLayout.WEST
        );

        // Conteúdo
        painelPrincipal.add(
                criarConteudo(),
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel criarSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
                new Dimension(235, 0)
        );

        sidebar.setBackground(
                GREEN
        );

        sidebar.setLayout(
                new BorderLayout()
        );

        // =====================================================
        // TOPO
        // =====================================================

        JPanel topo = new JPanel();

        topo.setOpaque(false);

        topo.setLayout(
                new BoxLayout(
                        topo,
                        BoxLayout.Y_AXIS
                )
        );

        topo.setBorder(
                new EmptyBorder(
                        25,
                        20,
                        15,
                        20
                )
        );

        JLabel lblLogo = new JLabel(
                "CoStock"
        );

        lblLogo.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        28
                )
        );

        lblLogo.setForeground(
                WHITE
        );

        lblLogo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        topo.add(lblLogo);

        topo.add(
                Box.createVerticalStrut(5)
        );

        JLabel lblSistema = new JLabel(
                "Sistema de Gestão"
        );

        lblSistema.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        12
                )
        );

        lblSistema.setForeground(
                new Color(220, 250, 230)
        );

        lblSistema.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        topo.add(lblSistema);

        topo.add(
                Box.createVerticalStrut(25)
        );

        sidebar.add(
                topo,
                BorderLayout.NORTH
        );

        // =====================================================
        // MENU
        // =====================================================

        JPanel menu = new JPanel();

        menu.setOpaque(false);

        menu.setLayout(
                new BoxLayout(
                        menu,
                        BoxLayout.Y_AXIS
                )
        );

        menu.setBorder(
                new EmptyBorder(
                        5,
                        12,
                        5,
                        12
                )
        );

        JLabel lblMenu = new JLabel(
                "MENU"
        );

        lblMenu.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        11
                )
        );

        lblMenu.setForeground(
                new Color(210, 245, 220)
        );

        lblMenu.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        10,
                        0
                )
        );

        lblMenu.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        menu.add(lblMenu);

        // =====================================================
        // BOTÕES
        // =====================================================

        btnInicio = criarBotaoMenu(
                "⌂",
                "Início"
        );

        btnUsuarios = criarBotaoMenu(
                "♙",
                "Usuários"
        );

        btnCargos = criarBotaoMenu(
                "◆",
                "Cargos e Permissões"
        );

        btnProdutos = criarBotaoMenu(
                "▣",
                "Produtos"
        );

        btnCategorias = criarBotaoMenu(
                "▤",
                "Categorias"
        );

        btnEstoque = criarBotaoMenu(
                "▥",
                "Estoque"
        );

        btnCaixas = criarBotaoMenu(
                "▣",
                "Caixas"
        );

        btnRelatorios = criarBotaoMenu(
                "▥",
                "Relatórios"
        );

        menu.add(btnInicio);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnUsuarios);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnCargos);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnProdutos);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnCategorias);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnEstoque);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnCaixas);

        menu.add(
                Box.createVerticalStrut(5)
        );

        menu.add(btnRelatorios);

        menu.add(
                Box.createVerticalStrut(20)
        );

        // =====================================================
        // SEPARADOR
        // =====================================================

        JSeparator separador = new JSeparator();

        separador.setForeground(
                new Color(130, 215, 160)
        );

        separador.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        menu.add(separador);

        menu.add(
                Box.createVerticalStrut(12)
        );

        // =====================================================
        // CONFIGURAÇÕES
        // =====================================================

        btnConfiguracoes = criarBotaoMenu(
                "⚙",
                "Configurações"
        );

        menu.add(
                btnConfiguracoes
        );

        sidebar.add(
                menu,
                BorderLayout.CENTER
        );

        // =====================================================
        // RODAPÉ
        // =====================================================

        JPanel rodape = new JPanel();

        rodape.setOpaque(false);

        rodape.setLayout(
                new BoxLayout(
                        rodape,
                        BoxLayout.Y_AXIS
                )
        );

        rodape.setBorder(
                new EmptyBorder(
                        10,
                        12,
                        20,
                        12
                )
        );

        btnSair = criarBotaoMenu(
                "↪",
                "Sair"
        );

        rodape.add(
                btnSair
        );

        rodape.add(
                Box.createVerticalStrut(15)
        );

        JLabel versao = new JLabel(
                "CoStock v1.0"
        );

        versao.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        10
                )
        );

        versao.setForeground(
                new Color(210, 245, 220)
        );

        versao.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        rodape.add(
                versao
        );

        sidebar.add(
                rodape,
                BorderLayout.SOUTH
        );

        // =====================================================
        // AÇÕES
        // =====================================================

        adicionarAcaoMenu(
                btnInicio,
                "Início"
        );

        adicionarAcaoMenu(
                btnUsuarios,
                "Usuários"
        );

        adicionarAcaoMenu(
                btnCargos,
                "Cargos e Permissões"
        );

        adicionarAcaoMenu(
                btnProdutos,
                "Produtos"
        );

        adicionarAcaoMenu(
                btnCategorias,
                "Categorias"
        );

        adicionarAcaoMenu(
                btnEstoque,
                "Estoque"
        );

        adicionarAcaoMenu(
                btnCaixas,
                "Caixas"
        );

        adicionarAcaoMenu(
                btnRelatorios,
                "Relatórios"
        );

        adicionarAcaoMenu(
                btnConfiguracoes,
                "Configurações"
        );

        // =====================================================
        // SAIR
        // =====================================================

        btnSair.addActionListener(e -> {

            int resposta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Deseja realmente sair do sistema?",
                            "Sair",
                            JOptionPane.YES_NO_OPTION
                    );

            if (resposta ==
                    JOptionPane.YES_OPTION) {

                dispose();

                // Futuramente:
                // new LoginGUI().setVisible(true);
            }
        });

        // Início selecionado
        selecionarBotao(
                btnInicio
        );

        return sidebar;
    }

    // =========================================================
    // CRIAR BOTÃO DO MENU
    // =========================================================

    private JButton criarBotaoMenu(
            String icone,
            String texto
    ) {

        JButton botao = new JButton();

        botao.setText(
                icone + "   " + texto
        );

        botao.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        13
                )
        );

        botao.setForeground(
                WHITE
        );

        botao.setBackground(
                GREEN
        );

        botao.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        botao.setBorder(
                new EmptyBorder(
                        11,
                        14,
                        11,
                        10
                )
        );

        botao.setFocusPainted(
                false
        );

        botao.setOpaque(
                true
        );

        botao.setContentAreaFilled(
                true
        );

        botao.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        botao.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        46
                )
        );

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        botao.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        if (!botao.getBackground()
                                .equals(WHITE)) {

                            botao.setBackground(
                                    GREEN_DARK
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        if (!botao.getBackground()
                                .equals(WHITE)) {

                            botao.setBackground(
                                    GREEN
                            );
                        }
                    }
                }
        );

        return botao;
    }

    // =========================================================
    // AÇÕES DOS BOTÕES DO MENU
    // =========================================================

    private void adicionarAcaoMenu(
            JButton botao,
            String nome
    ) {

        botao.addActionListener(e -> {

            selecionarBotao(
                    botao
            );

            if (nome.equals("Início")) {
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Tela de "
                            + nome
                            + " ainda está em desenvolvimento.",
                    "CoStock",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    // =========================================================
    // SELECIONAR BOTÃO
    // =========================================================

    private void selecionarBotao(
            JButton selecionado
    ) {

        JButton[] botoes = {

                btnInicio,
                btnUsuarios,
                btnCargos,
                btnProdutos,
                btnCategorias,
                btnEstoque,
                btnCaixas,
                btnRelatorios,
                btnConfiguracoes
        };

        for (JButton botao : botoes) {

            if (botao == null) {
                continue;
            }

            botao.setForeground(
                    WHITE
            );

            botao.setBackground(
                    GREEN
            );

            botao.setFont(
                    new Font(
                            "Poppins",
                            Font.PLAIN,
                            13
                    )
            );
        }

        if (selecionado != null) {

            selecionado.setBackground(
                    WHITE
            );

            selecionado.setForeground(
                    GREEN
            );

            selecionado.setFont(
                    new Font(
                            "Poppins",
                            Font.BOLD,
                            13
                    )
            );
        }
    }

    // =========================================================
    // CONTEÚDO
    // =========================================================

    private JPanel criarConteudo() {

        painelConteudo = new JPanel(
                new BorderLayout()
        );

        painelConteudo.setBackground(
                BACKGROUND
        );

        painelConteudo.add(
                criarHeader(),
                BorderLayout.NORTH
        );

        painelConteudo.add(
                criarDashboard(),
                BorderLayout.CENTER
        );

        return painelConteudo;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel criarHeader() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(
                WHITE
        );

        header.setBorder(
                new EmptyBorder(
                        15,
                        25,
                        15,
                        25
                )
        );

        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo = new JLabel(
                "Painel Administrativo"
        );

        titulo.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        20
                )
        );

        titulo.setForeground(
                DARK
        );

        header.add(
                titulo,
                BorderLayout.WEST
        );

        // =====================================================
        // DIREITA
        // =====================================================

        JPanel direita = new JPanel();

        direita.setOpaque(false);

        direita.setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        15,
                        0
                )
        );

        // =====================================================
        // PESQUISA
        // =====================================================

        JTextField campoPesquisa =
                new JTextField();

        campoPesquisa.setPreferredSize(
                new Dimension(
                        230,
                        38
                )
        );

        campoPesquisa.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        12
                )
        );

        campoPesquisa.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                LIGHT_GRAY
                        ),
                        new EmptyBorder(
                                0,
                                12,
                                0,
                                12
                        )
                )
        );

        campoPesquisa.setToolTipText(
                "Pesquisar no sistema"
        );

        direita.add(
                campoPesquisa
        );

        // =====================================================
        // SEPARADOR
        // =====================================================

        JSeparator separador =
                new JSeparator(
                        SwingConstants.VERTICAL
                );

        separador.setPreferredSize(
                new Dimension(
                        1,
                        30
                )
        );

        direita.add(
                separador
        );

        // =====================================================
        // USUÁRIO
        // =====================================================

        JPanel usuario = new JPanel();

        usuario.setOpaque(false);

        usuario.setLayout(
                new BoxLayout(
                        usuario,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel nome = new JLabel(
                "Administrador"
        );

        nome.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        12
                )
        );

        nome.setForeground(
                DARK
        );

        nome.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        JLabel cargo = new JLabel(
                "Administrador"
        );

        cargo.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        10
                )
        );

        cargo.setForeground(
                TEXT_GRAY
        );

        cargo.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        usuario.add(nome);

        usuario.add(cargo);

        direita.add(
                usuario
        );

        // =====================================================
        // AVATAR
        // =====================================================

        JLabel avatar =
                criarAvatar();

        direita.add(
                avatar
        );

        header.add(
                direita,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // AVATAR
    // =========================================================

    private JLabel criarAvatar() {

        JLabel avatar =
                new JLabel("A");

        avatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        avatar.setVerticalAlignment(
                SwingConstants.CENTER
        );

        avatar.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        avatar.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        16
                )
        );

        avatar.setForeground(
                WHITE
        );

        avatar.setOpaque(
                true
        );

        avatar.setBackground(
                GREEN
        );

        avatar.setBorder(
                BorderFactory.createLineBorder(
                        GREEN
                )
        );

        return avatar;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private JPanel criarDashboard() {

        JPanel painel = new JPanel();

        painel.setBackground(
                BACKGROUND
        );

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        30,
                        30
                )
        );

        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Olá, Administrador!"
                );

        titulo.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        27
                )
        );

        titulo.setForeground(
                DARK
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        painel.add(
                titulo
        );

        painel.add(
                Box.createVerticalStrut(3)
        );

        JLabel subtitulo =
                new JLabel(
                        "Gerencie os principais recursos do sistema CoStock."
                );

        subtitulo.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        13
                )
        );

        subtitulo.setForeground(
                TEXT_GRAY
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        painel.add(
                subtitulo
        );

        painel.add(
                Box.createVerticalStrut(25)
        );

        // =====================================================
        // CARDS
        // =====================================================

        JPanel cards =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                18,
                                0
                        )
                );

        cards.setOpaque(false);

        cards.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        145
                )
        );

        cards.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // Usuários
        cards.add(
                criarCard(
                        "USUÁRIOS",
                        "12",
                        "Funcionários cadastrados",
                        BLUE,
                        "♙"
                )
        );

        // Produtos
        cards.add(
                criarCard(
                        "PRODUTOS",
                        "148",
                        "Produtos cadastrados",
                        GREEN,
                        "▣"
                )
        );

        // Estoque
        cards.add(
                criarCard(
                        "ESTOQUE",
                        "23",
                        "Produtos abaixo do mínimo",
                        RED,
                        "▥"
                )
        );

        // Caixas
        cards.add(
                criarCard(
                        "CAIXAS",
                        "3",
                        "Caixas em operação",
                        GREEN,
                        "▤"
                )
        );

        painel.add(
                cards
        );

        painel.add(
                Box.createVerticalStrut(30)
        );

        // =====================================================
        // ACESSO RÁPIDO
        // =====================================================

        JLabel acesso =
                new JLabel(
                        "Acesso rápido"
                );

        acesso.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        18
                )
        );

        acesso.setForeground(
                DARK
        );

        acesso.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        painel.add(
                acesso
        );

        painel.add(
                Box.createVerticalStrut(15)
        );

        // =====================================================
        // BOTÕES DE ACESSO
        // =====================================================

        JPanel acessos =
                new JPanel(
                        new GridLayout(
                                2,
                                3,
                                18,
                                18
                        )
                );

        acessos.setOpaque(false);

        acessos.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // Usuários
        acessos.add(
                criarBotaoAcao(
                        "Gerenciar usuários",
                        "Cadastre e gerencie funcionários",
                        BLUE,
                        "♙"
                )
        );

        // Produtos
        acessos.add(
                criarBotaoAcao(
                        "Produtos",
                        "Cadastre e edite produtos",
                        GREEN,
                        "▣"
                )
        );

        // Categorias
        acessos.add(
                criarBotaoAcao(
                        "Categorias",
                        "Gerencie categorias de produtos",
                        PURPLE,
                        "▤"
                )
        );

        // Estoque
        acessos.add(
                criarBotaoAcao(
                        "Estoque",
                        "Consulte o estoque do mercado",
                        GREEN,
                        "▥"
                )
        );

        // Caixas
        acessos.add(
                criarBotaoAcao(
                        "Caixas",
                        "Acompanhe os caixas",
                        RED,
                        "▤"
                )
        );

        // Relatórios
        acessos.add(
                criarBotaoAcao(
                        "Relatórios",
                        "Visualize relatórios do sistema",
                        DARK,
                        "▥"
                )
        );

        painel.add(
                acessos
        );

        return painel;
    }

    // =========================================================
    // CARD
    // =========================================================

    private JPanel criarCard(
            String titulo,
            String valor,
            String descricao,
            Color cor,
            String icone
    ) {

        JPanel card =
                new RoundedPanel(
                        18,
                        WHITE
                );

        card.setLayout(
                new BorderLayout()
        );

        card.setBorder(
                new EmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        // =====================================================
        // TEXTOS
        // =====================================================

        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        10
                )
        );

        lblTitulo.setForeground(
                TEXT_GRAY
        );

        JLabel lblValor =
                new JLabel(
                        valor
                );

        lblValor.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        30
                )
        );

        lblValor.setForeground(
                DARK
        );

        JLabel lblDescricao =
                new JLabel(
                        descricao
                );

        lblDescricao.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        10
                )
        );

        lblDescricao.setForeground(
                TEXT_GRAY
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(
                lblValor
        );

        textos.add(
                Box.createVerticalStrut(2)
        );

        textos.add(
                lblDescricao
        );

        card.add(
                textos,
                BorderLayout.CENTER
        );

        // =====================================================
        // ÍCONE
        // =====================================================

        JLabel lblIcone =
                new JLabel(
                        icone,
                        SwingConstants.CENTER
                );

        lblIcone.setPreferredSize(
                new Dimension(
                        52,
                        52
                )
        );

        lblIcone.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        22
                )
        );

        lblIcone.setForeground(
                WHITE
        );

        lblIcone.setOpaque(
                true
        );

        lblIcone.setBackground(
                cor
        );

        card.add(
                lblIcone,
                BorderLayout.EAST
        );

        return card;
    }

    // =========================================================
    // BOTÃO DE ACESSO RÁPIDO
    // =========================================================

    private JPanel criarBotaoAcao(
            String titulo,
            String descricao,
            Color cor,
            String icone
    ) {

        RoundedPanel painel =
                new RoundedPanel(
                        18,
                        WHITE
                );

        painel.setLayout(
                new BorderLayout(
                        15,
                        0
                )
        );

        painel.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        painel.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        // =====================================================
        // ÍCONE
        // =====================================================

        JLabel lblIcone =
                new JLabel(
                        icone,
                        SwingConstants.CENTER
                );

        lblIcone.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );

        lblIcone.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        20
                )
        );

        lblIcone.setForeground(
                WHITE
        );

        lblIcone.setOpaque(
                true
        );

        lblIcone.setBackground(
                cor
        );

        painel.add(
                lblIcone,
                BorderLayout.WEST
        );

        // =====================================================
        // TEXTOS
        // =====================================================

        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Poppins",
                        Font.BOLD,
                        13
                )
        );

        lblTitulo.setForeground(
                DARK
        );

        JLabel lblDescricao =
                new JLabel(
                        descricao
                );

        lblDescricao.setFont(
                new Font(
                        "Poppins",
                        Font.PLAIN,
                        10
                )
        );

        lblDescricao.setForeground(
                TEXT_GRAY
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(5)
        );

        textos.add(
                lblDescricao
        );

        painel.add(
                textos,
                BorderLayout.CENTER
        );

        // =====================================================
        // AÇÃO
        // =====================================================

        painel.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        painel.setBackground(
                                new Color(
                                        250,
                                        250,
                                        250
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        painel.setBackground(
                                WHITE
                        );
                    }

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        JOptionPane.showMessageDialog(
                                AdministradorGUI.this,
                                "Tela de "
                                        + titulo
                                        + " ainda está em desenvolvimento.",
                                "CoStock",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
        );

        return painel;
    }

    // =========================================================
    // PAINEL ARREDONDADO
    // =========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        private final Color backgroundColor;

        public RoundedPanel(
                int radius,
                Color backgroundColor
        ) {

            this.radius =
                    radius;

            this.backgroundColor =
                    backgroundColor;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    backgroundColor
            );

            g2.fill(
                    new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            radius,
                            radius
                    )
            );

            g2.dispose();

            super.paintComponent(
                    g
            );
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager.getSystemLookAndFeelClassName()
                        );

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    new AdministradorGUI()
                            .setVisible(true);
                }
        );
    }
}
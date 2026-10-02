package view;

import dao.CaixaDAO;
import model.Caixa;
import model.StatusCaixa;
import model.Usuario;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CaixaAberturaGUI extends JFrame {

    // =====================================================
    // COMPONENTES DA TELA
    // =====================================================

    private JTextField txtOperador;
    private JTextField txtCaixa;
    private JTextField txtData;
    private JTextField txtValorAbertura;

    private JButton btnAbrir;
    private JButton btnCancelar;

    // =====================================================
    // USUÁRIO E DAO
    // =====================================================

    private Usuario usuario;
    private CaixaDAO caixaDAO;

    // =====================================================
    // FORMATAÇÃO DA DATA
    // =====================================================

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public CaixaAberturaGUI(Usuario usuario) {

        this.usuario = usuario;
        this.caixaDAO = new CaixaDAO();

        configurarJanela();
        inicializarComponentes();
        configurarTela();
    }

    // =====================================================
    // CONFIGURAÇÃO DA JANELA
    // =====================================================

    private void configurarJanela() {

        setTitle("CoStock - Abertura de Caixa");

        setSize(600, 500);

        setMinimumSize(new Dimension(500, 450));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);
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

        // =================================================
        // CABEÇALHO
        // =================================================

        JPanel painelCabecalho =
                new JPanel(new BorderLayout());

        painelCabecalho.setBackground(
                new Color(23, 60, 56)
        );

        painelCabecalho.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel lblTitulo =
                new JLabel("CoStock | Abertura de Caixa");

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        lblTitulo.setForeground(Color.WHITE);

        painelCabecalho.add(
                lblTitulo,
                BorderLayout.WEST
        );

        // =================================================
        // PAINEL CENTRAL
        // =================================================

        JPanel painelCentral =
                new JPanel(new GridBagLayout());

        painelCentral.setBackground(Color.WHITE);

        painelCentral.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 228)
                        ),
                        BorderFactory.createEmptyBorder(
                                30, 40, 30, 40
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(10, 10, 10, 10);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // =================================================
        // OPERADOR
        // =================================================

        JLabel lblOperador =
                new JLabel("Operador:");

        lblOperador.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtOperador =
                new JTextField();

        txtOperador.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtOperador.setPreferredSize(
                new Dimension(300, 40)
        );

        txtOperador.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        painelCentral.add(
                lblOperador,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        painelCentral.add(
                txtOperador,
                gbc
        );

        // =================================================
        // IDENTIFICADOR DO CAIXA
        // =================================================

        JLabel lblCaixa =
                new JLabel("Caixa:");

        lblCaixa.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtCaixa =
                new JTextField();

        txtCaixa.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtCaixa.setPreferredSize(
                new Dimension(300, 40)
        );

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        painelCentral.add(
                lblCaixa,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        painelCentral.add(
                txtCaixa,
                gbc
        );

        // =================================================
        // DATA
        // =================================================

        JLabel lblData =
                new JLabel("Data/Hora:");

        lblData.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtData =
                new JTextField();

        txtData.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        txtData.setPreferredSize(
                new Dimension(300, 40)
        );

        txtData.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        painelCentral.add(
                lblData,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        painelCentral.add(
                txtData,
                gbc
        );

        // =================================================
        // VALOR DE ABERTURA
        // =================================================

        JLabel lblValor =
                new JLabel("Valor de abertura:");

        lblValor.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        txtValorAbertura =
                new JTextField();

        txtValorAbertura.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        txtValorAbertura.setPreferredSize(
                new Dimension(300, 45)
        );

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        painelCentral.add(
                lblValor,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        painelCentral.add(
                txtValorAbertura,
                gbc
        );

        // =================================================
        // RODAPÉ
        // =================================================

        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                15,
                                15
                        )
                );

        painelBotoes.setBackground(
                new Color(245, 247, 248)
        );

        btnAbrir =
                criarBotao(
                        "Abrir Caixa",
                        new Color(20, 125, 100)
                );

        btnCancelar =
                criarBotao(
                        "Cancelar",
                        new Color(100, 100, 100)
                );

        painelBotoes.add(btnAbrir);
        painelBotoes.add(btnCancelar);

        // =================================================
        // EVENTOS
        // =================================================

        btnAbrir.addActionListener(
                e -> abrirCaixa()
        );

        btnCancelar.addActionListener(
                e -> cancelar()
        );

        txtValorAbertura.addActionListener(
                e -> abrirCaixa()
        );

        // =================================================
        // MONTAGEM DA TELA
        // =================================================

        painelPrincipal.add(
                painelCabecalho,
                BorderLayout.NORTH
        );

        painelPrincipal.add(
                painelCentral,
                BorderLayout.CENTER
        );

        painelPrincipal.add(
                painelBotoes,
                BorderLayout.SOUTH
        );

        setContentPane(painelPrincipal);
    }

    // =====================================================
    // CONFIGURAR TELA
    // =====================================================

    private void configurarTela() {

        txtOperador.setText(
                usuario.getNome()
        );

        txtCaixa.setText("");

        txtData.setText(
                LocalDateTime.now().format(formatter)
        );

        txtValorAbertura.setText("");

        SwingUtilities.invokeLater(() ->
                txtCaixa.requestFocus()
        );
    }

    // =====================================================
    // CRIAR BOTÃO
    // =====================================================

    private JButton criarBotao(
            String texto,
            Color cor
    ) {

        JButton botao =
                new JButton(texto);

        botao.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        botao.setForeground(Color.WHITE);

        botao.setBackground(cor);

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        botao.setPreferredSize(
                new Dimension(150, 45)
        );

        return botao;
    }

    // =====================================================
    // ABRIR CAIXA
    // =====================================================

    private void abrirCaixa() {

        // ---------------------------------------------
        // IDENTIFICADOR DO CAIXA
        // ---------------------------------------------

        String caixaTexto =
                txtCaixa
                        .getText()
                        .trim();

        if (caixaTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe o identificador do caixa.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            txtCaixa.requestFocus();

            return;
        }

        // ---------------------------------------------
        // VALOR DE ABERTURA
        // ---------------------------------------------

        String valorTexto =
                txtValorAbertura
                        .getText()
                        .trim();

        if (valorTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe o valor de abertura do caixa.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            txtValorAbertura.requestFocus();

            return;
        }

        // ---------------------------------------------
        // CONVERTER VALOR
        // ---------------------------------------------

        BigDecimal valorAbertura;

        try {

            valorTexto =
                    valorTexto.replace(",", ".");

            valorAbertura =
                    new BigDecimal(valorTexto);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe um valor válido.\n"
                    + "Exemplo: 100,00",
                    "Valor inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            txtValorAbertura.requestFocus();

            return;
        }

        // ---------------------------------------------
        // VALOR NEGATIVO
        // ---------------------------------------------

        if (
                valorAbertura.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "O valor de abertura não pode ser negativo.",
                    "Valor inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            txtValorAbertura.requestFocus();

            return;
        }

        // ---------------------------------------------
        // CONFIRMAÇÃO
        // ---------------------------------------------

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja abrir o caixa "
                        + caixaTexto
                        + " com o valor de R$ "
                        + valorAbertura.toString()
                        + "?",
                        "Confirmar abertura",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        // ---------------------------------------------
        // SALVAR NO BANCO
        // ---------------------------------------------

        try {

            // Verifica se já existe caixa aberto
            Caixa caixaAberto =
                    caixaDAO.buscarCaixaAberto();

            if (caixaAberto != null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Já existe um caixa aberto no sistema.",
                        "Caixa já aberto",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // -----------------------------------------
            // CRIA O OBJETO CAIXA
            // -----------------------------------------

            Caixa caixa =
                    new Caixa();

            caixa.setIdCaixa(
                    caixaTexto
            );

            caixa.setUsuario(
                    usuario
            );

            caixa.setDataAbertura(
                    LocalDateTime.now()
            );

            caixa.setValorAbertura(
                    valorAbertura
            );

            caixa.setStatus(
                    StatusCaixa.ABERTO
            );

            // -----------------------------------------
            // CADASTRA NO BANCO
            // -----------------------------------------

            boolean sucesso =
                    caixaDAO.cadastrar(caixa);

            if (!sucesso) {

                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível abrir o caixa.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // -----------------------------------------
            // SUCESSO
            // -----------------------------------------

            JOptionPane.showMessageDialog(
                    this,
                    "Caixa "
                    + caixaTexto
                    + " aberto com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // -----------------------------------------
            // ABRE A FRENTE DE CAIXA
            // -----------------------------------------

            CaixaGUI telaCaixa =
                    new CaixaGUI(
                            usuario,
                            caixa
                    );

            telaCaixa.setVisible(true);

            // Fecha a tela de abertura
            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao abrir o caixa:\n"
                    + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // CANCELAR
    // =====================================================

    private void cancelar() {

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja sair do sistema?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

        if (
                resposta ==
                JOptionPane.YES_OPTION
        ) {

            System.exit(0);
        }
    }

    // =====================================================
    // MAIN PARA TESTE
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Usuario usuario =
                    new Usuario();

            usuario.setNome("Usuário de teste");

            CaixaAberturaGUI tela =
                    new CaixaAberturaGUI(
                            usuario
                    );

            tela.setVisible(true);
        });
    }
}
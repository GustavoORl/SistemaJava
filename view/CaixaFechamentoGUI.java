package view;

import dao.CaixaDAO;
import model.Caixa;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class CaixaFechamentoGUI extends JFrame {

    // =====================================================
    // DADOS
    // =====================================================

    private final Caixa caixa;

    private final CaixaDAO caixaDAO =
            new CaixaDAO();

    // =====================================================
    // COMPONENTES
    // =====================================================

    private JLabel lblOperador;
    private JLabel lblDataAbertura;
    private JLabel lblValorAbertura;

    private JTextField txtValorFechamento;

    private JButton btnFechar;
    private JButton btnCancelar;

    // =====================================================
    // FORMATAÇÃO
    // =====================================================

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(
                    new Locale("pt", "BR")
            );

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public CaixaFechamentoGUI(Caixa caixa) {

        this.caixa = caixa;

        configurarJanela();

        inicializarComponentes();

        configurarEventos();

        preencherDados();
    }

    // =====================================================
    // CONFIGURAÇÃO DA JANELA
    // =====================================================

    private void configurarJanela() {

        setTitle("CoStock - Fechamento de Caixa");

        setSize(550, 500);

        setMinimumSize(
                new Dimension(500, 450)
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
    }

    // =====================================================
    // COMPONENTES
    // =====================================================

    private void inicializarComponentes() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout());

        painelPrincipal.setBackground(
                new Color(245, 247, 248)
        );

        // -------------------------------------------------
        // CABEÇALHO
        // -------------------------------------------------

        JPanel cabecalho =
                new JPanel(new BorderLayout());

        cabecalho.setBackground(
                new Color(23, 60, 56)
        );

        cabecalho.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titulo =
                new JLabel(
                        "CoStock | Fechamento de Caixa"
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        titulo.setForeground(Color.WHITE);

        cabecalho.add(
                titulo,
                BorderLayout.WEST
        );

        // -------------------------------------------------
        // PAINEL CENTRAL
        // -------------------------------------------------

        JPanel painelCentral =
                new JPanel(
                        new GridBagLayout()
                );

        painelCentral.setBackground(Color.WHITE);

        painelCentral.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 25, 30
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        // Operador
        JLabel tituloOperador =
                criarLabelTitulo(
                        "Operador"
                );

        lblOperador =
                criarLabelValor();

        // Data
        JLabel tituloData =
                criarLabelTitulo(
                        "Data de abertura"
                );

        lblDataAbertura =
                criarLabelValor();

        // Valor abertura
        JLabel tituloValorAbertura =
                criarLabelTitulo(
                        "Valor de abertura"
                );

        lblValorAbertura =
                criarLabelValor();

        // Valor fechamento
        JLabel tituloValorFechamento =
                criarLabelTitulo(
                        "Valor encontrado no caixa"
                );

        txtValorFechamento =
                new JTextField();

        txtValorFechamento.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        18
                )
        );

        txtValorFechamento.setPreferredSize(
                new Dimension(250, 42)
        );

        // -------------------------------------------------
        // ADICIONAR COMPONENTES
        // -------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 0;

        painelCentral.add(
                tituloOperador,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                lblOperador,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                tituloData,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                lblDataAbertura,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                tituloValorAbertura,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                lblValorAbertura,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                tituloValorFechamento,
                gbc
        );

        gbc.gridy++;

        painelCentral.add(
                txtValorFechamento,
                gbc
        );

        // -------------------------------------------------
        // RODAPÉ
        // -------------------------------------------------

        JPanel rodape =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                15
                        )
                );

        rodape.setBackground(
                new Color(245, 247, 248)
        );

        btnCancelar =
                criarBotao(
                        "Cancelar",
                        new Color(100, 100, 100)
                );

        btnFechar =
                criarBotao(
                        "Fechar caixa",
                        new Color(190, 60, 60)
                );

        rodape.add(btnCancelar);
        rodape.add(btnFechar);

        // -------------------------------------------------
        // MONTAR TELA
        // -------------------------------------------------

        painelPrincipal.add(
                cabecalho,
                BorderLayout.NORTH
        );

        painelPrincipal.add(
                painelCentral,
                BorderLayout.CENTER
        );

        painelPrincipal.add(
                rodape,
                BorderLayout.SOUTH
        );

        setContentPane(
                painelPrincipal
        );
    }

    // =====================================================
    // LABELS
    // =====================================================

    private JLabel criarLabelTitulo(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(
                new Color(80, 80, 80)
        );

        return label;
    }

    private JLabel criarLabelValor() {

        JLabel label =
                new JLabel();

        label.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        label.setForeground(
                new Color(30, 30, 30)
        );

        return label;
    }

    // =====================================================
    // BOTÃO
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
                        13
                )
        );

        botao.setForeground(Color.WHITE);

        botao.setBackground(cor);

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setPreferredSize(
                new Dimension(140, 42)
        );

        return botao;
    }

    // =====================================================
    // PREENCHER DADOS
    // =====================================================

    private void preencherDados() {

        if (caixa == null) {
            return;
        }

        if (caixa.getUsuario() != null) {

            lblOperador.setText(
                    caixa.getUsuario().getNome()
            );
        }

        if (caixa.getDataAbertura() != null) {

            lblDataAbertura.setText(
                    caixa.getDataAbertura()
                            .format(formatoData)
            );
        }

        if (caixa.getValorAbertura() != null) {

            lblValorAbertura.setText(
                    formatoMoeda.format(
                            caixa.getValorAbertura()
                    )
            );
        }

        txtValorFechamento.requestFocus();
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    private void configurarEventos() {

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnFechar.addActionListener(
                e -> fecharCaixa()
        );
    }

    // =====================================================
    // FECHAR CAIXA
    // =====================================================

    private void fecharCaixa() {

        String valorTexto =
                txtValorFechamento
                        .getText()
                        .trim();

        // -------------------------------------------------
        // VALIDAÇÃO
        // -------------------------------------------------

        if (valorTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe o valor encontrado no caixa.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            txtValorFechamento.requestFocus();

            return;
        }

        // -------------------------------------------------
        // CONVERTER VALOR
        // -------------------------------------------------

        BigDecimal valorFechamento;

        try {

            valorTexto =
                    valorTexto.replace(",", ".");

            valorFechamento =
                    new BigDecimal(valorTexto);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe um valor válido.\n"
                    + "Exemplo: 850,00",
                    "Valor inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            txtValorFechamento.requestFocus();

            return;
        }

        // -------------------------------------------------
        // VALOR NEGATIVO
        // -------------------------------------------------

        if (
                valorFechamento.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "O valor de fechamento não pode ser negativo.",
                    "Valor inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            txtValorFechamento.requestFocus();

            return;
        }

        // -------------------------------------------------
        // CONFIRMAÇÃO
        // -------------------------------------------------

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente fechar o caixa?\n\n"
                        + "Valor de fechamento: "
                        + formatoMoeda.format(
                                valorFechamento
                        ),
                        "Confirmar fechamento",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        // -------------------------------------------------
        // FECHAR NO BANCO
        // -------------------------------------------------

        try {

            LocalDateTime agora = LocalDateTime.now();

boolean sucesso = caixaDAO.fechar(
    caixa.getIdAbertura(),
    agora,
    valorFechamento
);

            if (!sucesso) {

                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível fechar o caixa.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Atualiza o objeto em memória
            caixa.setDataFechamento(
                    LocalDateTime.now()
            );

            caixa.setValorFechamento(
                    valorFechamento
            );

            caixa.setStatus(
                    model.StatusCaixa.FECHADO
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Caixa fechado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Volta para o login
            LoginGUI login =
                    new LoginGUI();

            login.setVisible(true);

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao fechar o caixa:\n"
                    + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }
}
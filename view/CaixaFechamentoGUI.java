
package view;

import dao.CaixaDAO;
import model.Caixa;
import model.ResumoFechamentoCaixa;
import model.StatusCaixa;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CaixaFechamentoGUI extends JFrame {

    // =====================================================
    // DADOS
    // =====================================================

    private final Caixa caixa;
    private final CaixaDAO caixaDAO = new CaixaDAO();

    private ResumoFechamentoCaixa resumo;

    // =====================================================
    // CORES
    // =====================================================

    private static final Color VERDE_ESCURO =
            new Color(23, 60, 56);

    private static final Color FUNDO =
            new Color(245, 247, 248);

    private static final Color TEXTO =
            new Color(30, 30, 30);

    private static final Color CINZA =
            new Color(100, 100, 100);

    private static final Color VERMELHO =
            new Color(190, 60, 60);

    private static final Color VERDE =
            new Color(30, 125, 90);

    // =====================================================
    // COMPONENTES
    // =====================================================

    private JLabel lblOperador;
    private JLabel lblDataAbertura;
    private JLabel lblValorAbertura;

    private JLabel lblQuantidadeVendas;
    private JLabel lblTotalVendas;

    private JLabel lblDinheiro;
    private JLabel lblPix;
    private JLabel lblCredito;
    private JLabel lblDebito;
    private JLabel lblPrazo;

    private JLabel lblDinheiroEsperado;
    private JLabel lblDiferenca;
    private JLabel lblSituacao;

    private JTextField txtValorFechamento;
    private JTextArea txtObservacao;

    private JButton btnFechar;
    private JButton btnCancelar;
    private JButton btnAtualizar;

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
        carregarResumo();
    }

    // =====================================================
    // JANELA
    // =====================================================

    private void configurarJanela() {

        setTitle("CoStock - Fechamento de Caixa");

        setSize(650, 820);

        setMinimumSize(new Dimension(570, 650));

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

        painelPrincipal.setBackground(FUNDO);

        // -------------------------------------------------
        // CABEÇALHO
        // -------------------------------------------------

        JPanel cabecalho =
                new JPanel(new BorderLayout());

        cabecalho.setBackground(VERDE_ESCURO);

        cabecalho.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titulo = new JLabel(
                "CoStock | Fechamento de Caixa"
        );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 22)
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
                new JPanel(new GridBagLayout());

        painelCentral.setBackground(Color.WHITE);

        painelCentral.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 30, 25, 30
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // -------------------------------------------------
        // DADOS DA ABERTURA
        // -------------------------------------------------

        adicionarTituloSecao(
                painelCentral,
                gbc,
                "Informações do caixa"
        );

        lblOperador = criarLabelValor();
        lblDataAbertura = criarLabelValor();
        lblValorAbertura = criarLabelValor();

        adicionarLinha(
                painelCentral,
                gbc,
                "Operador",
                lblOperador
        );

        adicionarLinha(
                painelCentral,
                gbc,
                "Data de abertura",
                lblDataAbertura
        );

        adicionarLinha(
                painelCentral,
                gbc,
                "Valor de abertura",
                lblValorAbertura
        );

        adicionarSeparador(painelCentral, gbc);

        // -------------------------------------------------
        // RESUMO DAS VENDAS
        // -------------------------------------------------

        adicionarTituloSecao(
                painelCentral,
                gbc,
                "Resumo financeiro"
        );

        lblQuantidadeVendas = criarLabelValor();
        lblTotalVendas = criarLabelDestaque();

        adicionarLinha(
                painelCentral,
                gbc,
                "Vendas finalizadas",
                lblQuantidadeVendas
        );

        adicionarLinha(
                painelCentral,
                gbc,
                "Total de vendas",
                lblTotalVendas
        );

        adicionarSeparador(painelCentral, gbc);

        // -------------------------------------------------
        // FORMAS DE PAGAMENTO
        // -------------------------------------------------

        adicionarTituloSecao(
                painelCentral,
                gbc,
                "Vendas por forma de pagamento"
        );

        lblDinheiro = criarLabelValor();
        lblPix = criarLabelValor();
        lblCredito = criarLabelValor();
        lblDebito = criarLabelValor();
        lblPrazo = criarLabelValor();

        adicionarLinha(
                painelCentral, gbc,
                "Dinheiro", lblDinheiro
        );

        adicionarLinha(
                painelCentral, gbc,
                "PIX", lblPix
        );

        adicionarLinha(
                painelCentral, gbc,
                "Cartão de crédito", lblCredito
        );

        adicionarLinha(
                painelCentral, gbc,
                "Cartão de débito", lblDebito
        );

        adicionarLinha(
                painelCentral, gbc,
                "A prazo", lblPrazo
        );

        adicionarSeparador(painelCentral, gbc);

        // -------------------------------------------------
        // CONFERÊNCIA
        // -------------------------------------------------

        adicionarTituloSecao(
                painelCentral,
                gbc,
                "Conferência do dinheiro"
        );

        lblDinheiroEsperado = criarLabelDestaque();

        adicionarLinha(
                painelCentral,
                gbc,
                "Dinheiro esperado na gaveta",
                lblDinheiroEsperado
        );

        JLabel tituloValorContado =
                criarLabelTitulo(
                        "Valor encontrado no caixa (R$)"
                );

        gbc.gridy++;
        painelCentral.add(
                tituloValorContado,
                gbc
        );

        txtValorFechamento = new JTextField();

        txtValorFechamento.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        txtValorFechamento.setPreferredSize(
                new Dimension(250, 42)
        );

        gbc.gridy++;
        painelCentral.add(
                txtValorFechamento,
                gbc
        );

        lblDiferenca = criarLabelDestaque();

        adicionarLinha(
                painelCentral,
                gbc,
                "Diferença apurada",
                lblDiferenca
        );

        lblSituacao = criarLabelValor();

        gbc.gridy++;
        painelCentral.add(
                lblSituacao,
                gbc
        );

        // -------------------------------------------------
        // OBSERVAÇÃO
        // -------------------------------------------------

        JLabel tituloObservacao =
                criarLabelTitulo(
                        "Observações do fechamento"
                );

        gbc.gridy++;
        painelCentral.add(
                tituloObservacao,
                gbc
        );

        txtObservacao = new JTextArea(3, 20);

        txtObservacao.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        txtObservacao.setLineWrap(true);
        txtObservacao.setWrapStyleWord(true);

        txtObservacao.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 8, 8, 8
                )
        );

        JScrollPane scrollObservacao =
                new JScrollPane(txtObservacao);

        scrollObservacao.setPreferredSize(
                new Dimension(250, 85)
        );

        gbc.gridy++;
        painelCentral.add(
                scrollObservacao,
                gbc
        );

        // -------------------------------------------------
        // RODAPÉ
        // -------------------------------------------------

        JPanel rodape = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        15
                )
        );

        rodape.setBackground(FUNDO);

        btnCancelar = criarBotao(
                "Cancelar",
                CINZA
        );

        btnAtualizar = criarBotao(
                "Atualizar",
                VERDE_ESCURO
        );

        btnFechar = criarBotao(
                "Fechar caixa",
                VERMELHO
        );

        btnFechar.setEnabled(false);

        rodape.add(btnCancelar);
        rodape.add(btnAtualizar);
        rodape.add(btnFechar);

        // -------------------------------------------------
        // SCROLL
        // -------------------------------------------------

        JScrollPane scrollPrincipal =
                new JScrollPane(painelCentral);

        scrollPrincipal.setBorder(null);

        scrollPrincipal.getVerticalScrollBar()
                .setUnitIncrement(16);

        // -------------------------------------------------
        // MONTAGEM
        // -------------------------------------------------

        painelPrincipal.add(
                cabecalho,
                BorderLayout.NORTH
        );

        painelPrincipal.add(
                scrollPrincipal,
                BorderLayout.CENTER
        );

        painelPrincipal.add(
                rodape,
                BorderLayout.SOUTH
        );

        setContentPane(painelPrincipal);
    }

    // =====================================================
    // COMPONENTES AUXILIARES
    // =====================================================

    private JLabel criarLabelTitulo(String texto) {

        JLabel label = new JLabel(texto);

        label.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        label.setForeground(
                new Color(80, 80, 80)
        );

        return label;
    }

    private JLabel criarLabelValor() {

        JLabel label = new JLabel("-");

        label.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        label.setForeground(TEXTO);

        return label;
    }

    private JLabel criarLabelDestaque() {

        JLabel label = new JLabel("-");

        label.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        label.setForeground(VERDE_ESCURO);

        return label;
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

        botao.setPreferredSize(
                new Dimension(140, 42)
        );

        return botao;
    }

    private void adicionarTituloSecao(
            JPanel painel,
            GridBagConstraints gbc,
            String texto
    ) {

        JLabel titulo = new JLabel(texto);

        titulo.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        titulo.setForeground(VERDE_ESCURO);

        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 8, 5);

        painel.add(titulo, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
    }

    private void adicionarLinha(
            JPanel painel,
            GridBagConstraints gbc,
            String titulo,
            JLabel valor
    ) {

        JPanel linha = new JPanel(
                new BorderLayout(10, 0)
        );

        linha.setOpaque(false);

        JLabel labelTitulo =
                criarLabelTitulo(titulo);

        linha.add(
                labelTitulo,
                BorderLayout.WEST
        );

        linha.add(
                valor,
                BorderLayout.EAST
        );

        gbc.gridy++;
        painel.add(linha, gbc);
    }

    private void adicionarSeparador(
            JPanel painel,
            GridBagConstraints gbc
    ) {

        gbc.gridy++;
        gbc.insets = new Insets(12, 5, 12, 5);

        painel.add(
                new JSeparator(),
                gbc
        );

        gbc.insets = new Insets(5, 5, 5, 5);
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

        SwingUtilities.invokeLater(
                () -> txtValorFechamento.requestFocusInWindow()
        );
    }

    // =====================================================
    // CARREGAR RESUMO
    // =====================================================

    private void carregarResumo() {

        if (caixa == null
                || caixa.getIdTabelaCaixa() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sessão de caixa inválida.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            btnFechar.setEnabled(false);
            return;
        }

        btnFechar.setEnabled(false);
        btnAtualizar.setEnabled(false);

        SwingWorker<ResumoFechamentoCaixa, Void> worker =
                new SwingWorker<>() {

            @Override
            protected ResumoFechamentoCaixa doInBackground()
                    throws Exception {

                return caixaDAO.buscarResumoFechamento(
                        caixa.getIdTabelaCaixa()
                );
            }

            @Override
            protected void done() {

                btnAtualizar.setEnabled(true);

                try {

                    resumo = get();

                    atualizarResumoTela();

                    btnFechar.setEnabled(true);

                } catch (Exception e) {

                    resumo = null;
                    btnFechar.setEnabled(false);

                    JOptionPane.showMessageDialog(
                            CaixaFechamentoGUI.this,
                            "Erro ao consultar o resumo:\n"
                                    + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private void atualizarResumoTela() {

        if (resumo == null) {
            return;
        }

        lblValorAbertura.setText(
                formatoMoeda.format(
                        resumo.getValorAbertura()
                )
        );

        lblQuantidadeVendas.setText(
                String.valueOf(
                        resumo.getQuantidadeVendas()
                )
        );

        lblTotalVendas.setText(
                formatoMoeda.format(
                        resumo.getTotalVendas()
                )
        );

        lblDinheiro.setText(
                formatoMoeda.format(
                        resumo.getTotalDinheiro()
                )
        );

        lblPix.setText(
                formatoMoeda.format(
                        resumo.getTotalPix()
                )
        );

        lblCredito.setText(
                formatoMoeda.format(
                        resumo.getTotalCredito()
                )
        );

        lblDebito.setText(
                formatoMoeda.format(
                        resumo.getTotalDebito()
                )
        );

        lblPrazo.setText(
                formatoMoeda.format(
                        resumo.getTotalPrazo()
                )
        );

        lblDinheiroEsperado.setText(
                formatoMoeda.format(
                        resumo.getDinheiroEsperado()
                )
        );

        atualizarDiferenca();
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    private void configurarEventos() {

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnAtualizar.addActionListener(
                e -> carregarResumo()
        );

        btnFechar.addActionListener(
                e -> fecharCaixa()
        );

        txtValorFechamento.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarDiferenca();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarDiferenca();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarDiferenca();
            }
        });
    }

    // =====================================================
    // CONVERTER VALOR
    // =====================================================

    private BigDecimal converterValor(
            String texto
    ) {

        if (texto == null
                || texto.trim().isEmpty()) {

            throw new NumberFormatException(
                    "Informe o valor encontrado no caixa."
            );
        }

        String valor = texto.trim()
                .replace("R$", "")
                .replace(" ", "");

        // Formato brasileiro: 1.250,50
        if (valor.contains(",")) {

            valor = valor.replace(".", "")
                    .replace(",", ".");
        }

        BigDecimal resultado =
                new BigDecimal(valor);

        if (resultado.signum() < 0) {

            throw new NumberFormatException(
                    "O valor não pode ser negativo."
            );
        }

        if (resultado.scale() > 2) {

            throw new NumberFormatException(
                    "Informe no máximo duas casas decimais."
            );
        }

        return resultado.setScale(
                2,
                RoundingMode.UNNECESSARY
        );
    }

    // =====================================================
    // ATUALIZAR DIFERENÇA
    // =====================================================

    private void atualizarDiferenca() {

        if (resumo == null) {

            lblDiferenca.setText("-");
            lblSituacao.setText(
                    "Aguardando resumo financeiro."
            );

            return;
        }

        try {

            BigDecimal valorContado =
                    converterValor(
                            txtValorFechamento.getText()
                    );

            BigDecimal diferenca =
                    resumo.calcularDiferenca(
                            valorContado
                    );

            lblDiferenca.setText(
                    formatoMoeda.format(diferenca)
            );

            int comparacao =
                    diferenca.compareTo(BigDecimal.ZERO);

            if (comparacao == 0) {

                lblDiferenca.setForeground(VERDE);

                lblSituacao.setText(
                        "Caixa conferido: valores corretos."
                );

                lblSituacao.setForeground(VERDE);

            } else if (comparacao < 0) {

                lblDiferenca.setForeground(VERMELHO);

                lblSituacao.setText(
                        "Falta de dinheiro no caixa."
                );

                lblSituacao.setForeground(VERMELHO);

            } else {

                lblDiferenca.setForeground(
                        new Color(170, 110, 20)
                );

                lblSituacao.setText(
                        "Sobra de dinheiro no caixa."
                );

                lblSituacao.setForeground(
                        new Color(170, 110, 20)
                );
            }

        } catch (NumberFormatException e) {

            lblDiferenca.setText("-");

            lblSituacao.setText(
                    "Informe o dinheiro contado."
            );

            lblSituacao.setForeground(CINZA);
        }
    }

    // =====================================================
    // FECHAR CAIXA
    // =====================================================

    private void fecharCaixa() {

        if (resumo == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Atualize o resumo financeiro.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        BigDecimal valorContado;

        try {

            valorContado = converterValor(
                    txtValorFechamento.getText()
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Valor inválido",
                    JOptionPane.WARNING_MESSAGE
            );

            txtValorFechamento.requestFocusInWindow();
            return;
        }

        String observacao =
                txtObservacao.getText().trim();

        if (observacao.length() > 500) {

            JOptionPane.showMessageDialog(
                    this,
                    "A observação deve ter no máximo 500 caracteres.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        BigDecimal diferencaPrevia =
                resumo.calcularDiferenca(
                        valorContado
                );

        String mensagem =
                "Deseja realmente fechar o caixa?\n\n"
                + "Total de vendas: "
                + formatoMoeda.format(
                        resumo.getTotalVendas()
                )
                + "\nDinheiro esperado: "
                + formatoMoeda.format(
                        resumo.getDinheiroEsperado()
                )
                + "\nDinheiro contado: "
                + formatoMoeda.format(valorContado)
                + "\nDiferença: "
                + formatoMoeda.format(diferencaPrevia)
                + "\n\nEsta operação encerrará a sessão.";

        int resposta = JOptionPane.showConfirmDialog(
                this,
                mensagem,
                "Confirmar fechamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        btnFechar.setEnabled(false);
        btnAtualizar.setEnabled(false);

        SwingWorker<ResumoFechamentoCaixa, Void> worker =
                new SwingWorker<>() {

            @Override
            protected ResumoFechamentoCaixa doInBackground()
                    throws SQLException {

                return caixaDAO.fecharComConferencia(
                        caixa.getIdTabelaCaixa(),
                        valorContado,
                        observacao
                );
            }

            @Override
            protected void done() {

                try {

                    ResumoFechamentoCaixa resumoFinal =
                            get();

                    LocalDateTime agora =
                            LocalDateTime.now();

                    caixa.setDataFechamento(agora);
                    caixa.setValorFechamento(valorContado);
                    caixa.setStatus(StatusCaixa.FECHADO);

                    BigDecimal diferencaFinal =
                            resumoFinal.calcularDiferenca(
                                    valorContado
                            );

                    JOptionPane.showMessageDialog(
                            CaixaFechamentoGUI.this,
                            "Caixa fechado com sucesso!\n\n"
                            + "Valor esperado: "
                            + formatoMoeda.format(
                                    resumoFinal.getDinheiroEsperado()
                            )
                            + "\nValor contado: "
                            + formatoMoeda.format(
                                    valorContado
                            )
                            + "\nDiferença: "
                            + formatoMoeda.format(
                                    diferencaFinal
                            ),
                            "Fechamento concluído",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    LoginGUI login = new LoginGUI();
                    login.setVisible(true);

                    dispose();

                } catch (Exception e) {

                    btnFechar.setEnabled(true);
                    btnAtualizar.setEnabled(true);

                    JOptionPane.showMessageDialog(
                            CaixaFechamentoGUI.this,
                            "Não foi possível fechar o caixa:\n"
                                    + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}

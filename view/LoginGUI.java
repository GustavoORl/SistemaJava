package view;

import dao.UsuarioDAO;
import model.Usuario;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Tela inicial do CoStock: autentica no banco e direciona por cargo. */
public class LoginGUI extends JFrame {
    private static final Color VERDE = Color.decode("#173C38");
    private static final Color FUNDO = Color.decode("#F5F7F6");
    private static final Color TEXTO = Color.decode("#20312E");
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;

    public LoginGUI() {
        super("CoStock | Entrar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(490, 540);
        setMinimumSize(new Dimension(450, 520));
        setLocationRelativeTo(null);
        criarComponentes();
    }

    private void criarComponentes() {
        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(FUNDO);
        fundo.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(fundo);

        JPanel cartao = new JPanel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(new EmptyBorder(34, 38, 34, 38));
        cartao.setPreferredSize(new Dimension(400, 435));
        fundo.add(cartao);

        JLabel marca = new JLabel("CoStock");
        marca.setFont(new Font("SansSerif", Font.BOLD, 32));
        marca.setForeground(VERDE);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(marca);
        cartao.add(Box.createVerticalStrut(8));

        JLabel descricao = new JLabel("Acesse sua conta para continuar");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descricao.setForeground(Color.decode("#64746F"));
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(descricao);
        cartao.add(Box.createVerticalStrut(35));

        cartao.add(rotulo("E-mail"));
        cartao.add(Box.createVerticalStrut(7));
        txtEmail = new JTextField();
        estilizarCampo(txtEmail);
        cartao.add(txtEmail);
        cartao.add(Box.createVerticalStrut(18));

        cartao.add(rotulo("Senha"));
        cartao.add(Box.createVerticalStrut(7));
        txtSenha = new JPasswordField();
        estilizarCampo(txtSenha);
        cartao.add(txtSenha);
        cartao.add(Box.createVerticalStrut(26));

        btnEntrar = new JButton("Entrar no sistema");
        btnEntrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnEntrar.setBackground(VERDE);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setOpaque(true);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.addActionListener(e -> realizarLogin());
        cartao.add(btnEntrar);
        cartao.add(Box.createVerticalStrut(15));

        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMensagem.setForeground(Color.decode("#B42318"));
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(lblMensagem);
        getRootPane().setDefaultButton(btnEntrar);
    }

    private JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(VERDE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CFDCD6")),
                new EmptyBorder(9, 12, 9, 12)));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        char[] caracteres = txtSenha.getPassword();
        String senha = new String(caracteres);
        Arrays.fill(caracteres, '\0');

        if (email.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Informe seu e-mail e sua senha.");
            return;
        }

        btnEntrar.setEnabled(false);
        btnEntrar.setText("Verificando...");
        lblMensagem.setText(" ");

        // Executa a consulta fora da thread gráfica para não congelar a tela.
        new SwingWorker<Usuario, Void>() {
            @Override protected Usuario doInBackground() {
                return usuarioDAO.autenticar(email, senha);
            }
            @Override protected void done() {
                btnEntrar.setEnabled(true);
                btnEntrar.setText("Entrar no sistema");
                try {
                    Usuario usuario = get();
                    if (usuario == null) {
                        lblMensagem.setText("E-mail ou senha inválidos, ou usuário inativo.");
                        txtSenha.setText("");
                        return;
                    }
                    direcionarUsuario(usuario);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    lblMensagem.setText("A autenticação foi interrompida.");
                } catch (ExecutionException ex) {
                    lblMensagem.setText("Falha ao consultar o banco de dados.");
                    JOptionPane.showMessageDialog(LoginGUI.this,
                            ex.getCause().getMessage(), "Erro de conexão", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void direcionarUsuario(Usuario usuario) {
        String cargo = usuario.getCargo() == null ? "" : usuario.getCargo().getNome().trim();
        if (cargo.equalsIgnoreCase("Administrador")) {
            new AdministradorGUI().setVisible(true);
            dispose();
        } else if (cargo.equalsIgnoreCase("Operador de Caixa") || cargo.equalsIgnoreCase("Caixa")) {
            new CaixaAberturaGUI(usuario).setVisible(true);
            dispose();
        } else if (cargo.equalsIgnoreCase("Gerente")) {
            JOptionPane.showMessageDialog(this,
                    "Login realizado. A tela do gerente ainda não foi implementada.",
                    "Área do gerente", JOptionPane.INFORMATION_MESSAGE);
            // Mantém o login aberto até a tela do gerente existir.
        } else {
            JOptionPane.showMessageDialog(this,
                    "O cargo '" + cargo + "' ainda não possui uma tela associada.",
                    "Acesso não configurado", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new LoginGUI().setVisible(true);
        });
    }
}

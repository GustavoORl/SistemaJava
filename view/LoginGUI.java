
package view;

import dao.UsuarioDAO;
import java.awt.*;
import javax.swing.*;
import model.Usuario;

public class LoginGUI extends JFrame {

    private JPanel painelPrincipal;
    private JLabel lblTitulo;
    private JLabel lblEmail;
    private JLabel lblSenha;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;

    private UsuarioDAO usuarioDAO;

    public LoginGUI() {

        usuarioDAO = new UsuarioDAO();

        setTitle("Sistema de Mercado - Login");
        setSize(420, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        criarComponentes();
    }

    private void criarComponentes() {

        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(null);
        painelPrincipal.setBackground(new Color(245, 247, 250));

        lblTitulo = new JLabel("SISTEMA DE MERCADO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(30, 60, 90));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(30, 35, 340, 40);

        JLabel lblSubtitulo = new JLabel("Acesse sua conta");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setBounds(30, 80, 340, 25);

        lblEmail = new JLabel("E-mail");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        lblEmail.setBounds(45, 140, 300, 25);

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 15));
        txtEmail.setBounds(45, 170, 310, 40);

        lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 14));
        lblSenha.setBounds(45, 230, 300, 25);

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 15));
        txtSenha.setBounds(45, 260, 310, 40);

        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(new Color(0, 128, 128));
        btnEntrar.setBounds(45, 330, 310, 45);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblMensagem = new JLabel("");
        lblMensagem.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        lblMensagem.setBounds(30, 385, 340, 25);

        // Evento do botão
        btnEntrar.addActionListener(e -> realizarLogin());

        // Permite apertar Enter para entrar
        getRootPane().setDefaultButton(btnEntrar);

        painelPrincipal.add(lblTitulo);
        painelPrincipal.add(lblSubtitulo);
        painelPrincipal.add(lblEmail);
        painelPrincipal.add(txtEmail);
        painelPrincipal.add(lblSenha);
        painelPrincipal.add(txtSenha);
        painelPrincipal.add(btnEntrar);
        painelPrincipal.add(lblMensagem);

        add(painelPrincipal);
    }

   
private void realizarLogin() {

    String email = txtEmail.getText().trim();
    String senha = new String(txtSenha.getPassword());

    if (email.isEmpty() || senha.isEmpty()) {
        lblMensagem.setText("Preencha todos os campos.");
        return;
    }

    Usuario usuario = usuarioDAO.autenticar(email, senha);

    if (usuario == null) {
        lblMensagem.setText(
            "E-mail ou senha inválidos, ou usuário inativo!"
        );

        txtSenha.setText("");
        return;
    }

    // Obtém o nome do cargo do usuário autenticado
    String cargo = usuario.getCargo().getNome();

    // Verifica o cargo e direciona para a tela correspondente
    if (cargo.equalsIgnoreCase("administrador")) {

        CoStockDashboard dashboard = new CoStockDashboard();
        dashboard.setVisible(true);

        dispose();

    } else if (cargo.equalsIgnoreCase("caixa")) {

          CaixaAberturaGUI telaAbertura =
            new CaixaAberturaGUI(usuario);

    telaAbertura.setVisible(true);

    dispose();

    } else {

        JOptionPane.showMessageDialog(
            this,
            "Não existe uma tela disponível para o cargo: " + cargo,
            "Acesso não disponível",
            JOptionPane.WARNING_MESSAGE
        );
    }
}

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
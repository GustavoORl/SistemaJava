package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginGUI;

/** Ponto de entrada oficial do CoStock: sempre começa pelo login. */
public class CoStock {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new LoginGUI().setVisible(true);
        });
    }
}

package view;

import model.Usuario;

/** Painel do gerente com navegação limitada a caixa e estoque. */
public class GerenteGUI extends AdministradorGUI {
    public GerenteGUI(Usuario usuario) {
        super(usuario, true);
    }
}

package Interfaz.login;

import Interfaz.cambiarclave.ControllerCambiarClave;
import Interfaz.cambiarclave.cambiarclaveView;
import logic.Service;
import logic.Sesion;
import logic.Usuario;

import javax.swing.*;
import java.awt.*;

public class ControllerLogin {

    private final ModelLogin model;
    private final LoginView view;

    public ControllerLogin(ModelLogin model, LoginView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
    }

    public void login() {
        String id = view.getTxtUsuario().getText().trim();
        String clave = new String(view.getTxtClave().getPassword()).trim();

        if (id.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar usuario y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = Service.instance().login(id, clave);

            if (usuario == null) {
                JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Sesion.setUsuario(usuario);
            model.setCurrentUser(usuario);
            view.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cambiarClave() {
        String id = view.getTxtUsuario().getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese el ID del usuario cuya clave desea cambiar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuario = Service.instance().buscarPorId(id);
        if (usuario == null) {
            JOptionPane.showMessageDialog(view, "No existe un usuario con ese ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(view);
        if (owner == null) owner = view;
        cambiarclaveView dialogo = new cambiarclaveView(owner);
        new ControllerCambiarClave(dialogo, usuario);
        dialogo.setVisible(true);
    }

    public void cancel() {
        Sesion.logout();
        view.dispose();
    }

    public void clear() {
        view.getTxtUsuario().setText("");
        view.getTxtClave().setText("");
    }
}

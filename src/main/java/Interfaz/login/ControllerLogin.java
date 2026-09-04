package Interfaz.login;

import logic.Service;
import logic.Sesion;
import logic.Usuario;

import javax.swing.*;

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
            // Se consulta el servicio (capturando cualquier excepción de lectura/XML)
            Usuario usuario = Service.instance().login(id, clave);

            if (usuario == null) {
                JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Asignación de la sesión estática y del modelo
            Sesion.setUsuario(usuario);
            model.setCurrentUser(usuario);

            // Cierra el JDialog modal para liberar el hilo y continuar hacia doRun()
            view.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
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
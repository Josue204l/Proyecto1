package Interfaz.cambiarclave;

import logic.Usuario;

import javax.swing.*;

public class ControllerCambiarClave {

    private final ModelCambiarClave model;
    private final cambiarclaveView view;

    public ControllerCambiarClave(cambiarclaveView view, Usuario usuario) {
        this.view = view;
        this.model = new ModelCambiarClave(usuario);
        view.setController(this);
    }

    public void cambiar() {
        String actual = new String(view.getClaveActual().getPassword());
        String nueva = new String(view.getClaveNueva().getPassword());
        String confirmar = new String(view.getClaveConfirmar().getPassword());
        try {
            model.cambiar(actual, nueva, confirmar);
            JOptionPane.showMessageDialog(view, "Clave cambiada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

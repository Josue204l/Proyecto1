package Interfaz.cambiarclave;

import Interfaz.Highlighter;
import javax.swing.*;
import java.awt.*;

public class cambiarclaveView extends JDialog {
    private JPanel mainPanel;
    private JPasswordField txtClaveActual;
    private JPasswordField txtClaveNueva;
    private JPasswordField txtConfirmacion;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public cambiarclaveView(Frame parent) {
        super(parent, "Cambiar Contraseña", true); // modal
        setContentPane(mainPanel);

        // ========== ASIGNACIÓN DE ICONOS A LOS BOTONES ==========
        // Botón Guardar
        if (btnGuardar != null) {
            btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/save.png")));
        }

        // Botón Cancelar
        if (btnCancelar != null) {
            btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/error.png")));
        }
        // ========================================================

        // CAMBIO: Listener de resaltado para los campos de contraseña
        Highlighter highlighter = new Highlighter();

        if (txtClaveActual != null) {
            txtClaveActual.addMouseListener(highlighter);
        }

        if (txtClaveNueva != null) {
            txtClaveNueva.addMouseListener(highlighter);
        }

        if (txtConfirmacion != null) {
            txtConfirmacion.addMouseListener(highlighter);
        }

        pack();
        setLocationRelativeTo(parent);

        if (btnCancelar != null) {
            btnCancelar.addActionListener(e -> dispose());
        }
    }

    public JPanel getMainPanel() { return mainPanel; }
    public JPasswordField getClaveActual() { return txtClaveActual; }
    public JPasswordField getClaveNueva() { return txtClaveNueva; }
    public JPasswordField getClaveConfirmar() { return txtConfirmacion; }
    public JButton getAceptarButton() { return btnGuardar; }
    public JButton getCancelarButton() { return btnCancelar; }
}
package Interfaz.login;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JDialog {

    // Paneles principales según el .form
    private JPanel contentPane;
    private JPanel icono;
    private JPanel panel;

    // Campos de texto y etiquetas
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JLabel ID;
    private JLabel Clave;

    // Botones según los bindings del .form
    private JButton ingresarButton;
    private JButton btnCancelar;
    private JButton cambiarButton;

    private ControllerLogin controller;

    public LoginView(Frame parent) {
        super(parent, "Inicio de Sesión", true); // true = modal
        setContentPane(contentPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Listener opcional para cancelar/cerrar
        if (btnCancelar != null) {
            btnCancelar.addActionListener(e -> dispose());
        }

        pack();
        setLocationRelativeTo(parent);
    }

    public void setController(ControllerLogin controller) {
        this.controller = controller;
        if (ingresarButton != null) {
            ingresarButton.addActionListener(e -> controller.login());
        }
    }

    // Getters
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtClave() { return txtClave; }
    public JButton getIngresarButton() { return ingresarButton; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getCambiarButton() { return cambiarButton; }
    public JPanel getContentPane() { return contentPane; }
}
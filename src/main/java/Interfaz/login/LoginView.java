package Interfaz.login;

import utils.UiHelper;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JDialog {

    private JPanel contentPane;
    private JPanel icono;
    private JPanel panel;

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JLabel ID;
    private JLabel Clave;

    private JButton ingresarButton;
    private JButton btnCancelar;
    private JButton cambiarButton;

    private ControllerLogin controller;

    public LoginView(Frame parent) {
        super(parent, "Inicio de Sesión", true);

        if (contentPane == null) {
            contentPane = new JPanel();
        }

        setContentPane(contentPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ImageIcon appIcon = UiHelper.icono("icono.png");
        if (appIcon != null) setIconImage(appIcon.getImage());

        UiHelper.setIcon(ingresarButton, "check.png");
        UiHelper.setIcon(btnCancelar, "door.png");
        UiHelper.setIcon(cambiarButton, "password.png");

        configurarEventosBasicos();
        pack();
        setLocationRelativeTo(parent != null ? parent : null);
    }

    private void configurarEventosBasicos() {
        if (btnCancelar != null) {
            btnCancelar.addActionListener(e -> {
                if (controller != null) {
                    controller.cancel();
                } else {
                    dispose();
                }
            });
        }
    }

    public void setController(ControllerLogin controller) {
        this.controller = controller;

        if (ingresarButton != null) {
            ingresarButton.addActionListener(e -> controller.login());
        }

        if (cambiarButton != null) {
            cambiarButton.addActionListener(e -> controller.cambiarClave());
        }
    }

    // --- Getters de Componentes ---
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtClave() { return txtClave; }
    public JButton getIngresarButton() { return ingresarButton; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getCambiarButton() { return cambiarButton; }

    @Override
    public JPanel getContentPane() { return contentPane; }
}
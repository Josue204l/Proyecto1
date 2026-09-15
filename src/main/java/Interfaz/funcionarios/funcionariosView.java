package Interfaz.funcionarios;

import utils.UiHelper;

import javax.swing.*;

public class funcionariosView {

    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JButton button2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;
    private JTable table1;

    private ControllerFuncionario controller;

    public funcionariosView() {
        if (button1 != null) button1.setText("Buscar");
        if (button2 != null) button2.setText("Imprimir");
        UiHelper.setIcon(button1, "search.png");
        UiHelper.setIcon(button2, "pdf.png");
        UiHelper.setIcon(guardarButton, "save.png");
        UiHelper.setIcon(borrarButton, "error.png");
        UiHelper.setIcon(limpiarButton, "broom.png");
    }

    public void setController(ControllerFuncionario controller) {
        this.controller = controller;
        configurarListeners();
    }

    private void configurarListeners() {
        if (guardarButton != null) guardarButton.addActionListener(e -> controller.guardar());
        if (borrarButton != null) borrarButton.addActionListener(e -> controller.eliminar());
        if (limpiarButton != null) limpiarButton.addActionListener(e -> controller.limpiar());
        if (button1 != null) button1.addActionListener(e -> controller.buscar());
        if (button2 != null) button2.addActionListener(e -> controller.print());

        if (table1 != null) {
            table1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table1.getSelectedRow() >= 0) {
                    controller.cargarSeleccionado();
                }
            });
        }
    }

    // --- Contenedor Principal ---
    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    // --- Getters Adaptados para ControllerFuncionario ---
    public JTable getTable() { return table1; }
    public JTable getTablaFuncionarios() { return table1; }

    public JButton getBuscarButton() { return button1; }
    public JButton getImprimirButton() { return button2; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }

    // Campos de Búsqueda
    public JTextField getTxtBuscarId() { return textField1; }
    public JTextField getTxtBuscarNombre() { return textField2; }

    // Campos de Formulario
    public JTextField getTxtId() { return textField3; }
    public JTextField getTxtNombre() { return textField4; }
    public JTextField getTxtTelefono() { return textField5; }
}
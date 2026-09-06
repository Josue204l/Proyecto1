package Interfaz.funcionarios;

import javax.swing.*;

public class funcionariosView {
    private JPanel panel1;
    private JTable table1;
    private JTextField textField1; // Buscar por ID
    private JTextField textField2; // Buscar por Nombre

    // Vinculados con button1 y button2 del .form
    private JButton button1; // Buscar
    private JButton button2; // Imprimir

    private JTextField textField3; // ID Formulario
    private JTextField textField4; // Nombre Formulario
    private JTextField textField5; // Teléfono Formulario
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;

    public funcionariosView() {}

    // Panel Principal
    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    // Tabla
    public JTable getTablaFuncionarios() {
        if (table1 == null) {
            table1 = new JTable();
        }
        return table1;
    }

    // Campos de Búsqueda
    public JTextField getTxtBuscarId() { return textField1; }
    public JTextField getTxtBuscarNombre() { return textField2; }

    // Campos de Formulario
    public JTextField getTxtId() { return textField3; }
    public JTextField getTxtNombre() { return textField4; }
    public JTextField getTxtTelefono() { return textField5; }

    // Botones (mapeados a las variables reales del .form)
    public JButton getBuscarButton() { return button1; }
    public JButton getImprimirButton() { return button2; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }
}
package Interfaz.funcionarios;

import javax.swing.*;

public class funcionariosView {
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

    public JPanel getMainPanel() { return new JPanel(); }
    public JTable getTable() { return table1; }
    public JTextField getTxtId() { return textField1; }
    public JTextField getTxtNombre() { return textField2; }
    public JTextField getTxtClave() { return textField3; }
    public JTextField getTxtTelefono() { return textField4; }
    public JTextField getTxtPuesto() { return textField5; }
    public JButton getBtnBuscar() { return button1; }
    public JButton getBtnImprimir() { return button2; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }
}

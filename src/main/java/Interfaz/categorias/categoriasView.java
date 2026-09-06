package Interfaz.categorias;

import javax.swing.*;

public class categoriasView {
    private JPanel panel1;
    private JTable table1;
    private JTextField textField1;
    private JButton buscarButton;
    private JButton imprimirButton;
    private JTextField textField2;
    private JTextField textField3;
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;

    public JPanel getMainPanel() { return panel1 != null ? panel1 : new JPanel(); }
    public JTable getTablaCategorias() { return table1; }
    public JTextField getTxtBuscarDescripcion() { return textField1; }
    public JTextField getTxtId() { return textField2; }
    public JTextField getTxtDescripcion() { return textField3; }
    public JButton getBuscarButton() { return buscarButton; }
    public JButton getImprimirButton() { return imprimirButton; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }
}
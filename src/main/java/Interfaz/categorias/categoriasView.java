package Interfaz.categorias;

import utils.UiHelper;

import javax.swing.*;
import java.awt.*;

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
    private ControllerCategoria controller;

    public categoriasView() {
        if (panel1 == null) {
            crearUi();
        }
        if (getTxtId() != null) {
            getTxtId().setEditable(false);
        }
    }

    private void crearUi() {
        panel1 = new JPanel(new BorderLayout(8, 8));
        panel1.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        textField1 = new JTextField(18);
        buscarButton = new JButton("Buscar");
        imprimirButton = new JButton("Imprimir");
        busqueda.add(new JLabel("Descripción"));
        busqueda.add(textField1);
        busqueda.add(buscarButton);
        busqueda.add(imprimirButton);

        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Categoría"));
        textField3 = new JTextField();
        textField3.setEditable(false);
        textField2 = new JTextField();
        form.add(new JLabel("ID"));
        form.add(textField3);
        form.add(new JLabel("Descripción"));
        form.add(textField2);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guardarButton = new JButton("Guardar");
        borrarButton = new JButton("Borrar");
        limpiarButton = new JButton("Limpiar");
        acciones.add(guardarButton);
        acciones.add(borrarButton);
        acciones.add(limpiarButton);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(busqueda, BorderLayout.NORTH);
        norte.add(form, BorderLayout.CENTER);
        norte.add(acciones, BorderLayout.SOUTH);

        table1 = new JTable();
        panel1.add(norte, BorderLayout.NORTH);
        panel1.add(new JScrollPane(table1), BorderLayout.CENTER);
    }

    public JPanel getMainPanel() { return panel1 != null ? panel1 : new JPanel(); }
    public JTable getTable() { return table1; }
    public JTextField getTxtBuscar() { return textField1; }
    public JTextField getTxtId() { return textField3; }
    public JTextField getTxtDescripcion() { return textField2; }
    public JButton getBuscarButton() { return buscarButton; }
    public JButton getImprimirButton() { return imprimirButton; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }

    public void setController(ControllerCategoria controller) {
        this.controller = controller;
        addListeners();
    }

    public void addListeners() {
        if (controller == null) return;
        UiHelper.bind(guardarButton, e -> controller.guardar());
        UiHelper.bind(borrarButton, e -> controller.eliminar());
        UiHelper.bind(limpiarButton, e -> controller.limpiar());
        UiHelper.bind(buscarButton, e -> controller.buscar());
        UiHelper.bind(imprimirButton, e -> controller.print());
        if (table1 != null) {
            table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) controller.cargarSeleccionado();
            });
        }
    }
}

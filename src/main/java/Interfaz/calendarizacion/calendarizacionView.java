package Interfaz.calendarizacion;

import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class calendarizacionView implements PropertyChangeListener {

    private JPanel panel;
    private JTable table1;
    private JTextField textField1;
    private JButton button1;
    private JComboBox<Categoria> comboBox1;
    private JButton btnCargar;
    private JButton btnImprimir;

    private ControllerCalendarizacion controller;
    private ModelCalendarizacion model;

    private static final String[] COLUMNAS = {"ID", "Título", "Fecha", "Hora Inicio", "Hora Fin", "Recursos", "Categorías", "Estado"};
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public calendarizacionView() {
        if (btnCargar != null) {
            btnCargar.addActionListener(e -> {
                if (controller != null) controller.filtrar();
            });
        }
    }

    public JPanel getMainPanel() {
        return panel;
    }

    public JTextField getTxtFecha() {
        return textField1;
    }

    public JComboBox<Categoria> getCmbCategoria() {
        return comboBox1;
    }

    public JButton getBtnCargar() {
        return btnCargar;
    }

    public JButton getBtnImprimir() {
        return btnImprimir;
    }

    public JTable getTblCalendarizacion() {
        return table1;
    }

    public void setController(ControllerCalendarizacion controller) {
        this.controller = controller;
    }

    public void setModel(ModelCalendarizacion model) {
        this.model = model;
        this.model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelCalendarizacion.CATEGORIAS.equals(evt.getPropertyName())) {
            DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
            if (model != null && model.getCategorias() != null) {
                for (Categoria cat : model.getCategorias()) {
                    comboModel.addElement(cat);
                }
            }
            if (comboBox1 != null) {
                comboBox1.setModel(comboModel);
            }
        } else if (ModelCalendarizacion.RESERVAS.equals(evt.getPropertyName())) {
            if (model != null) {
                actualizarTabla(model.getReservas());
            }
        }

        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
    }

    private void actualizarTabla(List<Reserva> reservas) {
        if (table1 == null) return;

        DefaultTableModel tableModel = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        if (reservas != null) {
            for (Reserva r : reservas) {
                String recursosStr = (r.getRecursosAsignados() != null) ?
                        r.getRecursosAsignados().stream().map(Recurso::getNombre).collect(Collectors.joining(", ")) : "";

                String categoriasStr = (r.getCategoriasRequeridas() != null) ?
                        r.getCategoriasRequeridas().stream().map(Categoria::getNombre).collect(Collectors.joining(", ")) : "";

                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getTitulo(),
                        r.getFecha() != null ? r.getFecha().format(FMT_FECHA) : "",
                        r.getHoraInicio() != null ? r.getHoraInicio().format(FMT_HORA) : "",
                        r.getHoraFin() != null ? r.getHoraFin().format(FMT_HORA) : "",
                        recursosStr,
                        categoriasStr,
                        r.getEstado()
                });
            }
        }
        table1.setModel(tableModel);
    }
}
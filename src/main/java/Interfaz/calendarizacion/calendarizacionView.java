package Interfaz.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
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

    private DatePicker datePicker;
    private ControllerCalendarizacion controller;
    private ModelCalendarizacion model;

    private static final String[] COLUMNAS = {"ID", "Título", "Fecha", "Hora Inicio", "Hora Fin", "Recursos", "Categorías", "Estado"};
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // ========== CONSTRUCTOR MODIFICADO PARA ASIGNAR ICONOS ==========
    public calendarizacionView() {
        this.datePicker = new DatePicker();

        // Icono para el Botón Cargar / Filtrar
        if (btnCargar != null) {
            btnCargar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/reload.png")));
        }

        // Icono para el Botón Imprimir PDF o Reporte
        if (btnImprimir != null) {
            btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/pdf.png")));
        }

        configurarListeners();
    }
    // ================================================================

    private void configurarListeners() {
        if (btnCargar != null) {
            btnCargar.addActionListener(e -> {
                if (controller != null) controller.filtrar();
            });
        }
        if (btnImprimir != null) {
            btnImprimir.addActionListener(e -> {
                if (controller != null) controller.print();
            });
        }
    }

    public void setController(ControllerCalendarizacion controller) {
        this.controller = controller;
    }

    public void setModel(ModelCalendarizacion model) {
        this.model = model;
        if (this.model != null) {
            this.model.addPropertyChangeListener(this);
        }
    }

    /**
     * Carga de categorías requerida por el ControllerCalendarizacion
     */
    public void cargarCategorias(List<Categoria> categorias) {
        DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
        if (categorias != null) {
            for (Categoria cat : categorias) {
                comboModel.addElement(cat);
            }
        }
        if (comboBox1 != null) {
            comboBox1.setModel(comboModel);
        }
    }

    /**
     * Adaptador para exponer DatePicker a ControllerCalendarizacion
     */
    public DatePicker getDatePicker() {
        if (datePicker == null) {
            datePicker = new DatePicker();
        }
        if (textField1 != null && !textField1.getText().isBlank()) {
            try {
                LocalDate parsed = LocalDate.parse(textField1.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                datePicker.setDate(parsed);
            } catch (Exception ignored) {
                try {
                    LocalDate parsedIso = LocalDate.parse(textField1.getText().trim());
                    datePicker.setDate(parsedIso);
                } catch (Exception ignored2) {}
            }
        }
        return datePicker;
    }

    // --- Getters de componentes ---
    public JPanel getMainPanel() { return panel; }
    public JTextField getTxtFecha() { return textField1; }
    public JComboBox<Categoria> getCmbCategoria() { return comboBox1; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTblCalendarizacion() { return table1; }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelCalendarizacion.CATEGORIAS.equals(evt.getPropertyName())) {
            if (evt.getNewValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<Categoria> lista = (List<Categoria>) evt.getNewValue();
                cargarCategorias(lista);
            }
        }

        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
    }
}
package Interfaz.actividades;

import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.*;

public class actividadesView {
    private JPanel panel1;
    private JTextField textField1; // Filtro o fecha rápida
    private JButton button1;       // Botón de búsqueda / actualización
    private JButton btnImprimir;   // Botón de imprimir PDF
    private JTable table1;
    private JLabel lblSemana;      // Etiqueta del rango de la semana
    private DatePicker datePicker; // Selector de fecha

    private ControllerActividades controller;

    public actividadesView() {  // <---------- progreso hasta ahora, falta actividades y estadísticas de imagenes y faltan los listener de colores
        if (datePicker == null) {
            datePicker = new DatePicker();
        }
        if (lblSemana == null) {
            lblSemana = new JLabel("Semana");
        }
    }

    public void setController(ControllerActividades controller) {
        this.controller = controller;
        configurarListeners();
    }

    private void configurarListeners() {
        if (button1 != null) {
            button1.addActionListener(e -> controller.cargar());
        }
        if (btnImprimir != null) {
            btnImprimir.addActionListener(e -> controller.print());
        }
        if (datePicker != null) {
            datePicker.addDateChangeListener(e -> controller.cargar());
        }
    }

    // --- Getters requeridos por ControllerActividades ---
    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    public JTable getTable() {
        return table1;
    }

    public JTextField getTxtBuscar() {
        return textField1;
    }

    public JButton getBtnBuscar() {
        return button1;
    }

    public JButton getBtnImprimir() {
        return btnImprimir;
    }

    public JLabel getLblSemana() {
        return lblSemana;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }
}
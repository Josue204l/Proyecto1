package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.*;

public class estadisticasView {

    // --- Panel Principal (Coincide estrictamente con binding="mainPanel" del .form) ---
    private JPanel mainPanel;

    // --- Sección Recursos (Coincide con bindings del .form) ---
    private JButton btnCargarRecursos;
    private JTable tableRecursos;
    private JPanel chartRecursos;

    // --- Sección Actividades (Coincide con bindings del .form) ---
    private JButton btnCargarActividades;
    private JTable tableActividades;
    private JPanel chartActividades;

    // --- Selectores de Fechas ---
    // (Instanciados programáticamente o mediante un panel personalizado para evitar romper el .form)
    private DatePicker dpRecursosDesde = new DatePicker();
    private DatePicker dpRecursosHasta = new DatePicker();
    private DatePicker dpActividadesDesde = new DatePicker();
    private DatePicker dpActividadesHasta = new DatePicker();

    private ControllerEstadisticas controller;

    public estadisticasView() {
        // IntelliJ inyecta automáticamente los componentes definidos en el .form
    }

    public void setController(ControllerEstadisticas controller) {
        this.controller = controller;

        if (btnCargarRecursos != null) {
            btnCargarRecursos.addActionListener(e -> controller.cargarRecursos());
        }

        if (btnCargarActividades != null) {
            btnCargarActividades.addActionListener(e -> controller.cargarActividades());
        }
    }

    // --- GETTERS ---
    public JPanel getMainPanel() {
        return mainPanel;
    }

    // Recursos
    public DatePicker getDpRecursosDesde() { return dpRecursosDesde; }
    public DatePicker getDpRecursosHasta() { return dpRecursosHasta; }
    public JTable getTableRecursos() { return tableRecursos; }
    public JPanel getChartRecursos() { return chartRecursos; }

    // Actividades
    public DatePicker getDpActividadesDesde() { return dpActividadesDesde; }
    public DatePicker getDpActividadesHasta() { return dpActividadesHasta; }
    public JTable getTableActividades() { return tableActividades; }
    public JPanel getChartActividades() { return chartActividades; }
}
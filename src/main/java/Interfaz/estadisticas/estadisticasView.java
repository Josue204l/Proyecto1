package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import utils.UiHelper;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class estadisticasView {

    private JPanel mainPanel;

    private JButton btnCargarRecursos;
    private JButton btnPdfRecursos;
    private JTable tableRecursos;
    private JPanel chartRecursos;
    private JPanel panelRecursosDesde;
    private JPanel panelRecursosHasta;

    private JButton btnCargarActividades;
    private JButton btnPdfActividades;
    private JTable tableActividades;
    private JPanel chartActividades;
    private JPanel panelActividadesDesde;
    private JPanel panelActividadesHasta;

    private DatePicker dpRecursosDesde = new DatePicker();
    private DatePicker dpRecursosHasta = new DatePicker();
    private DatePicker dpActividadesDesde = new DatePicker();
    private DatePicker dpActividadesHasta = new DatePicker();

    private ControllerEstadisticas controller;

    public estadisticasView() {
        initCustomComponents();
    }

    private void initCustomComponents() {
        LocalDate hoy = LocalDate.now();
        dpRecursosDesde.setDate(hoy.minusMonths(1));
        dpRecursosHasta.setDate(hoy);
        dpActividadesDesde.setDate(hoy.minusMonths(1));
        dpActividadesHasta.setDate(hoy);

        if (panelRecursosDesde != null) { panelRecursosDesde.removeAll(); panelRecursosDesde.add(dpRecursosDesde, BorderLayout.CENTER); }
        if (panelRecursosHasta != null) { panelRecursosHasta.removeAll(); panelRecursosHasta.add(dpRecursosHasta, BorderLayout.CENTER); }
        if (panelActividadesDesde != null) { panelActividadesDesde.removeAll(); panelActividadesDesde.add(dpActividadesDesde, BorderLayout.CENTER); }
        if (panelActividadesHasta != null) { panelActividadesHasta.removeAll(); panelActividadesHasta.add(dpActividadesHasta, BorderLayout.CENTER); }

        UiHelper.setIcon(btnCargarRecursos, "search.png");
        UiHelper.setIcon(btnPdfRecursos, "pdf.png");
        UiHelper.setIcon(btnCargarActividades, "search.png");
        UiHelper.setIcon(btnPdfActividades, "pdf.png");
    }

    public void setController(ControllerEstadisticas controller) {
        this.controller = controller;

        if (btnCargarRecursos != null) btnCargarRecursos.addActionListener(e -> controller.cargarRecursos());
        if (btnCargarActividades != null) btnCargarActividades.addActionListener(e -> controller.cargarActividades());
        if (btnPdfRecursos != null) btnPdfRecursos.addActionListener(e -> controller.printRecursos());
        if (btnPdfActividades != null) btnPdfActividades.addActionListener(e -> controller.printActividades());

        SwingUtilities.invokeLater(() -> {
            if (this.controller != null) {
                this.controller.cargarRecursos();
                this.controller.cargarActividades();
            }
        });
    }

    public JPanel getMainPanel() { return mainPanel; }
    public DatePicker getDpRecursosDesde() { return dpRecursosDesde; }
    public DatePicker getDpRecursosHasta() { return dpRecursosHasta; }
    public JTable getTableRecursos() { return tableRecursos; }
    public JPanel getChartRecursos() { return chartRecursos; }
    public JButton getBtnPdfRecursos() { return btnPdfRecursos; }
    public DatePicker getDpActividadesDesde() { return dpActividadesDesde; }
    public DatePicker getDpActividadesHasta() { return dpActividadesHasta; }
    public JTable getTableActividades() { return tableActividades; }
    public JPanel getChartActividades() { return chartActividades; }
    public JButton getBtnPdfActividades() { return btnPdfActividades; }
}
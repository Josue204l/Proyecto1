package Interfaz.estadisticas;

import logic.Observer;
import logic.Service;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.PDFGenerator;

import java.time.LocalDate;
import java.util.Map;

public class ControllerEstadisticas implements Observer {

    private final estadisticasView view;
    private final ModelEstadisticas model;

    public ControllerEstadisticas(estadisticasView view, ModelEstadisticas model) {
        this.view = view;
        this.model = model;

        registrarEventos();
        Service.instance().addObserver(this);
        cargarDatos();
    }

    private void registrarEventos() {
        if (view.getCargarButton() != null) {
            view.getCargarButton().addActionListener(e -> cargarDatos());
        }
        if (view.getBtnFiltrar() != null) {
            view.getBtnFiltrar().addActionListener(e -> cargarDatos());
        }
        if (view.getBtnExportar() != null) {
            view.getBtnExportar().addActionListener(e -> {
                if (view.getTable() != null) {
                    PDFGenerator.generarReporteTabla("Estadisticas", view.getTable());
                }
            });
        }
    }

    public void cargarDatos() {
        LocalDate desde = parseFecha(view.getTxtFechaDesde() != null ? view.getTxtFechaDesde().getText() : "");
        LocalDate hasta = parseFecha(view.getTxtFechaHasta() != null ? view.getTxtFechaHasta().getText() : "");

        Map<String, Integer> datosRecursos = model.getUsoCategoriasPorPeriodo(desde, hasta);
        model.actualizarTablaRecursos(datosRecursos);

        if (view.getTable() != null) {
            view.getTable().setModel(model.getTableModel());
        }

        ChartPanel chart = generarGraficoRecursos(datosRecursos);
        view.mostrarGrafico(chart);
    }

    private ChartPanel generarGraficoRecursos(Map<String, Integer> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(entry.getValue(), "Reservas", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Categorías de Recursos Reservados", "Categoría", "Cantidad",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        return new ChartPanel(chart);
    }

    private LocalDate parseFecha(String texto) {
        if (texto == null || texto.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void update() {
        cargarDatos();
    }
}

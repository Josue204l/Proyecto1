package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ControllerEstadisticas {

    private final estadisticasView view;
    private final ModelEstadisticas model;

    public ControllerEstadisticas(estadisticasView view, ModelEstadisticas model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            view.setController(this);
        }
    }

    public ControllerEstadisticas(ModelEstadisticas model) {
        this(null, model);
    }

    public void cargarDatos() {
        cargarRecursos();
        cargarActividades();
    }

    public void cargarRecursos() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            List<EstadisticaFila> filas = model.calcularRecursos(rango[0], rango[1]);

            if (view.getTableRecursos() != null) {
                view.getTableRecursos().setModel(model.getTableRecursos());
            }

            pintarGrafico(view.getChartRecursos(), "Recursos reservados", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarActividades() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            List<EstadisticaFila> filas = model.calcularActividades(rango[0], rango[1]);

            if (view.getTableActividades() != null) {
                view.getTableActividades().setModel(model.getTableActividades());
            }

            pintarGrafico(view.getChartActividades(), "Actividades por semana", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate[] leerRango(DatePicker desdeCampo, DatePicker hastaCampo) throws Exception {
        LocalDate desde = desdeCampo != null ? desdeCampo.getDate() : null;
        LocalDate hasta = hastaCampo != null ? hastaCampo.getDate() : null;
        if (desde == null || hasta == null) {
            throw new Exception("Seleccione las fechas desde y hasta.");
        }
        if (hasta.isBefore(desde)) {
            throw new Exception("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
        return new LocalDate[]{desde, hasta};
    }

    private void pintarGrafico(JPanel contenedor, String titulo, List<EstadisticaFila> filas) {
        if (contenedor == null || filas == null) return;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EstadisticaFila f : filas) {
            dataset.addValue(f.getCantidad(), "Cantidad", f.getEtiqueta());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                titulo,
                "Categoría / Semana",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 220));

        contenedor.removeAll();
        contenedor.setLayout(new BorderLayout());
        contenedor.add(chartPanel, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    public ModelEstadisticas getModel() {
        return model;
    }
}
package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControllerEstadisticas {

    private final estadisticasView view;
    private final ModelEstadisticas model;

    public ControllerEstadisticas(estadisticasView view, ModelEstadisticas model) {
        this.view = view;
        this.model = model;
        if (view != null) view.setController(this);
        cargarRecursos();
        cargarActividades();
    }

    public ControllerEstadisticas(ModelEstadisticas model) {
        this(null, model);
    }

    public void cargarDatos() {
        cargarRecursos();
        cargarActividades();
    }

    public void cargarRecursos() {
        try {
            LocalDate[] rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            List<EstadisticaFila> filas = model.calcularRecursos(rango[0], rango[1]);
            if (view.getTableRecursos() != null) {
                view.getTableRecursos().setModel(model.getTableRecursos());
            }
            pintar(view.getChartRecursos(), "Recursos reservados", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarActividades() {
        try {
            LocalDate[] rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            List<EstadisticaFila> filas = model.calcularActividades(rango[0], rango[1]);
            if (view.getTableActividades() != null) {
                view.getTableActividades().setModel(model.getTableActividades());
            }
            pintar(view.getChartActividades(), "Actividades por semana", filas);
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

    private void pintar(BarChartPanel chart, String titulo, List<EstadisticaFila> filas) {
        if (chart == null) return;
        List<String> labels = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (EstadisticaFila f : filas) {
            labels.add(f.getEtiqueta());
            valores.add(f.getCantidad());
        }
        chart.setDatos(titulo, labels, valores);
    }

    public ModelEstadisticas getModel() {
        return model;
    }
}

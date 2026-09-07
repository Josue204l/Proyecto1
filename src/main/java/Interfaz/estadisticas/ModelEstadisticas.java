package Interfaz.estadisticas;

import data.Data;
import logic.Categoria;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class ModelEstadisticas {

    public static final String LISTA = "lista";

    private TableModelEstadisticas tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelEstadisticas() {
        this.tableModel = new TableModelEstadisticas(new ArrayList<>());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public TableModelEstadisticas getTableModel() { return tableModel; }

    /**
     * Cuenta cuántas reservas activas hay por categoría en el período dado.
     * Retorna mapa: nombre de categoría -> cantidad de reservas.
     */
    public Map<String, Integer> getUsoCategoriasPorPeriodo(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Reserva r : Data.getInstancia().getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;

            if (r.getCategoriasRequeridas() != null) {
                for (Categoria c : r.getCategoriasRequeridas()) {
                    String nombre = c.getNombre() != null ? c.getNombre() : c.getId();
                    conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
                }
            }
        }
        return conteo;
    }

    /**
     * Cuenta actividades (reservas activas) por semana del año en el período dado.
     * Retorna mapa: "Semana N (yyyy)" -> cantidad.
     */
    public Map<String, Integer> getActividadesPorSemana(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        WeekFields wf = WeekFields.of(Locale.getDefault());

        for (Reserva r : Data.getInstancia().getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;

            int semana = r.getFecha().get(wf.weekOfWeekBasedYear());
            int anio = r.getFecha().get(wf.weekBasedYear());
            String clave = "Semana " + semana + " (" + anio + ")";
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }
        return conteo;
    }

    public void actualizarTablaRecursos(Map<String, Integer> datos) {
        List<String[]> filas = datos.entrySet().stream()
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())})
                .collect(Collectors.toList());
        tableModel.setFilasGenericas(filas, new String[]{"Categoría", "Cantidad de Reservas"});
        propertyChangeSupport.firePropertyChange(LISTA, null, filas);
    }

    public void actualizarTablaActividades(Map<String, Integer> datos) {
        List<String[]> filas = datos.entrySet().stream()
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())})
                .collect(Collectors.toList());
        tableModel.setFilasGenericas(filas, new String[]{"Semana", "Cantidad de Actividades"});
        propertyChangeSupport.firePropertyChange(LISTA, null, filas);
    }
}

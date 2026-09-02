package Interfaz.actividades;

import logic.Recurso;
import logic.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class TableModelActividades extends AbstractTableModel {

    public static final int ID = 0;
    public static final int TITULO = 1;
    public static final int FECHA = 2;

    private final String[] cols = {"ID / Código", "Actividad", "Fecha"};
    private List<Reserva> filas;

    public TableModelActividades(List<Reserva> filas) {
        this.filas = filas;
    }

    public List<Reserva> getFilas() {
        return filas;
    }

    public void setFilas(List<Reserva> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public Reserva getRowAt(int row) {
        if (filas != null && row >= 0 && row < filas.size()) {
            return filas.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return filas != null ? filas.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int col) {
        return cols[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Reserva r = filas.get(row);
        switch (col) {
            case ID:
                return r.getId();
            case TITULO:
                if (r.getRecursosAsignados() != null && !r.getRecursosAsignados().isEmpty()) {
                    return r.getRecursosAsignados().stream()
                            .map(Recurso::getNombre)
                            .collect(Collectors.joining(", "));
                }
                return r.getTitulo() != null ? r.getTitulo() : "Sin asignar";
            case FECHA:
                return r.getFecha() != null ? r.getFecha().toString() : "";
            default:
                return "";
        }
    }
}
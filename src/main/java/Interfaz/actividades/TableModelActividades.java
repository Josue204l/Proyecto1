package Interfaz.actividades;

import logic.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModelActividades extends AbstractTableModel {

    public static final int ID = 0;
    public static final int TITULO = 1;
    public static final int FECHA = 2;
    public static final int HORARIO = 3;
    public static final int FUNCIONARIO = 4;
    public static final int ESTADO = 5;

    private final String[] cols = {"ID", "Actividad", "Fecha", "Horario", "Funcionario", "Estado"};
    private List<Reserva> filas;

    public TableModelActividades(List<Reserva> filas) {
        this.filas = filas;
    }

    public List<Reserva> getFilas() { return filas; }

    public void setFilas(List<Reserva> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public Reserva getRowAt(int row) {
        if (filas != null && row >= 0 && row < filas.size()) return filas.get(row);
        return null;
    }

    @Override
    public int getRowCount() { return filas != null ? filas.size() : 0; }

    @Override
    public int getColumnCount() { return cols.length; }

    @Override
    public String getColumnName(int col) { return cols[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Reserva r = filas.get(row);
        switch (col) {
            case ID: return r.getId();
            case TITULO: return r.getTitulo() != null ? r.getTitulo() : "";
            case FECHA: return r.getFecha() != null ? r.getFecha().toString() : "";
            case HORARIO:
                String ini = r.getHoraInicio() != null ? r.getHoraInicio().toString() : "";
                String fin = r.getHoraFin() != null ? r.getHoraFin().toString() : "";
                return ini + " - " + fin;
            case FUNCIONARIO:
                return r.getFuncionario() != null ? r.getFuncionario().getNombre() : "";
            case ESTADO: return r.getEstado() != null ? r.getEstado() : "";
            default: return "";
        }
    }
}

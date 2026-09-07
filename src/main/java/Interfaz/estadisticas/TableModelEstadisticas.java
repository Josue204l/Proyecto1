package Interfaz.estadisticas;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModelEstadisticas extends AbstractTableModel {

    private List<String[]> filas;
    private String[] columnas;

    public TableModelEstadisticas(List<String[]> filas) {
        this.filas = filas != null ? filas : new ArrayList<>();
        this.columnas = new String[]{"Categoría", "Cantidad de Reservas"};
    }

    public void setFilasGenericas(List<String[]> filas, String[] columnas) {
        this.filas = filas != null ? filas : new ArrayList<>();
        this.columnas = columnas;
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() { return filas.size(); }

    @Override
    public int getColumnCount() { return columnas.length; }

    @Override
    public String getColumnName(int col) { return columnas[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        String[] fila = filas.get(row);
        return (col < fila.length) ? fila[col] : "";
    }
}

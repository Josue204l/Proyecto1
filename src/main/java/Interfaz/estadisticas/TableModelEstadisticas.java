package Interfaz.estadisticas;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModelEstadisticas extends AbstractTableModel {

    private final String[] cols;
    private List<EstadisticaFila> filas;

    public TableModelEstadisticas(String colEtiqueta, String colCantidad) {
        this.cols = new String[]{colEtiqueta, colCantidad};
        this.filas = new ArrayList<>();
    }

    public void setFilas(List<EstadisticaFila> filas) {
        this.filas = filas != null ? filas : new ArrayList<>();
        fireTableDataChanged();
    }

    public List<EstadisticaFila> getFilas() {
        return filas;
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
        EstadisticaFila f = filas.get(row);
        return col == 0 ? f.getEtiqueta() : f.getCantidad();
    }
}

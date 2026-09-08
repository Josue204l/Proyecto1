//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Interfaz.calendarizacion;

import utils.Horarios;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import logic.Recurso;
import logic.Reserva;

public class TableModelCalendarizacion extends AbstractTableModel {
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private List<LocalTime> horas = Horarios.horasDelDia();
    private List<Recurso> recursos = new ArrayList();
    private String[][] celdas = new String[0][0];

    public TableModelCalendarizacion() {
    }

    public void setDatos(List<Recurso> recursosCategoria, List<Reserva> reservas, LocalDate fecha) {
        this.recursos = (List<Recurso>)(recursosCategoria != null ? recursosCategoria : new ArrayList());
        this.horas = Horarios.horasDelDia();
        this.celdas = new String[this.horas.size()][this.recursos.size()];
        if (fecha != null) {
            for(Reserva reserva : reservas) {
                if (reserva != null && reserva.isActiva() && reserva.getFecha() != null && fecha.equals(reserva.getFecha())) {
                    for(int c = 0; c < this.recursos.size(); ++c) {
                        Recurso recurso = (Recurso)this.recursos.get(c);
                        if (recurso != null && reserva.usaRecurso(recurso.getId())) {
                            for(int f = 0; f < this.horas.size(); ++f) {
                                if (reserva.cubreHora((LocalTime)this.horas.get(f))) {
                                    String actual = this.celdas[f][c];
                                    String etiqueta = reserva.etiquetaCelda();
                                    this.celdas[f][c] = actual != null && !actual.isBlank() ? actual + " | " + etiqueta : etiqueta;
                                }
                            }
                        }
                    }
                }
            }
        }

        this.fireTableStructureChanged();
    }

    public int getRowCount() {
        return this.horas.size();
    }

    public int getColumnCount() {
        return 1 + this.recursos.size();
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Hora";
        } else {
            Recurso r = (Recurso)this.recursos.get(column - 1);
            return r.getDescripcion() != null ? r.getDescripcion() : r.getId();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return ((LocalTime)this.horas.get(rowIndex)).format(FMT_HORA);
        } else {
            String valor = this.celdas[rowIndex][columnIndex - 1];
            return valor != null ? valor : "";
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}

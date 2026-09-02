package Interfaz.calendarizacion;

import Interfaz.AbstractTableModelBase;
import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TableModelCalendarizacion extends AbstractTableModelBase<Reserva> {

    public static final int ID = 0;
    public static final int TITULO = 1;
    public static final int FECHA = 2;
    public static final int HORA_INICIO = 3;
    public static final int HORA_FIN = 4;
    public static final int RECURSO = 5;
    public static final int CATEGORIA = 6;
    public static final int ESTADO = 7;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public TableModelCalendarizacion(int[] cols, List<Reserva> rows) {
        super(cols, rows);
    }

    @Override
    protected void initColNames() {
        colNames = new String[8];
        colNames[ID] = "ID";
        colNames[TITULO] = "Título";
        colNames[FECHA] = "Fecha";
        colNames[HORA_INICIO] = "Hora Inicio";
        colNames[HORA_FIN] = "Hora Fin";
        colNames[RECURSO] = "Recursos";
        colNames[CATEGORIA] = "Categorías";
        colNames[ESTADO] = "Estado";
    }

    @Override
    protected Object getPropetyAt(Reserva r, int col) {
        switch (cols[col]) {
            case ID:
                return r.getId();
            case TITULO:
                return r.getTitulo();
            case FECHA:
                return r.getFecha() != null ? r.getFecha().format(FMT_FECHA) : "";
            case HORA_INICIO:
                return r.getHoraInicio() != null ? r.getHoraInicio().format(FMT_HORA) : "";
            case HORA_FIN:
                return r.getHoraFin() != null ? r.getHoraFin().format(FMT_HORA) : "";
            case RECURSO:
                return r.getRecurso() != null ? r.getRecurso().getNombre() :
                        (r.getRecursosAsignados() != null ? r.getRecursosAsignados().stream().map(Recurso::getNombre).collect(Collectors.joining(", ")) : "");
            case CATEGORIA:
                return r.getCategoria() != null ? r.getCategoria().getNombre() :
                        (r.getCategoriasRequeridas() != null ? r.getCategoriasRequeridas().stream().map(Categoria::getNombre).collect(Collectors.joining(", ")) : "");
            case ESTADO:
                return r.getEstado();
            default:
                return "";
        }
    }
}
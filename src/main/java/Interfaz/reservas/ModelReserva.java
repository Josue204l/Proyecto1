package Interfaz.reservas;

import data.Data;
import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ModelReserva {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Reserva seleccionado;
    private TableModelReserva tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelReserva() {
        this.seleccionado = new Reserva();
        this.tableModel = new TableModelReserva(getReservas());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Reserva getSeleccionado() { return seleccionado; }

    public void setSeleccionado(Reserva seleccionado) {
        Reserva old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelReserva getTableModel() { return tableModel; }

    public List<Reserva> getReservas() {
        return Data.getInstancia().getReservas();
    }

    public List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha, LocalTime inicio, LocalTime fin) throws Exception {
        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (Categoria cat : categorias) {
            Recurso recursoEncontrado = null;

            // Buscar recursos pertenecientes a la categoría dada
            for (Recurso r : Data.getInstancia().getRecursos()) {
                // Evitar reutilizar un recurso ya asignado en esta misma selección
                boolean yaAsignadoEnEstaReserva = asignados.stream().anyMatch(a -> a.getId().equals(r.getId()));
                if (yaAsignadoEnEstaReserva) continue;

                if (r.getCategoria() != null && r.getCategoria().getId().equals(cat.getId())) {
                    // Verificar si está libre en ese horario en otras reservas activas
                    boolean libre = true;
                    for (Reserva res : getReservas()) {
                        if ("ACTIVA".equalsIgnoreCase(res.getEstado()) && res.getFecha().equals(fecha)) {
                            boolean solapaHorario = inicio.isBefore(res.getHoraFin()) && res.getHoraInicio().isBefore(fin);
                            if (solapaHorario && res.getRecursosAsignados().stream().anyMatch(rec -> rec.getId().equals(r.getId()))) {
                                libre = false;
                                break;
                            }
                        }
                    }
                    if (libre) {
                        recursoEncontrado = r;
                        break; // Se asigna el primer recurso disponible
                    }
                }
            }

            if (recursoEncontrado != null) {
                asignados.add(recursoEncontrado);
            } else {
                noDisponibles.add(cat.getDescripcion());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new Exception("Sin disponibilidad para las categorías: " + String.join(", ", noDisponibles));
        }

        return asignados;
    }

    public void guardar(Reserva reserva) throws Exception {
        List<Reserva> lista = Data.getInstancia().getReservas();
        int index = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(reserva.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            lista.set(index, reserva);
        } else {
            lista.add(reserva);
        }

        // Guardar persistencia en disco XML
        Data.getInstancia().guardarReservas();

        this.tableModel.setFilas(lista);
        propertyChangeSupport.firePropertyChange(LISTA, null, lista);
    }

    public String generarId() {
        return "RES-" + System.currentTimeMillis();
    }

    public boolean eliminar(String id) {
        boolean eliminado = Data.getInstancia().getReservas().removeIf(r -> r.getId().equals(id));
        if (eliminado) {
            // Guardar persistencia en disco XML tras eliminar
            Data.getInstancia().guardarReservas();

            this.tableModel.setFilas(getReservas());
            propertyChangeSupport.firePropertyChange(LISTA, null, getReservas());
        }
        return eliminado;
    }
}
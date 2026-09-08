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
import java.util.stream.Collectors;

public class ModelReserva {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Reserva seleccionado;
    private TableModelReserva tableModel;
    private final PropertyChangeSupport propertyChangeSupport;
    private String funcionarioId;

    public ModelReserva() {
        this.seleccionado = new Reserva();
        this.tableModel = new TableModelReserva(new ArrayList<>());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setFuncionarioId(String funcionarioId) {
        this.funcionarioId = funcionarioId;
        refrescarTabla();
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

    public List<Reserva> getReservasDelFuncionario() {
        if (funcionarioId == null) return new ArrayList<>(getReservas());
        return getReservas().stream()
                .filter(r -> r.getSolicitante() != null && funcionarioId.equals(r.getSolicitante().getId()))
                .collect(Collectors.toList());
    }

    public void refrescarTabla() {
        this.tableModel.setFilas(getReservasDelFuncionario());
        propertyChangeSupport.firePropertyChange(LISTA, null, getReservasDelFuncionario());
    }

    public List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha, LocalTime inicio, LocalTime fin) throws Exception {
        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (Categoria cat : categorias) {
            Recurso recursoEncontrado = null;

            for (Recurso r : Data.getInstancia().getRecursos()) {
                boolean yaAsignadoEnEstaReserva = asignados.stream().anyMatch(a -> a.getId().equals(r.getId()));
                if (yaAsignadoEnEstaReserva) continue;

                if (r.getCategoria() != null && r.getCategoria().getId().equals(cat.getId())) {
                    boolean libre = true;
                    for (Reserva res : getReservas()) {
                        if (res.isActiva() && fecha.equals(res.getFecha())) {
                            boolean solapaHorario = inicio.isBefore(res.getHoraFin()) && res.getHoraInicio().isBefore(fin);
                            if (solapaHorario && res.usaRecurso(r.getId())) {
                                libre = false;
                                break;
                            }
                        }
                    }
                    if (libre) {
                        recursoEncontrado = r;
                        break;
                    }
                }
            }

            if (recursoEncontrado != null) {
                asignados.add(recursoEncontrado);
            } else {
                noDisponibles.add(cat.getEtiqueta());
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

        Data.getInstancia().guardarReservas();
        refrescarTabla();
    }

    public String generarId() {
        return "RES-" + System.currentTimeMillis();
    }

    public Reserva buscarPorId(String id) {
        return getReservas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean cancelar(String id) throws Exception {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) return false;
        if (!reserva.isActiva()) {
            throw new Exception("La reserva ya no está activa.");
        }
        if (!reserva.esFutura()) {
            throw new Exception("Solo se pueden cancelar reservas futuras.");
        }
        reserva.setEstado("CANCELADA");
        Data.getInstancia().guardarReservas();
        refrescarTabla();
        return true;
    }

    public boolean eliminar(String id) {
        try {
            return cancelar(id);
        } catch (Exception e) {
            return false;
        }
    }
}

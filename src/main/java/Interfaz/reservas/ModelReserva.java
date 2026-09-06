package Interfaz.reservas;

import data.Data;
import logic.Categoria;
import logic.Funcionario;
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

    private final Funcionario usuarioActual;
    private TableModelReserva tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelReserva(Funcionario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.tableModel = new TableModelReserva(getMisReservas());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public TableModelReserva getTableModel() { return tableModel; }

    public List<Reserva> getMisReservas() {
        if (usuarioActual == null) return new ArrayList<>();
        return Data.getInstancia().getReservas().stream()
                .filter(r -> r.getSolicitante() != null && r.getSolicitante().getId().equals(usuarioActual.getId()))
                .collect(Collectors.toList());
    }

    public List<Categoria> getCategorias() {
        return Data.getInstancia().getCategorias();
    }

    public List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha, LocalTime inicio, LocalTime fin) throws Exception {
        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (Categoria cat : categorias) {
            Recurso recursoEncontrado = null;

            for (Recurso r : Data.getInstancia().getRecursos()) {
                boolean yaAsignado = asignados.stream().anyMatch(a -> a.getId().equals(r.getId()));
                if (yaAsignado) continue;

                if (r.getCategoria() != null && r.getCategoria().getId().equals(cat.getId())) {
                    boolean libre = true;
                    // Solo validar solapamiento contra reservas que estén ACTIVAS
                    for (Reserva res : Data.getInstancia().getReservas()) {
                        if ("ACTIVA".equalsIgnoreCase(res.getEstado()) && res.getFecha().equals(fecha)) {
                            boolean solapa = inicio.isBefore(res.getHoraFin()) && res.getHoraInicio().isBefore(fin);
                            if (solapa && res.getRecursosAsignados() != null && res.getRecursosAsignados().stream().anyMatch(rec -> rec.getId().equals(r.getId()))) {
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
                noDisponibles.add(cat.getDescripcion());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new Exception("No hay recursos disponibles para: " + String.join(", ", noDisponibles));
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
        this.tableModel.setFilas(getMisReservas());
        propertyChangeSupport.firePropertyChange(LISTA, null, getMisReservas());
    }

    public void cancelarReserva(Reserva reserva) throws Exception {
        reserva.setEstado("CANCELADA");
        Data.getInstancia().guardarReservas();
        this.tableModel.setFilas(getMisReservas());
        propertyChangeSupport.firePropertyChange(LISTA, null, getMisReservas());
    }

    public String generarId() {
        int max = Data.getInstancia().getReservas().stream().mapToInt(r -> {
            try {
                return Integer.parseInt(r.getId().replace("RES-", ""));
            } catch (Exception e) {
                return 0;
            }
        }).max().orElse(0);
        return String.format("RES-%06d", max + 1);
    }
}
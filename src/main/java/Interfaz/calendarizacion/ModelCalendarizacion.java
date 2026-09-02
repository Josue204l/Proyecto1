package Interfaz.calendarizacion;

import data.Data;
import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModelCalendarizacion {

    private List<Reserva> reservas;
    private List<Categoria> categorias;
    private final PropertyChangeSupport propertyChangeSupport;

    public static final String RESERVAS = "reservas";
    public static final String CATEGORIAS = "categorias";

    public ModelCalendarizacion() {
        this.reservas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        List<Reserva> oldReservas = this.reservas;
        this.reservas = reservas;
        propertyChangeSupport.firePropertyChange(RESERVAS, oldReservas, reservas);
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        List<Categoria> oldCategorias = this.categorias;
        this.categorias = categorias;
        propertyChangeSupport.firePropertyChange(CATEGORIAS, oldCategorias, categorias);
    }

    public List<Reserva> getReservasPorFecha(LocalDate fecha) {
        if (fecha == null) return Collections.emptyList();
        return Data.getInstancia().getReservas().stream()
                .filter(r -> r.getFecha() != null && r.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorCategoria(Categoria categoria) {
        if (categoria == null) return Collections.emptyList();
        return Data.getInstancia().getReservas().stream()
                .filter(r -> r.getCategoriasRequeridas() != null &&
                        r.getCategoriasRequeridas().stream().anyMatch(c -> c.getId().equals(categoria.getId())))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorFechaYCategoria(LocalDate fecha, Categoria categoria) {
        if (fecha == null || categoria == null) return Collections.emptyList();
        return Data.getInstancia().getReservas().stream()
                .filter(r -> r.getFecha() != null && r.getFecha().equals(fecha))
                .filter(r -> r.getCategoriasRequeridas() != null &&
                        r.getCategoriasRequeridas().stream().anyMatch(c -> c.getId().equals(categoria.getId())))
                .collect(Collectors.toList());
    }

    public boolean agregarReserva(Reserva nueva) {
        for (Reserva existente : Data.getInstancia().getReservas()) {
            if ("ACTIVA".equalsIgnoreCase(existente.getEstado())
                    && !existente.getId().equals(nueva.getId())
                    && existente.seSolapaCon(nueva)) {
                return false;
            }
        }
        Data.getInstancia().getReservas().add(nueva);
        setReservas(Data.getInstancia().getReservas());
        return true;
    }

    public boolean eliminarReserva(String id) {
        boolean removed = Data.getInstancia().getReservas().removeIf(r -> r.getId().equals(id));
        if (removed) {
            setReservas(Data.getInstancia().getReservas());
        }
        return removed;
    }

    public void cambiarEstado(String id, String nuevoEstado) {
        Data.getInstancia().getReservas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .ifPresent(r -> r.setEstado(nuevoEstado));
        setReservas(Data.getInstancia().getReservas());
    }

    public List<Recurso> getRecursos() {
        return Data.getInstancia().getRecursos();
    }

    public List<Reserva> getTodasLasReservas() {
        return Data.getInstancia().getReservas();
    }

    public String generarId() {
        return "RES-" + String.format("%06d", System.currentTimeMillis() % 1000000);
    }
}
package Interfaz.recursos;

import data.Data;
import logic.Categoria;
import logic.Recurso;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelRecurso {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Recurso seleccionado;
    private TableModelRecurso tableModel;
    private List<Recurso> listaFiltrada;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelRecurso() {
        this.seleccionado = new Recurso();
        this.listaFiltrada = new ArrayList<>(getRecursos());
        this.tableModel = new TableModelRecurso(listaFiltrada);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Recurso getSeleccionado() { return seleccionado; }

    public void setSeleccionado(Recurso seleccionado) {
        Recurso old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelRecurso getTableModel() { return tableModel; }

    public List<Recurso> getRecursos() {
        return Data.getInstancia().getRecursos();
    }

    public List<Categoria> getCategorias() {
        return Data.getInstancia().getCategorias();
    }

    public void buscar(Categoria cat, String descripcion) {
        listaFiltrada = getRecursos().stream().filter(r -> {
            boolean coincideCat = (cat == null) || (r.getCategoria() != null && r.getCategoria().getId().equals(cat.getId()));
            boolean coincideDesc = descripcion.isEmpty() || (r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(descripcion.toLowerCase()));
            return coincideCat && coincideDesc;
        }).collect(Collectors.toList());
        tableModel.setFilas(listaFiltrada);
    }

    public void restablecerFiltro() {
        listaFiltrada = new ArrayList<>(getRecursos());
        tableModel.setFilas(listaFiltrada);
    }

    public void guardar(Recurso recurso) throws Exception {
        List<Recurso> recursos = Data.getInstancia().getRecursos();
        int index = -1;
        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).getId().equals(recurso.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            recursos.set(index, recurso);
        } else {
            recursos.add(recurso);
        }

        Data.getInstancia().guardarRecursos();
        restablecerFiltro();
        propertyChangeSupport.firePropertyChange(LISTA, null, recursos);
    }

    public boolean eliminar(String id) throws Exception {
        // Validar si el recurso está en alguna reserva activa
        boolean enReservaActiva = Data.getInstancia().getReservas().stream()
                .filter(r -> "ACTIVA".equalsIgnoreCase(r.getEstado()))
                .anyMatch(r -> r.getRecursosAsignados() != null && r.getRecursosAsignados().stream().anyMatch(rec -> rec.getId().equals(id)));

        if (enReservaActiva) {
            throw new Exception("No se puede borrar el recurso porque está en una reserva ACTIVA.");
        }

        boolean eliminado = Data.getInstancia().getRecursos().removeIf(r -> r.getId().equals(id));
        if (eliminado) {
            Data.getInstancia().guardarRecursos();
            restablecerFiltro();
            propertyChangeSupport.firePropertyChange(LISTA, null, getRecursos());
        }
        return eliminado;
    }
}
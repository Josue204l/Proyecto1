package Interfaz.categorias;

import data.Data;
import logic.Categoria;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelCategoria {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Categoria seleccionado;
    private TableModelCategoria tableModel;
    private List<Categoria> listaFiltrada;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelCategoria() {
        this.seleccionado = new Categoria();
        this.listaFiltrada = new ArrayList<>(getCategorias());
        this.tableModel = new TableModelCategoria(listaFiltrada);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Categoria getSeleccionado() { return seleccionado; }

    public void setSeleccionado(Categoria seleccionado) {
        Categoria old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelCategoria getTableModel() { return tableModel; }

    public List<Categoria> getCategorias() {
        return Data.getInstancia().getCategorias();
    }

    public String generarNuevoId() {
        int max = getCategorias().stream().mapToInt(c -> {
            try {
                return Integer.parseInt(c.getId().replace("CAT-", ""));
            } catch (Exception e) {
                return 0;
            }
        }).max().orElse(0);
        return String.format("CAT-%06d", max + 1);
    }

    public void buscar(String descripcion) {
        listaFiltrada = getCategorias().stream()
                .filter(c -> descripcion.isEmpty() || (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(descripcion.toLowerCase())))
                .collect(Collectors.toList());
        tableModel.setFilas(listaFiltrada);
    }

    public void restablecerFiltro() {
        listaFiltrada = new ArrayList<>(getCategorias());
        tableModel.setFilas(listaFiltrada);
    }

    public void guardar(Categoria categoria) throws Exception {
        List<Categoria> categorias = Data.getInstancia().getCategorias();
        int index = -1;
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId().equals(categoria.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            categorias.set(index, categoria);
        } else {
            categorias.add(categoria);
        }

        Data.getInstancia().guardarCategorias();
        restablecerFiltro();
        propertyChangeSupport.firePropertyChange(LISTA, null, categorias);
    }

    public boolean eliminar(String id) throws Exception {
        // Validar si existen recursos asociados a esta categoría
        boolean tieneRecursos = Data.getInstancia().getRecursos().stream()
                .anyMatch(r -> r.getCategoria() != null && r.getCategoria().getId().equals(id));

        if (tieneRecursos) {
            throw new Exception("No se puede eliminar la categoría porque tiene recursos asociados.");
        }

        boolean eliminado = Data.getInstancia().getCategorias().removeIf(c -> c.getId().equals(id));
        if (eliminado) {
            Data.getInstancia().guardarCategorias();
            restablecerFiltro();
            propertyChangeSupport.firePropertyChange(LISTA, null, getCategorias());
        }
        return eliminado;
    }
}
package Interfaz.funcionarios;

import data.Data;
import logic.Funcionario;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelFuncionario {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Funcionario seleccionado;
    private TableModelFuncionario tableModel;
    private List<Funcionario> listaFiltrada;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelFuncionario() {
        this.seleccionado = new Funcionario();
        this.listaFiltrada = new ArrayList<>(getFuncionarios());
        this.tableModel = new TableModelFuncionario(listaFiltrada);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Funcionario getSeleccionado() { return seleccionado; }

    public void setSeleccionado(Funcionario seleccionado) {
        Funcionario old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelFuncionario getTableModel() { return tableModel; }

    public List<Funcionario> getFuncionarios() {
        return Data.getInstancia().getFuncionarios();
    }

    public void buscar(String id, String nombre) {
        listaFiltrada = getFuncionarios().stream().filter(f -> {
            boolean coincideId = id.isEmpty() || f.getId().toLowerCase().contains(id.toLowerCase());
            boolean coincideNombre = nombre.isEmpty() || f.getNombre().toLowerCase().contains(nombre.toLowerCase());
            return coincideId && coincideNombre;
        }).collect(Collectors.toList());
        tableModel.setFilas(listaFiltrada);
    }

    public void restablecerFiltro() {
        listaFiltrada = new ArrayList<>(getFuncionarios());
        tableModel.setFilas(listaFiltrada);
    }

    public void guardar(Funcionario funcionario) throws Exception {
        List<Funcionario> funcionarios = Data.getInstancia().getFuncionarios();
        int index = -1;
        for (int i = 0; i < funcionarios.size(); i++) {
            if (funcionarios.get(i).getId().equals(funcionario.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            // Preservar la clave actual si se está modificando un usuario existente
            funcionario.setClave(funcionarios.get(index).getClave());
            funcionarios.set(index, funcionario);
        } else {
            funcionarios.add(funcionario);
        }

        Data.getInstancia().guardarFuncionarios();
        restablecerFiltro();
        propertyChangeSupport.firePropertyChange(LISTA, null, funcionarios);
    }

    public boolean eliminar(String id) throws Exception {
        // Validar si el funcionario tiene reservas activas
        boolean tieneReservas = Data.getInstancia().getReservas().stream()
                .anyMatch(r -> r.getSolicitante() != null && r.getSolicitante().getId().equals(id) && "ACTIVA".equalsIgnoreCase(r.getEstado()));

        if (tieneReservas) {
            throw new Exception("No se puede borrar el funcionario porque posee reservas activas asignadas.");
        }

        boolean eliminado = Data.getInstancia().getFuncionarios().removeIf(f -> f.getId().equals(id));
        if (eliminado) {
            Data.getInstancia().guardarFuncionarios();
            restablecerFiltro();
            propertyChangeSupport.firePropertyChange(LISTA, null, getFuncionarios());
        }
        return eliminado;
    }
}
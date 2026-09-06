package Interfaz.funcionarios;

import logic.Funcionario;
import utils.PDFGenerator;

import javax.swing.*;

public class ControllerFuncionario {

    private final funcionariosView view;
    private final ModelFuncionario model;

    public ControllerFuncionario(funcionariosView view, ModelFuncionario model) {
        this.view = view;
        this.model = model;
        registrarEventos();
        actualizarTabla();
    }

    private void registrarEventos() {
        if (view.getBuscarButton() != null) {
            view.getBuscarButton().addActionListener(e -> buscar());
        }
        if (view.getGuardarButton() != null) {
            view.getGuardarButton().addActionListener(e -> guardar());
        }
        if (view.getBorrarButton() != null) {
            view.getBorrarButton().addActionListener(e -> borrar());
        }
        if (view.getLimpiarButton() != null) {
            view.getLimpiarButton().addActionListener(e -> limpiar());
        }
        if (view.getImprimirButton() != null) {
            view.getImprimirButton().addActionListener(e -> imprimirPDF());
        }

        if (view.getTablaFuncionarios() != null) {
            view.getTablaFuncionarios().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    cargarSeleccionado();
                }
            });
        }
    }

    private void buscar() {
        String idBuscado = view.getTxtBuscarId() != null ? view.getTxtBuscarId().getText().trim() : "";
        String nombreBuscado = view.getTxtBuscarNombre() != null ? view.getTxtBuscarNombre().getText().trim() : "";
        model.buscar(idBuscado, nombreBuscado);
        actualizarTabla();
    }

    private void guardar() {
        try {
            String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
            String nombre = view.getTxtNombre() != null ? view.getTxtNombre().getText().trim() : "";
            String telefono = view.getTxtTelefono() != null ? view.getTxtTelefono().getText().trim() : "";

            if (id.isEmpty()) throw new Exception("El ID es obligatorio.");
            if (nombre.isEmpty()) throw new Exception("El nombre es obligatorio.");

            // Si es un nuevo funcionario, la clave inicial es su ID
            Funcionario f = new Funcionario(id, id, "FUNCIONARIO", nombre, telefono);
            model.guardar(f);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Funcionario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrar() {
        String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione o ingrese un funcionario para borrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("1234".equals(id)) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se puede eliminar el usuario Administrador principal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Desea eliminar al funcionario " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (model.eliminar(id)) {
                    actualizarTabla();
                    limpiar();
                    JOptionPane.showMessageDialog(view.getMainPanel(), "Funcionario eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view.getMainPanel(), "No se encontró el funcionario especificado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccionado() {
        if (view.getTablaFuncionarios() == null) return;
        int row = view.getTablaFuncionarios().getSelectedRow();
        if (row >= 0) {
            String id = (String) view.getTablaFuncionarios().getValueAt(row, 0);
            Funcionario f = model.getFuncionarios().stream().filter(func -> func.getId().equals(id)).findFirst().orElse(null);
            if (f != null) {
                if (view.getTxtId() != null) view.getTxtId().setText(f.getId());
                if (view.getTxtNombre() != null) view.getTxtNombre().setText(f.getNombre());
                if (view.getTxtTelefono() != null) view.getTxtTelefono().setText(f.getTelefono());
            }
        }
    }

    private void limpiar() {
        if (view.getTxtBuscarId() != null) view.getTxtBuscarId().setText("");
        if (view.getTxtBuscarNombre() != null) view.getTxtBuscarNombre().setText("");
        if (view.getTxtId() != null) view.getTxtId().setText("");
        if (view.getTxtNombre() != null) view.getTxtNombre().setText("");
        if (view.getTxtTelefono() != null) view.getTxtTelefono().setText("");
        if (view.getTablaFuncionarios() != null) view.getTablaFuncionarios().clearSelection();
        model.restablecerFiltro();
        actualizarTabla();
    }

    private void imprimirPDF() {
        if (view.getTablaFuncionarios() != null) {
            PDFGenerator.generarReporteTabla("Listado de Funcionarios", view.getTablaFuncionarios());
        }
    }

    private void actualizarTabla() {
        if (view.getTablaFuncionarios() != null) {
            view.getTablaFuncionarios().setModel(model.getTableModel());
        }
    }

    public ModelFuncionario getModel() { return model; }
}
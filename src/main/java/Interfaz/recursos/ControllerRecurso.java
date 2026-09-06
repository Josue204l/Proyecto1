package Interfaz.recursos;

import logic.Categoria;
import logic.Recurso;
import utils.PDFGenerator;

import javax.swing.*;

public class ControllerRecurso {

    private final recursosView view;
    private final ModelRecurso model;

    public ControllerRecurso(recursosView view, ModelRecurso model) {
        this.view = view;
        this.model = model;
        poblarCombos();
        registrarEventos();
        actualizarTabla();
    }

    private void poblarCombos() {
        if (view.getCmbFiltroCategoria() != null) {
            DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
            for (Categoria c : model.getCategorias()) {
                comboModel.addElement(c);
            }
            view.getCmbFiltroCategoria().setModel(comboModel);
        }

        if (view.getCmbCategoriaForm() != null) {
            DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
            for (Categoria c : model.getCategorias()) {
                comboModel.addElement(c);
            }
            view.getCmbCategoriaForm().setModel(comboModel);
        }
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

        if (view.getTablaRecursos() != null) {
            view.getTablaRecursos().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    cargarSeleccionado();
                }
            });
        }
    }

    private void buscar() {
        Categoria cat = (Categoria) (view.getCmbFiltroCategoria() != null ? view.getCmbFiltroCategoria().getSelectedItem() : null);
        String desc = view.getTxtBuscarDescripcion() != null ? view.getTxtBuscarDescripcion().getText().trim() : "";
        model.buscar(cat, desc);
        actualizarTabla();
    }

    private void guardar() {
        try {
            String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
            String descripcion = view.getTxtDescripcion() != null ? view.getTxtDescripcion().getText().trim() : "";
            Categoria cat = (Categoria) (view.getCmbCategoriaForm() != null ? view.getCmbCategoriaForm().getSelectedItem() : null);

            if (id.isEmpty()) throw new Exception("El ID o número de activo es obligatorio.");
            if (descripcion.isEmpty()) throw new Exception("La descripción es obligatoria.");
            if (cat == null) throw new Exception("Debe seleccionar una categoría.");

            // CORREGIDO: (id, descripcion, categoria)
            Recurso recurso = new Recurso(id, descripcion, cat);
            model.guardar(recurso);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Recurso guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void borrar() {
        String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione un recurso para borrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Desea eliminar el recurso " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (model.eliminar(id)) {
                    actualizarTabla();
                    limpiar();
                    JOptionPane.showMessageDialog(view.getMainPanel(), "Recurso eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view.getMainPanel(), "No se encontró el recurso especificado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccionado() {
        if (view.getTablaRecursos() == null) return;
        int row = view.getTablaRecursos().getSelectedRow();
        if (row >= 0) {
            String id = (String) view.getTablaRecursos().getValueAt(row, 0);
            Recurso r = model.getRecursos().stream().filter(rec -> rec.getId().equals(id)).findFirst().orElse(null);
            if (r != null) {
                if (view.getTxtId() != null) view.getTxtId().setText(r.getId());
                if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText(r.getDescripcion());
                if (view.getCmbCategoriaForm() != null && r.getCategoria() != null) {
                    view.getCmbCategoriaForm().setSelectedItem(r.getCategoria());
                }
            }
        }
    }

    private void limpiar() {
        if (view.getTxtBuscarDescripcion() != null) view.getTxtBuscarDescripcion().setText("");
        if (view.getTxtId() != null) view.getTxtId().setText("");
        if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText("");
        if (view.getTablaRecursos() != null) view.getTablaRecursos().clearSelection();
        model.restablecerFiltro();
        actualizarTabla();
    }

    private void imprimirPDF() {
        if (view.getTablaRecursos() != null) {
            PDFGenerator.generarReporteTabla("Listado de Recursos", view.getTablaRecursos());
        }
    }

    private void actualizarTabla() {
        if (view.getTablaRecursos() != null) {
            view.getTablaRecursos().setModel(model.getTableModel());
        }
    }

    public ModelRecurso getModel() { return model; }
}
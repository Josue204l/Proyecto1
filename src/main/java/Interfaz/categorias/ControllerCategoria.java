package Interfaz.categorias;

import logic.Categoria;
import utils.PDFGenerator;

import javax.swing.*;

public class ControllerCategoria {

    private final categoriasView view;
    private final ModelCategoria model;

    public ControllerCategoria(categoriasView view, ModelCategoria model) {
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

        if (view.getTablaCategorias() != null) {
            view.getTablaCategorias().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    cargarSeleccionado();
                }
            });
        }
    }

    private void buscar() {
        String descripcion = view.getTxtBuscarDescripcion() != null ? view.getTxtBuscarDescripcion().getText().trim() : "";
        model.buscar(descripcion);
        actualizarTabla();
    }

    private void guardar() {
        try {
            String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
            String descripcion = view.getTxtDescripcion() != null ? view.getTxtDescripcion().getText().trim() : "";

            if (descripcion.isEmpty()) throw new Exception("La descripción de la categoría es obligatoria.");

            if (id.isEmpty()) {
                id = model.generarNuevoId();
            }

            Categoria cat = new Categoria(id, descripcion, descripcion);
            model.guardar(cat);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Categoría guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrar() {
        String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione una categoría para borrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Desea eliminar la categoría " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (model.eliminar(id)) {
                    actualizarTabla();
                    limpiar();
                    JOptionPane.showMessageDialog(view.getMainPanel(), "Categoría eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view.getMainPanel(), "No se encontró la categoría especificada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarSeleccionado() {
        if (view.getTablaCategorias() == null) return;
        int row = view.getTablaCategorias().getSelectedRow();
        if (row >= 0) {
            String id = (String) view.getTablaCategorias().getValueAt(row, 0);
            Categoria c = model.getCategorias().stream().filter(cat -> cat.getId().equals(id)).findFirst().orElse(null);
            if (c != null) {
                if (view.getTxtId() != null) view.getTxtId().setText(c.getId());
                if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText(c.getDescripcion());
            }
        }
    }

    private void limpiar() {
        if (view.getTxtBuscarDescripcion() != null) view.getTxtBuscarDescripcion().setText("");
        if (view.getTxtId() != null) view.getTxtId().setText("");
        if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText("");
        if (view.getTablaCategorias() != null) view.getTablaCategorias().clearSelection();
        model.restablecerFiltro();
        actualizarTabla();
    }

    private void imprimirPDF() {
        if (view.getTablaCategorias() != null) {
            PDFGenerator.generarReporteTabla("Listado de Categorías", view.getTablaCategorias());
        }
    }

    private void actualizarTabla() {
        if (view.getTablaCategorias() != null) {
            view.getTablaCategorias().setModel(model.getTableModel());
        }
    }

    public ModelCategoria getModel() { return model; }
}
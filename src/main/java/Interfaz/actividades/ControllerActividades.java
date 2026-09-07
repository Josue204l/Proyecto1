package Interfaz.actividades;

import logic.Observer;
import logic.Reserva;
import logic.Service;
import utils.PDFGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerActividades implements Observer {

    private final actividadesView view;
    private final ModelActividades model;

    public ControllerActividades(actividadesView view, ModelActividades model) {
        this.view = view;
        this.model = model;

        registrarEventos();
        Service.instance().addObserver(this);
        actualizarTabla();
    }

    private void registrarEventos() {
        if (view.getBtnBuscar() != null) {
            view.getBtnBuscar().addActionListener(e -> buscar());
        }
        if (view.getTable() != null) {
            view.getTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) cargarSeleccionada();
            });
        }
    }

    private void buscar() {
        String texto = view.getTxtBuscar() != null ? view.getTxtBuscar().getText().trim() : "";
        List<Reserva> filtradas = model.getActividades().stream()
                .filter(r -> "ACTIVA".equalsIgnoreCase(r.getEstado()))
                .filter(r -> texto.isEmpty() ||
                        (r.getTitulo() != null && r.getTitulo().toLowerCase().contains(texto.toLowerCase())) ||
                        (r.getFuncionario() != null && r.getFuncionario().getNombre().toLowerCase().contains(texto.toLowerCase())))
                .collect(Collectors.toList());
        model.getTableModel().setFilas(filtradas);
        if (view.getTable() != null) view.getTable().setModel(model.getTableModel());
    }

    private void cargarSeleccionada() {
        if (view.getTable() == null) return;
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            Reserva r = model.getTableModel().getRowAt(row);
            if (r != null && view.getTxtBuscar() != null) {
                view.getTxtBuscar().setText(r.getTitulo() != null ? r.getTitulo() : "");
            }
        }
    }

    public void actualizarTabla() {
        if (view.getTable() != null) {
            List<Reserva> activas = model.getActividades().stream()
                    .filter(r -> "ACTIVA".equalsIgnoreCase(r.getEstado()))
                    .collect(Collectors.toList());
            model.getTableModel().setFilas(activas);
            view.getTable().setModel(model.getTableModel());
        }
    }

    public ModelActividades getModel() { return model; }

    @Override
    public void update() {
        actualizarTabla();
    }
}

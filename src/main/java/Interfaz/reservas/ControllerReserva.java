package Interfaz.reservas;

import logic.Observer;
import logic.Service;
import logic.Funcionario;
import logic.Reserva;
import utils.PDFGenerator;

import javax.swing.*;

public class ControllerReserva implements Observer {

    private final reservasView view;
    private final Funcionario funcionarioActual;
    private final ModelReserva model;

    public ControllerReserva(reservasView view, Funcionario funcionarioActual) {
        this.view = view;
        this.funcionarioActual = funcionarioActual;
        this.model = new ModelReserva(funcionarioActual);
        this.view.setController(this);

        registrarEventos();
        Service.instance().addObserver(this);
        actualizarTabla();
    }

    private void registrarEventos() {
        if (view.getImprimirButton() != null) {
            view.getImprimirButton().addActionListener(e ->
                PDFGenerator.generarReporteTabla("Mis_Reservas", view.getTablaMisReservas()));
        }
        if (view.getCancelarReservaSelecionadaButton() != null) {
            view.getCancelarReservaSelecionadaButton().addActionListener(e -> cancelarSeleccionada());
        }
        if (view.getReservasButton() != null) {
            view.getReservasButton().addActionListener(e -> crearDesdeFormulario());
        }
        if (view.getLImpiarButton() != null) {
            view.getLImpiarButton().addActionListener(e -> limpiarFormulario());
        }
        if (view.getTablaMisReservas() != null) {
            view.getTablaMisReservas().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) cargarSeleccionada();
            });
        }
    }

    private void cargarSeleccionada() {
        if (view.getTablaMisReservas() == null) return;
        int row = view.getTablaMisReservas().getSelectedRow();
        if (row >= 0) {
            Reserva r = model.getTableModel().getRowAt(row);
            if (r != null) {
                if (view.getTxtActividad() != null) view.getTxtActividad().setText(r.getTitulo());
                if (view.getTextFecha() != null) view.getTextFecha().setText(r.getFecha() != null ? r.getFecha().toString() : "");
                if (view.getTxtHoraInicio() != null) view.getTxtHoraInicio().setText(r.getHoraInicio() != null ? r.getHoraInicio().toString() : "");
                if (view.getTxtHoraFin() != null) view.getTxtHoraFin().setText(r.getHoraFin() != null ? r.getHoraFin().toString() : "");
            }
        }
    }

    private void cancelarSeleccionada() {
        if (view.getTablaMisReservas() == null) return;
        int row = view.getTablaMisReservas().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione una reserva para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Reserva r = model.getTableModel().getRowAt(row);
        if (r == null || "CANCELADA".equalsIgnoreCase(r.getEstado())) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "La reserva ya está cancelada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(),
                "¿Desea cancelar la reserva '" + r.getTitulo() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                model.cancelarReserva(r);
                Service.instance().notifyObservers();
                JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva cancelada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void crearDesdeFormulario() {
        try {
            String titulo = view.getTxtActividad() != null ? view.getTxtActividad().getText().trim() : "";
            String fechaStr = view.getTextFecha() != null ? view.getTextFecha().getText().trim() : "";
            String inicioStr = view.getTxtHoraInicio() != null ? view.getTxtHoraInicio().getText().trim() : "";
            String finStr = view.getTxtHoraFin() != null ? view.getTxtHoraFin().getText().trim() : "";

            if (titulo.isEmpty()) throw new Exception("El título de la actividad es obligatorio.");
            if (fechaStr.isEmpty()) throw new Exception("La fecha es obligatoria.");
            if (inicioStr.isEmpty() || finStr.isEmpty()) throw new Exception("Las horas de inicio y fin son obligatorias.");

            java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
            java.time.LocalTime inicio = java.time.LocalTime.parse(inicioStr);
            java.time.LocalTime fin = java.time.LocalTime.parse(finStr);

            if (!fin.isAfter(inicio)) throw new Exception("La hora de fin debe ser posterior a la hora de inicio.");

            java.util.List<logic.Categoria> categorias = new java.util.ArrayList<>();
            if (view.getListaCategorias() != null) {
                categorias = view.getListaCategorias().getSelectedValuesList();
            }
            if (categorias.isEmpty()) throw new Exception("Debe seleccionar al menos una categoría de recurso.");

            java.util.List<logic.Recurso> recursos = model.asignarRecursosDisponibles(categorias, fecha, inicio, fin);

            Reserva nueva = new Reserva(model.generarId(), titulo, fecha, inicio, fin, recursos, categorias, funcionarioActual);
            model.guardar(nueva);
            Service.instance().notifyObservers();
            limpiarFormulario();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        if (view.getTxtActividad() != null) view.getTxtActividad().setText("");
        if (view.getTextFecha() != null) view.getTextFecha().setText("");
        if (view.getTxtHoraInicio() != null) view.getTxtHoraInicio().setText("");
        if (view.getTxtHoraFin() != null) view.getTxtHoraFin().setText("");
        if (view.getTextFrase() != null) view.getTextFrase().setText("");
        if (view.getTablaMisReservas() != null) view.getTablaMisReservas().clearSelection();
    }

    public void crearReserva(Reserva reserva) {
        try {
            model.guardar(reserva);
            Service.instance().notifyObservers();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarTabla() {
        if (view.getTablaMisReservas() != null) {
            view.getTablaMisReservas().setModel(model.getTableModel());
            model.getTableModel().setFilas(model.getMisReservas());
        }
    }

    public ModelReserva getModel() { return model; }

    public Funcionario getFuncionarioActual() { return funcionarioActual; }

    @Override
    public void update() {
        actualizarTabla();
    }
}

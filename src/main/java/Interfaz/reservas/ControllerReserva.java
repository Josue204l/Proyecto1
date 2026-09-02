package Interfaz.reservas;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ControllerReserva {

    private final ModelReserva model;
    private final reservasView view;
    private final Funcionario usuarioActual;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public ControllerReserva(reservasView view, Funcionario usuarioActual) {
        this.view = view;
        this.model = new ModelReserva();
        this.usuarioActual = usuarioActual;
        view.setController(this);
        registrarEventos();
        actualizarTabla();
    }

    private void registrarEventos() {
        if (view.getReservasButton() != null)
            view.getReservasButton().addActionListener(e -> guardar());
        if (view.getCancelarReservaSelecionadaButton() != null)
            view.getCancelarReservaSelecionadaButton().addActionListener(e -> cancelarSeleccionada());
        if (view.getLImpiarButton() != null)
            view.getLImpiarButton().addActionListener(e -> limpiar());
    }

    private void guardar() {
        try {
            String titulo = view.getTxtActividad() != null ? view.getTxtActividad().getText().trim() : "";
            String fechaStr = view.getTextFecha() != null ? view.getTextFecha().getText().trim() : "";
            String horaInicioStr = view.getTxtHoraInicio() != null ? view.getTxtHoraInicio().getText().trim() : "";
            String horaFinStr = view.getTxtHoraFin() != null ? view.getTxtHoraFin().getText().trim() : "";

            if (titulo.isEmpty()) throw new Exception("El título de la actividad es obligatorio.");
            if (fechaStr.isEmpty()) throw new Exception("La fecha es obligatoria.");
            if (horaInicioStr.isEmpty() || horaFinStr.isEmpty()) throw new Exception("Las horas son obligatorias.");

            LocalDate fecha = LocalDate.parse(fechaStr, FMT_FECHA);
            if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha no puede ser en el pasado.");

            LocalTime horaInicio = LocalTime.parse(horaInicioStr, FMT_HORA);
            LocalTime horaFin = LocalTime.parse(horaFinStr, FMT_HORA);
            if (!horaFin.isAfter(horaInicio)) throw new Exception("La hora de fin debe ser posterior a la hora de inicio.");

            // Obtener las categorías seleccionadas desde la vista
            List<Categoria> categoriasSeleccionadas = obtenerCategoriasSeleccionadas();
            if (categoriasSeleccionadas.isEmpty()) {
                throw new Exception("Debe seleccionar al menos una categoría de recursos.");
            }

            // Asignar primer recurso libre de cada categoría
            List<Recurso> recursosAsignados = model.asignarRecursosDisponibles(categoriasSeleccionadas, fecha, horaInicio, horaFin);

            Reserva nueva = new Reserva(model.generarId(), titulo, fecha, horaInicio, horaFin, recursosAsignados, usuarioActual);

            model.guardar(nueva);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Formato inválido. Use dd/MM/yyyy para fecha y HH:mm para hora.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Categoria> obtenerCategoriasSeleccionadas() {
        List<Categoria> lista = new ArrayList<>();
        if (view.getListaCategorias() != null) {
            lista = view.getListaCategorias().getSelectedValuesList();
        }
        return lista;
    }

    private void cancelarSeleccionada() {
        if (view.getTablaMisReservas() == null) return;
        int fila = view.getTablaMisReservas().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione una reserva para cancelar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) view.getTablaMisReservas().getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Cancelar la reserva seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.eliminar(id);
            actualizarTabla();
        }
    }

    private void limpiar() {
        if (view.getTxtActividad() != null) view.getTxtActividad().setText("");
        if (view.getTextFecha() != null) view.getTextFecha().setText("");
        if (view.getTxtHoraInicio() != null) view.getTxtHoraInicio().setText("");
        if (view.getTxtHoraFin() != null) view.getTxtHoraFin().setText("");
    }

    private void actualizarTabla() {
        if (view.getTablaMisReservas() != null) {
            view.getTablaMisReservas().setModel(model.getTableModel());
        }
    }

    public ModelReserva getModel() { return model; }

    public List<Reserva> getReservas() { return model.getReservas(); }
}
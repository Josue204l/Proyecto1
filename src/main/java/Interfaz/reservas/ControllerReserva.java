package Interfaz.reservas;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import services.AIService;
import services.ReservaExtraccion;
import utils.PDFGenerator;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        this.usuarioActual = usuarioActual;
        this.model = new ModelReserva(usuarioActual);
        view.setController(this);
        poblarCategorias();
        registrarEventos();
        actualizarTabla();
    }

    private void poblarCategorias() {
        if (view.getListaCategorias() != null) {
            DefaultListModel<Categoria> listModel = new DefaultListModel<>();
            for (Categoria cat : model.getCategorias()) {
                listModel.addElement(cat);
            }
            view.getListaCategorias().setModel(listModel);
        }
    }

    private void registrarEventos() {
        if (view.getReservasButton() != null)
            view.getReservasButton().addActionListener(e -> guardar());
        if (view.getCancelarReservaSelecionadaButton() != null)
            view.getCancelarReservaSelecionadaButton().addActionListener(e -> cancelarSeleccionada());
        if (view.getLImpiarButton() != null)
            view.getLImpiarButton().addActionListener(e -> limpiar());
        if (view.getExtraerButton() != null)
            view.getExtraerButton().addActionListener(e -> extraerConIA());
        if (view.getImprimirButton() != null)
            view.getImprimirButton().addActionListener(e -> imprimirPDF());
    }

    private void extraerConIA() {
        if (view.getTextFrase() == null) return;

        String frase = view.getTextFrase().getText().trim();
        if (frase.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Por favor, ingrese una frase en el campo de texto para extraer los datos con IA.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            view.getExtraerButton().setEnabled(false);
            AIService aiService = new AIService();
            ReservaExtraccion datos = aiService.extraerReserva(frase);

            if (datos != null) {
                if (datos.getActividad() != null && view.getTxtActividad() != null) {
                    view.getTxtActividad().setText(datos.getActividad());
                }

                if (datos.getFecha() != null && view.getTextFecha() != null) {
                    try {
                        LocalDate fechaIso = LocalDate.parse(datos.getFecha());
                        view.getTextFecha().setText(fechaIso.format(FMT_FECHA));
                    } catch (Exception e) {
                        view.getTextFecha().setText(datos.getFecha());
                    }
                }

                if (datos.getHoraInicio() != null && view.getTxtHoraInicio() != null) {
                    view.getTxtHoraInicio().setText(datos.getHoraInicio());
                }

                if (datos.getHoraFinal() != null && view.getTxtHoraFin() != null) {
                    view.getTxtHoraFin().setText(datos.getHoraFinal());
                }

                if (datos.getCategoriasRecurso() != null && !datos.getCategoriasRecurso().isEmpty() && view.getListaCategorias() != null) {
                    List<String> categoriasNombreIA = datos.getCategoriasRecurso();
                    ListModel<Categoria> modelCat = view.getListaCategorias().getModel();
                    List<Integer> indicesParaSeleccionar = new ArrayList<>();

                    for (int i = 0; i < modelCat.getSize(); i++) {
                        Categoria cat = modelCat.getElementAt(i);
                        for (String nombreIa : categoriasNombreIA) {
                            if (cat.getDescripcion() != null && cat.getDescripcion().toLowerCase().contains(nombreIa.trim().toLowerCase())) {
                                indicesParaSeleccionar.add(i);
                                break;
                            }
                        }
                    }

                    int[] indicesArray = indicesParaSeleccionar.stream().mapToInt(Integer::intValue).toArray();
                    view.getListaCategorias().setSelectedIndices(indicesArray);

                    if (view.getTxtCategoriasRequeridas() != null) {
                        view.getTxtCategoriasRequeridas().setText(String.join(", ", categoriasNombreIA));
                    }
                }

                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Datos extraídos correctamente por la IA.",
                        "Extracción Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Error al comunicarse con el servicio de IA: " + ex.getMessage(),
                    "Error IA", JOptionPane.ERROR_MESSAGE);
        } finally {
            view.getExtraerButton().setEnabled(true);
        }
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

            List<Categoria> categoriasSeleccionadas = obtenerCategoriasSeleccionadas();
            if (categoriasSeleccionadas.isEmpty()) {
                throw new Exception("Debe seleccionar al menos una categoría de recursos.");
            }

            List<Recurso> recursosAsignados = model.asignarRecursosDisponibles(categoriasSeleccionadas, fecha, horaInicio, horaFin);

            Reserva nueva = new Reserva(model.generarId(), titulo, fecha, horaInicio, horaFin, recursosAsignados, usuarioActual);
            nueva.setEstado("ACTIVA");

            model.guardar(nueva);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva realizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Formato inválido. Use dd/MM/yyyy para fecha y HH:mm para hora.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
        Reserva res = model.getMisReservas().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);

        if (res == null) return;

        // Validar que la reserva sea en una fecha/hora futura
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraInicioReserva = LocalDateTime.of(res.getFecha(), res.getHoraInicio());

        if (!fechaHoraInicioReserva.isAfter(ahora)) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Solo se pueden cancelar reservas futuras.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Desea cancelar la reserva seleccionada?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                model.cancelarReserva(res);
                actualizarTabla();
                JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        if (view.getTextFrase() != null) view.getTextFrase().setText("");
        if (view.getTxtActividad() != null) view.getTxtActividad().setText("");
        if (view.getTextFecha() != null) view.getTextFecha().setText("");
        if (view.getTxtHoraInicio() != null) view.getTxtHoraInicio().setText("");
        if (view.getTxtHoraFin() != null) view.getTxtHoraFin().setText("");
        if (view.getTxtCategoriasRequeridas() != null) view.getTxtCategoriasRequeridas().setText("");
        if (view.getListaCategorias() != null) view.getListaCategorias().clearSelection();
    }

    private void imprimirPDF() {
        if (view.getTablaMisReservas() != null) {
            PDFGenerator.generarReporteTabla("Mis Reservas", view.getTablaMisReservas());
        }
    }

    private void actualizarTabla() {
        if (view.getTablaMisReservas() != null) {
            view.getTablaMisReservas().setModel(model.getTableModel());
        }
    }

    public ModelReserva getModel() { return model; }
}
package Interfaz.reservas;

import data.Data;
import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelReserva {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private final Funcionario usuarioActual;
    private final TableModelReserva tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelReserva(Funcionario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.tableModel = new TableModelReserva(getMisReservas());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(
            PropertyChangeListener listener) {

        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public TableModelReserva getTableModel() {
        return tableModel;
    }

    public List<Reserva> getMisReservas() {

        if (usuarioActual == null) {
            return new ArrayList<>();
        }

        return Data.getInstancia()
                .getReservas()
                .stream()
                .filter(r ->
                        r.getSolicitante() != null
                                && r.getSolicitante().getId() != null
                                && r.getSolicitante()
                                .getId()
                                .equals(usuarioActual.getId()))
                .collect(Collectors.toList());
    }

    public List<Categoria> getCategorias() {
        return Data.getInstancia().getCategorias();
    }

    /**
     * Busca el primer recurso disponible de cada categoría.
     *
     * Si una categoría no tiene recursos disponibles,
     * se agrega a la lista de categorías no disponibles.
     */
    public List<Recurso> asignarRecursosDisponibles(
            List<Categoria> categorias,
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin) throws Exception {

        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        if (categorias == null || categorias.isEmpty()) {
            throw new Exception(
                    "Debe seleccionar al menos una categoría."
            );
        }

        for (Categoria categoria : categorias) {

            if (categoria == null) {
                continue;
            }

            Recurso encontrado = null;

            for (Recurso recurso :
                    Data.getInstancia().getRecursos()) {

                if (recurso == null
                        || recurso.getCategoria() == null) {
                    continue;
                }

                if (!categoria.getId().equals(
                        recurso.getCategoria().getId())) {
                    continue;
                }

                // Un mismo recurso no puede asignarse dos veces
                // dentro de la misma reserva.
                boolean yaAsignado = asignados.stream()
                        .anyMatch(r ->
                                r.getId().equals(recurso.getId()));

                if (yaAsignado) {
                    continue;
                }

                boolean disponible = true;

                for (Reserva existente :
                        Data.getInstancia().getReservas()) {

                    if (existente == null) {
                        continue;
                    }

                    if ("CANCELADA".equalsIgnoreCase(
                            existente.getEstado())) {
                        continue;
                    }

                    if (existente.getFecha() == null
                            || existente.getHoraInicio() == null
                            || existente.getHoraFin() == null) {
                        continue;
                    }

                    if (!fecha.equals(existente.getFecha())) {
                        continue;
                    }

                    boolean mismoRecurso =
                            existente.getRecursosAsignados() != null
                                    && existente
                                    .getRecursosAsignados()
                                    .stream()
                                    .anyMatch(r ->
                                            r != null
                                                    && r.getId() != null
                                                    && r.getId().equals(
                                                    recurso.getId()));

                    if (!mismoRecurso) {
                        continue;
                    }

                    boolean seSolapan =
                            inicio.isBefore(
                                    existente.getHoraFin())
                                    && existente.getHoraInicio()
                                    .isBefore(fin);

                    if (seSolapan) {
                        disponible = false;
                        break;
                    }
                }

                if (disponible) {
                    encontrado = recurso;
                    break;
                }
            }

            if (encontrado != null) {
                asignados.add(encontrado);
            } else {
                noDisponibles.add(
                        categoria.getDescripcion());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new Exception(
                    "No hay recursos disponibles para: "
                            + String.join(
                            ", ",
                            noDisponibles)
            );
        }

        return asignados;
    }

    /**
     * Indica si la reserva comienza en el futuro.
     */
    public boolean esReservaFutura(Reserva reserva) {

        if (reserva == null
                || reserva.getFecha() == null
                || reserva.getHoraInicio() == null) {
            return false;
        }

        LocalDateTime inicio =
                LocalDateTime.of(
                        reserva.getFecha(),
                        reserva.getHoraInicio());

        return inicio.isAfter(LocalDateTime.now());
    }

    /**
     * Método mantenido para compatibilidad con otros módulos.
     *
     * La creación normal de reservas debe realizarse mediante
     * Service.agregarReserva().
     */
    public void guardar(Reserva reserva) throws Exception {

        if (reserva == null) {
            throw new Exception(
                    "La reserva no puede ser nula."
            );
        }

        List<Reserva> lista =
                Data.getInstancia().getReservas();

        int index = -1;

        for (int i = 0; i < lista.size(); i++) {

            Reserva actual = lista.get(i);

            if (actual.getId() != null
                    && actual.getId().equals(reserva.getId())) {

                index = i;
                break;
            }
        }

        if (index >= 0) {
            lista.set(index, reserva);
        } else {
            lista.add(reserva);
        }

        Data.getInstancia().guardarReservas();

        tableModel.setFilas(getMisReservas());

        propertyChangeSupport.firePropertyChange(
                LISTA,
                null,
                getMisReservas()
        );
    }

    /**
     * Cancela una reserva futura.
     */
    public void cancelarReserva(Reserva reserva)
            throws Exception {

        if (reserva == null) {
            throw new Exception(
                    "La reserva no puede ser nula."
            );
        }

        if ("CANCELADA".equalsIgnoreCase(
                reserva.getEstado())) {
            throw new Exception(
                    "La reserva ya está cancelada."
            );
        }

        if (!esReservaFutura(reserva)) {
            throw new Exception(
                    "Solo se pueden cancelar reservas futuras."
            );
        }

        // Delegamos la operación al Service para mantener
        // la lógica centralizada.
        logic.Service.instance()
                .cancelarReserva(reserva);

        tableModel.setFilas(getMisReservas());

        propertyChangeSupport.firePropertyChange(
                LISTA,
                null,
                getMisReservas()
        );
    }

    /**
     * Genera un nuevo ID de reserva.
     */
    public String generarId() {

        int max =
                Data.getInstancia()
                        .getReservas()
                        .stream()
                        .mapToInt(r -> {

                            try {

                                if (r.getId() == null) {
                                    return 0;
                                }

                                return Integer.parseInt(
                                        r.getId()
                                                .replace(
                                                        "RES-",
                                                        "")
                                );

                            } catch (Exception e) {
                                return 0;
                            }
                        })
                        .max()
                        .orElse(0);

        return String.format(
                "RES-%06d",
                max + 1
        );
    }
}
package logic;

import data.Data;
import utils.AuditoriaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {

    private static Service instance;
    private Data data;
    private final List<Observer> observers = new ArrayList<>();

    private Service() {
        this.data = Data.getInstancia();
    }

    public static synchronized Service instance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    // ==========================================
    // PATRÓN OBSERVER
    // ==========================================

    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    // ==========================================
    // AUTENTICACIÓN Y PERSISTENCIA
    // ==========================================

    public Data getData() {
        return data;
    }

    public void store() {
        if (data != null) {
            data.guardarTodo();
            notifyObservers();
        }
    }

    public Usuario login(String id, String clave) {
        if (data != null && data.getFuncionarios() != null) {
            for (Funcionario f : data.getFuncionarios()) {
                if (f.getId().equals(id) && f.getClave().equals(clave)) {
                    SessionManager.getInstance().setUsuarioLogueado(f);
                    AuditoriaService.registrarAccion(f.getId(), "LOGIN", "Inicio de sesión exitoso");
                    return f;
                }
            }
        }
        AuditoriaService.registrarAccion(id, "LOGIN_FALLIDO", "Intento de inicio de sesión inválido");
        return null;
    }

    public void logout() {
        Usuario u = SessionManager.getInstance().getUsuarioLogueado();
        if (u != null) {
            AuditoriaService.registrarAccion(u.getId(), "LOGOUT", "Cierre de sesión");
            SessionManager.getInstance().logout();
        }
    }

    public List<Funcionario> getFuncionarios() { return data.getFuncionarios(); }
    public List<Categoria> getCategorias() { return data.getCategorias(); }
    public List<Recurso> getRecursos() { return data.getRecursos(); }
    public List<Reserva> getReservas() { return data.getReservas(); }

    // ==========================================
    // VALIDACIÓN Y GESTIÓN DE RESERVAS
    // ==========================================

    private boolean compartenRecurso(Reserva r1, Reserva r2) {
        if (r1.getRecursosAsignados() == null || r2.getRecursosAsignados() == null) {
            return false;
        }
        for (Recurso rec1 : r1.getRecursosAsignados()) {
            for (Recurso rec2 : r2.getRecursosAsignados()) {
                if (rec1.getId().equals(rec2.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean existeSolapamiento(Reserva nuevaReserva) {
        List<Reserva> existentes = getReservas();
        if (existentes == null || nuevaReserva == null) return false;

        for (Reserva existente : existentes) {
            if ("CANCELADA".equalsIgnoreCase(existente.getEstado())) {
                continue;
            }

            if (existente.getId() != null && existente.getId().equals(nuevaReserva.getId())) {
                continue;
            }

            if (nuevaReserva.seSolapaCon(existente)) {
                if (compartenRecurso(nuevaReserva, existente)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void agregarReserva(Reserva nuevaReserva) throws Exception {
        if (existeSolapamiento(nuevaReserva)) {
            throw new Exception("El recurso seleccionado ya se encuentra reservado en el rango de fecha y hora especificado.");
        }
        getReservas().add(nuevaReserva);
        data.guardarReservas();

        Usuario actual = SessionManager.getInstance().getUsuarioLogueado();
        String usrId = (actual != null) ? actual.getId() : "SISTEMA";
        AuditoriaService.registrarAccion(usrId, "CREAR_RESERVA", "Reserva creada con ID: " + nuevaReserva.getId());

        notifyObservers();
    }

    public void cancelarReserva(Reserva reserva) {
        if (reserva != null) {
            reserva.setEstado("CANCELADA");
            data.guardarReservas();

            Usuario actual = SessionManager.getInstance().getUsuarioLogueado();
            String usrId = (actual != null) ? actual.getId() : "SISTEMA";
            AuditoriaService.registrarAccion(usrId, "CANCELAR_RESERVA", "Reserva cancelada con ID: " + reserva.getId());

            notifyObservers();
        }
    }

    // ==========================================
    // BÚSQUEDAS Y FILTROS AVANZADOS (Fase 2)
    // ==========================================

    /**
     * Devuelve los recursos que NO están ocupados en una fecha y rango de horas.
     */
    public List<Recurso> getRecursosDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        List<Recurso> disponibles = new ArrayList<>(getRecursos());

        Reserva temp = new Reserva();
        temp.setFecha(fecha);
        temp.setHoraInicio(horaInicio);
        temp.setHoraFin(horaFin);

        for (Reserva r : getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;

            if (temp.seSolapaCon(r) && r.getRecursosAsignados() != null) {
                disponibles.removeAll(r.getRecursosAsignados());
            }
        }
        return disponibles;
    }

    /**
     * Filtra la lista de reservas según el ID del funcionario y/o su estado.
     */
    public List<Reserva> buscarReservas(String idFuncionario, String estado) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : getReservas()) {
            boolean matchFunc = (idFuncionario == null || idFuncionario.trim().isEmpty()) ||
                    (r.getFuncionario() != null && r.getFuncionario().getId().equalsIgnoreCase(idFuncionario.trim()));
            boolean matchEstado = (estado == null || estado.trim().isEmpty() || estado.equalsIgnoreCase("TODOS")) ||
                    (r.getEstado() != null && r.getEstado().equalsIgnoreCase(estado.trim()));

            if (matchFunc && matchEstado) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // ==========================================
    // MÉTODOS DE ESTADÍSTICAS
    // ==========================================

    /**
     * Categorías reservadas en un período y su conteo.
     */
    public Map<String, Integer> getUsoCategoriasPorPeriodo(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new HashMap<>();
        for (Reserva r : getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;
            if (r.getCategoriasRequeridas() != null) {
                for (logic.Categoria c : r.getCategoriasRequeridas()) {
                    String nombre = c.getNombre() != null ? c.getNombre() : c.getId();
                    conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
                }
            }
        }
        return conteo;
    }

    /**
     * Actividades (reservas activas) agrupadas por semana en un período.
     */
    public Map<String, Integer> getActividadesPorSemana(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new java.util.LinkedHashMap<>();
        java.time.temporal.WeekFields wf = java.time.temporal.WeekFields.of(java.util.Locale.getDefault());
        for (Reserva r : getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;
            int semana = r.getFecha().get(wf.weekOfWeekBasedYear());
            int anio = r.getFecha().get(wf.weekBasedYear());
            String clave = "Semana " + semana + " (" + anio + ")";
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }
        return conteo;
    }

    public Map<String, Integer> getUsoPorRecurso() {
        Map<String, Integer> conteo = new HashMap<>();
        List<Reserva> reservas = getReservas();

        if (reservas != null) {
            for (Reserva r : reservas) {
                if (r.getRecursosAsignados() != null) {
                    for (Recurso recurso : r.getRecursosAsignados()) {
                        String nombre = recurso.getNombre();
                        conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
                    }
                }
            }
        }
        return conteo;
    }

    public Map<String, Integer> getReservasPorEstado() {
        Map<String, Integer> conteo = new HashMap<>();
        List<Reserva> reservas = getReservas();

        if (reservas != null) {
            for (Reserva r : reservas) {
                String estado = (r.getEstado() != null) ? r.getEstado() : "DESCONOCIDO";
                conteo.put(estado, conteo.getOrDefault(estado, 0) + 1);
            }
        }
        return conteo;
    }
}
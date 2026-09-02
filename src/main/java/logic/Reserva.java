package logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {

    private String id;
    private String titulo;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<Recurso> recursosAsignados;
    private List<Categoria> categoriasRequeridas;
    private Funcionario solicitante;
    private String estado; // "ACTIVA", "CANCELADA", etc.

    // Constructor por defecto para XML / Beans
    public Reserva() {
        this.recursosAsignados = new ArrayList<>();
        this.categoriasRequeridas = new ArrayList<>();
        this.estado = "ACTIVA";
    }

    // Constructor Principal (Listas de recursos y categorías)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursosAsignados,
                   List<Categoria> categoriasRequeridas, Funcionario solicitante) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursosAsignados = (recursosAsignados != null) ? recursosAsignados : new ArrayList<>();
        this.categoriasRequeridas = (categoriasRequeridas != null) ? categoriasRequeridas : new ArrayList<>();
        this.solicitante = solicitante;
        this.estado = "ACTIVA";
    }

    // Constructor Sobrecargado (Solo lista de recursos)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursosAsignados, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recursosAsignados, new ArrayList<>(), solicitante);
    }

    // Constructor Legacy / Compatibilidad (Un solo recurso y una sola categoría)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, Recurso recurso, Categoria categoria, Funcionario solicitante) {
        this();
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.solicitante = solicitante;
        if (recurso != null) this.recursosAsignados.add(recurso);
        if (categoria != null) this.categoriasRequeridas.add(categoria);
    }

    // Constructor Legacy / Compatibilidad (Un solo recurso sin categoría)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, Recurso recurso, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recurso, null, solicitante);
    }

    // Getters y Setters Básicos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public List<Recurso> getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(List<Recurso> recursosAsignados) { this.recursosAsignados = recursosAsignados; }

    public List<Categoria> getCategoriasRequeridas() { return categoriasRequeridas; }
    public void setCategoriasRequeridas(List<Categoria> categoriasRequeridas) { this.categoriasRequeridas = categoriasRequeridas; }

    public Funcionario getSolicitante() { return solicitante; }
    public void setSolicitante(Funcionario solicitante) { this.solicitante = solicitante; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Helpers de compatibilidad
    public Recurso getRecurso() {
        return (recursosAsignados != null && !recursosAsignados.isEmpty()) ? recursosAsignados.get(0) : null;
    }

    public Categoria getCategoria() {
        return (categoriasRequeridas != null && !categoriasRequeridas.isEmpty()) ? categoriasRequeridas.get(0) : null;
    }

    public boolean seSolapaCon(Reserva otra) {
        if (otra == null || !this.fecha.equals(otra.getFecha())) {
            return false;
        }
        return this.horaInicio.isBefore(otra.getHoraFin()) && otra.getHoraInicio().isBefore(this.horaFin);
    }
}
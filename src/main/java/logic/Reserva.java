//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package logic;

import data.LocalDateAdapter;
import data.LocalTimeAdapter;
import data.XmlIdAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {
    @XmlID
    @XmlJavaTypeAdapter(XmlIdAdapter.class)
    private String id;
    private String titulo;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;
    @XmlElementWrapper(
            name = "recursosAsignados"
    )
    @XmlElement(
            name = "recurso"
    )
    @XmlIDREF
    private List<Recurso> recursosAsignados;
    @XmlElementWrapper(
            name = "categoriasRequeridas"
    )
    @XmlElement(
            name = "categoria"
    )
    @XmlIDREF
    private List<Categoria> categoriasRequeridas;
    @XmlIDREF
    private Funcionario solicitante;
    private String estado;

    public Reserva() {
        this.recursosAsignados = new ArrayList();
        this.categoriasRequeridas = new ArrayList();
        this.estado = "ACTIVA";
    }

    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<Recurso> recursosAsignados, List<Categoria> categoriasRequeridas, Funcionario solicitante) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursosAsignados = copiarRecursos(recursosAsignados);
        this.categoriasRequeridas = copiarCategorias(categoriasRequeridas);
        this.solicitante = solicitante;
        this.estado = "ACTIVA";
    }

    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<Recurso> recursosAsignados, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recursosAsignados, new ArrayList(), solicitante);
    }

    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Recurso recurso, Categoria categoria, Funcionario solicitante) {
        this();
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.solicitante = solicitante;
        if (recurso != null) {
            this.recursosAsignados.add(recurso);
        }

        if (categoria != null) {
            this.categoriasRequeridas.add(categoria);
        }

    }

    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Recurso recurso, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recurso, (Categoria)null, solicitante);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public List<Recurso> getRecursosAsignados() {
        return this.recursosAsignados;
    }

    public void setRecursosAsignados(List<Recurso> recursosAsignados) {
        this.recursosAsignados = copiarRecursos(recursosAsignados);
    }

    public List<Categoria> getCategoriasRequeridas() {
        return this.categoriasRequeridas;
    }

    public void setCategoriasRequeridas(List<Categoria> categoriasRequeridas) {
        this.categoriasRequeridas = copiarCategorias(categoriasRequeridas);
    }

    private static List<Recurso> copiarRecursos(List<Recurso> origen) {
        return origen != null ? new ArrayList(origen) : new ArrayList();
    }

    private static List<Categoria> copiarCategorias(List<Categoria> origen) {
        return origen != null ? new ArrayList(origen) : new ArrayList();
    }

    public Funcionario getSolicitante() {
        return this.solicitante;
    }

    public void setSolicitante(Funcionario solicitante) {
        this.solicitante = solicitante;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Recurso getRecurso() {
        return this.recursosAsignados != null && !this.recursosAsignados.isEmpty() ? (Recurso)this.recursosAsignados.get(0) : null;
    }

    public Categoria getCategoria() {
        return this.categoriasRequeridas != null && !this.categoriasRequeridas.isEmpty() ? (Categoria)this.categoriasRequeridas.get(0) : null;
    }

    public boolean seSolapaCon(Reserva otra) {
        if (otra != null && this.fecha != null && this.fecha.equals(otra.getFecha())) {
            if (this.horaInicio != null && this.horaFin != null && otra.getHoraInicio() != null && otra.getHoraFin() != null) {
                return this.horaInicio.isBefore(otra.getHoraFin()) && otra.getHoraInicio().isBefore(this.horaFin);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isActiva() {
        return this.estado == null || this.estado.isBlank() || "ACTIVA".equalsIgnoreCase(this.estado);
    }

    public boolean esFutura() {
        return this.fecha != null && this.horaInicio != null ? LocalDateTime.of(this.fecha, this.horaInicio).isAfter(LocalDateTime.now()) : false;
    }

    public boolean cubreHora(LocalTime hora) {
        if (hora != null && this.horaInicio != null && this.horaFin != null) {
            return !hora.isBefore(this.horaInicio) && hora.isBefore(this.horaFin);
        } else {
            return false;
        }
    }

    public boolean usaRecurso(String recursoId) {
        return recursoId != null && this.recursosAsignados != null ? this.recursosAsignados.stream().anyMatch((r) -> r != null && recursoId.equals(r.getId())) : false;
    }

    public String etiquetaCelda() {
        String actividad = this.titulo != null ? this.titulo : "";
        String nombre = this.solicitante != null ? this.solicitante.getNombre() : "";
        if (actividad.isEmpty()) {
            return nombre;
        } else {
            return nombre != null && !nombre.isEmpty() ? actividad + " / " + nombre : actividad;
        }
    }
}

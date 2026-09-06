package data;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private static Data instancia;
    private List<Funcionario> funcionarios;
    private List<Reserva> reservas;
    private List<Recurso> recursos;
    private List<Categoria> categorias;

    private Data() {
        funcionarios = XmlPersister.cargarFuncionarios();
        if (funcionarios == null || funcionarios.isEmpty()) {
            funcionarios = new ArrayList<>();
            funcionarios.add(new Funcionario("1234", "123", "ADMIN", "Administrador", "00000000"));
            XmlPersister.guardarFuncionarios(funcionarios);
        }

        categorias = XmlPersister.cargarCategorias();
        if (categorias == null) categorias = new ArrayList<>();

        recursos = XmlPersister.cargarRecursos();
        if (recursos == null) recursos = new ArrayList<>();

        reservas = XmlPersister.cargarReservas();
        if (reservas == null) reservas = new ArrayList<>();

        // Asegurar persistencia final en shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::guardarTodo));
    }

    public static synchronized Data getInstancia() {
        if (instancia == null) {
            instancia = new Data();
        }
        return instancia;
    }

    // --- Listas de datos ---
    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public List<Reserva> getReservas() { return reservas; }
    public List<Recurso> getRecursos() { return recursos; }
    public List<Categoria> getCategorias() { return categorias; }

    // --- Métodos de guardado explícito ---
    public void guardarTodo() {
        XmlPersister.guardarFuncionarios(funcionarios);
        XmlPersister.guardarCategorias(categorias);
        XmlPersister.guardarRecursos(recursos);
        XmlPersister.guardarReservas(reservas);
    }

    public void guardarFuncionarios() { XmlPersister.guardarFuncionarios(funcionarios); }
    public void guardarCategorias() { XmlPersister.guardarCategorias(categorias); }
    public void guardarRecursos() { XmlPersister.guardarRecursos(recursos); }
    public void guardarReservas() { XmlPersister.guardarReservas(reservas); }
}
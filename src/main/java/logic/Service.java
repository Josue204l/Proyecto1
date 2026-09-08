package logic;

import data.Data;
import java.util.List;

public class Service {

    private static Service instance;
    private Data data;

    private Service() {
        this.data = Data.getInstancia();
    }

    public static synchronized Service instance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    // Alias para garantizar compatibilidad con ControllerLogin y AIService
    public static Service getInstancia() {
        return instance();
    }

    public Data getData() {
        return data;
    }

    public void store() {
        if (data != null) {
            data.guardarTodo();
        }
    }

    public Usuario login(String id, String clave) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null && usuario.getClave() != null && usuario.getClave().equals(clave)) {
            return usuario;
        }
        return null;
    }

    public Usuario buscarPorId(String id) {
        if (id == null || data == null || data.getFuncionarios() == null) return null;
        for (Funcionario f : data.getFuncionarios()) {
            if (id.equals(f.getId())) {
                return f;
            }
        }
        return null;
    }

    // Método puente para obtener el funcionario
    public Funcionario getFuncionario(String id) {
        Usuario u = buscarPorId(id);
        if (u instanceof Funcionario) {
            return (Funcionario) u;
        }
        return null;
    }

    public List<Funcionario> getFuncionarios() { return data.getFuncionarios(); }
    public List<Categoria> getCategorias() { return data.getCategorias(); }
    public List<Recurso> getRecursos() { return data.getRecursos(); }
    public List<Reserva> getReservas() { return data.getReservas(); }
}
package data;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class XmlPersister {

    private static final String DIR = "datos/";
    private static final String FUNCIONARIOS = DIR + "funcionarios.xml";
    private static final String RECURSOS = DIR + "recursos.xml";
    private static final String CATEGORIAS = DIR + "categorias.xml";
    private static final String RESERVAS = DIR + "reservas.xml";

    @SuppressWarnings("unchecked")
    public static List<Funcionario> cargarFuncionarios() { return cargar(FUNCIONARIOS); }
    public static void guardarFuncionarios(List<Funcionario> lista) { guardar(FUNCIONARIOS, lista); }

    @SuppressWarnings("unchecked")
    public static List<Recurso> cargarRecursos() { return cargar(RECURSOS); }
    public static void guardarRecursos(List<Recurso> lista) { guardar(RECURSOS, lista); }

    @SuppressWarnings("unchecked")
    public static List<Categoria> cargarCategorias() { return cargar(CATEGORIAS); }
    public static void guardarCategorias(List<Categoria> lista) { guardar(CATEGORIAS, lista); }

    @SuppressWarnings("unchecked")
    public static List<Reserva> cargarReservas() { return cargar(RESERVAS); }
    public static void guardarReservas(List<Reserva> lista) { guardar(RESERVAS, lista); }

    @SuppressWarnings("unchecked")
    private static <T> List<T> cargar(String ruta) {
        File archivo = new File(ruta);
        if (!archivo.exists()) return null;
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(archivo)))) {
            // Captura errores internos en deserialización si una propiedad no coincide
            decoder.setExceptionListener(e -> System.err.println("Error deserializando XML [" + ruta + "]: " + e.getMessage()));
            return (List<T>) decoder.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void guardar(String ruta, Object objeto) {
        new File(DIR).mkdirs();
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(ruta)))) {
            configurarDelegates(encoder);
            encoder.writeObject(objeto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enseña a XMLEncoder cómo instanciar objetos LocalDate y LocalTime
     * usando sus métodos estáticos parse().
     */
    private static void configurarDelegates(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(LocalDate.class, new DefaultPersistenceDelegate() {
            @Override
            protected Expression instantiate(Object oldInstance, Encoder out) {
                LocalDate date = (LocalDate) oldInstance;
                return new Expression(date, LocalDate.class, "parse", new Object[]{date.toString()});
            }
        });

        encoder.setPersistenceDelegate(LocalTime.class, new DefaultPersistenceDelegate() {
            @Override
            protected Expression instantiate(Object oldInstance, Encoder out) {
                LocalTime time = (LocalTime) oldInstance;
                return new Expression(time, LocalTime.class, "parse", new Object[]{time.toString()});
            }
        });
    }
}
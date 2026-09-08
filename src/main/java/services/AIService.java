package services;

import logic.Categoria;
import logic.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIService {

    public static class DatosExtraidos {
        public String actividad = "";
        public LocalDate fecha = null;
        public LocalTime horaInicio = null;
        public LocalTime horaFin = null;
        public List<Categoria> categoriasEncontradas = new ArrayList<>();
    }

    public static DatosExtraidos extraerInformacion(String frase) {
        DatosExtraidos datos = new DatosExtraidos();
        if (frase == null || frase.trim().isEmpty()) {
            return datos;
        }

        String texto = frase.trim();

        // 1. Extraer Fecha (Formatos: YYYY-MM-DD o DD/MM/YYYY)
        Pattern patronFecha = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})|(\\d{2}/\\d{2}/\\d{4})");
        Matcher matcherFecha = patronFecha.matcher(texto);
        if (matcherFecha.find()) {
            String fStr = matcherFecha.group();
            try {
                if (fStr.contains("-")) {
                    datos.fecha = LocalDate.parse(fStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } else {
                    datos.fecha = LocalDate.parse(fStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            } catch (Exception ignored) {}
        }

        // 2. Extraer Horas (Formatos: HH:mm)
        Pattern patronHora = Pattern.compile("(\\d{2}:\\d{2})");
        Matcher matcherHora = patronHora.matcher(texto);
        List<LocalTime> horas = new ArrayList<>();
        while (matcherHora.find()) {
            try {
                horas.add(LocalTime.parse(matcherHora.group()));
            } catch (Exception ignored) {}
        }

        if (horas.size() >= 1) datos.horaInicio = horas.get(0);
        if (horas.size() >= 2) datos.horaFin = horas.get(1);

        // 3. Coincidencia de Categorías
        List<Categoria> todasCategorias = Service.instance().getCategorias();
        if (todasCategorias != null) {
            for (Categoria cat : todasCategorias) {
                String nombre = cat.getNombre() != null ? cat.getNombre().toLowerCase() : "";
                String desc = cat.getDescripcion() != null ? cat.getDescripcion().toLowerCase() : "";
                if ((!nombre.isEmpty() && texto.toLowerCase().contains(nombre)) ||
                        (!desc.isEmpty() && texto.toLowerCase().contains(desc))) {
                    datos.categoriasEncontradas.add(cat);
                }
            }
        }

        // 4. Inferencia del Título/Actividad (Elimina fechas/horas para dejar el texto limpio)
        String actividadLimpia = texto.replaceAll("(\\d{4}-\\d{2}-\\d{2})|(\\d{2}/\\d{2}/\\d{4})", "")
                .replaceAll("(\\d{2}:\\d{2})", "")
                .replaceAll("(?i)(el|de|a|las|en|para|con)", "")
                .replaceAll("\\s+", " ")
                .trim();
        datos.actividad = actividadLimpia.isEmpty() ? texto : actividadLimpia;

        return datos;
    }
}
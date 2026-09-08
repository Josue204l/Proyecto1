package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

// Imports de iText 7
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import utils.PDFGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ControllerEstadisticas {

    private final estadisticasView view;
    private final ModelEstadisticas model;

    public ControllerEstadisticas(estadisticasView view, ModelEstadisticas model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            view.setController(this);

            // Establecer fechas por defecto si están vacías (primer día del año actual hasta hoy)
            if (view.getDpRecursosDesde().getDate() == null) {
                view.getDpRecursosDesde().setDate(LocalDate.now().withDayOfYear(1));
            }
            if (view.getDpRecursosHasta().getDate() == null) {
                view.getDpRecursosHasta().setDate(LocalDate.now());
            }
            if (view.getDpActividadesDesde().getDate() == null) {
                view.getDpActividadesDesde().setDate(LocalDate.now().withDayOfYear(1));
            }
            if (view.getDpActividadesHasta().getDate() == null) {
                view.getDpActividadesHasta().setDate(LocalDate.now());
            }
        }
    }

    public ControllerEstadisticas(ModelEstadisticas model) {
        this(null, model);
    }

    public void cargarDatos() {
        cargarRecursos();
        cargarActividades();
    }

    public void cargarRecursos() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            List<EstadisticaFila> filas = model.calcularRecursos(rango[0], rango[1]);

            if (view.getTableRecursos() != null) {
                view.getTableRecursos().setModel(model.getTableRecursos());
            }

            pintarGrafico(view.getChartRecursos(), "Recursos reservados", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarActividades() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            List<EstadisticaFila> filas = model.calcularActividades(rango[0], rango[1]);

            if (view.getTableActividades() != null) {
                view.getTableActividades().setModel(model.getTableActividades());
            }

            pintarGrafico(view.getChartActividades(), "Actividades por semana", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera el PDF usando iText 7 y la clase auxiliar utils.PDFGenerator
     */
    public void print() {
        if (view == null) return;

        try {
            // 1. Obtener rango de fechas de Recursos o Actividades
            LocalDate[] rango;
            try {
                rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            } catch (Exception e) {
                rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            }

            // 2. Seleccionar dónde guardar el archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Reporte PDF");
            fileChooser.setSelectedFile(new File("Reporte_Estadisticas.pdf"));

            int userSelection = fileChooser.showSaveDialog(view.getMainPanel());
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String dest = fileChooser.getSelectedFile().getAbsolutePath();
            if (!dest.toLowerCase().endsWith(".pdf")) {
                dest += ".pdf";
            }

            // 3. Crear el documento PDF con iText 7
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE ESTADÍSTICAS").setBold().setFontSize(16);
            document.add(titulo);

            Paragraph rangoFechas = new Paragraph("Rango de Fechas: " + rango[0] + " a " + rango[1]);
            document.add(rangoFechas);
            document.add(new Paragraph("\n"));

            // --- TABLA DE RECURSOS ---
            document.add(new Paragraph("Recursos Reservados").setBold());
            List<EstadisticaFila> recursos = model.calcularRecursos(rango[0], rango[1]);

            Table tablaRecursos = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            tablaRecursos.useAllAvailableWidth();

            // Encabezados usando PDFGenerator.getCell
            tablaRecursos.addHeaderCell(PDFGenerator.getCell(new Paragraph("Categoría").setBold(), 0, true));
            tablaRecursos.addHeaderCell(PDFGenerator.getCell(new Paragraph("Cantidad").setBold(), 0, true));

            for (EstadisticaFila f : recursos) {
                tablaRecursos.addCell(PDFGenerator.getCell(new Paragraph(f.getEtiqueta()), 0, true));
                tablaRecursos.addCell(PDFGenerator.getCell(new Paragraph(String.valueOf(f.getCantidad())), 0, true));
            }
            document.add(tablaRecursos);
            document.add(new Paragraph("\n"));

            // --- TABLA DE ACTIVIDADES ---
            document.add(new Paragraph("Actividades por Semana").setBold());
            List<EstadisticaFila> actividades = model.calcularActividades(rango[0], rango[1]);

            Table tablaActividades = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            tablaActividades.useAllAvailableWidth();

            // Encabezados
            tablaActividades.addHeaderCell(PDFGenerator.getCell(new Paragraph("Semana").setBold(), 0, true));
            tablaActividades.addHeaderCell(PDFGenerator.getCell(new Paragraph("Cantidad").setBold(), 0, true));

            for (EstadisticaFila f : actividades) {
                tablaActividades.addCell(PDFGenerator.getCell(new Paragraph(f.getEtiqueta()), 0, true));
                tablaActividades.addCell(PDFGenerator.getCell(new Paragraph(String.valueOf(f.getCantidad())), 0, true));
            }
            document.add(tablaActividades);

            document.close();

            // 4. Abrir el PDF automáticamente usando utils.PDFGenerator
            PDFGenerator.openPdf(dest);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Error al generar el reporte PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate[] leerRango(DatePicker desdeCampo, DatePicker hastaCampo) throws Exception {
        LocalDate desde = desdeCampo != null ? desdeCampo.getDate() : null;
        LocalDate hasta = hastaCampo != null ? hastaCampo.getDate() : null;
        if (desde == null || hasta == null) {
            throw new Exception("Seleccione las fechas desde y hasta.");
        }
        if (hasta.isBefore(desde)) {
            throw new Exception("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
        return new LocalDate[]{desde, hasta};
    }

    private void pintarGrafico(JPanel contenedor, String titulo, List<EstadisticaFila> filas) {
        if (contenedor == null || filas == null) return;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EstadisticaFila f : filas) {
            dataset.addValue(f.getCantidad(), "Cantidad", f.getEtiqueta());
        }

        JFreeChart chart = ChartFactory.createBarChart(titulo, "Categoría / Semana", "Cantidad", dataset, PlotOrientation.VERTICAL, false, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 220));

        contenedor.removeAll();
        contenedor.setLayout(new BorderLayout());
        contenedor.add(chartPanel, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    public ModelEstadisticas getModel() {
        return model;
    }
}
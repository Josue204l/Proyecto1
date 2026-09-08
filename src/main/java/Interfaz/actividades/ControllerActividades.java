package Interfaz.actividades;

import Interfaz.utils.PDFGenerator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import javax.swing.*;
import java.time.LocalDate;

public class ControllerActividades {

    private final actividadesView view;
    private final ModelActividades model;

    public ControllerActividades(actividadesView view, ModelActividades model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            view.setController(this);
            if (view.getTable() != null) {
                view.getTable().setRowHeight(28);
                view.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
        }
        cargar();
    }

    public ControllerActividades(ModelActividades model) {
        this(null, model);
    }

    public void cargar() {
        if (view == null) return;
        LocalDate fecha = view.getDatePicker() != null && view.getDatePicker().getDate() != null
                ? view.getDatePicker().getDate()
                : LocalDate.now();
        model.cargarSemana(fecha);
        if (view.getTable() != null) {
            view.getTable().setModel(model.getTableModel());
            view.getTable().getColumnModel().getColumn(0).setPreferredWidth(70);
            for (int i = 1; i < view.getTable().getColumnCount(); i++) {
                view.getTable().getColumnModel().getColumn(i).setPreferredWidth(140);
            }
        }
        if (view.getLblSemana() != null) {
            view.getLblSemana().setText(model.etiquetaSemana());
        }
    }

    public void print() {
        try {
            String dest = "actividades.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Actividades semanales"));
            JTable tabla = view.getTable();
            if (tabla != null && tabla.getColumnCount() > 0) {
                Table table = new Table(tabla.getColumnCount());
                for (int c = 0; c < tabla.getColumnCount(); c++) {
                    table.addHeaderCell(Pdf.getCell(new Paragraph(String.valueOf(tabla.getColumnName(c))), 1, true));
                }
                for (int r = 0; r < tabla.getRowCount(); r++) {
                    for (int c = 0; c < tabla.getColumnCount(); c++) {
                        Object val = tabla.getValueAt(r, c);
                        table.addCell(Pdf.getCell(new Paragraph(val == null ? "" : val.toString()), 0, true));
                    }
                }
                document.add(table);
            }
            document.close();
            Pdf.openPdf(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ModelActividades getModel() {
        return model;
    }
}

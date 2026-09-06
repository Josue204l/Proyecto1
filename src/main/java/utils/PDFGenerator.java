package utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.FileOutputStream;

public class PDFGenerator {

    public static void generarReporteTabla(String titulo, JTable tabla) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) {
                path += ".pdf";
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();

                // Título
                document.add(new Paragraph(titulo));
                document.add(new Paragraph(" ")); // Espacio

                // Contenido de la Tabla
                PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());

                // Encabezados
                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    pdfTable.addCell(tabla.getColumnName(i));
                }

                // Filas
                for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                    for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                        Object val = tabla.getValueAt(rows, cols);
                        pdfTable.addCell(val != null ? val.toString() : "");
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(null, "PDF generado con éxito en:\n" + path);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
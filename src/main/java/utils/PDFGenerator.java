package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    /**
     * Genera un reporte PDF con estilo profesional basado en una JTable y lo abre automáticamente.
     */
    public static void generarReporteTabla(String titulo, JTable tabla) {
        if (tabla == null || tabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos en la tabla para exportar a PDF.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File(titulo.replaceAll("\\s+", "_") + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            File archivoDestino = new File(path);
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);

            try {
                PdfWriter.getInstance(document, new FileOutputStream(archivoDestino));
                document.open();

                // Título
                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
                Paragraph pTitulo = new Paragraph(titulo, fontTitulo);
                pTitulo.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitulo);

                // Fecha y hora de generación
                Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
                String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                Paragraph pFecha = new Paragraph("Generado el: " + fechaActual, fontSub);
                pFecha.setAlignment(Element.ALIGN_CENTER);
                pFecha.setSpacingAfter(20);
                document.add(pFecha);

                // Tabla
                PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Encabezados
                Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i), fontHeader));
                    cell.setBackgroundColor(new BaseColor(41, 128, 185));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(6);
                    pdfTable.addCell(cell);
                }

                // Filas
                Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
                for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                    for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                        Object val = tabla.getValueAt(rows, cols);
                        PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", fontBody));
                        cell.setPadding(5);
                        if (rows % 2 == 1) {
                            cell.setBackgroundColor(new BaseColor(245, 245, 245));
                        }
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(null, "PDF generado con éxito en:\n" + path, "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Abrir automáticamente el documento creado
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivoDestino);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
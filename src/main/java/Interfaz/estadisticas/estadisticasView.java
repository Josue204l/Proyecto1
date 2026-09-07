package Interfaz.estadisticas;

import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.*;

public class estadisticasView {
    private JPanel panel1;
    private JTextField textField1;
    private JButton button1;
    private JTextField textField2;
    private JButton button2;
    private JButton cargarButton;
    private JTable table1;

    // Panel contenedor dinámico para el gráfico
    private JPanel panelGrafico;

    public estadisticasView() {
        // Inicializamos el contenedor del gráfico
        if (panelGrafico == null) {
            panelGrafico = new JPanel(new BorderLayout());
        }

        // Si panel1 fue inicializado por el GUI Designer pero necesita alojar componentes de forma dinámica
        if (panel1 != null) {
            panel1.setLayout(new BorderLayout());
            panel1.add(panelGrafico, BorderLayout.SOUTH);
        }
    }

    /**
     * Muestra el gráfico generado por JFreeChart dentro de la interfaz.
     * @param chartPanel Panel retornado por el ControllerEstadisticas.
     */
    public void mostrarGrafico(ChartPanel chartPanel) {
        if (panelGrafico == null) {
            panelGrafico = new JPanel(new BorderLayout());
            if (panel1 != null) {
                panel1.setLayout(new BorderLayout());
                panel1.add(panelGrafico, BorderLayout.SOUTH);
            }
        }

        panelGrafico.removeAll();
        panelGrafico.setLayout(new BorderLayout());
        panelGrafico.add(chartPanel, BorderLayout.CENTER);
        panelGrafico.revalidate();
        panelGrafico.repaint();
    }

    // Getters
    public JPanel getMainPanel() { return panel1 != null ? panel1 : new JPanel(); }
    public JTable getTable() { return table1; }
    public JTextField getTxtFechaDesde() { return textField1; }
    public JTextField getTxtFechaHasta() { return textField2; }
    public JButton getBtnFiltrar() { return button1; }
    public JButton getBtnExportar() { return button2; }
    public JButton getCargarButton() { return cargarButton; }
    public JPanel getPanelGrafico() { return panelGrafico; }
}
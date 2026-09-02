package Interfaz.reservas;

import data.Data;
import logic.Categoria;

import javax.swing.*;
import java.util.List;

public class reservasView {

    // Panel Principal
    private JPanel reservaspanel;

    // Referencia al controlador
    private ControllerReserva controller;

    // Campos de Texto
    private JTextField txtFrase;
    private JTextField txtActividad;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextField txtCategoriasRequeridas;

    // Botones de acción y selectores
    private JButton extraerButton;
    private JButton btnSeleccionarFecha;
    private JButton btnHoraInicio;
    private JButton btnHoraFin;
    private JButton LImpiarButton;
    private JButton reservasButton;
    private JButton cancelarReservaSelecionadaButton;
    private JButton imprimirButton;

    // Listas y Tablas (Soporta tipo genérico Categoria)
    private JList<Categoria> listCategorias;
    private JTable tableMisReservas;

    public reservasView() {
        inicializarListaCategorias();
    }

    /**
     * Configura el modelo de JList para mostrar objetos Categoria
     * y habilita la selección múltiple.
     */
    public void inicializarListaCategorias() {
        if (listCategorias != null) {
            DefaultListModel<Categoria> model = new DefaultListModel<>();
            List<Categoria> categorias = Data.getInstancia().getCategorias();
            if (categorias != null) {
                for (Categoria cat : categorias) {
                    model.addElement(cat);
                }
            }
            listCategorias.setModel(model);
            listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
    }

    // --- MÉTODOS DE CONTROLADOR ---

    public void setController(ControllerReserva controller) {
        this.controller = controller;
    }

    public ControllerReserva getController() {
        return controller;
    }

    // --- MÉTODOS GETTER PARA COMPONENTES ---

    public JPanel getMainPanel() {
        return reservaspanel;
    }

    public JPanel getReservasPanel() {
        return reservaspanel;
    }

    public JTable getTablaMisReservas() {
        return tableMisReservas;
    }

    public JTextField getTextFrase() {
        return txtFrase;
    }

    public JTextField getTextFecha() {
        return txtFecha;
    }

    public JTextField getTxtActividad() {
        return txtActividad;
    }

    public JTextField getTxtHoraInicio() {
        return txtHoraInicio;
    }

    public JTextField getTxtHoraFin() {
        return txtHoraFin;
    }

    public JTextField getTxtCategoriasRequeridas() {
        return txtCategoriasRequeridas;
    }

    public JButton getExtraerButton() {
        return extraerButton;
    }

    public JButton getBtnSeleccionarFecha() {
        return btnSeleccionarFecha;
    }

    public JButton getBtnHoraInicio() {
        return btnHoraInicio;
    }

    public JButton getBtnHoraFin() {
        return btnHoraFin;
    }

    public JButton getLImpiarButton() {
        return LImpiarButton;
    }

    public JButton getReservasButton() {
        return reservasButton;
    }

    public JButton getCancelarReservaSelecionadaButton() {
        return cancelarReservaSelecionadaButton;
    }

    public JButton getImprimirButton() {
        return imprimirButton;
    }

    /**
     * Devuelve el JList fuertemente tipado con objetos Categoria.
     */
    public JList<Categoria> getListaCategorias() {
        return listCategorias;
    }
}
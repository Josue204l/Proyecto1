package Interfaz.reservas;

import logic.Categoria;
import logic.Service;

import javax.swing.*;
import java.util.List;

public class reservasView {

    private JPanel reservaspanel;
    private JTextField txtFrase;
    private JTextField txtActividad;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JList<Categoria> listCategorias;
    private JTable tableMisReservas;
    private JButton extraerButton;
    private JButton btnSeleccionarFecha;
    private JButton btnHoraInicio;
    private JButton btnHoraFin;
    private JButton reservasButton;
    private JButton cancelarReservaSelecionadaButton;
    private JButton LImpiarButton;
    private JButton imprimirButton;

    private ControllerReserva controller;

    public reservasView() {
        inicializarListaCategorias();
    }

    public void setController(ControllerReserva controller) {
        this.controller = controller;
    }

    private void inicializarListaCategorias() {
        DefaultListModel<Categoria> listModel = new DefaultListModel<>();
        List<Categoria> categorias = Service.instance().getCategorias();
        if (categorias != null) {
            for (Categoria c : categorias) {
                listModel.addElement(c);
            }
        }
        listCategorias.setModel(listModel);
        listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    // --- Getters de Componentes ---
    public JPanel getMainPanel() { return reservaspanel; }
    public JTextField getTextFrase() { return txtFrase; }
    public JTextField getTxtActividad() { return txtActividad; }
    public JTextField getTextFecha() { return txtFecha; }
    public JTextField getTxtHoraInicio() { return txtHoraInicio; }
    public JTextField getTxtHoraFin() { return txtHoraFin; }
    public JList<Categoria> getListaCategorias() { return listCategorias; }
    public JTable getTablaMisReservas() { return tableMisReservas; }

    // --- Getters de Botones ---
    public JButton getExtraerButton() { return extraerButton; }
    public JButton getBtnSeleccionarFecha() { return btnSeleccionarFecha; }
    public JButton getBtnHoraInicio() { return btnHoraInicio; }
    public JButton getBtnHoraFin() { return btnHoraFin; }
    public JButton getReservasButton() { return reservasButton; }
    public JButton getCancelarReservaSelecionadaButton() { return cancelarReservaSelecionadaButton; }
    public JButton getLImpiarButton() { return LImpiarButton; }
    public JButton getImprimirButton() { return imprimirButton; }
}
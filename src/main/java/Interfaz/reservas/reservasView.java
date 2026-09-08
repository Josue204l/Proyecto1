package Interfaz.reservas;

import com.github.lgooddatepicker.components.DatePicker;
import logic.Categoria;
import logic.Service;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class reservasView {

    private JPanel reservaspanel;
    private JTextField txtFrase;
    private JTextField txtActividad;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextField txtCategoriasRequeridas; // Agregado para mostrar texto de categorias requeridas
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

    // Componente wrapper/adaptador para DatePicker
    private DatePicker datePickerFecha;

    private ControllerReserva controller;

    public reservasView() {
        this.datePickerFecha = new DatePicker();
        inicializarListaCategorias();
        configurarListeners();
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
        if (listCategorias != null) {
            listCategorias.setModel(listModel);
            listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
    }

    private void configurarListeners() {
        if (extraerButton != null) {
            extraerButton.addActionListener(e -> {
                if (controller != null) controller.extraerConIA();
            });
        }
        if (reservasButton != null) {
            reservasButton.addActionListener(e -> {
                if (controller != null) controller.guardar();
            });
        }
        if (cancelarReservaSelecionadaButton != null) {
            cancelarReservaSelecionadaButton.addActionListener(e -> {
                if (controller != null) controller.cancelarSeleccionada();
            });
        }
        if (LImpiarButton != null) {
            LImpiarButton.addActionListener(e -> {
                if (controller != null) controller.limpiar();
            });
        }
        if (imprimirButton != null) {
            imprimirButton.addActionListener(e -> {
                if (controller != null) controller.print();
            });
        }
        if (btnHoraInicio != null) {
            btnHoraInicio.addActionListener(e -> {
                if (controller != null) controller.elegirHoraInicio();
            });
        }
        if (btnHoraFin != null) {
            btnHoraFin.addActionListener(e -> {
                if (controller != null) controller.elegirHoraFin();
            });
        }
    }

    // --- Adaptador de DatePicker a JTextField ---
    public DatePicker getDatePickerFecha() {
        if (datePickerFecha == null) {
            datePickerFecha = new DatePicker();
        }
        // Sincronizar texto de txtFecha hacia el DatePicker si contiene una fecha
        if (txtFecha != null && !txtFecha.getText().isBlank()) {
            try {
                LocalDate parsed = LocalDate.parse(txtFecha.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                datePickerFecha.setDate(parsed);
            } catch (Exception ignored) {
                try {
                    LocalDate parsedIso = LocalDate.parse(txtFecha.getText().trim());
                    datePickerFecha.setDate(parsedIso);
                } catch (Exception ignored2) {}
            }
        }
        return datePickerFecha;
    }

    // --- Getters de Componentes ---
    public JPanel getMainPanel() { return reservaspanel; }
    public JTextField getTextFrase() { return txtFrase; }
    public JTextField getTxtActividad() { return txtActividad; }
    public JTextField getTextFecha() { return txtFecha; }
    public JTextField getTxtHoraInicio() { return txtHoraInicio; }
    public JTextField getTxtHoraFin() { return txtHoraFin; }

    public JTextField getTxtCategoriasRequeridas() {
        if (txtCategoriasRequeridas == null) {
            txtCategoriasRequeridas = new JTextField();
        }
        return txtCategoriasRequeridas;
    }

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
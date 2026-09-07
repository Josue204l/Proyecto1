package Interfaz.calendarizacion;

import data.Data;
import logic.Categoria;
import logic.Observer;
import logic.Reserva;
import logic.Service;
import logic.Funcionario;
import utils.PDFGenerator;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class ControllerCalendarizacion implements Observer {

    private final calendarizacionView view;
    private final ModelCalendarizacion model;
    private final Funcionario funcionario;

    public ControllerCalendarizacion(calendarizacionView view, ModelCalendarizacion model, Funcionario funcionario) {
        this.view = view;
        this.model = model;
        this.funcionario = funcionario;

        this.view.setController(this);
        this.view.setModel(this.model);

        // Poblar combo de categorías
        poblarComboCategorias();

        registrarEventos();
        Service.instance().addObserver(this);
        filtrar();
    }

    private void poblarComboCategorias() {
        if (view.getCmbCategoria() != null) {
            DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
            comboModel.addElement(null); // opción "Todas"
            for (Categoria c : Data.getInstancia().getCategorias()) {
                comboModel.addElement(c);
            }
            view.getCmbCategoria().setModel(comboModel);
        }
    }

    private void registrarEventos() {
        if (view.getBtnCargar() != null) {
            view.getBtnCargar().addActionListener(e -> filtrar());
        }
        if (view.getBtnImprimir() != null) {
            view.getBtnImprimir().addActionListener(e ->
                PDFGenerator.generarReporteTabla("Reporte_Calendarizacion", view.getTblCalendarizacion()));
        }
    }

    public void filtrar() {
        LocalDate fecha = null;
        if (view.getTxtFecha() != null && !view.getTxtFecha().getText().trim().isEmpty()) {
            try {
                fecha = LocalDate.parse(view.getTxtFecha().getText().trim());
            } catch (Exception ignored) {}
        }

        Categoria categoria = null;
        if (view.getCmbCategoria() != null) {
            categoria = (Categoria) view.getCmbCategoria().getSelectedItem();
        }

        List<Reserva> reservas;
        if (fecha != null && categoria != null) {
            reservas = model.getReservasPorFechaYCategoria(fecha, categoria);
        } else if (fecha != null) {
            reservas = model.getReservasPorFecha(fecha);
        } else if (categoria != null) {
            reservas = model.getReservasPorCategoria(categoria);
        } else {
            reservas = model.getTodasLasReservas();
        }

        model.setReservas(reservas);
    }

    @Override
    public void update() {
        poblarComboCategorias();
        filtrar();
    }
}

package Interfaz.main;

import Interfaz.reservas.ControllerReserva;
import Interfaz.reservas.reservasView;
import logic.Funcionario;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        setTitle("Sistema de Gestión de Reservas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Instanciar la vista y el controlador de reservas
        reservasView viewReserva = new reservasView();

        if (usuario instanceof Funcionario funcionario) {
            new ControllerReserva(viewReserva, funcionario);
        } else {
            // Manejo por defecto o admin
            new ControllerReserva(viewReserva, null);
        }

        // Cargar el panel principal de la vista en el JFrame
        setContentPane(viewReserva.getMainPanel());

        // Ajustar tamaño de la ventana
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
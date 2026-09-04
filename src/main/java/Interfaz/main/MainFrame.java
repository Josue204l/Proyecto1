package Interfaz.main;

import Interfaz.reservas.ControllerReserva;
import Interfaz.reservas.reservasView;
import logic.Funcionario;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        setLocationRelativeTo(null);
        setVisible(true);
    }
}
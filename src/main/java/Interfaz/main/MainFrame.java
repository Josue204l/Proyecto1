package Interfaz.main;

// Importaciones de módulos y vistas
import Interfaz.actividades.ControllerActividades;
import Interfaz.actividades.ModelActividades;
import Interfaz.actividades.actividadesView;
import Interfaz.calendarizacion.ControllerCalendarizacion;
import Interfaz.calendarizacion.ModelCalendarizacion;
import Interfaz.calendarizacion.calendarizacionView;
import Interfaz.categorias.ControllerCategoria;
import Interfaz.categorias.ModelCategoria;
import Interfaz.categorias.categoriasView;
import Interfaz.estadisticas.ControllerEstadisticas;
import Interfaz.estadisticas.ModelEstadisticas;
import Interfaz.estadisticas.estadisticasView;
import Interfaz.funcionarios.ControllerFuncionario;
import Interfaz.funcionarios.ModelFuncionario;
import Interfaz.funcionarios.funcionariosView;
import Interfaz.recursos.ControllerRecurso;
import Interfaz.recursos.ModelRecurso;
import Interfaz.recursos.recursosView;
import Interfaz.reservas.ControllerReserva;
import Interfaz.reservas.reservasView;

import logic.Funcionario;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        super("SISTEMA DE RESERVAS - " + usuario.getId() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.getRol());

        // Cast seguro a Funcionario si corresponde
        Funcionario funcionarioActual = (usuario instanceof Funcionario) ? (Funcionario) usuario : null;

        // 1. Pestañas exclusivas para ADMINISTRADOR
        if (esAdmin) {
            // Módulo Funcionarios
            funcionariosView vFunc = new funcionariosView();
            ModelFuncionario mFunc = new ModelFuncionario();
            new ControllerFuncionario(mFunc);
            tabbedPane.addTab("Funcionarios", vFunc.getMainPanel());

            // Módulo Categorías
            categoriasView vCat = new categoriasView();
            ModelCategoria mCat = new ModelCategoria();
            new ControllerCategoria(mCat);
            tabbedPane.addTab("Categorías", vCat.getMainPanel());

            // Módulo Recursos
            recursosView vRec = new recursosView();
            ModelRecurso mRec = new ModelRecurso();
            new ControllerRecurso(mRec);
            tabbedPane.addTab("Recursos", vRec.getMainPanel());
        }

        // 2. Pestaña exclusiva para FUNCIONARIO
        if (funcionarioActual != null) {
            reservasView vRes = new reservasView();
            new ControllerReserva(vRes, funcionarioActual);
            tabbedPane.addTab("Mis Reservas", vRes.getMainPanel());
        }

        // 3. Pestañas comunes para AMBOS ROLES

        // Módulo Calendarización
        calendarizacionView vCal = new calendarizacionView();
        ModelCalendarizacion mCal = new ModelCalendarizacion();
        new ControllerCalendarizacion(vCal, mCal, funcionarioActual);
        tabbedPane.addTab("Calendarización", vCal.getMainPanel());

        // Módulo Actividades
        actividadesView vAct = new actividadesView();
        ModelActividades mAct = new ModelActividades();
        new ControllerActividades(mAct);
        tabbedPane.addTab("Actividades", vAct.getMainPanel());

        // Módulo Estadísticas
        estadisticasView vEst = new estadisticasView();
        ModelEstadisticas mEst = new ModelEstadisticas();
        new ControllerEstadisticas(mEst);
        tabbedPane.addTab("Estadísticas", vEst.getMainPanel());

        setContentPane(tabbedPane);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
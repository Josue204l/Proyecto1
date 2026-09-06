package Interfaz.main;

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
import Interfaz.cambiarclave.ControllerCambiarClave;
import Interfaz.cambiarclave.cambiarclaveView;

import logic.Funcionario;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final Usuario usuarioActual;

    public MainFrame(Usuario usuario) {
        super("SISTEMA DE RESERVAS - " + usuario.getId() + " (" + usuario.getRol() + ")");
        this.usuarioActual = usuario;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setJMenuBar(crearMenuBar());

        JTabbedPane tabbedPane = new JTabbedPane();
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.getRol());

        Funcionario funcionarioActual = (usuario instanceof Funcionario) ? (Funcionario) usuario : null;

        // 1. Pestañas de ADMINISTRADOR
        if (esAdmin) {
            funcionariosView vFunc = new funcionariosView();
            ModelFuncionario mFunc = new ModelFuncionario();
            new ControllerFuncionario(vFunc, mFunc);
            tabbedPane.addTab("Funcionarios", vFunc.getMainPanel());

            categoriasView vCat = new categoriasView();
            ModelCategoria mCat = new ModelCategoria();
            new ControllerCategoria(vCat, mCat);
            tabbedPane.addTab("Categorías", vCat.getMainPanel());

            recursosView vRec = new recursosView();
            ModelRecurso mRec = new ModelRecurso();
            new ControllerRecurso(vRec, mRec);
            tabbedPane.addTab("Recursos", vRec.getMainPanel());
        }

        // 2. Pestaña de FUNCIONARIO
        if (funcionarioActual != null && !esAdmin) {
            reservasView vRes = new reservasView();
            new ControllerReserva(vRes, funcionarioActual);
            tabbedPane.addTab("Mis Reservas", vRes.getMainPanel());
        }

        // 3. Pestañas comunes
        calendarizacionView vCal = new calendarizacionView();
        ModelCalendarizacion mCal = new ModelCalendarizacion();
        new ControllerCalendarizacion(vCal, mCal, funcionarioActual);
        tabbedPane.addTab("Calendarización", vCal.getMainPanel());

        // Módulo Actividades
        actividadesView vAct = new actividadesView();
        ModelActividades mAct = new ModelActividades();
        new ControllerActividades(mAct); // CORREGIDO: solo pide el modelo
        tabbedPane.addTab("Actividades", vAct.getMainPanel());

        // Módulo Estadísticas
        estadisticasView vEst = new estadisticasView();
        ModelEstadisticas mEst = new ModelEstadisticas();
        new ControllerEstadisticas(mEst); // CORREGIDO: solo pide el modelo
        tabbedPane.addTab("Estadísticas", vEst.getMainPanel());

        setContentPane(tabbedPane);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuOpciones = new JMenu("Opciones");

        JMenuItem itemCambiarClave = new JMenuItem("Cambiar Clave");
        itemCambiarClave.addActionListener(e -> abrirDialogoCambiarClave());

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.addActionListener(e -> {
            this.dispose();
            try {
                Class<?> loginViewClass = Class.forName("Interfaz.login.LoginView");
                JFrame loginFrame = (JFrame) loginViewClass.getDeclaredConstructor().newInstance();
                loginFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Por favor, reinicie la aplicación para iniciar sesión nuevamente.");
            }
        });

        menuOpciones.add(itemCambiarClave);
        menuOpciones.addSeparator();
        menuOpciones.add(itemCerrarSesion);
        menuBar.add(menuOpciones);

        return menuBar;
    }

    private void abrirDialogoCambiarClave() {
        // CORREGIDO: cambiarclaveView hereda de JDialog y recibe 'this' (Frame parent)
        cambiarclaveView dialog = new cambiarclaveView(this);
        new ControllerCambiarClave(dialog, usuarioActual);
        dialog.setVisible(true);
    }
}
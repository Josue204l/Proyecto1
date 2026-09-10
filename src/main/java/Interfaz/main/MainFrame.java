package Interfaz.main;

import Interfaz.actividades.ControllerActividades;
import Interfaz.actividades.ModelActividades;
import Interfaz.actividades.actividadesView;
import Interfaz.calendarizacion.ControllerCalendarizacion;
import Interfaz.calendarizacion.ModelCalendarizacion;
import Interfaz.calendarizacion.calendarizacionView;
import Interfaz.cambiarclave.ControllerCambiarClave;
import Interfaz.cambiarclave.cambiarclaveView;
import Interfaz.categorias.ControllerCategoria;
import Interfaz.categorias.ModelCategoria;
import Interfaz.categorias.categoriasView;
import Interfaz.estadisticas.ControllerEstadisticas;
import Interfaz.estadisticas.ModelEstadisticas;
import Interfaz.estadisticas.estadisticasView;
import Interfaz.funcionarios.ControllerFuncionario;
import Interfaz.funcionarios.ModelFuncionario;
import Interfaz.funcionarios.funcionariosView;
import Interfaz.login.ControllerLogin;
import Interfaz.login.LoginView;
import Interfaz.login.ModelLogin;
import Interfaz.recursos.ControllerRecurso;
import Interfaz.recursos.ModelRecurso;
import Interfaz.recursos.recursosView;
import Interfaz.reservas.ControllerReserva;
import Interfaz.reservas.reservasView;

import logic.Funcionario;
import logic.Sesion;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        super("SISTEMA DE RESERVAS - " + usuario.getId() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.getRol());
        Funcionario funcionarioActual = (usuario instanceof Funcionario) ? (Funcionario) usuario : null;

        // ========== CARGA DE ICONOS PARA LAS PESTAÑAS ==========

        // >>> MODIFICADO: ahora se utiliza el método seguro cargarIcono()
        ImageIcon icoFuncionarios = cargarIcono("/iconos/case.png");
        ImageIcon icoCategorias = cargarIcono("/iconos/categories.png");
        ImageIcon icoRecursos = cargarIcono("/iconos/resource.png");
        ImageIcon icoReservas = cargarIcono("/iconos/reserves.png");
        ImageIcon icoCalendarizacion = cargarIcono("/iconos/calendar.png");
        ImageIcon icoActividades = cargarIcono("/iconos/activities.png");
        ImageIcon icoEstadisticas = cargarIcono("/iconos/stads.png");

        // ========================================================

        if (esAdmin) {
            funcionariosView vFunc = new funcionariosView();
            ModelFuncionario mFunc = new ModelFuncionario();
            new ControllerFuncionario(vFunc, mFunc);

            // >>> MODIFICADO: se agregó icoFuncionarios
            tabbedPane.addTab("Funcionarios", icoFuncionarios, vFunc.getMainPanel());

            categoriasView vCat = new categoriasView();
            ModelCategoria mCat = new ModelCategoria();
            new ControllerCategoria(vCat, mCat);

            // >>> MODIFICADO: se agregó icoCategorias
            tabbedPane.addTab("Categorías", icoCategorias, vCat.getMainPanel());

            recursosView vRec = new recursosView();
            ModelRecurso mRec = new ModelRecurso();
            new ControllerRecurso(vRec, mRec);

            // >>> MODIFICADO: se agregó icoRecursos
            tabbedPane.addTab("Recursos", icoRecursos, vRec.getMainPanel());
        }

        if (!esAdmin && funcionarioActual != null) {
            reservasView vRes = new reservasView();
            new ControllerReserva(vRes, funcionarioActual);

            // >>> MODIFICADO: se agregó icoReservas
            tabbedPane.addTab("Mis Reservas", icoReservas, vRes.getMainPanel());
        }

        calendarizacionView vCal = new calendarizacionView();
        ModelCalendarizacion mCal = new ModelCalendarizacion();
        new ControllerCalendarizacion(vCal, mCal);

        // >>> MODIFICADO: se agregó icoCalendarizacion
        tabbedPane.addTab("Calendarización", icoCalendarizacion, vCal.getMainPanel());

        actividadesView vAct = new actividadesView();
        ModelActividades mAct = new ModelActividades();
        new ControllerActividades(vAct, mAct);

        // >>> MODIFICADO: se agregó icoActividades
        tabbedPane.addTab("Actividades", icoActividades, vAct.getMainPanel());

        estadisticasView vEst = new estadisticasView();
        ModelEstadisticas mEst = new ModelEstadisticas();
        new ControllerEstadisticas(vEst, mEst);

        // >>> MODIFICADO: se agregó icoEstadisticas
        tabbedPane.addTab("Estadísticas", icoEstadisticas, vEst.getMainPanel());

        setJMenuBar(crearMenu(usuario));
        setContentPane(tabbedPane);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // >>> AÑADIDO: método para cargar los iconos sin provocar NullPointerException
    private ImageIcon cargarIcono(String ruta) {
        java.net.URL recurso = getClass().getResource(ruta);

        if (recurso == null) {
            System.out.println("No se encontró el icono: " + ruta);
            return null;
        }

        return new ImageIcon(recurso);
    }

    private JMenuBar crearMenu(Usuario usuario) {
        JMenuBar bar = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");

        JMenuItem cambiar = new JMenuItem("Cambiar clave");
        cambiar.addActionListener(e -> {
            cambiarclaveView dialogo = new cambiarclaveView(this);
            new ControllerCambiarClave(dialogo, usuario);
            dialogo.setVisible(true);
        });

        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.addActionListener(e -> cerrarSesion());

        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));

        archivo.add(cambiar);
        archivo.add(cerrar);
        archivo.addSeparator();
        archivo.add(salir);
        bar.add(archivo);
        return bar;
    }

    private void cerrarSesion() {
        Sesion.logout();
        dispose();
        LoginView loginView = new LoginView(null);
        new ControllerLogin(new ModelLogin(), loginView);
        loginView.setVisible(true);
        if (Sesion.isLoggedIn()) {
            new MainFrame(Sesion.getUsuario());
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import software.modelos.CamposActualizar;
import software.modelos.Herramienta;
import software.modelos.Patrones;
import software.modelos.Usuario;

/**
 *
 * @author Leudis Wan Der Biest
 */
public class confAplicacion {

    public static final String SISTEMA_SEPARADOR_RUTA = System.getProperty("file.separator");
    public static final String SISTEMA_SALTO_LINEA = System.getProperty("line.separator");

    public JFrame APLICACION_FRAME_ACTIVO;
    private CardLayout LAYOUT_CONTENEDOR_PANELES;

    private Usuario datosUsuario;
    private Herramienta HERRAMIENTA_ACTIVA;
    private ArrayList<Usuario> LISTA_OPERADORES;
    private ArrayList<Herramienta> HERRAMIENTAS;
    private ArrayList<Patrones> PATRONES;
    private boolean REALIZANDO_APRIETE;

    private SerialPort PUERTO_COM;
    MonitorPuertoCOM monitorCOM;
    HiloReloj monitorReloj;

    private HashMap<KeyStroke, Action> teclasPresionadas = new HashMap<>();

    public void cargarFrameActivo(JFrame FRAME_CONTENEDOR) {
        try {
            APLICACION_FRAME_ACTIVO = FRAME_CONTENEDOR;
        } catch (Exception ex) {
            guardarLogger(this.getClass().toString() + " > confAplicacion", ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR EN CONFIGURACIÓN DE APLICACIÓN", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static Image cargarIconoAplicacion() {
        Image iconoAplicacion = null;
        try {
            Properties propSistema = new Properties();
            InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
            propSistema.load(inputSteam);
            iconoAplicacion = Toolkit.getDefaultToolkit().getImage(propSistema.getProperty("aplicacion.icono", "").replaceAll("\\[separador]", "\\" + SISTEMA_SEPARADOR_RUTA));
        } catch (IOException e) {
            iconoAplicacion = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icon_frame.png"));
        }
        return iconoAplicacion;
    }

    public void cargarTituloAplicacionContenedor() {
        try {
            if (APLICACION_FRAME_ACTIVO != null) {
                Properties propSistema = new Properties();
                InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
                propSistema.load(inputSteam);
                APLICACION_FRAME_ACTIVO.setTitle(propSistema.getProperty("aplicacion.titulo", ""));
            }
        } catch (IOException e) {

        }
    }

    public void cargarTituloAplicacionLogin() {
        try {
            if (APLICACION_FRAME_ACTIVO != null) {
                Properties propSistema = new Properties();
                InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
                propSistema.load(inputSteam);
                APLICACION_FRAME_ACTIVO.setTitle(propSistema.getProperty("aplicacion.login.titulo", ""));
            }
        } catch (IOException e) {

        }
    }

    public static Properties cargarPropiedadesAplicacion() {
        Properties propSistema = new Properties();
        try {
            InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
            propSistema.load(inputSteam);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR AL CARGAR PROPIEDADES", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return propSistema;
    }

    public static void guardarLogger(String claseReporte, String mensajeReporte) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fechaActual = LocalDateTime.now();
        String fechaHora = formatoFecha.format(fechaActual);

        try {
            FileWriter salida = new FileWriter("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "logs" + SISTEMA_SEPARADOR_RUTA + fechaHora, true);

            formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
            fechaHora = formatoFecha.format(fechaActual);
            salida.write(SISTEMA_SALTO_LINEA + fechaHora + " | ORIGEN: " + claseReporte + ""
                    + SISTEMA_SALTO_LINEA + "LOG: " + mensajeReporte + SISTEMA_SALTO_LINEA);
            salida.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR GENERANDO LOGGER", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void guardarLogger(String claseReporte, String mensajeReporte, String mensajeExcepcion) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fechaActual = LocalDateTime.now();
        String fechaHora = formatoFecha.format(fechaActual);

        try {
            FileWriter salida = new FileWriter("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "logs" + SISTEMA_SEPARADOR_RUTA + fechaHora, true);

            formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
            fechaHora = formatoFecha.format(fechaActual);
            salida.write(SISTEMA_SALTO_LINEA + fechaHora + " | ORIGEN: " + claseReporte + ""
                    + SISTEMA_SALTO_LINEA + "MENSAJE: [" + mensajeReporte + "] "
                    + SISTEMA_SALTO_LINEA + "EXCEPCION:  " + mensajeExcepcion
                    + SISTEMA_SALTO_LINEA);
            salida.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + SISTEMA_SALTO_LINEA + mensajeExcepcion, "ERROR GENERANDO LOGGER", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static boolean crearBaseDatos() {
        Properties PROP_SISTEMA = new Properties();
        try {
            InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
            PROP_SISTEMA.load(inputSteam);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR AL CARGAR PROPIEDADES", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return false;
        }
        try {
            Connection connectionPostgres = null;
            PreparedStatement statetmentPostgres = null;
            Class.forName("org.postgresql.Driver");
            connectionPostgres = DriverManager.getConnection("jdbc:postgresql://" + PROP_SISTEMA.getProperty("bd.host") + ":" + PROP_SISTEMA.getProperty("bd.puerto") + "/", PROP_SISTEMA.getProperty("bd.usuario"), PROP_SISTEMA.getProperty("bd.clave"));
            statetmentPostgres = connectionPostgres.prepareStatement(PROP_SISTEMA.getProperty("bd.crearbd"));
            statetmentPostgres.executeUpdate();
            statetmentPostgres.close();
            connectionPostgres.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR CREANDO BD", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return false;
        }
    }

    public static boolean restaurarBaseDatos() {
        Properties PROP_SISTEMA = new Properties();
        try {
            InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
            PROP_SISTEMA.load(inputSteam);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR AL CARGAR PROPIEDADES", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return false;
        }
        try {
            Connection connectionPostgres = null;
            PreparedStatement statetmentPostgres = null;
            Class.forName("org.postgresql.Driver");
            connectionPostgres = DriverManager.getConnection("jdbc:postgresql://" + PROP_SISTEMA.getProperty("bd.host") + ":" + PROP_SISTEMA.getProperty("bd.puerto") + "/" + PROP_SISTEMA.getProperty("bd.basedatos"), PROP_SISTEMA.getProperty("bd.usuario"), PROP_SISTEMA.getProperty("bd.clave"));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(PROP_SISTEMA.getProperty("bd.patharchivobackup").replaceAll("\\[separador]", "\\" + SISTEMA_SEPARADOR_RUTA))));
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            statetmentPostgres = connectionPostgres.prepareStatement(buf.toString("UTF-8"));
            statetmentPostgres.execute();

            statetmentPostgres = connectionPostgres.prepareStatement(PROP_SISTEMA.getProperty("sql.crearusradmin"));
            statetmentPostgres.execute();

            statetmentPostgres.close();
            connectionPostgres.close();
            return true;
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR RESTAURANDO BD", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return false;
        }
    }

    public void validarOperadorAdministrador(JMenu menuAdmin) {
        try {
            if (this.getDatosUsuario() == null) {
                System.exit(0);
            }
            if (!this.getDatosUsuario().isAdmin()) {
                menuAdmin.setVisible(false);
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > limpiarCamposAdminOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void validarSalidaSistema() {
        try {
            int dialogResult = JOptionPane.showConfirmDialog(null, "¿Esta eguro que desea salir de la aplicación?", "Cerrar el sistema", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > limpiarCamposAdminOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void activarAtajosTeclado() {
        KeyStroke key1 = KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);
        teclasPresionadas.put(key1, new AbstractAction("action1") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CONTROL + I");
            }
        });
        KeyStroke key2 = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
        teclasPresionadas.put(key2, new AbstractAction("action1") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CONTROL + F");
            }
        });

        KeyStroke key3 = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        teclasPresionadas.put(key3, new AbstractAction("action1") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CONTROL + S");
                siguienteAprieteHerrmienta();
            }
        });

        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                if (teclasPresionadas.containsKey(keyStroke)) {
                    final Action a = teclasPresionadas.get(keyStroke);
                    final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            a.actionPerformed(ae);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    public void siguienteAprieteHerrmienta() {
        if (this.isRealizandoApriete() == false) {
            return;
        }
        int siguienteApriete = this.monitorCOM.setFilaApriete(this.monitorCOM.getFilaApriete() + 1);
        switch (siguienteApriete) {
            case 0:
                this.desactivarPuertoCOM();
                this.monitorCOM.limpiarCamposAprietes();
                JOptionPane.showMessageDialog(null, "Los aprietes a la herramienta han sido finalizados y guardados", "APRIETES FINALIZADOS", JOptionPane.INFORMATION_MESSAGE);
                break;
            case -1:
                JOptionPane.showMessageDialog(null, "No se puede avanzar al siguiente apriete, la medidacion debe estar dentro los rangos permitidos", "ERROR EN APRIETES", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                this.monitorCOM.setPAUSA_APRIETE(true);
                break;
        }
    }

    public boolean activarPuertoCOM(CamposActualizar camposActualizar) {
        boolean puertoAbierto = false;
        try {
            if (this.isRealizandoApriete() == true) {
                this.monitorCOM.setPAUSA_APRIETE(false);
                return true;
            }

            Properties propSistema = new Properties();
            InputStream inputSteam = new FileInputStream(new File("." + SISTEMA_SEPARADOR_RUTA + "recursos" + SISTEMA_SEPARADOR_RUTA + "confAplicacion.properties"));
            propSistema.load(inputSteam);

            if (String.valueOf(camposActualizar.getCliente().getText()).equals("")) {
                JOptionPane.showMessageDialog(null, propSistema.getProperty("mensaje.validacionnoclientes", ""), "ERROR AL INCIAR CONEXIÓN", JOptionPane.ERROR_MESSAGE);
                return puertoAbierto;
            }
            if (String.valueOf(camposActualizar.getODT().getText()).equals("")) {
                JOptionPane.showMessageDialog(null, propSistema.getProperty("mensaje.validacionnoodt", ""), "ERROR AL INCIAR CONEXIÓN", JOptionPane.ERROR_MESSAGE);
                return puertoAbierto;
            }
            if (camposActualizar.getTblAprietes().getRowCount() == 0 || this.HERRAMIENTA_ACTIVA == null) {
                JOptionPane.showMessageDialog(null, propSistema.getProperty("mensaje.validacionnoaprietespuerto", ""), "ERROR AL INCIAR CONEXIÓN", JOptionPane.ERROR_MESSAGE);
                return puertoAbierto;
            }

            if (this.PUERTO_COM == null) {
                JOptionPane.showMessageDialog(null, propSistema.getProperty("mensaje.validacionnopuerto", ""), "ERROR AL INCIAR CONEXIÓN", JOptionPane.ERROR_MESSAGE);
                return puertoAbierto;
            }
            this.PUERTO_COM.setComPortParameters(9600, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            this.PUERTO_COM.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

            puertoAbierto = this.PUERTO_COM.openPort();
            if (!puertoAbierto) {
                JOptionPane.showMessageDialog(null, propSistema.getProperty("mensaje.validacionerrorpuerto", ""), "ERROR AL INCIAR CONEXIÓN", JOptionPane.ERROR_MESSAGE);
                return puertoAbierto;
            }
            camposActualizar.setOperador(datosUsuario);
            camposActualizar.setHerramienta(this.getHerramientaActiva());

            this.monitorCOM = new MonitorPuertoCOM(camposActualizar);
            this.PUERTO_COM.addDataListener(this.monitorCOM);
            this.monitorReloj = new HiloReloj(camposActualizar.getFechaHora());
            this.setRealizandoApriete(true);

        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > activarPuertoCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return puertoAbierto;
    }

    public void desactivarPuertoCOM() {
        try {
            this.PUERTO_COM.removeDataListener();
            this.PUERTO_COM.closePort();
            this.monitorReloj.detenerReloj();
            this.setRealizandoApriete(false);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > desactivarPuertoCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void detenerAprietes() {
        try {
            int confCerrar = JOptionPane.showConfirmDialog(null, "Si detiene la toma de datos se perdera toda la información tomada hast ahora. ¿Esta seguro que desea finalizar los aprietes?", "Finalizar apriete", JOptionPane.YES_NO_OPTION);
            if (confCerrar == 0) {
                this.PUERTO_COM.removeDataListener();
                this.PUERTO_COM.closePort();
                this.monitorReloj.detenerReloj();
                this.setRealizandoApriete(false);
                this.monitorCOM.limpiarCamposAprietes();
                this.monitorCOM.eliminarAprieteActual();
            }

        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > desactivarPuertoCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    /**
     * @return the LISTA_OPERADORES
     */
    public ArrayList<Usuario> getListaOperadores() {
        return LISTA_OPERADORES;
    }

    /**
     * @param LISTA_OPERADORES the LISTA_OPERADORES to set
     */
    public void setListaOperadores(ArrayList<Usuario> LISTA_OPERADORES) {
        this.LISTA_OPERADORES = LISTA_OPERADORES;
    }

    /**
     * @return the LAYOUT_CONTENEDOR_PANELES
     */
    public CardLayout getContenedorPaneles() {
        return LAYOUT_CONTENEDOR_PANELES;
    }

    /**
     * @param LAYOUT_CONTENEDOR_PANELES the LAYOUT_CONTENEDOR_PANELES to set
     */
    public void setContenedorPaneles(CardLayout LAYOUT_CONTENEDOR_PANELES) {
        this.LAYOUT_CONTENEDOR_PANELES = LAYOUT_CONTENEDOR_PANELES;
    }

    /**
     * @return the HERRAMIENTAS
     */
    public ArrayList<Herramienta> getHerramientas() {
        return HERRAMIENTAS;
    }

    /**
     * @param HERRAMIENTAS the HERRAMIENTAS to set
     */
    public void setHerramientas(ArrayList<Herramienta> HERRAMIENTAS) {
        this.HERRAMIENTAS = HERRAMIENTAS;
    }

    /**
     * @return the REALIZANDO_APRIETE
     */
    public boolean isRealizandoApriete() {
        return REALIZANDO_APRIETE;
    }

    /**
     * @param REALIZANDO_APRIETE the REALIZANDO_APRIETE to set
     */
    public void setRealizandoApriete(boolean REALIZANDO_APRIETE) {
        this.REALIZANDO_APRIETE = REALIZANDO_APRIETE;
    }

    /**
     * @return the HERRAMIENTA_ACTIVA
     */
    public Herramienta getHerramientaActiva() {
        return HERRAMIENTA_ACTIVA;
    }

    /**
     * @param HERRAMIENTA_ACTIVA the HERRAMIENTA_ACTIVA to set
     */
    public void setHerramientaActiva(Herramienta HERRAMIENTA_ACTIVA) {
        this.HERRAMIENTA_ACTIVA = HERRAMIENTA_ACTIVA;
    }

    /**
     * @return the PUERTO_COM
     */
    public SerialPort getPuertoCOM() {
        return PUERTO_COM;
    }

    /**
     * @param PUERTO_COM the PUERTO_COM to set
     */
    public void setPuertoCOM(SerialPort PUERTO_COM) {
        this.PUERTO_COM = PUERTO_COM;
    }

    public Usuario getDatosUsuario() {
        return datosUsuario;
    }

    public void setDatosUsuario(Usuario datosUsuario) {
        this.datosUsuario = datosUsuario;
    }

    /**
     * @return the PATRONES
     */
    public ArrayList<Patrones> getPatrones() {
        return PATRONES;
    }

    /**
     * @param PATRONES the PATRONES to set
     */
    public void setPatrones(ArrayList<Patrones> PATRONES) {
        this.PATRONES = PATRONES;
    }
}

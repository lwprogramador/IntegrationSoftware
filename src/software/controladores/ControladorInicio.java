/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import software.Software;
import software.modelos.Usuario;
import software.vistas.Contenedor;
import software.vistas.Login;

/**
 *
 * @author MSI
 */
public class ControladorInicio {

    private Properties PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();

    public void cargarAplicacion() {
        JWindow splashVentana = new JWindow();
        try {
            /*CARGANDO IMAGEN A LA VENTANA SPLASH*/
            File imgIcon = new File(this.PROP_SISTEMA.getProperty("aplicacion.imageninicio", "").replaceAll("\\[separador]", "\\" + confAplicacion.SISTEMA_SEPARADOR_RUTA));
            if (imgIcon.exists()) {
                splashVentana.getContentPane().add(new JLabel("", new ImageIcon(imgIcon.getAbsolutePath()), SwingConstants.CENTER));
            } else {
                splashVentana.getContentPane().add(new JLabel("", new ImageIcon(ClassLoader.getSystemResource("software/iconos/inicio_aplicacion.jpg")), SwingConstants.CENTER));
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        splashVentana.setSize(1000, 450);
        splashVentana.setLocationRelativeTo(null);
        splashVentana.setVisible(true);

        try {
            /*REALIZANDO VERIFICACION DE LA BD*/
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", "COMPROBANDO BASE DE DATOS");
            Connection conexionSQL = null;
            conexionSQL = DriverManager.getConnection("jdbc:postgresql://" + this.PROP_SISTEMA.getProperty("bd.host") + ":" + this.PROP_SISTEMA.getProperty("bd.puerto") + "/" + this.PROP_SISTEMA.getProperty("bd.basedatos"), this.PROP_SISTEMA.getProperty("bd.usuario"), this.PROP_SISTEMA.getProperty("bd.clave"));
            PreparedStatement stPrueba = conexionSQL.prepareStatement(this.PROP_SISTEMA.getProperty("sql.comprobartablas"));
            ResultSet rsPrueba = stPrueba.executeQuery();
            if (rsPrueba != null && rsPrueba.next()) {
                stPrueba.close();
                rsPrueba.close();
            }
            conexionSQL.close();
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", "LA BASE DATOS " + this.PROP_SISTEMA.getProperty("bd.basedatos") + " HA SIDO COMPROBADA CORRECTAMENTE");
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", "NO SE HA COMPROBADO LA BASE DATOS, INICIANDO CREACION DE NUEVA BASE DE DATOS");
            boolean crearBD = confAplicacion.crearBaseDatos();
            if (crearBD == true) /*RESTURANDO BASE DE DATOS*/ {
                confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", "SE HA CREADO LA BASE DATOS, INICIANDO RESTAURACION DE NUEVA BASE DE DATOS");
                boolean restaurandoBD = confAplicacion.restaurarBaseDatos();
                if (restaurandoBD == true) {
                    confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", "SE HA RESTAURADO LA BASE DATOS CORRECTAMENTE");
                }
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }

        try {
            splashVentana.dispose();
            Login nvaVista = new Login();
            nvaVista.setVisible(true);
            nvaVista.setLocationRelativeTo(null);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void cerrarAplicacion() {
        try {
            int confCerrar = JOptionPane.showConfirmDialog(null, this.PROP_SISTEMA.getProperty("mensaje.login.body.cerrar", ""), this.PROP_SISTEMA.getProperty("mensaje.login.titulo.cerrar", ""), JOptionPane.YES_NO_OPTION);
            if (confCerrar == 0) {
                System.exit(0);
            }
        } catch (HeadlessException e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cerrarAplicacion", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            System.exit(0);
        }
    }

    public Usuario loginAplicacion(String usuario, String clave) {
        Usuario usrLogin = null;
        try {
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.loginusuario"));
            queryEjecutar.setString(1, usuario);
            queryEjecutar.setString(2, clave);
            usrLogin = con.realizarLoginUsuario(queryEjecutar);
            if (usrLogin == null || usrLogin.isActivo() == false) {
                JOptionPane.showMessageDialog(null, "Los datos ingresados no coinciden, asegurese que su usuario esta activo y los datos ingresados esten correctos", "LOGIN FALLIDO", JOptionPane.WARNING_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarAplicacion", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return usrLogin;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package software;

import java.util.Arrays;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import software.controladores.ControladorInicio;
import software.controladores.confAplicacion;
import software.vistas.Contenedor;

/**
 *
 * @author MSI
 */
public class Software {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new ControladorInicio().cargarAplicacion();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            confAplicacion.guardarLogger(Software.class.toString() + " > main", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

}

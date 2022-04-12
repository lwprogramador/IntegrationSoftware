/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author MSI
 */
public class ConexionCOM implements Runnable {

    private Properties PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();
    private SerialPort[] PUERTOS_COM;
    public SerialPort PUERTO_USO_COM;

    private JMenu MENU_PUERTOS;
    private Thread HILO_SUBPROCESO;
    private boolean CONTINUAR_HILO = true;

    public ConexionCOM(JMenu menuItem) {
        this.MENU_PUERTOS = menuItem;
    }

    @Override
    public void run() {
        while (this.CONTINUAR_HILO == true) {
            try {
                listarPuertosCOM();
                Thread.sleep(10000);
            } catch (InterruptedException e) {

            }
        }

    }

    public void inicarBuscadorDePuertos() {
        this.CONTINUAR_HILO = true;
        this.HILO_SUBPROCESO = new Thread(this);
        this.HILO_SUBPROCESO.start();
    }

    public void pausarBuscadorDePuertos() {
        this.CONTINUAR_HILO = false;
        this.HILO_SUBPROCESO.interrupt();
    }

    public void listarPuertosCOM() {
        try {
            this.MENU_PUERTOS.removeAll();

            this.MENU_PUERTOS.add(new JRadioButtonMenuItem(this.PROP_SISTEMA.getProperty("default.menuitemcom", "")));
            this.MENU_PUERTOS.addSeparator();

            this.PUERTOS_COM = SerialPort.getCommPorts();
            if (this.PUERTOS_COM != null && this.PUERTOS_COM.length > 0) {
                for (SerialPort puerto : this.PUERTOS_COM) {
                    JRadioButtonMenuItem item;
                    if (this.PUERTO_USO_COM != null && puerto.getSystemPortName().equals(this.PUERTO_USO_COM.getSystemPortName())) {
                        item = new JRadioButtonMenuItem(puerto.getSystemPortName(), true);
                    } else {
                        item = new JRadioButtonMenuItem(puerto.getSystemPortName());
                    }

                    item.addActionListener((e) -> {
                        System.out.println("COMAND: " + e.getActionCommand());
                        for (SerialPort itemPuerto : this.PUERTOS_COM) {
                            if (itemPuerto.getSystemPortName().equals(e.getActionCommand())) {
                                this.PUERTO_USO_COM = itemPuerto;
                                break;
                            }
                        }
                    });
                    this.MENU_PUERTOS.add(item);

                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > listarPuertosCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

}

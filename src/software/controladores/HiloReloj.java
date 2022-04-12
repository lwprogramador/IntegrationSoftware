/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JTextField;

/**
 *
 * @author leudiswanderbiest
 */
public class HiloReloj implements Runnable {

    JTextField campoFecha;
    String hora, minutos, segundos;
    Calendar calendario;
    Thread hilo;
    boolean continuarHilo = true;

    public HiloReloj(JTextField campoFecha) {
        this.campoFecha = campoFecha;

        hilo = new Thread(this);
        hilo.start();
    }

    public void detenerReloj() {
        continuarHilo = false;
        this.campoFecha.setText("");
    }

    private void calcularFecha() {
        Calendar lCalendar = new GregorianCalendar();
        Date fechaHora = new Date();
        lCalendar.setTime(fechaHora);

        this.campoFecha.setText(lCalendar.get(Calendar.DAY_OF_MONTH) + "/" + lCalendar.get(Calendar.MONTH) + "/" + lCalendar.get(Calendar.YEAR) + " " + lCalendar.get(Calendar.HOUR_OF_DAY) + ":" + lCalendar.get(Calendar.MINUTE) + ":" + lCalendar.get(Calendar.SECOND));
    }

    @Override
    public void run() {
        while (continuarHilo == true) {
            calcularFecha();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }

    }
}

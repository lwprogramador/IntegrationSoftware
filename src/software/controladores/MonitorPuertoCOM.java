/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import software.modelos.CamposActualizar;

/**
 *
 * @author leudiswanderbiest
 */
public class MonitorPuertoCOM implements SerialPortDataListener {

    private static Properties PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();
    private ConexionBD con = new ConexionBD();
    private final StringBuilder inputBuffer = new StringBuilder();
    CamposActualizar camposActualizar;
    private int FILA_APRIETE;
    private Color COLOR_ROJO = new Color(204, 51, 0);
    private Color COLOR_VERDE = new Color(0, 204, 102);
    private boolean MEDIDA_ACEPTABLE = false;
    private int ID_APRIETE_HEAD;

    private boolean PAUSA_APRIETE;

    private ChartPanel graficaPanel;
    private final TimeSeries linea1;

    private int TEST_VAL = 1;

    public MonitorPuertoCOM(CamposActualizar camposActualizar) {
        this.camposActualizar = camposActualizar;
        this.linea1 = new TimeSeries("Monitor de apriete", Millisecond.class);

        try {
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(this.linea1);

            final JFreeChart grafica = crearGrafica(dataset);
            graficaPanel = new ChartPanel(grafica);

            this.camposActualizar.getPanelGrafica().setLayout(new java.awt.BorderLayout());
            this.camposActualizar.getPanelGrafica().add(graficaPanel);
            this.camposActualizar.getPanelGrafica().validate();

            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.guardarloghea"));
            queryEjecutar.setString(1, this.camposActualizar.getOperador().getCodigo());
            queryEjecutar.setString(2, this.camposActualizar.getHerramienta().getCodigo());
            queryEjecutar.setString(3, String.valueOf(this.camposActualizar.getODT().getText()));
            queryEjecutar.setString(4, String.valueOf(this.camposActualizar.getSerie().getText()));
            queryEjecutar.setString(5, String.valueOf(this.camposActualizar.getCliente().getText()));
            queryEjecutar.setString(6, String.valueOf(this.camposActualizar.getDireccion().getText()));
            queryEjecutar.setString(7, String.valueOf(this.camposActualizar.getPatrones().getSelectedItem()).split("\\|")[0].trim());
            this.ID_APRIETE_HEAD = con.guardarAprieteHead(queryEjecutar);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > MonitorPuertoCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent evtPuertoCOM) {
        try {
            if (evtPuertoCOM.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE && evtPuertoCOM.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
                return;
            }

            if (this.isPAUSA_APRIETE() == true) {
                this.camposActualizar.getTblAprietes().setRowSelectionInterval(this.getFilaApriete(), this.getFilaApriete());
                this.camposActualizar.getLinea1().setBackground(COLOR_ROJO);
                this.camposActualizar.getTblAprietes().setSelectionBackground(COLOR_ROJO);

                this.TEST_VAL = 0;
                return;
            }
            inputBuffer.append(new String(evtPuertoCOM.getReceivedData(), 0, evtPuertoCOM.getReceivedData().length).replaceAll("  ", ""));
            if (!inputBuffer.toString().contains("\n ")) {
                return;
            }

            String lineaDatos = inputBuffer.toString().trim();
            inputBuffer.setLength(0);

            lineaDatos = lineaDatos.replaceAll("FIN", "");
            System.out.println("Salida Datos: \n" + lineaDatos);
            String[] lineas = lineaDatos.split("\n");
            double linea1 = 0.0;
            DefaultTableModel modeloTablaAprietes = (DefaultTableModel) this.camposActualizar.getTblAprietes().getModel();

            try {
                String[] lineasData = lineas[1].substring(3).split("; ");
                try {
                    linea1 = Double.parseDouble(lineasData[1].replaceAll("Fuerza: ", "").replaceAll(" PiesLibras", "").trim());
                    //TEST_VAL++;
                    //linea1 = TEST_VAL;
                } catch (NumberFormatException e) {
                    confAplicacion.guardarLogger(this.getClass().toString(), "linea1 = Double.parseDouble(lineasData[1]) " + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
                }
            } catch (Exception ex) {
                confAplicacion.guardarLogger(this.getClass().toString(), "serialEvent - " + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
            }

            double aprieteEMPpos = 0.0;
            double aprieteEMPneg = 0.0;

            try {
                aprieteEMPpos = Double.parseDouble(String.valueOf(modeloTablaAprietes.getValueAt(this.getFilaApriete(), 3)).trim());
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString(), "aprieteEMPpos = Double.parseDouble(String.valueOf(modeloTablaAprietes.getValueAt(this.getFilaApriete(), 3)).trim())" + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
            try {
                aprieteEMPneg = Double.parseDouble(String.valueOf(modeloTablaAprietes.getValueAt(this.getFilaApriete(), 4)).trim());
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString(), "aprieteEMPneg = Double.parseDouble(String.valueOf(modeloTablaAprietes.getValueAt(this.getFilaApriete(), 4)).trim())" + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }

            modeloTablaAprietes.setValueAt(String.format("%.5f", linea1).replaceAll(",", "."), this.getFilaApriete(), 5);
            System.out.println("MEDIDAS: " + linea1 + "; " + aprieteEMPneg + "; " + linea1 + "; " + aprieteEMPpos);
            if (linea1 < aprieteEMPneg || linea1 > aprieteEMPpos) {
                this.camposActualizar.getLinea1().setBackground(COLOR_ROJO);
                this.camposActualizar.getTblAprietes().setSelectionBackground(COLOR_ROJO);
                this.MEDIDA_ACEPTABLE = false;
            } else {
                this.camposActualizar.getLinea1().setBackground(COLOR_VERDE);
                this.camposActualizar.getTblAprietes().setSelectionBackground(COLOR_VERDE);
                this.MEDIDA_ACEPTABLE = true;
            }
            this.camposActualizar.getTblAprietes().setRowSelectionInterval(this.getFilaApriete(), this.getFilaApriete());
            this.camposActualizar.getLinea1().setText(String.format("%.3f", linea1).replaceAll(",", ".") + " PiesLibras");
            actualizarGrafica(linea1, 0.0, 0.0, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString(), "serialEvent - " + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void limpiarCamposAprietes() {
        this.FILA_APRIETE = 0;
        DefaultTableModel modeloTablaAprietes = (DefaultTableModel) this.camposActualizar.getTblAprietes().getModel();
        modeloTablaAprietes.setRowCount(0);

        this.camposActualizar.getCliente().setText("");
        this.camposActualizar.getODT().setText("");
        this.camposActualizar.getSerie().setText("");
        this.camposActualizar.getLinea1().setText(String.format("%.2f", 0.0).replaceAll(",", ".") + " PiesLibras");
        /*this.camposActualizar.getLinea2().setText(String.format("%.2f", 0.0).replaceAll(",", ".") + " PiesLibras");
        this.camposActualizar.getAuxiliar1().setText(String.format("%.3f", 0.0).replaceAll(",", ".") + " PSI");
        this.camposActualizar.getAuxiliar2().setText(String.format("%.3f", 0.0).replaceAll(",", ".") + " PSI");*/
        this.camposActualizar.getHerramientas().setSelectedIndex(0);
        this.camposActualizar.getDireccion().setText("");
        this.camposActualizar.getPatrones().setSelectedIndex(0);
        this.camposActualizar.getPanelGrafica().remove(this.graficaPanel);
        this.camposActualizar.getPanelGrafica().revalidate();
        this.camposActualizar.getPanelGrafica().repaint();
    }

    private JFreeChart crearGrafica(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart("Grafica de aprietes", "Tiempo", "Valores", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(6000.0);
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 6000.0);
        return result;
    }

    public void actualizarGrafica(double linea1, double linea2, double auxiliar1, double auxiliar2) {
        final Millisecond now = new Millisecond();
        this.linea1.add(new Millisecond(), linea1);
        try {
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.guardarlogdet"));
            queryEjecutar.setInt(1, this.ID_APRIETE_HEAD);
            queryEjecutar.setString(2, linea1 + "");
            queryEjecutar.setString(3, linea2 + "");
            queryEjecutar.setString(4, auxiliar1 + "");
            queryEjecutar.setString(5, auxiliar2 + "");
            queryEjecutar.setString(6, String.valueOf(this.camposActualizar.getTblAprietes().getValueAt(this.getFilaApriete(), 1)));
            queryEjecutar.setString(7, String.valueOf(this.camposActualizar.getTblAprietes().getValueAt(this.getFilaApriete(), 0)));

            queryEjecutar.setString(8, String.valueOf(this.camposActualizar.getTblAprietes().getValueAt(this.getFilaApriete(), 2)));
            queryEjecutar.setString(9, String.valueOf(this.camposActualizar.getTblAprietes().getValueAt(this.getFilaApriete(), 4)));
            queryEjecutar.setString(10, String.valueOf(this.camposActualizar.getTblAprietes().getValueAt(this.getFilaApriete(), 3)));

            con.guardarAprieteDet(queryEjecutar);
        } catch (SQLException e) {
            confAplicacion.guardarLogger(this.getClass().toString(), "actualizarGrafica - " + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void eliminarAprieteActual() {
        try {
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.eliminaraprietelog"));
            queryEjecutar.setInt(1, this.ID_APRIETE_HEAD);
            queryEjecutar.setInt(2, this.ID_APRIETE_HEAD);
            queryEjecutar.executeUpdate();
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > MonitorPuertoCOM", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public int setFilaApriete(int FILA_APRIETE) {
        if (this.MEDIDA_ACEPTABLE == false) {
            return -1;
        }
        DefaultTableModel modeloTablaAprietes = (DefaultTableModel) this.camposActualizar.getTblAprietes().getModel();
        if (FILA_APRIETE >= modeloTablaAprietes.getRowCount()) {
            this.FILA_APRIETE = 0;
            return 0;
        } else {
            this.FILA_APRIETE = FILA_APRIETE;
            return 1;
        }
    }

    public int getFilaApriete() {
        return this.FILA_APRIETE;
    }

    /**
     * @return the PAUSA_APRIETE
     */
    public boolean isPAUSA_APRIETE() {
        return PAUSA_APRIETE;
    }

    /**
     * @param PAUSA_APRIETE the PAUSA_APRIETE to set
     */
    public void setPAUSA_APRIETE(boolean PAUSA_APRIETE) {
        this.PAUSA_APRIETE = PAUSA_APRIETE;
        if (PAUSA_APRIETE == true) {
            this.actualizarGrafica(0.0, 0.0, 0.0, 0.0);
        }
    }
}

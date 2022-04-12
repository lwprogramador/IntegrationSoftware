/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author leudiswanderbiest
 */
public class ReportesPDF {

    Properties propSis = confAplicacion.cargarPropiedadesAplicacion();

    public ReportesPDF() {

    }

    public void ejecutarReporteHerramientaAjustada(String codigoAjuste) {
        try {

            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(this.propSis.getProperty("sql.traerseries"));
            queryEjecutar.setInt(1, Integer.parseInt(codigoAjuste));
            ResultSet datosSeries = queryEjecutar.executeQuery();

            XYSeries valAplicado = new XYSeries("Valor Aplicado");
            XYSeries aux1 = new XYSeries("Aux 1");
            XYSeries aux2 = new XYSeries("Aux 2");

            if (datosSeries != null) {
                int count = 0;
                while (datosSeries.next()) {
                    valAplicado.add(count, datosSeries.getInt("det_val_apriete"));
                    aux1.add(count, datosSeries.getDouble("det_auxiliar_1"));
                    aux2.add(count, datosSeries.getDouble("det_auxiliar_2"));
                    count++;
                }
            }

            XYSeriesCollection dsValAplicado = new XYSeriesCollection();
            XYSeriesCollection dsAux1 = new XYSeriesCollection();
            XYSeriesCollection dsAux2 = new XYSeriesCollection();
            dsValAplicado.addSeries(valAplicado);
            dsAux1.addSeries(aux1);
            dsAux2.addSeries(aux2);

            //construct the plot
            XYPlot plot = new XYPlot();
            plot.setDataset(0, dsValAplicado);
            plot.setDataset(1, dsAux1);
            plot.setDataset(2, dsAux2);

            //customize the plot with renderers and axis
            plot.setRenderer(0, new XYSplineRenderer());//use default fill paint for first series
            XYSplineRenderer splinerenderer = new XYSplineRenderer();
            splinerenderer.setSeriesFillPaint(0, Color.BLUE);
            plot.setRenderer(1, splinerenderer);
            plot.setRangeAxis(0, new NumberAxis("Valor Aplicado"));
            plot.setRangeAxis(1, new NumberAxis("Aux 1"));
            plot.setRangeAxis(2, new NumberAxis("Aux 2"));
            plot.setDomainAxis(new NumberAxis(""));

            //Map the data to the appropriate axis
            plot.mapDatasetToRangeAxis(0, 0);
            plot.mapDatasetToRangeAxis(1, 1);

            //generate the chart
            JFreeChart chart = new JFreeChart("Representación Gráfica", null, plot, true);
            chart.setBackgroundPaint(Color.WHITE);

            BufferedImage objBufferedImage = chart.createBufferedImage(900, 450);
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            try {
                ImageIO.write(objBufferedImage, "png", bas);
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] byteArray = bas.toByteArray();

            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage image = ImageIO.read(in);
            File outputfile = new File("." + confAplicacion.SISTEMA_SEPARADOR_RUTA + "recursos" + confAplicacion.SISTEMA_SEPARADOR_RUTA + "reportesimage.png");
            ImageIO.write(image, "png", outputfile);

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime fechaActual = LocalDateTime.now();
            String fechaHora = formatoFecha.format(fechaActual);

            String nombre = "";
            JFileChooser pathSeleccionada = new JFileChooser();
            pathSeleccionada.setSelectedFile(new File(propSis.getProperty("default.nombrereporte") + "-" + codigoAjuste + "-" + fechaHora));
            int OPTION = pathSeleccionada.showOpenDialog(null);
            File pahtGuardarArchivo = pathSeleccionada.getSelectedFile();

            System.out.println("Guardar" + pahtGuardarArchivo);
            if (pahtGuardarArchivo != null && OPTION == JFileChooser.APPROVE_OPTION) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile("." + confAplicacion.SISTEMA_SEPARADOR_RUTA + "recursos" + confAplicacion.SISTEMA_SEPARADOR_RUTA + "reportes" + confAplicacion.SISTEMA_SEPARADOR_RUTA + this.propSis.getProperty("reporte.herramienta.ajustada") + ".jasper");

                Map parameters = new HashMap();
                parameters.put("PAHT_ICON", "." + confAplicacion.SISTEMA_SEPARADOR_RUTA + "recursos" + confAplicacion.SISTEMA_SEPARADOR_RUTA + "imagenes" + confAplicacion.SISTEMA_SEPARADOR_RUTA + "inicio_aplicacion.png");
                parameters.put("AJUSTE", codigoAjuste);
                parameters.put("PAHT_GRAFICA", "." + confAplicacion.SISTEMA_SEPARADOR_RUTA + "recursos" + confAplicacion.SISTEMA_SEPARADOR_RUTA + "reportesimage.png");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conectarBD());
                JasperExportManager.exportReportToPdfFile(jasperPrint, pahtGuardarArchivo + ".pdf");

                long start = System.currentTimeMillis();
                PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                printRequestAttributeSet.add(MediaSizeName.ISO_A4);

                PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                if (this.propSis.getProperty("reporte.impresoradefault") != null && !this.propSis.getProperty("reporte.impresoradefault").isEmpty()) {
                    printServiceAttributeSet.add(new PrinterName(this.propSis.getProperty("reporte.impresoradefault"), null));
                }
                //printServiceAttributeSet.add(new PrinterName("Epson Stylus 820 ESC/P 2", null));

                JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
                configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
                configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
                configuration.setDisplayPageDialog(false);
                configuration.setDisplayPrintDialog(true);
                exporter.setConfiguration(configuration);
                exporter.exportReport();
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > ejecutarReporteHerramientaAjustada", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error de impresion", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Connection conectarBD() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + this.propSis.getProperty("bd.host") + ":" + this.propSis.getProperty("bd.puerto") + "/" + this.propSis.getProperty("bd.basedatos"), this.propSis.getProperty("bd.usuario"), this.propSis.getProperty("bd.clave"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(ConexionBD.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
}

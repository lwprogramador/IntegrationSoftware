/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.awt.Toolkit;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import software.modelos.ConsultaAprietes;
import software.modelos.Herramienta;
import software.modelos.HerramientaAprietes;

/**
 *
 * @author MSI
 */
public class ControladorAprietes {

    private static Properties PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();

    public void cargarHerramientas(JComboBox cmbHerramientas, ArrayList<Herramienta> herramientas) {
        try {
            if (herramientas == null) {
                return;
            }
            cmbHerramientas.removeAllItems();
            cmbHerramientas.addItem(this.PROP_SISTEMA.getProperty("default.herramientas"));

            for (Herramienta item : herramientas) {
                if (item.isActivo()) {
                    cmbHerramientas.addItem(String.valueOf(item.getNombre()));
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarHerramientas", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void cargarAprietes(JTable tblAprietes, Herramienta herramientaAjuste) {
        try {
            Vector columnas = new Vector();
            for (int i = 0; i < tblAprietes.getColumnCount(); i++) {
                columnas.add(tblAprietes.getColumnName(i));
            }

            DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
                @Override
                public Class<?> getColumnClass(int column) {
                    switch (column) {
                        case 0:
                            return ImageIcon.class;
                        default:
                            return Object.class;
                    }
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (HerramientaAprietes item : herramientaAjuste.getAprietes()) {
                Vector vItem = new Vector();
                ImageIcon icon;
                if (item.isAlta()) {
                    icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_fecha_arriba_ssm.png")));
                    icon.setDescription("ALTA");
                } else {
                    icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_flecha_abajo_ssm.png")));
                    icon.setDescription("BAJA");
                }
                vItem.add(icon);
                vItem.add(item.getTxApriete());
                vItem.add(String.format("%.5f", item.getApriete()).replaceAll(",", "."));
                vItem.add(String.format("%.5f", item.getEmpMin()).replaceAll(",", "."));
                vItem.add(String.format("%.5f", item.getEmpMax()).replaceAll(",", "."));
                vItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));

                modelo.addRow(vItem);
            }
            tblAprietes.setModel(modelo);
            tblAprietes.getColumnModel().getColumn(0).setPreferredWidth(25);

        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarHerramientas", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public String listarClientes(JComboBox comboClientes) {
        String Cliente = null;
        try {
            JComboBox desplegable;
            if (comboClientes == null) {
                desplegable = new JComboBox();
                desplegable.setRenderer(new ConfiguracionElementos.CustomComboBox());
            } else {
                desplegable = comboClientes;
                desplegable.removeAllItems();
            }
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.listarclientes"));
            ArrayList<String> clientes = con.listarClientes(queryEjecutar);
            desplegable.addItem(this.PROP_SISTEMA.getProperty("default.cliente", ""));
            if (clientes != null) {
                for (String item : clientes) {
                    desplegable.addItem(item);
                }
            }
            if (comboClientes == null) {
                JOptionPane.showMessageDialog(null, desplegable, "Seleccione cliente", JOptionPane.QUESTION_MESSAGE);
                Cliente = String.valueOf(desplegable.getSelectedItem());
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > listarClientes", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return Cliente;
    }

    public void cargarResultadosBusqueda(JTable tblConsulta, JTextField ODT, JComboBox Cliente, JComboBox Herramienta) {
        try {
            String SQL = this.PROP_SISTEMA.getProperty("sql.traerconsulta");
            if (!String.valueOf(ODT.getText()).equals("")) {
                SQL += " AND UPPER(al.odt) LIKE UPPER('%" + String.valueOf(ODT.getText()) + "%')";
            }
            if (!String.valueOf(Cliente.getSelectedItem()).equals("") && !String.valueOf(Cliente.getSelectedItem()).equals(this.PROP_SISTEMA.getProperty("default.cliente", ""))) {
                SQL += " AND UPPER(al.cliente) LIKE UPPER('%" + String.valueOf(Cliente.getSelectedItem()) + "%')";
            }
            if (!String.valueOf(Herramienta.getSelectedItem()).equals("") && !String.valueOf(Herramienta.getSelectedItem()).equals(this.PROP_SISTEMA.getProperty("default.herramientas"))) {
                SQL += " AND UPPER(he.tx_nombre) LIKE UPPER('%" + String.valueOf(Herramienta.getSelectedItem()) + "%')";
            }
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery("SELECT json_agg(_json.*) FROM (" + SQL + ") _json");
            ArrayList<ConsultaAprietes> aprietes = con.consultaAprietes(queryEjecutar);
            DefaultTableModel tblConsultaCliente = (DefaultTableModel) tblConsulta.getModel();
            tblConsultaCliente.setRowCount(0);
            if (aprietes != null) {
                for (ConsultaAprietes item : aprietes) {
                    Vector vItem = new Vector();
                    vItem.add(item.getId());
                    vItem.add(item.getFecha());
                    vItem.add(item.getODT());
                    vItem.add(item.getHerramienta());
                    vItem.add(item.getCliente());

                    tblConsultaCliente.addRow(vItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > listarClientes", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }
}

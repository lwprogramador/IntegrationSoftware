/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import software.modelos.Herramienta;
import software.modelos.HerramientaAprietes;
import software.modelos.Patrones;
import software.modelos.Usuario;

/**
 *
 * @author MSI
 */
public class ControladorAdministracion {

    private static Properties PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();
    private JTable ADMIN_HERRAMIENTAS_APRIETES;
    private int FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;
    private int COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;

    public void limpiarCamposAdminOperador(JTextField nombOperador, JTextField apellOperador, JTextField docOperador, JCheckBox adminOperador, JTextField usrOperador, JTextField claveOperador, JTextField conClaveOperador, JTextField codOperador) {
        try {
            nombOperador.setText("");
            apellOperador.setText("");
            docOperador.setText("");
            usrOperador.setText("");
            claveOperador.setText("");
            conClaveOperador.setText("");
            codOperador.setText("");
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > limpiarCamposAdminOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public ArrayList<Usuario> cargarTablaOperadores(JTable tablaOperadores) {
        ArrayList<Usuario> listaOperadores = null;
        try {
            listaOperadores = new ArrayList<>();
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.tablaoperador"));
            ResultSet datosGuardarOperador = queryEjecutar.executeQuery();
            if (datosGuardarOperador != null && datosGuardarOperador.next()) {
                listaOperadores = new Gson().fromJson(datosGuardarOperador.getString(1), new TypeToken<List<Usuario>>() {
                }.getType());

                if (listaOperadores != null && listaOperadores.size() > 0) {
                    DefaultTableModel modTabla = (DefaultTableModel) tablaOperadores.getModel();
                    modTabla.setRowCount(0);

                    for (Usuario items : listaOperadores) {
                        Vector vectItem = new Vector();
                        vectItem.add(items.getCodigo());
                        vectItem.add(items.getNombres() + " " + items.getApellidos());
                        vectItem.add(items.getDocumento());
                        vectItem.add((items.isAdmin() == true ? "SI" : "NO"));
                        vectItem.add(items.isActivo());
                        modTabla.addRow(vectItem);
                    }
                }
                datosGuardarOperador.close();
                queryEjecutar.close();
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return listaOperadores;
    }

    public ArrayList<Usuario> guardarOperador(JTextField nombOperador, JTextField apellOperador, JTextField docOperador, JCheckBox adminOperador, JTextField usrOperador, JTextField claveOperador, JTextField conClaveOperador, JTextField codOperador, JTable tablaOperadores) {
        ArrayList<Usuario> listaOperadores = null;
        try {
            if (String.valueOf(usrOperador.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopnousuario"), "Error de Usuario", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }
            if (String.valueOf(codOperador.getText()).trim().equals("") && String.valueOf(claveOperador.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopnocontrasena"), "Error de contraeñas", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }
            if (!String.valueOf(claveOperador.getText()).trim().equals(String.valueOf(conClaveOperador.getText()).trim())) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopcontrasena"), "Error de contraeñas", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }
            if (String.valueOf(nombOperador.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopnombres"), "Error en nombres", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }
            if (String.valueOf(apellOperador.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopapellidos"), "Error en apellidos", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }
            if (String.valueOf(docOperador.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionopdocumento"), "Error en documento", JOptionPane.WARNING_MESSAGE);
                return listaOperadores;
            }

            listaOperadores = new ArrayList<>();
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.guardaroperador"));
            queryEjecutar.setString(1, String.valueOf(nombOperador.getText()));
            queryEjecutar.setString(2, String.valueOf(apellOperador.getText()));
            queryEjecutar.setString(3, String.valueOf(docOperador.getText()));
            queryEjecutar.setString(4, String.valueOf(usrOperador.getText()));
            queryEjecutar.setString(5, String.valueOf(claveOperador.getText()));
            queryEjecutar.setString(6, String.valueOf(codOperador.getText()));
            queryEjecutar.setBoolean(7, adminOperador.isSelected());
            ResultSet datosGuardarOperador = queryEjecutar.executeQuery();
            if (datosGuardarOperador != null && datosGuardarOperador.next()) {
                listaOperadores = new Gson().fromJson(datosGuardarOperador.getString(1), new TypeToken<List<Usuario>>() {
                }.getType());

                if (listaOperadores != null && listaOperadores.size() > 0) {
                    DefaultTableModel modTabla = (DefaultTableModel) tablaOperadores.getModel();
                    modTabla.setRowCount(0);

                    for (Usuario items : listaOperadores) {
                        Vector vectItem = new Vector();
                        vectItem.add(items.getCodigo());
                        vectItem.add(items.getNombres() + " " + items.getApellidos());
                        vectItem.add(items.getDocumento());
                        vectItem.add((items.isAdmin() == true ? "SI" : "NO"));
                        vectItem.add(items.isActivo());
                        modTabla.addRow(vectItem);
                    }
                }
            }
            datosGuardarOperador.close();
            queryEjecutar.close();
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return listaOperadores;
    }

    public ArrayList<Usuario> actualizarEstadoOperador(int filaSleccionada, int columnaSleccionada, ArrayList<Usuario> listaOperador) {
        try {
            if (filaSleccionada < 0) {
                return listaOperador;
            }
            if (columnaSleccionada == 4) {
                Usuario operador = listaOperador.get(filaSleccionada);
                ConexionBD con = new ConexionBD();
                PreparedStatement queryEjecutar = con.prepararQuery(this.PROP_SISTEMA.getProperty("sql.estadooperador"));
                queryEjecutar.setBoolean(1, !operador.isActivo());
                queryEjecutar.setString(2, operador.getCodigo());
                int actualizarEstado = queryEjecutar.executeUpdate();
                if (actualizarEstado == 0) {
                    JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.erroractualizarestadooperador") + this.PROP_SISTEMA.getProperty("mensaje.errordefault"), "Actualizar operador", JOptionPane.ERROR_MESSAGE);
                } else {
                    operador.setActivo(!operador.isActivo());
                    listaOperador.set(filaSleccionada, operador);
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cambiarEstadoOperadores", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return listaOperador;
    }

    public void selecionarOperador(JTextField nombOperador, JTextField apellOperador, JTextField docOperador, JCheckBox adminOperador, JTextField usrOperador, JTextField claveOperador, JTextField conClaveOperador, JTextField codOperador, int filaSleccionada, int columnaSleccionada, ArrayList<Usuario> listaOperadores) {
        try {
            if (filaSleccionada > -1) {
                Usuario operador = listaOperadores.get(filaSleccionada);
                if (operador != null) {
                    nombOperador.setText(operador.getNombres());
                    apellOperador.setText(operador.getApellidos());
                    docOperador.setText(operador.getDocumento());
                    adminOperador.setSelected(operador.isAdmin());
                    usrOperador.setText(operador.getUsuario());
                    codOperador.setText(operador.getCodigo());
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > selecionarOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }

    }

    /**
     * *****************Modulo de administracion
     * herramientas********************************************************************************************************
     */
    public ArrayList<Herramienta> cargarTablaHerramientas(JTable tablaHerramientas) {
        ArrayList<Herramienta> herrameintas = new ArrayList<Herramienta>();
        try {
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.tablaherramientas"));
            herrameintas = con.obtenerHerramientas(queryEjecutar);
            DefaultTableModel modeloHerramientas = (DefaultTableModel) tablaHerramientas.getModel();
            if (herrameintas == null || herrameintas.isEmpty()) {
                return herrameintas;
            }
            modeloHerramientas.setRowCount(0);
            for (Herramienta item : herrameintas) {
                Vector vItem = new Vector();
                vItem.add(item.getCodigo());
                vItem.add(item.getNombre());
                vItem.add(item.getNroSerial());
                vItem.add(item.getMedidaHerramienta());
                vItem.add(item.getDiasFuera());
                vItem.add(item.getDistrict());
                vItem.add(item.isActivo());

                modeloHerramientas.addRow(vItem);
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarTablaHerramientas", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return herrameintas;
    }

    public void cargarTiposDeReportesHerramientas(JComboBox comboTpoReportes) {
        try {
            Enumeration propKeys = this.PROP_SISTEMA.keys();
            comboTpoReportes.removeAllItems();

            while (propKeys.hasMoreElements()) {
                String nombKey = (String) propKeys.nextElement();
                if (nombKey.startsWith("reporte.herramienta")) {
                    comboTpoReportes.addItem(String.valueOf(this.PROP_SISTEMA.getProperty(nombKey)));
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarTiposDeReportesHerramientas", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void limpiarCamposAdminHerramientas(JLabel codHerramienta, JTextField nbHerramienta, JTextField serialHerramienta, JTextField medidaHerramienta, JTextField diasFueraHerramienta, JTextField districtoHerramienta, JComboBox tpoReporte, JTable tblAprietes) {
        try {
            codHerramienta.setText("");
            nbHerramienta.setText("");
            serialHerramienta.setText("");

            medidaHerramienta.setText(String.format("%.5f", 0.0).replaceAll(",", "."));
            diasFueraHerramienta.setText(String.format("%.5f", 0.0).replaceAll(",", "."));
            districtoHerramienta.setText("");
            tpoReporte.setSelectedIndex(0);
            DefaultTableModel modeloTbl = (DefaultTableModel) tblAprietes.getModel();
            modeloTbl.setRowCount(0);
            this.agregarNuevoApriete();
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > lipiarCamposAdminHerramientas", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void cargarModeloTablaAprietes(JTable modeloTabla) {
        try {
            Vector columnas = new Vector();
            for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                columnas.add(modeloTabla.getColumnName(i));
            }
            Vector vectItem = new Vector();
            ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_fecha_arriba_ssm.png")));

            vectItem.add(icon);
            vectItem.add("Apriete " + 1);
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));

            DefaultTableModel model = new DefaultTableModel(null, columnas) {
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
                    switch (column) {
                        case 0:
                        case 4:
                        case 5:
                            return false;
                        default:
                            return true;
                    }
                }
            };

            model.addRow(vectItem);
            modeloTabla.setModel(model);
            modeloTabla.setPreferredScrollableViewportSize(modeloTabla.getPreferredSize());

            modeloTabla.setFont(new Font("", Font.PLAIN, 16));
            
            modeloTabla.setComponentPopupMenu(this.agregarMenusTablaHerramientas());

            this.ADMIN_HERRAMIENTAS_APRIETES = modeloTabla;
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarTablaAprietes", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void agregarNuevoApriete() {
        try {
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarCalculosAprietes", "Agregando aprietes");
            DefaultTableModel modelTblAprietes = (DefaultTableModel) this.ADMIN_HERRAMIENTAS_APRIETES.getModel();
            Vector vectItem = new Vector();
            ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_fecha_arriba_ssm.png")));
            icon.setDescription("ALTA");
            vectItem.add(icon);
            vectItem.add("Apriete " + (modelTblAprietes.getRowCount() + 1));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));
            vectItem.add(String.format("%.5f", 0.0).replaceAll(",", "."));

            modelTblAprietes.addRow(vectItem);
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarCalculosAprietes", "Apriete agregado");
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > agregarNuevoApriete", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void eliminarApriete() {
        try {
            DefaultTableModel modelTblAprietes = (DefaultTableModel) this.ADMIN_HERRAMIENTAS_APRIETES.getModel();
            if (this.getFilaAprietes() < modelTblAprietes.getRowCount()) {
                modelTblAprietes.removeRow(this.getFilaAprietes());
                if (modelTblAprietes.getRowCount() == 0) {
                    agregarNuevoApriete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > eliminarApriete", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void convertirAprieteBaja() {
        try {
            DefaultTableModel modelTblAprietes = (DefaultTableModel) this.ADMIN_HERRAMIENTAS_APRIETES.getModel();
            if (this.getFilaAprietes() < modelTblAprietes.getRowCount()) {
                ImageIcon icono = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_flecha_abajo_ssm.png")));
                icono.setDescription("BAJA");
                modelTblAprietes.setValueAt(icono, this.getFilaAprietes(), 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > convertirAprieteBaja", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void convertirAprieteAlta() {
        try {
            DefaultTableModel modelTblAprietes = (DefaultTableModel) this.ADMIN_HERRAMIENTAS_APRIETES.getModel();
            if (this.getFilaAprietes() < modelTblAprietes.getRowCount()) {
                ImageIcon icono = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_fecha_arriba_ssm.png")));
                icono.setDescription("ALTA");
                modelTblAprietes.setValueAt(icono, this.getFilaAprietes(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > convertirAprieteAlta", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void eliminarTodosAprietes() {
        try {
            DefaultTableModel modelTblAprietes = (DefaultTableModel) this.ADMIN_HERRAMIENTAS_APRIETES.getModel();
            modelTblAprietes.setRowCount(0);
            agregarNuevoApriete();
        } catch (Exception e) {
            e.printStackTrace();
            confAplicacion.guardarLogger(this.getClass().toString() + " > eliminarApriete", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public void actualizarCalculosAprietes(JTable jTableAprietes) {
        try {
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarCalculosAprietes", "Actualizando campos aprietes");
            if (jTableAprietes == null || jTableAprietes.getRowCount() == 0) {
                return;
            }
            if (this.getColumnaAprietes() == 0) {
                return;
            }
            DefaultTableModel modelAprietes = (DefaultTableModel) jTableAprietes.getModel();
            if (modelAprietes == null || modelAprietes.getRowCount() == 0) {
                return;
            }

            int iFila = this.getFilaAprietes();
            String nombreApriete = String.valueOf(modelAprietes.getValueAt(iFila, 1));
            String medidaExacta = String.valueOf(modelAprietes.getValueAt(iFila, 2));
            String valorEMP = String.valueOf(modelAprietes.getValueAt(iFila, 3));

            double medida = 0.0;
            double porcMedida = 0.0;
            double porcMedidaValor = 0.0;
            double empPos = 0.0;
            double empNeg = 0.0;

            try {
                medida = Double.parseDouble(medidaExacta);
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString() + " > medida = Double.parseDouble(medidaExacta)", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
            try {                
                porcMedidaValor = Double.parseDouble(valorEMP);
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString() + " > porcMedida = Double.parseDouble(porcentajeEMP)", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
            try {
                empNeg = medida - porcMedidaValor;
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString() + " > empNeg = Double.parseDouble(empNegativo)", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
            try {
                empPos = medida + porcMedidaValor;
            } catch (NumberFormatException e) {
                confAplicacion.guardarLogger(this.getClass().toString() + " > empNeg = Double.parseDouble(empNegativo)", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
            modelAprietes.setValueAt(String.format("%.5f", medida).replaceAll(",", "."), iFila, 2);
            modelAprietes.setValueAt(String.format("%.5f", porcMedidaValor).replaceAll(",", "."), iFila, 3);
            modelAprietes.setValueAt(String.format("%.5f", empPos).replaceAll(",", "."), iFila, 4);
            modelAprietes.setValueAt(String.format("%.5f", empNeg).replaceAll(",", "."), iFila, 5);
            
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarCalculosAprietes", "Actualizado campos aprietes");
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarCalculosAprietes", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public JPopupMenu agregarMenusTablaHerramientas() {
        JPopupMenu popupMenu = new JPopupMenu();
        try {
            ActionListener menuListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    switch (event.getActionCommand()) {
                        case "Agregar apriete":
                            agregarNuevoApriete();
                            break;
                        case "Eliminar apriete":
                            eliminarApriete();
                            break;
                        case "Convertir apriete a baja":
                            convertirAprieteBaja();
                            break;
                        case "Convertir apriete a alta":
                            convertirAprieteAlta();
                            break;
                        case "Eliminar todos los apriete":
                            eliminarTodosAprietes();
                            break;
                    }
                    System.out.println("Popup menu item [" + event.paramString() + event.getActionCommand() + "] was pressed.");
                }
            };

            JMenuItem itemAgregar = new JMenuItem(this.PROP_SISTEMA.getProperty("default.popupmenu.agregarapriete"));
            itemAgregar.addActionListener(menuListener);
            popupMenu.add(itemAgregar);

            JMenuItem itemEliminar = new JMenuItem(this.PROP_SISTEMA.getProperty("default.popupmenu.eliminarapriete"));
            itemEliminar.addActionListener(menuListener);
            popupMenu.add(itemEliminar);
            popupMenu.addSeparator();

            JMenuItem itemConvBaja = new JMenuItem(this.PROP_SISTEMA.getProperty("default.popupmenu.convertirbaja"));
            itemConvBaja.addActionListener(menuListener);
            popupMenu.add(itemConvBaja);

            JMenuItem itemConvAlta = new JMenuItem(this.PROP_SISTEMA.getProperty("default.popupmenu.convertiralta"));
            itemConvAlta.addActionListener(menuListener);
            popupMenu.add(itemConvAlta);

            popupMenu.addSeparator();
            JMenuItem itemEliminarTodo = new JMenuItem(this.PROP_SISTEMA.getProperty("default.popupmenu.eliminartodos"));
            itemEliminarTodo.addActionListener(menuListener);
            popupMenu.add(itemEliminarTodo);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarTablaAprietes", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return popupMenu;
    }

    public ArrayList<Herramienta> guardarHerramienta(JLabel codHerramienta, JTextField nbHerramienta, JTextField serialHerramienta, JTextField medidaHerramienta, JTextField diasFueraHerramienta, JTextField districtoHerramienta, JComboBox tpoReporte, JTable tblAprietes, JTable tblHerramientas) {
        ArrayList<Herramienta> herrameintas = new ArrayList<>();
        try {
            if (String.valueOf(nbHerramienta.getText()).equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionnombreherramienta"), "Error de Usuario", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (String.valueOf(serialHerramienta.getText()).equals("")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validacionserieherramienta"), "Error de Usuario", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (String.valueOf(diasFueraHerramienta.getText()).equals("") || String.valueOf(diasFueraHerramienta.getText()).equals("0")) {
                JOptionPane.showMessageDialog(null, this.PROP_SISTEMA.getProperty("mensaje.validaciondiasfueraherramienta"), "Error de Usuario", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarHerramienta", "INICIANDO GUARDADO");
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar;
            if (codHerramienta.getText() == null || codHerramienta.getText().equals("")) {
                queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.guardarherramienta"));
            } else {
                queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.modificarherramienta"));
                queryEjecutar.setString(7, codHerramienta.getText());
            }
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarHerramienta", "QUERY: " + queryEjecutar.toString());
            queryEjecutar.setString(1, String.valueOf(nbHerramienta.getText()));
            queryEjecutar.setString(2, String.valueOf(serialHerramienta.getText()));
            queryEjecutar.setString(3, String.valueOf(medidaHerramienta.getText()));
            queryEjecutar.setString(4, String.valueOf(diasFueraHerramienta.getText()));
            queryEjecutar.setString(5, String.valueOf(districtoHerramienta.getText()));
            queryEjecutar.setString(6, String.valueOf(tpoReporte.getSelectedItem()));
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarHerramienta", "QUERY EJECUTAR: " + queryEjecutar.toString());
            int herramientaGuardada = con.guardarHerrameintaHead(queryEjecutar);
            DefaultTableModel modelAprietes = (DefaultTableModel) tblAprietes.getModel();
            if (herramientaGuardada > 0) {
                for (int iFila = 0; iFila < modelAprietes.getRowCount(); iFila++) {
                    queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.guardarapriete"));
                    queryEjecutar.setInt(1, herramientaGuardada);

                    queryEjecutar.setBoolean(2, (String.valueOf(modelAprietes.getValueAt(iFila, 0)).equals("ALTA")));
                    queryEjecutar.setString(3, String.valueOf(modelAprietes.getValueAt(iFila, 1)));
                    queryEjecutar.setString(4, String.valueOf(modelAprietes.getValueAt(iFila, 2)));
                    queryEjecutar.setString(5, String.valueOf(modelAprietes.getValueAt(iFila, 3)));
                    queryEjecutar.setString(6, String.valueOf(modelAprietes.getValueAt(iFila, 4)));
                    queryEjecutar.setString(7, String.valueOf(modelAprietes.getValueAt(iFila, 5)));
                    queryEjecutar.execute();
                }
                limpiarCamposAdminHerramientas(codHerramienta, nbHerramienta, serialHerramienta, medidaHerramienta, diasFueraHerramienta, districtoHerramienta, tpoReporte, tblAprietes);
                herrameintas = this.cargarTablaHerramientas(tblHerramientas);
            }
        } catch (HeadlessException | SQLException e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarHerramienta", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return herrameintas;
    }

    public ArrayList<Herramienta> seleccionarHerramienta(JLabel codHerramienta, JTextField nbHerramienta, JTextField serialHerramienta, JTextField medidaHerramienta, JTextField diasFueraHerramienta, JTextField districtoHerramienta, JComboBox tpoReporte, JTable tblAprietes, JTable tblHerramientas, Herramienta itemHerramienta) {
        ArrayList<Herramienta> herrameintas = new ArrayList<>();
        try {
            if (tblHerramientas.getSelectedColumn() == 6) {
                DefaultTableModel modelTblHerramientas = (DefaultTableModel) tblHerramientas.getModel();
                ConexionBD con = new ConexionBD();
                PreparedStatement queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.estadoherramientas"));
                queryEjecutar.setBoolean(1, (boolean) modelTblHerramientas.getValueAt(tblHerramientas.getSelectedRow(), 6));
                queryEjecutar.setString(2, (String) modelTblHerramientas.getValueAt(tblHerramientas.getSelectedRow(), 0));
                queryEjecutar.execute();
            } else {
                codHerramienta.setText(itemHerramienta.getCodigo());
                nbHerramienta.setText(itemHerramienta.getNombre());
                serialHerramienta.setText(itemHerramienta.getNroSerial());
                medidaHerramienta.setText(itemHerramienta.getMedidaHerramienta());
                diasFueraHerramienta.setText(itemHerramienta.getDiasFuera());
                districtoHerramienta.setText(itemHerramienta.getDistrict());
                tpoReporte.setSelectedItem(itemHerramienta.getReporte());

                DefaultTableModel modelTblAprietes = (DefaultTableModel) tblAprietes.getModel();
                modelTblAprietes.setRowCount(0);

                for (HerramientaAprietes item : itemHerramienta.getAprietes()) {
                    Vector vectItem = new Vector();
                    ImageIcon icon;
                    if (item.isAlta()) {
                        icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_fecha_arriba_ssm.png")));
                        icon.setDescription("ALTA");
                    } else {
                        icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("software/iconos/icono_flecha_abajo_ssm.png")));
                        icon.setDescription("BAJA");
                    }

                    vectItem.add(icon);
                    vectItem.add(item.getTxApriete());
                    vectItem.add(String.format("%.5f", item.getApriete()).replaceAll(",", "."));
                    vectItem.add(String.format("%.5f", item.getPorEmp()).replaceAll(",", "."));
                    vectItem.add(String.format("%.5f", item.getEmpMax()).replaceAll(",", "."));
                    vectItem.add(String.format("%.5f", item.getEmpMin()).replaceAll(",", "."));
                    

                    modelTblAprietes.addRow(vectItem);
                }
            }
            herrameintas = cargarTablaHerramientas(tblHerramientas);
        } catch (HeadlessException | SQLException e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > seleccionarHerramienta", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return herrameintas;
    }

    /**
     * *****************Modulo de administracion
     * patron********************************************************************************************************
     */
    
    public void limpiarCamposAdminPatron(JLabel codPatron, JTextField nombEquipoPatron, JTextField marcaPatron, JTextField modeloPatron, JTextField seriePatron, JTextField fechaCalibPatron) {
        try {
            codPatron.setText("");
            nombEquipoPatron.setText("");
            marcaPatron.setText("");
            modeloPatron.setText("");
            seriePatron.setText("");
            fechaCalibPatron.setText("");
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > limpiarCamposAdminPatron", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    public ArrayList<Patrones> cargarPatrones(JTable tablaPatrones){
        ArrayList<Patrones> herrameintas = new ArrayList<Patrones>();
        try {
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.tablapatrones"));
            herrameintas = con.obtenerPatrones(queryEjecutar);
            DefaultTableModel modeloPatrones = (DefaultTableModel) tablaPatrones.getModel();
            if (herrameintas == null || herrameintas.isEmpty()) {
                return herrameintas;
            }
            modeloPatrones.setRowCount(0);
            for (Patrones item : herrameintas) {
                Vector vItem = new Vector();
                vItem.add(item.getCodigo());
                vItem.add(item.getNombre());
                vItem.add(item.getMarca() + " - " + item.getModelo());
                vItem.add(item.getSerie());
                vItem.add(item.isActivo());

                modeloPatrones.addRow(vItem);
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > cargarPatrones", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return herrameintas;
    }
        
    public ArrayList<Patrones> guardarPatron(JLabel codPatron, JTextField nombEquipoPatron, JTextField marcaPatron, JTextField modeloPatron, JTextField seriePatron, JTextField fechaCalibPatron, JTable tblAdminPatrones) {
        ArrayList<Patrones> listaPatrones = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            if (String.valueOf(nombEquipoPatron.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, ControladorAdministracion.PROP_SISTEMA.getProperty("mensaje.validacionnombrequipopatron"), "Error de validación para registro", JOptionPane.WARNING_MESSAGE);
                return listaPatrones;
            }
            if (String.valueOf(marcaPatron.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, ControladorAdministracion.PROP_SISTEMA.getProperty("mensaje.validacionmarcapatron"), "Error de validación para registro", JOptionPane.WARNING_MESSAGE);
                return listaPatrones;
            }
            if (String.valueOf(modeloPatron.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, ControladorAdministracion.PROP_SISTEMA.getProperty("mensaje.validacionmodelopatron"), "Error de validación para registro", JOptionPane.WARNING_MESSAGE);
                return listaPatrones;
            }
            if (String.valueOf(seriePatron.getText()).trim().equals("")) {
                JOptionPane.showMessageDialog(null, ControladorAdministracion.PROP_SISTEMA.getProperty("mensaje.validacionseriepatron"), "Error de validación para registro", JOptionPane.WARNING_MESSAGE);
                return listaPatrones;
            }
            Date calDate;
            try {
                calDate = format.parse(fechaCalibPatron.getText());
            } catch (ParseException e) {
                calDate = null;
            }            
            if (String.valueOf(fechaCalibPatron.getText()).trim().equals("") || calDate == null) {
                JOptionPane.showMessageDialog(null, ControladorAdministracion.PROP_SISTEMA.getProperty("mensaje.validacionfechacalpatron"), "Error de validación para registro", JOptionPane.WARNING_MESSAGE);
                return listaPatrones;
            }
            
            String codigoPatron = String.valueOf(codPatron.getText());
            ConexionBD con = new ConexionBD();
            PreparedStatement queryEjecutar;
            if(codigoPatron != null && !codigoPatron.isEmpty() && !codigoPatron.isBlank()){
                queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.actualizarpatron"));
                queryEjecutar.setString(6, codigoPatron);                
            }else{
                queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.guardarpatron"));
            }
            
            queryEjecutar.setString(1, String.valueOf(nombEquipoPatron.getText()).trim());
            queryEjecutar.setString(2, String.valueOf(marcaPatron.getText()).trim());
            queryEjecutar.setString(3, String.valueOf(modeloPatron.getText()).trim());
            queryEjecutar.setString(4, String.valueOf(seriePatron.getText()).trim());
            queryEjecutar.setString(5, String.valueOf(fechaCalibPatron.getText()).trim());
            
            String patronGuardado = con.guardarPatron(queryEjecutar);
            if(patronGuardado != null){                
                JOptionPane.showMessageDialog(null, "Los datos ha sido guardados", "DATOS GUARDADOS", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(null, "Los datos NO han sido guardados", "DATOS NO GUARDADOS", JOptionPane.INFORMATION_MESSAGE);
            }
            
            listaPatrones = cargarPatrones(tblAdminPatrones);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarOperador", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return listaPatrones;
    }
    
    public ArrayList<Patrones> seleccionarPatron(JLabel codPatron, JTextField nombEquipoPatron, JTextField marcaPatron, JTextField modeloPatron, JTextField seriePatron, JTextField fechaCalibPatron, JTable tblAdminPatrones, Patrones itempatron) {
        ArrayList<Patrones> patrones = new ArrayList<>();
        try {
            if (tblAdminPatrones.getSelectedColumn() == 4) {
                DefaultTableModel modelTblHerramientas = (DefaultTableModel) tblAdminPatrones.getModel();
                ConexionBD con = new ConexionBD();
                PreparedStatement queryEjecutar = con.prepararQuery(PROP_SISTEMA.getProperty("sql.estadoherramientas"));
                queryEjecutar.setBoolean(1, (boolean) modelTblHerramientas.getValueAt(tblAdminPatrones.getSelectedRow(), 4));
                queryEjecutar.setString(2, (String) modelTblHerramientas.getValueAt(tblAdminPatrones.getSelectedRow(), 0));
                queryEjecutar.execute();                
            } else {
                codPatron.setText(itempatron.getCodigo());
                nombEquipoPatron.setText(itempatron.getNombre());
                marcaPatron.setText(itempatron.getMarca());
                modeloPatron.setText(itempatron.getModelo());
                seriePatron.setText(itempatron.getSerie());
                fechaCalibPatron.setText(itempatron.getFechaCal());
            }
            
            patrones = cargarPatrones(tblAdminPatrones);
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > seleccionarHerramienta", e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return patrones;
    }
    /**
     * @return the FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO
     */
    public int getFilaAprietes() {
        return FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;
    }

    /**
     * @param FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO the
     * FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO to set
     */
    public void setFilaAprietes(int FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO) {
        this.FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO = FILA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;
    }

    /**
     * @return the COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO
     */
    public int getColumnaAprietes() {
        return COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;
    }

    /**
     * @param COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO the
     * COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO to set
     */
    public void setColumnaAprietes(int COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO) {
        this.COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO = COLUMNA_ADMIN_HERRAMIENTAS_APRIETES_SELECCIONADO;
    }
}

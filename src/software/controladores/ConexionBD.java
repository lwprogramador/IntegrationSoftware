/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import software.modelos.ConsultaAprietes;
import software.modelos.Herramienta;
import software.modelos.Patrones;
import software.modelos.Usuario;

/**
 *
 * @author leudiswanderbiest
 */
public class ConexionBD {

    private Connection conexionSQL;
    private Properties PROP_SISTEMA;

    public ConexionBD() {
        try {
            this.PROP_SISTEMA = confAplicacion.cargarPropiedadesAplicacion();
        } catch (Exception ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > ConexionBD", "ERROR " + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
    }

    private void conectarBD() {
        try {
            conexionSQL = DriverManager.getConnection("jdbc:postgresql://" + this.PROP_SISTEMA.getProperty("bd.host") + ":" + this.PROP_SISTEMA.getProperty("bd.puerto") + "/" + this.PROP_SISTEMA.getProperty("bd.basedatos"), this.PROP_SISTEMA.getProperty("bd.usuario"), this.PROP_SISTEMA.getProperty("bd.clave"));
            confAplicacion.guardarLogger(this.getClass().toString() + " > conectarBD", "COMENZANDO NUEVA CONEXIÓN A BASE DATOS");
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > conectarBD", ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
    }

    public PreparedStatement prepararQuery(String sql) throws SQLException {
        PreparedStatement query = null;
        try {
            if (conexionSQL == null || !conexionSQL.isValid(Integer.parseInt(this.PROP_SISTEMA.getProperty("bd.timeout")))) {
                conectarBD();
            }
            if (conexionSQL != null && conexionSQL.isValid(Integer.parseInt(this.PROP_SISTEMA.getProperty("bd.timeout")))) {
                query = conexionSQL.prepareStatement(sql);
                confAplicacion.guardarLogger(this.getClass().toString() + " > prepararQuery", "PREPARANDO QUERY SQL PARA LA EJECUCION " + query.toString());
            }
        } catch (SQLException sqlEx) {
            if (query != null && !query.isClosed()) {
                query.close();
            }
            if (conexionSQL != null && !conexionSQL.isClosed()) {
                conexionSQL.close();
            }
            confAplicacion.guardarLogger(this.getClass().toString() + " > prepararQuery", sqlEx.getMessage(), Arrays.toString(sqlEx.getStackTrace()).replace(",", "\n"));
        } catch (NumberFormatException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > prepararQuery", ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return query;
    }

    public void actualizarContador(String contador) {  
        PreparedStatement query = null;
        try {
            query = conexionSQL.prepareStatement("UPDATE tbl_contadores SET " + contador + " = " + contador + " + 1");
            query.executeQuery();
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > actualizarContador", query.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
    }
    
    public Usuario realizarLoginUsuario(PreparedStatement queryEjecutar) {
        Usuario retUsr = null;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    Gson gson = new Gson();
                    retUsr = gson.fromJson(new StringReader(resultSet.getString(1)), Usuario.class);
                    confAplicacion.guardarLogger(this.getClass().toString() + " > realizarLoginUsuario", "SE EJECUTO EL QUERY " + confAplicacion.SISTEMA_SALTO_LINEA + queryEjecutar.toString() + "", "");
                }
            }
        } catch (Exception ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > prepararQuery", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return retUsr;
    }

    public Usuario guardarOperador(PreparedStatement queryEjecutar) {
        Usuario retUsr = null;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    Gson gson = new Gson();
                    retUsr = gson.fromJson(new StringReader(resultSet.getString(1)), Usuario.class);
                    confAplicacion.guardarLogger(this.getClass().toString() + " > realizarLoginUsuario", "SE EJECUTO EL QUERY " + confAplicacion.SISTEMA_SALTO_LINEA + queryEjecutar.toString() + "", "");
                    if(queryEjecutar.toString().startsWith("INSERT INTO")){
                        actualizarContador("cont_operador");
                    }
                }
            }
        } catch (Exception ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarOperador", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return retUsr;
    }

    public int guardarHerrameintaHead(PreparedStatement queryEjecutar) {
        int herrID = -1;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    herrID = resultSet.getInt(1);
                    queryEjecutar = conexionSQL.prepareStatement(this.PROP_SISTEMA.getProperty("sql.eliminaraprietes"));
                    queryEjecutar.setInt(1, herrID);
                    queryEjecutar.execute();
                    if(queryEjecutar.toString().startsWith("INSERT INTO")){
                        actualizarContador("cont_herramienta");
                    }
                }
            }
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarHerrameintaHead", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return herrID;
    }

    public ArrayList<Herramienta> obtenerHerramientas(PreparedStatement queryEjecutar) {
        ArrayList<Herramienta> herramientas = new ArrayList<Herramienta>();
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    herramientas = new Gson().fromJson(resultSet.getString(1), new TypeToken<List<Herramienta>>() {
                    }.getType());
                }
            }
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > obtenerHerramientas", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return herramientas;
    }

    public int guardarAprieteHead(PreparedStatement queryEjecutar) {
        int aprieteHead = -1;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    aprieteHead = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarAprieteHead", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return aprieteHead;
    }

    public int guardarAprieteDet(PreparedStatement queryEjecutar) {
        int aprieteHead = -1;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    aprieteHead = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarAprieteHead", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return aprieteHead;
    }

    public String guardarPatron(PreparedStatement queryEjecutar) {
        String aprietePatron = null;
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    aprietePatron = resultSet.getString(1);
                    if(queryEjecutar.toString().startsWith("INSERT INTO")){
                        actualizarContador("cont_patron");
                    }
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarPatron", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return aprietePatron;
    }
    
    public ArrayList<Patrones> obtenerPatrones(PreparedStatement queryEjecutar) {
        ArrayList<Patrones> herramientas = new ArrayList<>();
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    herramientas = new Gson().fromJson(resultSet.getString(1), new TypeToken<List<Patrones>>() {
                    }.getType());
                }
            }
        } catch (SQLException ex) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > obtenerPatrones", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + ex.getMessage(), Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
        }
        return herramientas;
    }
    public ArrayList<String> listarClientes(PreparedStatement queryEjecutar) {
        ArrayList<String> aprieteHead = new ArrayList<>();
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    aprieteHead.add(resultSet.getString(1));
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarAprieteHead", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return aprieteHead;
    }

    public ArrayList<ConsultaAprietes> consultaAprietes(PreparedStatement queryEjecutar) {
        ArrayList<ConsultaAprietes> aprieteResultados = new ArrayList<>();
        try {
            ResultSet resultSet = queryEjecutar.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    aprieteResultados = new Gson().fromJson(resultSet.getString(1), new TypeToken<List<ConsultaAprietes>>() {
                    }.getType());
                }
            }
        } catch (Exception e) {
            confAplicacion.guardarLogger(this.getClass().toString() + " > guardarAprieteHead", queryEjecutar.toString() + confAplicacion.SISTEMA_SALTO_LINEA + e.getMessage(), Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        return aprieteResultados;
    }
    
    
}

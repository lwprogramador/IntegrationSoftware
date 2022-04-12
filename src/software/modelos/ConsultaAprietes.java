/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

/**
 *
 * @author MSI
 */
public class ConsultaAprietes {

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the Operador
     */
    public String getOperador() {
        return operador;
    }

    /**
     * @param Operador the Operador to set
     */
    public void setOperador(String Operador) {
        this.operador = Operador;
    }

    /**
     * @return the herramienta
     */
    public String getHerramienta() {
        return herramienta;
    }

    /**
     * @param herramienta the herramienta to set
     */
    public void setHerramienta(String herramienta) {
        this.herramienta = herramienta;
    }

    /**
     * @return the ODT
     */
    public String getODT() {
        return odt;
    }

    /**
     * @param ODT the ODT to set
     */
    public void setODT(String ODT) {
        this.odt = ODT;
    }

    /**
     * @return the cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    private int id;
    private String fecha;
    private String operador;
    private String herramienta;
    private String odt;
    private String cliente;
}

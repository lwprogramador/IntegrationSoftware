/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author MSI
 */
public class CamposActualizar {

    /**
     * @return the Serie
     */
    public JTextField getSerie() {
        return Serie;
    }

    /**
     * @param Serie the Serie to set
     */
    public void setSerie(JTextField Serie) {
        this.Serie = Serie;
    }

    /**
     * @return the Direccion
     */
    public JTextField getDireccion() {
        return Direccion;
    }

    /**
     * @param Direccion the Direccion to set
     */
    public void setDireccion(JTextField Direccion) {
        this.Direccion = Direccion;
    }

    /**
     * @return the Patrones
     */
    public JComboBox getPatrones() {
        return Patrones;
    }

    /**
     * @param Patrones the Patrones to set
     */
    public void setPatrones(JComboBox Patrones) {
        this.Patrones = Patrones;
    }

    /**
     * @return the herramienta
     */
    public Herramienta getHerramienta() {
        return herramienta;
    }

    /**
     * @param herramienta the herramienta to set
     */
    public void setHerramienta(Herramienta herramienta) {
        this.herramienta = herramienta;
    }

    /**
     * @return the operador
     */
    public Usuario getOperador() {
        return operador;
    }

    /**
     * @param operador the operador to set
     */
    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    /**
     * @return the panelGrafica
     */
    public JPanel getPanelGrafica() {
        return panelGrafica;
    }

    /**
     * @param panelGrafica the panelGrafica to set
     */
    public void setPanelGrafica(JPanel panelGrafica) {
        this.panelGrafica = panelGrafica;
    }

    /**
     * @return the fechaHora
     */
    public JTextField getFechaHora() {
        return fechaHora;
    }

    /**
     * @param fechaHora the fechaHora to set
     */
    public void setFechaHora(JTextField fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * @return the Linea1
     */
    public JTextField getLinea1() {
        return Linea1;
    }

    /**
     * @param Linea1 the Linea1 to set
     */
    public void setLinea1(JTextField Linea1) {
        this.Linea1 = Linea1;
    }

    /**
     * @return the Linea2
     */
    public JTextField getLinea2() {
        return Linea2;
    }

    /**
     * @param Linea2 the Linea2 to set
     */
    public void setLinea2(JTextField Linea2) {
        this.Linea2 = Linea2;
    }

    /**
     * @return the Auxiliar1
     */
    public JTextField getAuxiliar1() {
        return Auxiliar1;
    }

    /**
     * @param Auxiliar1 the Auxiliar1 to set
     */
    public void setAuxiliar1(JTextField Auxiliar1) {
        this.Auxiliar1 = Auxiliar1;
    }

    /**
     * @return the Auxiliar2
     */
    public JTextField getAuxiliar2() {
        return Auxiliar2;
    }

    /**
     * @param Auxiliar2 the Auxiliar2 to set
     */
    public void setAuxiliar2(JTextField Auxiliar2) {
        this.Auxiliar2 = Auxiliar2;
    }

    /**
     * @return the ODT
     */
    public JTextField getODT() {
        return ODT;
    }

    /**
     * @param ODT the ODT to set
     */
    public void setODT(JTextField ODT) {
        this.ODT = ODT;
    }

    /**
     * @return the Cliente
     */
    public JTextField getCliente() {
        return Cliente;
    }

    /**
     * @param Cliente the Cliente to set
     */
    public void setCliente(JTextField Cliente) {
        this.Cliente = Cliente;
    }

    /**
     * @return the Herramientas
     */
    public JComboBox getHerramientas() {
        return Herramientas;
    }

    /**
     * @param Herramientas the Herramientas to set
     */
    public void setHerramientas(JComboBox Herramientas) {
        this.Herramientas = Herramientas;
    }

    /**
     * @return the tblAprietes
     */
    public JTable getTblAprietes() {
        return tblAprietes;
    }

    /**
     * @param tblAprietes the tblAprietes to set
     */
    public void setTblAprietes(JTable tblAprietes) {
        this.tblAprietes = tblAprietes;
    }

    private Herramienta herramienta;
    private Usuario operador;
    private JPanel panelGrafica;
    private JTextField fechaHora;
    private JTextField Linea1;
    private JTextField Linea2;
    private JTextField Auxiliar1;
    private JTextField Auxiliar2;

    private JTextField ODT;
    private JTextField Serie;
    private JTextField Cliente;
    private JTextField Direccion;
    private JComboBox Herramientas;
    private JComboBox Patrones;
    private JTable tblAprietes;
}

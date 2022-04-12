/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

/**
 *
 * @author MSI
 */
public class HerramientaAprietes {

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the alta
     */
    public boolean isAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(boolean alta) {
        this.alta = alta;
    }

    /**
     * @return the tx_apriete
     */
    public String getTxApriete() {
        return tx_apriete;
    }

    /**
     * @param tx_apriete the tx_apriete to set
     */
    public void setTxApriete(String tx_apriete) {
        this.tx_apriete = tx_apriete;
    }

    /**
     * @return the apriete
     */
    public double getApriete() {
        return apriete;
    }

    /**
     * @param apriete the apriete to set
     */
    public void setApriete(double apriete) {
        this.apriete = apriete;
    }

    /**
     * @return the por_emp
     */
    public double getPorEmp() {
        return por_emp;
    }

    /**
     * @param por_emp the por_emp to set
     */
    public void setPorEmp(double por_emp) {
        this.por_emp = por_emp;
    }

    /**
     * @return the emp_max
     */
    public double getEmpMax() {
        return emp_max;
    }

    /**
     * @param emp_max the emp_max to set
     */
    public void setEmpMax(double emp_max) {
        this.emp_max = emp_max;
    }

    /**
     * @return the emp_min
     */
    public double getEmpMin() {
        return emp_min;
    }

    /**
     * @param emp_min the emp_min to set
     */
    public void setEmpMin(double emp_min) {
        this.emp_min = emp_min;
    }

    private String codigo;
    private boolean alta;
    private String tx_apriete;
    private double apriete;
    private double por_emp;
    private double emp_max;
    private double emp_min;
}

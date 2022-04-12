/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

import java.util.ArrayList;

/**
 *
 * @author MSI
 */
public class Herramienta {

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
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the nro_serial
     */
    public String getNroSerial() {
        return nro_serial;
    }

    /**
     * @param nro_serial the nro_serial to set
     */
    public void setNroSerial(String nro_serial) {
        this.nro_serial = nro_serial;
    }

    /**
     * @return the medida_herramienta
     */
    public String getMedidaHerramienta() {
        return medida_herramienta;
    }

    /**
     * @param medida_herramienta the medida_herramienta to set
     */
    public void setMedidaHerramienta(String medida_herramienta) {
        this.medida_herramienta = medida_herramienta;
    }

    /**
     * @return the dias_fuera
     */
    public String getDiasFuera() {
        return dias_fuera;
    }

    /**
     * @param dias_fuera the dias_fuera to set
     */
    public void setDiasFuera(String dias_fuera) {
        this.dias_fuera = dias_fuera;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return the reporte
     */
    public String getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the aprietes
     */
    public ArrayList<HerramientaAprietes> getAprietes() {
        return aprietes;
    }

    /**
     * @param aprietes the aprietes to set
     */
    public void setAprietes(ArrayList<HerramientaAprietes> aprietes) {
        this.aprietes = aprietes;
    }

    private String codigo;
    private boolean activo;
    private String nombre;
    private String nro_serial;
    private String medida_herramienta;
    private String dias_fuera;
    private String district;
    private String reporte;

    private ArrayList<HerramientaAprietes> aprietes;

}

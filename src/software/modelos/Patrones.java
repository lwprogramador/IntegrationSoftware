/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

/**
 *
 * @author Leudis Wan Der Biest
 */
public class Patrones {

    /**
     * @return the _unico
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param _unico the _unico to set
     */
    public void setCodigo(String _unico) {
        this.codigo = _unico;
    }

    /**
     * @return the bo_activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param bo_activo the bo_activo to set
     */
    public void setActivo(boolean bo_activo) {
        this.activo = bo_activo;
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
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * @return the modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the fechacal
     */
    public String getFechaCal() {
        return fechacal;
    }

    /**
     * @param fechacal the fechacal to set
     */
    public void setFechaCal(String fechacal) {
        this.fechacal = fechacal;
    }
    
    
    
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private String serie;
    private String fechacal;
    private boolean activo;
}

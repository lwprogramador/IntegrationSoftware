/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.modelos;

/**
 *
 * @author leudiswanderbiest
 */
public class modRespuestaBD {
    /**
     * @return the mensajeComp
     */
    public String getMensajeComp() {
        return mensajeComp;
    }

    /**
     * @param mensajeComp the mensajeComp to set
     */
    public void setMensajeComp(String mensajeComp) {
        this.mensajeComp = mensajeComp;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return the cod_accion
     */
    public int getCod_accion() {
        return cod_accion;
    }

    /**
     * @param cod_accion the cod_accion to set
     */
    public void setCod_accion(int cod_accion) {
        this.cod_accion = cod_accion;
    }

    /**
     * @return the contenido
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * @param contenido the contenido to set
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    public modRespuestaBD(){
        status = false;
        contenido = "[]"; 
        mensajeComp = "";
    }
    
    private boolean status;
    private int cod_accion;
    private String contenido;
    private String mensaje;
    private String mensajeComp;
    private String url;
}

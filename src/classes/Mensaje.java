/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.Serializable;

/**
 *
 * @author Usuario
 */
public class Mensaje implements Serializable{
    private String motivo;
    private Object contenido;

    public Mensaje(String motivo, Object contenido) {
        this.motivo = motivo;
        this.contenido = contenido;
    }

    public String getMotivo() {
        return motivo;
    }

    public Object getContenido() {
        return contenido;
    }
    
}

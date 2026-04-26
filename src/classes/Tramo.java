/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.Serializable;
import java.time.Duration;

/**
 *
 * @author Usuario
 */
public class Tramo implements Serializable{
    private int id;
    private Parada paradaOrigen;
    private Parada paradaDestino;
    private float distancia;
    private String tiempo;

    public Tramo(int id, Parada paradaOrigen, Parada paradaDestino, float distancia, String tiempo) {
        this.id = id;
        this.paradaOrigen = paradaOrigen;
        this.paradaDestino = paradaDestino;
        this.distancia = distancia;
        this.tiempo = tiempo;
    }

    public Parada getParadaOrigen() {
        return paradaOrigen;
    }

    public void setParadaOrigen(Parada paradaOrigen) {
        this.paradaOrigen = paradaOrigen;
    }

    public Parada getParadaDestino() {
        return paradaDestino;
    }

    public void setParadaDestino(Parada paradaDestino) {
        this.paradaDestino = paradaDestino;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}

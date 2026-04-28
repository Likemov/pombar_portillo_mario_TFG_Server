/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Usuario
 */
public class Billete implements Serializable{
    private int id;
    private Usuario usuario;
    private Viaje viaje;
    private Parada paradaOrigen;
    private Parada paradaDestino;
    private float precioBase;
    private float descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Billete(int id, Usuario usuario, Viaje viaje, Parada paradaOrigen, Parada paradaDestino, float precioBase, float descuento, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.usuario = usuario;
        this.viaje = viaje;
        this.paradaOrigen = paradaOrigen;
        this.paradaDestino = paradaDestino;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public Parada getParadaOrigen() {
        return paradaOrigen;
    }

    public void setParadaOrigen(Parada parada_origen) {
        this.paradaOrigen = parada_origen;
    }

    public Parada getParadaDestino() {
        return paradaDestino;
    }

    public void setParadaDestino(Parada parada_destino) {
        this.paradaDestino = parada_destino;
    }

    public float getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(float precio_base) {
        this.precioBase = precio_base;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fecha_inicio) {
        this.fechaInicio = fecha_inicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fecha_fin) {
        this.fechaFin = fecha_fin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public float getPrecioFinal() {
        return precioBase * (descuento / 100);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Billete)) return false;
        Billete b = (Billete) o;
        return id == b.getId();
    }
}

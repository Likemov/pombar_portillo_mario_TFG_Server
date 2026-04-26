/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class Usuario implements Serializable{
    private int id;
    private String correo;
    private String nombre;
    private String password;
    private ArrayList<Billete> listaBilletes;

    public Usuario(int id, String correo, String nombre, String password) {
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
        this.password = password;
        this.listaBilletes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Billete> getListaBilletes() {
        return listaBilletes;
    }

    public void setListaBilletes(ArrayList<Billete> listaBilletes) {
        this.listaBilletes = listaBilletes;
    }
    
    
}

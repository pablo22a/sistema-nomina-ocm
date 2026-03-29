/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author sebas
 */
public class Puesto {
    
     // Atributos que coinciden con las columnas de tu tabla 'puesto'
    private int idPuesto;
    private String nombre;
    private String descripcion;
    // La FK id_departamento la manejaremos después cuando sea necesario

    // Constructor vacío (muy importante para ciertas librerías)
    public Puesto() {
    }

    // Getters y Setters para poder acceder y modificar los datos
    public int getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return this.nombre;
    }
    
}

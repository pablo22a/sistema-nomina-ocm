/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDateTime;

/**
 *
 * @author sebas
 */
public class LogEntry {
    
    private int id;
    private String accion;
    private String detalle;
    private String usuario;
    private LocalDateTime fecha;

    // Constructor
    public LogEntry(int id, String accion, String detalle, String usuario, LocalDateTime fecha) {
        this.id = id;
        this.accion = accion;
        this.detalle = detalle;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    // Getters
    public int getId() { return id; }
    public String getAccion() { return accion; }
    public String getDetalle() { return detalle; }
    public String getUsuario() { return usuario; }
    public LocalDateTime getFecha() { return fecha; }
}

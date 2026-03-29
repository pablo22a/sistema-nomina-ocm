/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Pablo
 */
public class Nomina {
    
    // Atributos de la tabla nomina
    private int idNomina;
    private int idEmpleado;
    private int totalHorasTrabajadas;
    private LocalDateTime fecha;
    private BigDecimal totalPrestaciones;
    private BigDecimal totalNeto;
    private BigDecimal sueldoBase;

    public Nomina() {
    }

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getTotalHorasTrabajadas() {
        return totalHorasTrabajadas;
    }

    public void setTotalHorasTrabajadas(int totalHorasTrabajadas) {
        this.totalHorasTrabajadas = totalHorasTrabajadas;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalPrestaciones() {
        return totalPrestaciones;
    }

    public void setTotalPrestaciones(BigDecimal totalPrestaciones) {
        this.totalPrestaciones = totalPrestaciones;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public BigDecimal getSueldoBase() {
        return sueldoBase;
    }

    public void setSueldoBase(BigDecimal sueldoBase) {
        this.sueldoBase = sueldoBase;
    }
}

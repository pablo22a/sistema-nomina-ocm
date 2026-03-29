package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Pablo
 */
public class Prestacion {
    
    // Atributos de la tabla Prestacion
    private int idPrestacion;
    private String tipoPrestacion;
    private BigDecimal monto;
    private String descripcion;
    private int idEmpleado;
    private LocalDateTime fecha;
    
    public Prestacion() {}

    public int getIdPrestacion() {
        return idPrestacion;
    }

    public void setIdPrestacion(int idPrestacion) {
        this.idPrestacion = idPrestacion;
    }

    public String getTipoPrestacion() {
        return tipoPrestacion;
    }

    public void setTipoPrestacion(String tipoPrestacion) {
        this.tipoPrestacion = tipoPrestacion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
}

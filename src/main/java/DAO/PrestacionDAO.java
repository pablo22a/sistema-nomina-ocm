package DAO;

import conexion.ConexionMysql;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Prestacion;

/**
 *
 * @author Pablo
 */
public class PrestacionDAO {
    
    /**
     * Verifica si existe un empleado con el ID especificado
     * @param idEmpleado ID del empleado a verificar
     * @return true si existe, false si no existe
     */
    private boolean existeEmpleado(int idEmpleado) {
        String sql = "SELECT COUNT(*) FROM empleado WHERE id_empleado = ?";
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia del empleado: " + e.getMessage());
        }
        return false;
    }
    
    public boolean agregarPrestacion(Prestacion prestacion) {
        // Validar que el empleado existe antes de insertar
        if (!existeEmpleado(prestacion.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + prestacion.getIdEmpleado());
            return false;
        }
        
        String sql = "INSERT INTO prestacion (tipo_prestacion, monto, descripcion, id_empleado, fecha) VALUES(?, ?, ?, ?, ?);";
        Connection conn = ConexionMysql.getConexion();
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prestacion.getTipoPrestacion());
            pstmt.setBigDecimal(2, prestacion.getMonto());
            pstmt.setString(3, prestacion.getDescripcion());
            pstmt.setInt(4, prestacion.getIdEmpleado());
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(prestacion.getFecha()));
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al agregar prestación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modificarPrestacion(Prestacion prestacion) {
        // Validar que el empleado existe antes de actualizar
        if (!existeEmpleado(prestacion.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + prestacion.getIdEmpleado());
            return false;
        }
        
        String sql = "UPDATE prestacion SET tipo_prestacion = ?, monto = ?, descripcion = ?, id_empleado = ? WHERE id_prestacion = ?";
        java.sql.Connection conn = conexion.ConexionMysql.getConexion();
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prestacion.getTipoPrestacion());
            pstmt.setBigDecimal(2, prestacion.getMonto());
            pstmt.setString(3, prestacion.getDescripcion());
            pstmt.setInt(4, prestacion.getIdEmpleado());
            pstmt.setInt(5, prestacion.getIdPrestacion());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            } else {
                System.err.println("No se encontró prestación con ID: " + prestacion.getIdPrestacion());
                return false;
            }
            
        } catch(java.sql.SQLException e) {
            System.err.println("Error al modificar prestación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarPrestacion(int idPrestacion) {
        String sql = "DELETE FROM prestacion WHERE id_prestacion = ?";
        Connection conn = conexion.ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPrestacion);
            int filasAfectadas = pstmt.executeUpdate(); // Esto devuelve 0 si no encuentra el ID



            return filasAfectadas > 0; // Solo devuelve true si borró algo

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Prestacion> obtenerPrestacionesFiltradas(int idDepartamento) {
        return obtenerPrestacionesFiltradas(idDepartamento,null);
    }
    
    public List<Prestacion> obtenerPrestacionesFiltradas(int idDepartamento, java.time.LocalDate fecha) {
        List<Prestacion> prestaciones = new ArrayList<>();
        // Modificamos la consulta base para incluir todos los campos necesarios para el PDF
        String sqlBase = "SELECT p.id_prestacion, p.tipo_prestacion, p.monto, p.descripcion, p.id_empleado, "
                + "p.fecha FROM prestacion p "
                + "JOIN empleado e ON p.id_empleado = e.id_empleado ";

        List<Object> parametros = new ArrayList<>();
        boolean whereAdded = false;

        if (idDepartamento != 0) {
            sqlBase += "WHERE e.id_departamento = ? ";
            parametros.add(idDepartamento);
            whereAdded = true;
        }

        // --- Esta parte ahora funcionará porque 'fecha' existe ---
        if (fecha != null) {
            // Añade la condición de fecha (compara solo la parte de la fecha, no la hora)
            sqlBase += (whereAdded ? "AND " : "WHERE ") + "DATE(p.fecha) = ? ";
            parametros.add(java.sql.Date.valueOf(fecha)); // Convierte LocalDate a sql.Date
        }
        // --- Fin de la parte de fecha ---

        String sql = sqlBase + "ORDER BY p.fecha DESC";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Asigna los parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                    Prestacion prestacion = new Prestacion();
                    prestacion.setIdPrestacion(rs.getInt("id_prestacion"));
                    prestacion.setTipoPrestacion(rs.getString("tipo_prestacion"));
                    prestacion.setMonto(rs.getBigDecimal("monto"));
                    prestacion.setDescripcion(rs.getString("descripcion"));
                    prestacion.setIdEmpleado(rs.getInt("id_empleado"));
                    prestacion.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    prestaciones.add(prestacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestaciones;
    }
    
    public BigDecimal calcularTotalPrestaciones(int idEmpleado) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM prestacion WHERE id_empleado = ?";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpleado);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular total de prestaciones para empleado " + idEmpleado + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO; // Si hay error o no hay prestaciones, retorna 0
    }

    
}

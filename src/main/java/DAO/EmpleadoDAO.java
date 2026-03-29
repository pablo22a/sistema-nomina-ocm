package DAO;

import conexion.ConexionMysql;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Empleado;

/**
 *
 * @author Pablo
 */
public class EmpleadoDAO {
   
    public boolean agregarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleado (nombre, apellido1, apellido2, puesto, id_departamento, fecha_ingreso, activo, sueldo_base) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, empleado.getNombre());
            pstmt.setString(2, empleado.getApellido1());
            pstmt.setString(3, empleado.getApellido2());
            pstmt.setInt(4, empleado.getIdPuesto());
            pstmt.setInt(5, empleado.getIdDepartamento());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(empleado.getFechaIngreso()));
            pstmt.setBoolean(7, empleado.isActivo());
            pstmt.setBigDecimal(8, empleado.getSueldoBase() != null ? empleado.getSueldoBase() : java.math.BigDecimal.ZERO);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificarEmpleado(Empleado empleado) {
        // La columna del puesto se llama 'puesto' en tu BD, pero usamos el 'idPuesto' del modelo
        String sql = "UPDATE empleado SET nombre = ?, apellido1 = ?, apellido2 = ?, puesto = ?, id_departamento = ?, activo = ?, sueldo_base = ? WHERE id_empleado = ?";

        java.sql.Connection conn = conexion.ConexionMysql.getConexion();
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empleado.getNombre());
            pstmt.setString(2, empleado.getApellido1());
            pstmt.setString(3, empleado.getApellido2());
            pstmt.setInt(4, empleado.getIdPuesto()); // Usa el método que creamos en Empleado.java
            pstmt.setInt(5, empleado.getIdDepartamento());
            pstmt.setBoolean(6, empleado.isActivo());
            pstmt.setBigDecimal(7, empleado.getSueldoBase() != null ? empleado.getSueldoBase() : java.math.BigDecimal.ZERO);
            pstmt.setInt(8, empleado.getIdEmpleado()); // ID para la condición WHERE

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si la actualización fue exitosa

        } catch (java.sql.SQLException e) { // Imprime el error en la consola si algo sale mal
            // Imprime el error en la consola si algo sale mal
            return false;
        }
    }
    
    public boolean eliminarEmpleado(int idEmpleado) {
        Connection conn = conexion.ConexionMysql.getConexion();
        
        try {
            // Desactivar autocommit para hacer transacción
            conn.setAutoCommit(false);
            
            // 1. Primero eliminar prestaciones del empleado
            String sqlPrestaciones = "DELETE FROM prestacion WHERE id_empleado = ?";
            try (PreparedStatement pstmtPrest = conn.prepareStatement(sqlPrestaciones)) {
                pstmtPrest.setInt(1, idEmpleado);
                int prestacionesEliminadas = pstmtPrest.executeUpdate();

            }
            
            // 2. Eliminar asistencias del empleado (si no hay CASCADE)
            String sqlAsistencias = "DELETE FROM asistencia WHERE id_empleado = ?";
            try (PreparedStatement pstmtAsist = conn.prepareStatement(sqlAsistencias)) {
                pstmtAsist.setInt(1, idEmpleado);
                int asistenciasEliminadas = pstmtAsist.executeUpdate();

            }
            
            // 3. Eliminar nóminas del empleado (si no hay CASCADE)
            String sqlNominas = "DELETE FROM nomina WHERE id_empleado = ?";
            try (PreparedStatement pstmtNom = conn.prepareStatement(sqlNominas)) {
                pstmtNom.setInt(1, idEmpleado);
                int nominasEliminadas = pstmtNom.executeUpdate();

            }
            
            // 4. Finalmente eliminar el empleado
            String sqlEmpleado = "DELETE FROM empleado WHERE id_empleado = ?";
            try (PreparedStatement pstmtEmp = conn.prepareStatement(sqlEmpleado)) {
                pstmtEmp.setInt(1, idEmpleado);
                int filasAfectadas = pstmtEmp.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Confirmar transacción
                    conn.commit();

                    return true;
                } else {
                    // Rollback si no se eliminó el empleado
                    conn.rollback();
                    System.err.println("No se encontró empleado con ID: " + idEmpleado);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            try {
                // Rollback en caso de error
                conn.rollback();
                System.err.println("Error durante la eliminación, realizando rollback: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("Error durante rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Restaurar autocommit
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autocommit: " + e.getMessage());
            }
        }
    }

    // Método original para compatibilidad
    public List<Empleado> obtenerEmpleadosFiltrados(int idDepartamento) {
        return obtenerEmpleadosFiltrados(idDepartamento, null);
    }
    
    // Nuevo método con filtro de fecha de ingreso
    public List<Empleado> obtenerEmpleadosFiltrados(int idDepartamento, java.time.LocalDate fechaIngresoFiltro) {
        List<Empleado> empleados = new ArrayList<>();
        // --- SQL Corregido: Ahora seleccionamos apellido1 y apellido2 por separado, incluyendo sueldo_base ---
        String sqlBase = "SELECT e.id_empleado, e.puesto, e.nombre, e.apellido1, e.apellido2, "
                + "e.fecha_ingreso, e.id_departamento, e.activo, e.sueldo_base "
                + "FROM empleado e ";

        // Construir WHERE dinámicamente
        boolean whereAdded = false;
        if (idDepartamento != 0) {
            sqlBase += "WHERE e.id_departamento = ? ";
            whereAdded = true;
        }
        
        if (fechaIngresoFiltro != null) {
            sqlBase += (whereAdded ? "AND " : "WHERE ") + "DATE(e.fecha_ingreso) = ? ";
        }

        String sql = sqlBase + "ORDER BY e.fecha_ingreso DESC";

        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            
            if (idDepartamento != 0) {
                pstmt.setInt(paramIndex++, idDepartamento);
            }
            
            if (fechaIngresoFiltro != null) {
                pstmt.setDate(paramIndex, java.sql.Date.valueOf(fechaIngresoFiltro));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Empleado empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    // Usamos setIdPuesto que es más consistente
                    empleado.setIdPuesto(rs.getInt("puesto")); 
                    empleado.setNombre(rs.getString("nombre"));
                    // --- Lógica Corregida: Guardamos los apellidos por separado ---
                    empleado.setApellido1(rs.getString("apellido1"));
                    empleado.setApellido2(rs.getString("apellido2"));
                    empleado.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());
                    empleado.setIdDepartamento(rs.getInt("id_departamento"));
                    empleado.setActivo(rs.getBoolean("activo"));
                    empleado.setSueldoBase(rs.getBigDecimal("sueldo_base"));
                    empleados.add(empleado);
                }
            }
        } catch(SQLException e) {
            
        }
        return empleados;
    }
    
    public BigDecimal obtenerSueldoBase(int idEmpleado) {
        String sql = "SELECT sueldo_base FROM empleado WHERE id_empleado = ?";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpleado);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                BigDecimal sueldo = rs.getBigDecimal("sueldo_base");
                return sueldo != null ? sueldo : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener sueldo base para empleado " + idEmpleado + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO; // Si hay error o no encuentra el empleado, retorna 0
    }
    
    public Empleado obtenerPorId(int idEmpleado) {
        Empleado empleado = null;
        // Consulta SQL: Debe seleccionar todos los campos que mapeas en el objeto Empleado
        String sql = "SELECT id_empleado, puesto, sueldo_base, nombre, apellido1, apellido2, fecha_ingreso, id_departamento, activo FROM empleado WHERE id_empleado = ?";

        conexion.ConexionMysql conn = new conexion.ConexionMysql();
        java.sql.Connection connection = conn.getConexion();

        try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, idEmpleado);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    empleado = new models.Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    // --- Mapeo de campos ---

                    empleado.setSueldoBase(rs.getBigDecimal("sueldo_base"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellido1(rs.getString("apellido1"));
                    empleado.setApellido2(rs.getString("apellido2"));

                    empleado.setIdDepartamento(rs.getInt("id_departamento"));
                    empleado.setActivo(rs.getBoolean("activo"));
                }
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener empleado por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return empleado;
    }
    
}

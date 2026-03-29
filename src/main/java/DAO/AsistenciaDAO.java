package DAO;

import conexion.ConexionMysql;
import models.Asistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {
    
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
        }
        return false;
    }
    
    public boolean agregarAsistencia(Asistencia asistencia) {
        // Validar que el empleado existe antes de insertar
        if (!existeEmpleado(asistencia.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + asistencia.getIdEmpleado());
            return false;
        }
        
        // Usamos NOW() para que la base de datos ponga la fecha y hora actual automáticamente
        String sql = "INSERT INTO asistencia (id_empleado, horas_trabajadas, fecha) VALUES (?, ?, NOW())";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, asistencia.getIdEmpleado());
            pstmt.setInt(2, asistencia.getHorasTrabajadas());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            return false;
        }
        // NO cerrar 'conn' aquí porque es global
    }
    
    public boolean modificarAsistencia(Asistencia asistencia) {
        // Validar que el empleado existe antes de actualizar
        if (!existeEmpleado(asistencia.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + asistencia.getIdEmpleado());
            return false;
        }
        
        String sql = "UPDATE asistencia SET id_empleado = ?, horas_trabajadas = ?, fecha = ? WHERE id_asistencia = ?";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, asistencia.getIdEmpleado());
            pstmt.setInt(2, asistencia.getHorasTrabajadas());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(asistencia.getFecha()));
            pstmt.setInt(4, asistencia.getIdAsistencia()); // El ID para la condición WHERE

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            } else {
                System.err.println("No se encontró asistencia con ID: " + asistencia.getIdAsistencia());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al modificar asistencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        // NO cerrar 'conn' aquí porque es global
    }
    
    public boolean eliminarAsistencia(int idAsistencia) {
        String sql = "DELETE FROM asistencia WHERE id_asistencia = ?";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsistencia);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se borró la fila

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        // NO cerrar 'conn' aquí porque es global
    }

    // Método original para compatibilidad
    public List<Asistencia> obtenerAsistenciasFiltradas(int idDepartamento) {
        return obtenerAsistenciasFiltradas(idDepartamento, null);
    }
    
    // Nuevo método con filtro de fecha
    public List<Asistencia> obtenerAsistenciasFiltradas(int idDepartamento, java.time.LocalDate fechaFiltro) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sqlBase = "SELECT a.id_asistencia, a.id_empleado, e.nombre, " +
                         "CONCAT_WS(' ', e.apellido1, e.apellido2) AS apellidos, " +
                         "a.horas_trabajadas, a.fecha " +
                         "FROM asistencia a " +
                         "JOIN empleado e ON a.id_empleado = e.id_empleado ";

        // Construir WHERE dinámicamente
        boolean whereAdded = false;
        if (idDepartamento != 0) {
            sqlBase += "WHERE e.id_departamento = ? ";
            whereAdded = true;
        }
        
        if (fechaFiltro != null) {
            sqlBase += (whereAdded ? "AND " : "WHERE ") + "DATE(a.fecha) = ? ";
        }

        String sql = sqlBase + "ORDER BY a.fecha DESC";

        // SOLO try-with-resources para PreparedStatement y ResultSet
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            
            if (idDepartamento != 0) {
                pstmt.setInt(paramIndex++, idDepartamento);
            }
            
            if (fechaFiltro != null) {
                pstmt.setDate(paramIndex, java.sql.Date.valueOf(fechaFiltro));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Asistencia asistencia = new Asistencia();
                    asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
                    asistencia.setIdEmpleado(rs.getInt("id_empleado"));
                    asistencia.setHorasTrabajadas(rs.getInt("horas_trabajadas"));
                    asistencia.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    asistencia.setNombreEmpleado(rs.getString("nombre"));
                    asistencia.setApellidoEmpleado(rs.getString("apellidos"));
                    asistencias.add(asistencia);
                }
            }
        } catch (SQLException e) {
        }
        return asistencias;
        
        // Aquí irán los métodos para agregar, modificar y eliminar asistencias
    }
    
    public int calcularHorasTrabajadas(int idEmpleado) {
        String sql = "SELECT COALESCE(SUM(horas_trabajadas), 0) FROM asistencia WHERE id_empleado = ?";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpleado);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Retorna la suma total de horas trabajadas
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular horas trabajadas para empleado " + idEmpleado + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0; // Si hay error o no hay registros, retorna 0
    }
}

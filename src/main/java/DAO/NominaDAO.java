package DAO;

import conexion.ConexionMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import models.Nomina;
import java.time.LocalDate;

/**
 *
 * @author Pablo
 */
public class NominaDAO {
    
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
    
    public boolean agregarNomina(Nomina nomina) {
        // Validar que el empleado existe antes de insertar
        if (!existeEmpleado(nomina.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + nomina.getIdEmpleado());
            return false;
        }
        
        String sql = "INSERT INTO nomina (id_empleado, total_horas_trabajadas, fecha, total_prestaciones, total_neto, sueldo_base) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nomina.getIdEmpleado());
            pstmt.setInt(2, nomina.getTotalHorasTrabajadas());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(nomina.getFecha()));
            pstmt.setBigDecimal(4, nomina.getTotalPrestaciones());
            pstmt.setBigDecimal(5, nomina.getTotalNeto());
            pstmt.setBigDecimal(6, nomina.getSueldoBase());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al agregar nómina: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modificarNomina(Nomina nomina) {
        // Validar que el empleado existe antes de actualizar
        if (!existeEmpleado(nomina.getIdEmpleado())) {
            System.err.println("Error: No existe empleado con ID " + nomina.getIdEmpleado());
            return false;
        }
        
        String sql = "UPDATE nomina SET id_empleado = ?, total_horas_trabajadas = ?, fecha = ?, total_prestaciones = ?, total_neto = ?, sueldo_base = ? WHERE id_nomina = ?";
        
        java.sql.Connection conn = conexion.ConexionMysql.getConexion();
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nomina.getIdEmpleado());
            pstmt.setInt(2, nomina.getTotalHorasTrabajadas());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(nomina.getFecha()));
            pstmt.setBigDecimal(4, nomina.getTotalPrestaciones());
            pstmt.setBigDecimal(5, nomina.getTotalNeto());
            pstmt.setBigDecimal(6, nomina.getSueldoBase());
            pstmt.setInt(7, nomina.getIdNomina());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            } else {
                System.err.println("No se encontró nómina con ID: " + nomina.getIdNomina());
                return false;
            }

        } catch (java.sql.SQLException e) { 
            System.err.println("Error al modificar nómina: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarNomina(int idNomina) {
    String sql = "DELETE FROM nomina WHERE id_nomina = ?";
    Connection conn = conexion.ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
    try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)){

        pstmt.setInt(1, idNomina);
        int filasAfectadas = pstmt.executeUpdate(); // Esto devuelve 0 si no encuentra el ID



        return filasAfectadas > 0; // Solo devuelve true si borró algo

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    // Método de compatibilidad sin fecha
    public List<Nomina> obtenerNominasFiltradas(int idDepartamento) {
        return obtenerNominasFiltradas(idDepartamento, null);
    }
    
    // Método con filtro de fecha ya implementado correctamente
    public List<Nomina> obtenerNominasFiltradas(int idDepartamento, LocalDate fecha) { // <-- ¡Parámetro fecha AÑADIDO!
        List<Nomina> nominas = new ArrayList<>();
        // Modificamos la consulta base para incluir todos los campos necesarios para el PDF
        String sqlBase = "SELECT n.id_nomina, n.id_empleado, " +
                         "COALESCE(e.nombre, 'EMPLEADO ELIMINADO') AS nombre, " +
                         "COALESCE(CONCAT_WS(' ', e.apellido1, e.apellido2), '') AS apellidos, " +
                         "n.sueldo_base, n.total_horas_trabajadas, n.fecha, n.total_prestaciones, n.total_neto " +
                         "FROM nomina n " +
                         "LEFT JOIN empleado e ON n.id_empleado = e.id_empleado ";

        List<Object> parametros = new ArrayList<>();
        boolean whereAdded = false;

        if (idDepartamento != 0) {
            // Mostrar nóminas de empleados del departamento seleccionado (incluyendo empleados eliminados)
            // Usar INNER JOIN para filtrar solo empleados existentes del departamento especificado
            sqlBase = "SELECT n.id_nomina, n.id_empleado, " +
                     "e.nombre AS nombre, " +
                     "CONCAT_WS(' ', e.apellido1, e.apellido2) AS apellidos, " +
                     "n.sueldo_base, n.total_horas_trabajadas, n.fecha, n.total_prestaciones, n.total_neto " +
                     "FROM nomina n " +
                     "INNER JOIN empleado e ON n.id_empleado = e.id_empleado " +
                     "WHERE e.id_departamento = ? ";
            parametros.add(idDepartamento);
            whereAdded = true;
        }

        // --- Esta parte ahora funcionará porque 'fecha' existe ---
        if (fecha != null) {
            // Añade la condición de fecha (compara solo la parte de la fecha, no la hora)
            sqlBase += (whereAdded ? "AND " : "WHERE ") + "DATE(n.fecha) = ? ";
            parametros.add(java.sql.Date.valueOf(fecha)); // Convierte LocalDate a sql.Date
        }
        // --- Fin de la parte de fecha ---

        String sql = sqlBase + "ORDER BY n.fecha DESC";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Asigna los parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                    Nomina nomina = new Nomina();
                    nomina.setIdNomina(rs.getInt("id_nomina"));
                    nomina.setIdEmpleado(rs.getInt("id_empleado"));
                    nomina.setSueldoBase(rs.getBigDecimal("sueldo_base"));
                    nomina.setTotalHorasTrabajadas(rs.getInt("total_horas_trabajadas"));
                    nomina.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    nomina.setTotalPrestaciones(rs.getBigDecimal("total_prestaciones"));
                    nomina.setTotalNeto(rs.getBigDecimal("total_neto"));
                    nominas.add(nomina);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nominas;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import conexion.ConexionMysql;
import models.LogEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebas
 */
public class LogDAO {
    
    public List<LogEntry> obtenerLogs(String tipoLog) {
        List<LogEntry> logs = new ArrayList<>();
        
        // 1. Mapea el texto del ComboBox a la columna 'tabla' en la BD
        // (Asegúrate de que el texto coincida con tu ComboBox en LogsPanel)
        String tabla_filtrar = "";
        switch (tipoLog) {
            case "Log Puestos":       tabla_filtrar = "Puesto"; break;
            case "Log Empleados":     tabla_filtrar = "Empleado"; break;
            case "Log Asistencias":   tabla_filtrar = "Asistencia"; break;
            case "Log Departamentos": tabla_filtrar = "Departamento"; break;
            case "Log Nominas":       tabla_filtrar = "Nomina"; break;
            case "Log Prestaciones":  tabla_filtrar = "Prestacion"; break;
            default:
                // Si el texto no coincide, podemos devolver una lista vacía
                return logs; 
        }

        // 2. Nueva consulta SQL a la tabla 'log_general'
        String sqlQuery = "SELECT id_log, accion, detalle, usuario, fecha FROM log_general " +
                          "WHERE tabla = ? ORDER BY fecha DESC";

        // Usar la conexión global sin try-with-resources (consistente con otros DAOs)
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            
            // 3. Asigna el filtro
            pstmt.setString(1, tabla_filtrar);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // 4. Crea el objeto LogEntry (asumiendo que tu modelo se llama LogEntry)
                logs.add(new LogEntry(
                    rs.getInt("id_log"),
                    rs.getString("accion"),
                    rs.getString("detalle"),
                    rs.getString("usuario"),
                    rs.getTimestamp("fecha").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // NO cerrar 'conn' aquí porque es global
        // Devuelve la lista de logs (llena o vacía)
        return logs;
    }
}
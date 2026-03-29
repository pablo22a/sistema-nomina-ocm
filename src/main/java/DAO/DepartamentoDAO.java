package DAO;

import conexion.ConexionMysql;
import models.Departamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAO {
    
    public List<Departamento> obtenerTodos() {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = "SELECT id_departamento, nombre FROM departamento ORDER BY nombre";

        // Conexión global, NO usar try-with-resources para la conexión
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Departamento depto = new Departamento();
                depto.setIdDepartamento(rs.getInt("id_departamento"));
                depto.setNombre(rs.getString("nombre"));
                departamentos.add(depto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // NO cerrar 'conn' aquí porque es global
        return departamentos;
    }
    
    public Departamento obtenerPorId(int id) {
        Departamento depto = null;
        // Consulta SQL: DEBE incluir TODAS las columnas que mapeas.
        String sql = "SELECT id_departamento, nombre, descripcion FROM departamento WHERE id_departamento = ?";

        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Establecer el parámetro del ID
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 2. Revisar si se encontró un registro
                if (rs.next()) {
                    depto = new Departamento();
                    // 3. Mapear los campos del ResultSet al objeto Departamento
                    depto.setIdDepartamento(rs.getInt("id_departamento"));
                    depto.setNombre(rs.getString("nombre"));

                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener departamento por ID: " + e.getMessage());
            e.printStackTrace();
        }
        // 4. Retornar el objeto (será null si no se encontró)
        return depto;
    }
}
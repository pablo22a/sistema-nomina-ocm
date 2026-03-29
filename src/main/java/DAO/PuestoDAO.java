package DAO;

import conexion.ConexionMysql;
import models.Puesto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebas
 */
public class PuestoDAO {
    
    /**
     * Obtiene una lista de todos los puestos de la base de datos.
     * @return Una lista de objetos Puesto.
     */
    public List<Puesto> obtenerTodos() {
        List<Puesto> puestos = new ArrayList<>();
        String sql = "SELECT id_puesto, nombre, descripcion FROM puesto";

        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Puesto puesto = new Puesto();
                puesto.setIdPuesto(rs.getInt("id_puesto"));
                puesto.setNombre(rs.getString("nombre"));
                puesto.setDescripcion(rs.getString("descripcion"));
                
                puestos.add(puesto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los puestos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return puestos;
    }

    /**
     * Obtiene un puesto específico por su ID
     * @param id ID del puesto a buscar
     * @return Objeto Puesto o null si no se encuentra
     */
    public Puesto obtenerPorId(int id) {
        Puesto puesto = null;
        String sql = "SELECT id_puesto, nombre, descripcion FROM puesto WHERE id_puesto = ?";

        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    puesto = new Puesto();
                    puesto.setIdPuesto(rs.getInt("id_puesto"));
                    puesto.setNombre(rs.getString("nombre"));
                    puesto.setDescripcion(rs.getString("descripcion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener puesto por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return puesto;
    }
    
    // --- MÉTODOS QUE PROGRAMAREMOS DESPUÉS ---

    public boolean agregarPuesto(Puesto puesto) {
        // TODO: Escribir código SQL para INSERT
        return false;
    }

    public boolean actualizarPuesto(Puesto puesto) {
        // TODO: Escribir código SQL para UPDATE
        return false;
    }

    public boolean eliminarPuesto(int idPuesto) {
        // TODO: Escribir código SQL para DELETE
        return false;
    }
    
}
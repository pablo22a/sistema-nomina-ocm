package DAO;

import conexion.ConexionMysql;
import models.Usuario;
import util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author sebas
 */
public class UsuarioDAO {
    
    public static boolean autenticarUsuario(String username, String password) {
        // 1. Primero, obtener la contraseña almacenada para este usuario
        String sqlQuery = "SELECT password FROM usuarios WHERE username = ?";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    
                    // 2. COMPATIBILIDAD: Verificar si es hash o texto plano
                    
                    // Si contiene ":" es un hash (formato salt:hash)
                    if (storedPassword.contains(":")) {
                        // Es un hash nuevo - usar verificación con hash
                        return PasswordHasher.verifyPassword(password, storedPassword);
                    } else {
                        // Es texto plano (usuario existente) - comparación directa

                        boolean isValidPassword = password.equals(storedPassword);
                        
                        // Si la autenticación es exitosa, migrar automáticamente a hash
                        if (isValidPassword) {
                            migrarPasswordAHash(username, password);
                        }
                        
                        return isValidPassword;
                    }
                } else {
                    // Usuario no encontrado
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar el usuario: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Crea un nuevo usuario en la base de datos
     */
    public boolean crearUsuario(Usuario usuario) {
        String sqlQuery = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setString(4, usuario.getRol());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Obtiene todos los datos de un usuario por su username
     * Se usa después del login exitoso para cargar información completa en SessionManager
     * @param username Nombre de usuario
     * @return Usuario con todos sus datos o null si no se encuentra
     */
    public static Usuario obtenerUsuarioCompleto(String username) {
        String sqlQuery = "SELECT id_usuario, username, nombre_completo, rol FROM usuarios WHERE username = ?";
        
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setRol(rs.getString("rol"));
                    

                    return usuario;
                } else {
                    System.err.println("Usuario no encontrado: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario completo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    /**
     * Verifica si un username ya existe en la base de datos
     */
    public boolean existeUsername(String username) {
        String sqlQuery = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        
        Connection conn = ConexionMysql.getConexion(); // Conexión global, NO usar try-with-resources
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el conteo es > 0, el username ya existe
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si existe el username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Migra automáticamente una contraseña de texto plano a hash
     * Se ejecuta automáticamente cuando un usuario con contraseña en texto plano se conecta exitosamente
     */
    private static void migrarPasswordAHash(String username, String passwordTextoPlano) {
        String sqlQuery = "UPDATE usuarios SET password = ? WHERE username = ?";
        
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            // Generar hash de la contraseña
            String hashedPassword = PasswordHasher.hashPassword(passwordTextoPlano);
            
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username);
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Contraseña migrada exitosamente a hash para usuario: " + username);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al migrar contraseña a hash para usuario " + username + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al generar hash durante migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
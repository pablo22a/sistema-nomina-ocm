package util;

import models.Usuario;
import conexion.ConexionMysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Gestor de sesión centralizado para manejar el usuario actual y sus permisos
 * @author Pablo
 */
public class SessionManager {
    
    private static Usuario usuarioActual = null;
    private static String rolActual = null;
    private static String nombreUsuario = null;
    private static String username = null;
    
    /**
     * Inicia una sesión con los datos del usuario autenticado
     * @param usuario Usuario autenticado con todos sus datos
     */
    public static void iniciarSesion(Usuario usuario) {
        if (usuario != null) {
            usuarioActual = usuario;
            rolActual = usuario.getRol();
            nombreUsuario = usuario.getNombreCompleto();
            username = usuario.getUsername();
            
            // Establecer variable de sesión en MySQL para los triggers
            establecerUsuarioEnMySQL(username);
            

        }
    }
    
    /**
     * Cierra la sesión actual, limpiando todos los datos
     */
    public static void cerrarSesion() {

        
        // Limpiar variable de sesión en MySQL
        limpiarUsuarioEnMySQL();
        
        usuarioActual = null;
        rolActual = null;
        nombreUsuario = null;
        username = null;
    }
    
    /**
     * Verifica si el usuario actual es administrador
     * @return true si es admin, false en caso contrario
     */
    public static boolean esAdmin() {
        return "admin".equalsIgnoreCase(rolActual);
    }
    
    /**
     * Verifica si el usuario actual es capturista
     * @return true si es capturista, false en caso contrario
     */
    public static boolean esCapturista() {
        return "capturista".equalsIgnoreCase(rolActual);
    }
    
    /**
     * Obtiene el rol actual del usuario
     * @return rol del usuario o null si no hay sesión activa
     */
    public static String getRol() {
        return rolActual;
    }
    
    /**
     * Obtiene el nombre completo del usuario actual
     * @return nombre completo del usuario o "Usuario" si no hay sesión activa
     */
    public static String getNombreUsuario() {
        return nombreUsuario != null ? nombreUsuario : "Usuario";
    }
    
    /**
     * Obtiene el username del usuario actual
     * @return username del usuario o null si no hay sesión activa
     */
    public static String getUsername() {
        return username;
    }
    
    /**
     * Obtiene el usuario completo actual
     * @return objeto Usuario completo o null si no hay sesión activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Verifica si hay una sesión activa
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null && rolActual != null;
    }
    
    /**
     * Verifica si el usuario tiene permisos para acceder a funcionalidades de admin
     * @return true si puede acceder a panel de administración, false en caso contrario
     */
    public static boolean puedeAccederAdminSistema() {
        return esAdmin();
    }
    
    /**
     * Verifica si el usuario puede ver el panel de prestaciones
     * @return true si puede ver prestaciones (capturista o admin), false en caso contrario
     */
    public static boolean puedeVerPrestaciones() {
        return esCapturista() || esAdmin();
    }
    
    /**
     * Obtiene información de la sesión para debugging
     * @return String con información de la sesión actual
     */
    public static String getInfoSesion() {
        if (!haySesionActiva()) {
            return "No hay sesión activa";
        }
        
        return String.format("Usuario: %s (%s) - Rol: %s", 
                nombreUsuario, username, rolActual);
    }
    
    /**
     * Establece la variable de sesión de MySQL para que los triggers usen el usuario correcto
     * @param usuario Nombre del usuario de la aplicación
     */
    private static void establecerUsuarioEnMySQL(String usuario) {
        String sql = "SET @usuario_aplicacion = ?";
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al establecer usuario de sesión en MySQL: " + e.getMessage());
        }
    }
    
    /**
     * Limpia la variable de sesión de MySQL
     */
    private static void limpiarUsuarioEnMySQL() {
        String sql = "SET @usuario_aplicacion = NULL";
        Connection conn = ConexionMysql.getConexion();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al limpiar usuario de sesión en MySQL: " + e.getMessage());
        }
    }
}
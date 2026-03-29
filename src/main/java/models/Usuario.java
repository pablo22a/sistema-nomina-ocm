package models;

/**
 * Modelo para representar un usuario del sistema
 * @author Pablo
 */
public class Usuario {
    
    private int idUsuario;
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol; // "admin" o "capturista"
    
    // Constructor vacío
    public Usuario() {
    }
    
    // Constructor para crear usuario
    public Usuario(String username, String password, String nombreCompleto, String rol) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
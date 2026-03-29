package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConexionMysql {
    
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    
    private static Connection connection;
    
    // Cargar configuración al inicializar la clase
    static {
        cargarConfiguracion();
    }

    /**
     * Carga la configuración desde el archivo config.properties
     */
    private static void cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream input = ConexionMysql.class.getResourceAsStream("/config/config.properties")) {
            if (input == null) {
                // Si no existe config.properties, usar valores por defecto
                URL = "jdbc:mysql://localhost:3306/ocm_dump";
                USER = "root";
                PASSWORD = "password";

                return;
            }
            
            props.load(input);
            URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/ocm_nomina");
            USER = props.getProperty("db.user", "root");
            PASSWORD = props.getProperty("db.password", "");
            

            
        } catch (IOException e) {
            // En caso de error, usar valores por defecto
            URL = "jdbc:mysql://localhost:3306/ocm_nomina";
            USER = "root";
            PASSWORD = "";

        }
    }

    public static Connection getConexion() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

            } else {

            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void cerrarConexion() {
        try {
            if (connection != null) {
                if (!connection.isClosed()) {
                    connection.close();
                    JOptionPane.showMessageDialog(null, "Conexión cerrada exitosamente", "Conexión cerrada", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "La conexión ya estaba cerrada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
                connection = null; // Limpia la referencia
            } else {
                JOptionPane.showMessageDialog(null, "No hay conexión activa para cerrar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error cerrar conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
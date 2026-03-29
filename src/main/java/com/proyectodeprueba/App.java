package com.proyectodeprueba;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import com.proyectodeprueba.ui.MainFrame;
import conexion.ConexionMysql; 
import java.sql.Connection;    
import javax.swing.JOptionPane;

public class App {
     
    public static void main(String[] args) {
        
       
        Connection conn = ConexionMysql.getConexion();
   
        if (conn == null) {
            // Si la conexión es nula, significa que falló.
            JOptionPane.showMessageDialog(null, 
                "No se pudo conectar a la base de datos.\nVerifique que el servicio de MySQL esté activo y los datos de conexión sean correctos.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            
            // Cerramos la aplicación ya que no puede funcionar sin base de datos.
            System.exit(0); 
            
        } else {
            // Si la conexión fue exitosa, entonces procedemos a crear la interfaz gráfica.
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialized LaF");
            }
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            });
        }
    }
}
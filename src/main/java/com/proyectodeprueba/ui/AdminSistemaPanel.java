package com.proyectodeprueba.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.proyectodeprueba.ui.subventanas.confirmacion.ConfirmacionDialog;
import DAO.UsuarioDAO;
import models.Usuario;
import util.PasswordHasher;
import util.SessionManager;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

/**
 *
 * @author andrisharaizaespinoza
 */
public class AdminSistemaPanel extends javax.swing.JPanel {
 
         private MainFrame parent;

    /**
     * Creates new form AdminSistemaPanel
     */
    public AdminSistemaPanel(MainFrame parent) {
        initComponents();
        
        this.parent = parent;
        
        // Redondear esquinas txts
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtPasswordConfirm.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtNombre.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtRol.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        
        // Placeholder txts
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Usuario");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contraseña");
        txtPasswordConfirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirmar contraseña");
        txtNombre.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Juan Pérez García");
        txtRol.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "admin o capturista");
        
        // Estilos del botón
        btnCrear.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        btnCrear.putClientProperty(FlatClientProperties.BUTTON_TYPE, "roundRect");btnLogs.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAsistencias.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEmpleados.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnNomina.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAdmin.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        // NUEVO: Configurar interface según rol del usuario
        configurarInterfazPorRol();
    }
    
    /**
     * Carga la configuración de la base de datos desde config.properties
     */
    private Properties cargarConfiguracionDB() {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config/config.properties")) {
            if (input == null) {

                return props;
            }
            props.load(input);

        } catch (IOException e) {

        }
        return props;
    }
    
    /**
     * Extrae el nombre de la base de datos de una URL JDBC
     */
    private String extraerNombreDB(String jdbcUrl) {
        try {
            // Formato: jdbc:mysql://host:port/database?params
            String[] parts = jdbcUrl.split("/");
            if (parts.length >= 4) {
                String dbPart = parts[3];
                // Remover parámetros si existen (después de ?)
                int questionMarkIndex = dbPart.indexOf('?');
                if (questionMarkIndex > 0) {
                    return dbPart.substring(0, questionMarkIndex);
                }
                return dbPart;
            }
        } catch (Exception e) {

        }
        return "ocm_nomina"; // fallback por defecto
        
        
    }
    
    /**
     * Configura la interfaz según el rol del usuario actual
     * AdminSistemaPanel tiene protección especial - solo admins pueden estar aquí
     */
    private void configurarInterfazPorRol() {
        // Actualizar nombre del usuario en lblUser si existe
        if (SessionManager.haySesionActiva()) {
            // lblUser puede no existir en AdminSistemaPanel, verificar primero
            try {
                lblUser.setText("Usuario: " + SessionManager.getNombreUsuario());
            } catch (Exception e) {
                // lblUser no existe en este panel, ignorar
            }
        }
        
        // Verificar que solo admins puedan estar en este panel
        if (!SessionManager.esAdmin() && SessionManager.haySesionActiva()) {
            // Si no es admin, redirigir a home
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Acceso denegado. Solo los administradores pueden acceder a esta sección.", 
                    "Acceso Restringido", 
                    JOptionPane.WARNING_MESSAGE);
                parent.showPanel("home");
            });
        }
        
        // El botón Admin siempre dice "Admin Sistema" en este panel (ya que solo admins pueden estar aquí)
        if (btnAdmin != null) {
            btnAdmin.setText("Admin Sistema");
            btnAdmin.setVisible(true);
        }
    }
    
    /**
     * Método público para reconfigurar la interfaz cuando el panel se activa
     */
    public void actualizarInterfaz() {
        configurarInterfazPorRol();
    }
    
    private void generarRespaldo(String host, String port, String user, String password, String dbName, String mysqldumpPath) {
        // 1. Abre un diálogo para que el usuario elija dónde guardar el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Respaldo SQL");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos SQL (*.sql)", "sql"));
        // Sugiere un nombre de archivo con la fecha
        fileChooser.setSelectedFile(new File("respaldo_" + dbName + "_" + java.time.LocalDate.now() + ".sql"));


        int userSelection = fileChooser.showSaveDialog(this);


        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();


            if (!filePath.toLowerCase().endsWith(".sql")) {
                filePath += ".sql";
            }


            ProcessBuilder pb = new ProcessBuilder(
                    mysqldumpPath,
                    "--host=" + host,
                    "--port=" + port,
                    "--user=" + user,
                    "--password=" + password, // Pasa la contraseña
                    "--routines", // Incluye procedimientos y funciones
                    "--result-file=" + filePath, // Guarda directamente en el archivo
                    dbName); // La base de datos a respaldar

            pb.redirectErrorStream(true); // Combina la salida normal y la de errores

            try {
                // 4. Ejecuta el comando
                Process process = pb.start();

                // 5. Lee la salida del comando (útil para ver errores)
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // 6. Espera a que el comando termine y verifica el código de salida
                int exitCode = process.waitFor();

                // 7. Muestra un mensaje al usuario
                if (exitCode == 0) { // 0 significa éxito
                    JOptionPane.showMessageDialog(this, "Respaldo generado exitosamente en:\n" + filePath, "Respaldo Completo", JOptionPane.INFORMATION_MESSAGE);
                } else { // Cualquier otro código indica un error
                    JOptionPane.showMessageDialog(this, "Error al generar respaldo (código: " + exitCode + ").\nConsola:\n" + output.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | InterruptedException e) {
                // Error si no se pudo ejecutar el comando (ej. mysqldump no encontrado)
                JOptionPane.showMessageDialog(this, "Error al ejecutar mysqldump:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void restaurarRespaldo(String host, String port, String user, String password, String dbName, String mysqlPath) {
        // 1. Abre un diálogo para que el usuario elija el archivo .sql a restaurar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Respaldo SQL para Restaurar");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos SQL (*.sql)", "sql"));

        int userSelection = fileChooser.showOpenDialog(this);

        // 2. Si el usuario elige un archivo y hace clic en "Abrir"
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRestore = fileChooser.getSelectedFile();
            String filePath = fileToRestore.getAbsolutePath();

            // 3. Construye el comando para ejecutar mysql
            //    Asegúrate de que 'mysqlPath' sea correcto o esté en el PATH
            ProcessBuilder pb = new ProcessBuilder(
                    mysqlPath,
                    "--host=" + host,
                    "--port=" + port,
                    "--user=" + user,
                    "--password=" + password, // Pasa la contraseña
                    dbName); // La base de datos donde se restaurará

            // 4. Redirige el contenido del archivo .sql como entrada para el comando mysql
            pb.redirectInput(ProcessBuilder.Redirect.from(fileToRestore));
            pb.redirectErrorStream(true); // Combina salida normal y de errores

            try {
                // 5. Ejecuta el comando
                Process process = pb.start();

                // 6. Lee la salida del comando (útil para ver errores)
                 StringBuilder output = new StringBuilder();
                 try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // 7. Espera a que termine y verifica el código de salida
                int exitCode = process.waitFor();

                // 8. Muestra un mensaje al usuario
                if (exitCode == 0) { // 0 significa éxito
                     JOptionPane.showMessageDialog(this, "Respaldo restaurado exitosamente desde:\n" + filePath, "Restauración Completa", JOptionPane.INFORMATION_MESSAGE);
                     // Podrías necesitar actualizar alguna tabla o vista aquí
                } else { // Cualquier otro código indica un error
                     JOptionPane.showMessageDialog(this, "Error al restaurar (código: " + exitCode + ").\nConsola:\n" + output.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | InterruptedException e) {
                // Error si no se pudo ejecutar el comando (ej. mysql no encontrado)
                JOptionPane.showMessageDialog(this, "Error al ejecutar mysql:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    // En AdminSistemaPanel.java



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        topBar = new javax.swing.JPanel();
        userLogo = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        btnLogs = new javax.swing.JButton();
        btnAsistencias = new javax.swing.JButton();
        btnEmpleados = new javax.swing.JButton();
        btnNomina = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPasswordConfirm = new javax.swing.JPasswordField();
        btnCrear = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnGenerar = new javax.swing.JButton();
        btnRestaurar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRol = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(1200, 600));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 600));

        topBar.setBackground(new java.awt.Color(55, 55, 55));
        topBar.setRequestFocusEnabled(false);
        topBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        userLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/person.png"))); // NOI18N
        topBar.add(userLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 30));

        lblUser.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUser.setForeground(new java.awt.Color(255, 255, 255));
        lblUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUser.setText("Usuario: Adiministrador");
        topBar.add(lblUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 150, 30));

        btnLogs.setBackground(new java.awt.Color(228, 68, 28));
        btnLogs.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnLogs.setForeground(new java.awt.Color(255, 255, 255));
        btnLogs.setText("Logs");
        btnLogs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogsActionPerformed(evt);
            }
        });
        topBar.add(btnLogs, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, 160, 50));

        btnAsistencias.setBackground(new java.awt.Color(228, 68, 28));
        btnAsistencias.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnAsistencias.setForeground(new java.awt.Color(255, 255, 255));
        btnAsistencias.setText("Asistencia");
        btnAsistencias.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAsistencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsistenciasActionPerformed(evt);
            }
        });
        topBar.add(btnAsistencias, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 160, 50));

        btnEmpleados.setBackground(new java.awt.Color(228, 68, 28));
        btnEmpleados.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnEmpleados.setForeground(new java.awt.Color(255, 255, 255));
        btnEmpleados.setText("Empleados");
        btnEmpleados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpleadosActionPerformed(evt);
            }
        });
        topBar.add(btnEmpleados, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 150, 50));

        btnNomina.setBackground(new java.awt.Color(228, 68, 28));
        btnNomina.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnNomina.setForeground(new java.awt.Color(255, 255, 255));
        btnNomina.setText("Nomina");
        btnNomina.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNomina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNominaActionPerformed(evt);
            }
        });
        topBar.add(btnNomina, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 30, 160, 50));

        btnAdmin.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnAdmin.setForeground(new java.awt.Color(228, 68, 28));
        btnAdmin.setText("Admin Sistemas");
        btnAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        topBar.add(btnAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 30, 150, 50));

        jButton1.setBackground(new java.awt.Color(55, 55, 55));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/home.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        topBar.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Generar Respaldo");

        txtUsername.setBackground(new java.awt.Color(204, 204, 204));
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtPasswordConfirm.setBackground(new java.awt.Color(204, 204, 204));
        txtPasswordConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordConfirmActionPerformed(evt);
            }
        });

        btnCrear.setBackground(new java.awt.Color(55, 55, 55));
        btnCrear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCrear.setForeground(new java.awt.Color(255, 255, 255));
        btnCrear.setText("Crear");
        btnCrear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCrear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCrearMouseClicked(evt);
            }
        });
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

        txtPassword.setBackground(new java.awt.Color(204, 204, 204));
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Restaurar Respaldo");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Crear Usuario");

        btnGenerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/document.png"))); // NOI18N
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        btnRestaurar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arrow-up-circle.png"))); // NOI18N
        btnRestaurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestaurarActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(55, 55, 55));
        jPanel2.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Usuario:");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Contraseña:");

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Confirmar contraseña:");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Nombre completo:");

        txtNombre.setBackground(new java.awt.Color(204, 204, 204));
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Rol:");

        txtRol.setBackground(new java.awt.Color(204, 204, 204));
        txtRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRolActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(271, 271, 271)
                        .addComponent(btnRestaurar))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(97, 97, 97)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(272, 272, 272)
                            .addComponent(btnGenerar))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(txtPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtRol, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(234, 234, 234))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(93, 93, 93))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRol, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGenerar)
                                .addGap(27, 27, 27)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRestaurar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPasswordConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                        .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAsistenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsistenciasActionPerformed
        // TODO add your handling code here:
        parent.showPanel("asistencias");
    }//GEN-LAST:event_btnAsistenciasActionPerformed

    private void btnNominaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNominaActionPerformed
        // TODO add your handling code here:
        parent.showPanel("nomina");
    }//GEN-LAST:event_btnNominaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        parent.showPanel("home");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpleadosActionPerformed
        // TODO add your handling code here:
        parent.showPanel("empleados");
    }//GEN-LAST:event_btnEmpleadosActionPerformed

    private void btnCrearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCrearMouseClicked
        // TODO add your handling code here:
       
        //ConfirmacionDialog confirmacion = new ConfirmacionDialog((Frame) parent.getParent(), true);
        //confirmacion.mostrar();

        
    }//GEN-LAST:event_btnCrearMouseClicked

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
        // Obtener datos del formulario
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String passwordConfirm = new String(txtPasswordConfirm.getPassword());
        String nombreCompleto = txtNombre.getText().trim(); // Campo para nombre completo
        String rol = txtRol.getText().trim(); // Campo para rol
        
        // Validaciones básicas
        if (username.isEmpty() || password.isEmpty() || nombreCompleto.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos los campos son obligatorios:\n• Usuario\n• Contraseña\n• Nombre Completo\n• Rol", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar que el rol sea válido
        if (!rol.equalsIgnoreCase("admin") && !rol.equalsIgnoreCase("capturista")) {
            JOptionPane.showMessageDialog(this, 
                "El rol debe ser 'admin' o 'capturista'", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "La contraseña debe tener al menos 6 caracteres.", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si el username ya existe
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.existeUsername(username)) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // HASHEAR LA CONTRASEÑA ANTES DE GUARDAR
            String hashedPassword = PasswordHasher.hashPassword(password);
            
            // Crear usuario con contraseña hasheada
            Usuario nuevoUsuario = new Usuario(username, hashedPassword, nombreCompleto, rol.toLowerCase());
            
            if (dao.crearUsuario(nuevoUsuario)) {
                JOptionPane.showMessageDialog(this, 
                    "Usuario creado exitosamente.\n\n" +
                    "Detalles del usuario:\n" +
                    "• Usuario: " + username + "\n" +
                    "• Nombre: " + nombreCompleto + "\n" +
                    "• Rol: " + rol.toLowerCase(), 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar formulario
                txtUsername.setText("");
                txtPassword.setText("");
                txtPasswordConfirm.setText("");
                txtNombre.setText(""); // Limpiar nombre completo
                txtRol.setText(""); // Limpiar rol
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado al crear usuario: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCrearActionPerformed

    private void btnLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogsActionPerformed
        // TODO add your handling code here:
        parent.showPanel("logs");
    }//GEN-LAST:event_btnLogsActionPerformed

    private void txtPasswordConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordConfirmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordConfirmActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        // Cargar configuración desde config.properties
        Properties dbConfig = cargarConfiguracionDB();
        
        // Extraer valores de la configuración
        String dbHost = dbConfig.getProperty("db.host", "localhost");
        String dbPort = dbConfig.getProperty("db.port", "3306");
        String dbUser = dbConfig.getProperty("db.user", "root");
        String dbPassword = dbConfig.getProperty("db.password", "");
        
        // Extraer nombre de base de datos de la URL
        String dbUrl = dbConfig.getProperty("db.url", "jdbc:mysql://localhost:3306/ocm_nomina");
        String dbName = extraerNombreDB(dbUrl);
        
 

        // Detectar sistema operativo y configurar ruta de mysqldump
        String mysqldumpPath = getMysqldumpPath();
        if (mysqldumpPath == null) {
            JOptionPane.showMessageDialog(this, 
                "No se pudo encontrar mysqldump en el sistema.\n" +
                "Asegúrate de que MySQL esté instalado correctamente.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --------------------------------------------------

        // Llama directamente a la función de generar respaldo
        generarRespaldo(dbHost, dbPort, dbUser, dbPassword, dbName, mysqldumpPath);
    }//GEN-LAST:event_btnGenerarActionPerformed

    private void btnRestaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestaurarActionPerformed
        // Cargar configuración desde config.properties
        Properties dbConfig = cargarConfiguracionDB();
        
        // Extraer valores de la configuración
        String dbHost = dbConfig.getProperty("db.host", "localhost");
        String dbPort = dbConfig.getProperty("db.port", "3306");
        String dbUser = dbConfig.getProperty("db.user", "root");
        String dbPassword = dbConfig.getProperty("db.password", "");
        
        // Extraer nombre de base de datos de la URL
        String dbUrl = dbConfig.getProperty("db.url", "jdbc:mysql://localhost:3306/ocm_nomina");
        String dbName = extraerNombreDB(dbUrl);
        
 

        // Detectar sistema operativo y configurar ruta de mysql
        String mysqlPath = getMysqlPath();
        if (mysqlPath == null) {
            JOptionPane.showMessageDialog(this, 
                "No se pudo encontrar mysql en el sistema.\n" +
                "Asegúrate de que MySQL esté instalado correctamente.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --------------------------------------------------

        // Llama directamente a la función de restaurar respaldo
        restaurarRespaldo(dbHost, dbPort, dbUser, dbPassword, dbName, mysqlPath);

    }//GEN-LAST:event_btnRestaurarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRolActionPerformed

    /**
     * Detecta automáticamente la ruta de mysqldump según el sistema operativo
     */
    private String getMysqldumpPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] possiblePaths;
        
        if (os.contains("win")) {
            // Rutas comunes en Windows
            possiblePaths = new String[]{
                "mysqldump", // Si está en PATH
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqldump.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.4\\bin\\mysqldump.exe",
                "C:\\xampp\\mysql\\bin\\mysqldump.exe",
                "C:\\wamp64\\bin\\mysql\\mysql8.0.31\\bin\\mysqldump.exe"
            };
        } else if (os.contains("mac")) {
            // Rutas comunes en macOS
            possiblePaths = new String[]{
                "mysqldump", // Si está en PATH
                "/opt/homebrew/bin/mysqldump", // Homebrew Apple Silicon
                "/usr/local/bin/mysqldump", // Homebrew Intel
                "/usr/local/mysql/bin/mysqldump", // MySQL oficial
                "/Applications/XAMPP/xamppfiles/bin/mysqldump"
            };
        } else {
            // Linux y otros Unix
            possiblePaths = new String[]{
                "mysqldump", // Si está en PATH
                "/usr/bin/mysqldump",
                "/usr/local/bin/mysqldump",
                "/opt/lampp/bin/mysqldump"
            };
        }
        
        return findExecutable(possiblePaths);
    }
    
    /**
     * Detecta automáticamente la ruta de mysql según el sistema operativo
     */
    private String getMysqlPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] possiblePaths;
        
        if (os.contains("win")) {
            // Rutas comunes en Windows
            possiblePaths = new String[]{
                "mysql", // Si está en PATH
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysql.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.4\\bin\\mysql.exe",
                "C:\\xampp\\mysql\\bin\\mysql.exe",
                "C:\\wamp64\\bin\\mysql\\mysql8.0.31\\bin\\mysql.exe"
            };
        } else if (os.contains("mac")) {
            // Rutas comunes en macOS
            possiblePaths = new String[]{
                "mysql", // Si está en PATH
                "/opt/homebrew/bin/mysql", // Homebrew Apple Silicon
                "/usr/local/bin/mysql", // Homebrew Intel
                "/usr/local/mysql/bin/mysql", // MySQL oficial
                "/Applications/XAMPP/xamppfiles/bin/mysql"
            };
        } else {
            // Linux y otros Unix
            possiblePaths = new String[]{
                "mysql", // Si está en PATH
                "/usr/bin/mysql",
                "/usr/local/bin/mysql",
                "/opt/lampp/bin/mysql"
            };
        }
        
        return findExecutable(possiblePaths);
    }
    
    /**
     * Busca el primer ejecutable que existe en las rutas proporcionadas
     */
    private String findExecutable(String[] paths) {
        for (String path : paths) {
            try {
                // Intenta ejecutar el comando con --version para verificar que funciona
                ProcessBuilder pb = new ProcessBuilder(path, "--version");
                Process process = pb.start();
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {

                    return path;
                }
            } catch (Exception e) {
                // Si falla, continúa con la siguiente ruta
                continue;
            }
        }
        

        return null;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnAsistencias;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnLogs;
    private javax.swing.JButton btnNomina;
    private javax.swing.JButton btnRestaurar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel topBar;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordConfirm;
    private javax.swing.JTextField txtRol;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JLabel userLogo;
    // End of variables declaration//GEN-END:variables
}

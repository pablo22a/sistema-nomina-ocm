/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.proyectodeprueba.ui;

import DAO.AsistenciaDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.proyectodeprueba.ui.subventanas.AgregarAsistenciaPanel;
import util.SessionManager;
import java.awt.event.ActionListener;
import com.proyectodeprueba.ui.subventanas.AgregarDialog;
import javax.swing.table.JTableHeader;
import DAO.LogDAO;
import models.LogEntry;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter; // Para formatear la fecha

/**
 *
 * @author andrisharaizaespinoza
 */
public class LogsPanel extends javax.swing.JPanel {

    private MainFrame parent;
    /**
     * Creates new form LogsPanel
     */
    public LogsPanel(MainFrame parent) {
        
         initComponents();
        /*
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        */
      
        
        this.parent = parent;
        
        btnLogs.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAsistencias.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEmpleados.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnNomina.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAdmin.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        //btnActualizar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        LogsCB.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
     
        
        JTableHeader header = JTable.getTableHeader();
        header.putClientProperty(FlatClientProperties.STYLE, "background:#373737; foreground:#ffffff; font: bold 12 Segoe UI");
        
        // NUEVO: Configurar interface según rol del usuario
        configurarInterfazPorRol();
        
        cargarDatosIniciales();
    }
    
    /**
     * Configura la interfaz según el rol del usuario actual
     */
    private void configurarInterfazPorRol() {
        // Actualizar nombre del usuario en lblUser
        if (SessionManager.haySesionActiva()) {
            lblUser.setText("Usuario: " + SessionManager.getNombreUsuario());
        }
        
        // Configurar botón según rol
        if (SessionManager.esAdmin()) {
            btnAdmin.setText("Admin Sistema");
            btnAdmin.setVisible(true);
            
            // Quitar listeners anteriores
            for (ActionListener listener : btnAdmin.getActionListeners()) {
                btnAdmin.removeActionListener(listener);
            }
            
            // Acción para ir a admin sistema
            btnAdmin.addActionListener(e -> parent.showPanel("adminsistema"));
        } else if (SessionManager.esCapturista()) {
            btnAdmin.setText("Prestaciones");
            btnAdmin.setVisible(true);
            
            for (ActionListener listener : btnAdmin.getActionListeners()) {
                btnAdmin.removeActionListener(listener);
            }
            btnAdmin.addActionListener(e -> parent.showPanel("prestaciones"));
        } else {
            btnAdmin.setVisible(false);
        }
    }
    
    /**
     * Método público para reconfigurar la interfaz cuando el panel se activa
     */
    public void actualizarInterfaz() {
        configurarInterfazPorRol();
    }
    
    
    private void cargarDatosIniciales() {
    // 1. Obtiene el texto del elemento seleccionado en el ComboBox
    String tipoLogSeleccionado = (String) LogsCB.getSelectedItem();

    // 2. Llama a tu clase DAO para traer los datos de la base de datos
    LogDAO logDAO = new LogDAO();
    List<LogEntry> logs = logDAO.obtenerLogs(tipoLogSeleccionado);

    // 3. Muestra esos datos en la tabla
    cargarDatosEnTabla(logs);
}
    private void cargarDatosEnTabla(List<LogEntry> logs) {
        DefaultTableModel model = (DefaultTableModel) JTable.getModel();
        model.setRowCount(0);

        // Formateador para mostrar la fecha de una manera más legible
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (LogEntry log : logs) {
            /*Object[] row = new Object[5];
            row[0] = log.getId();
            row[1] = log.getAccion();
            row[2] = log.getDetalle();
            row[3] = log.getUsuario();
            row[4] = log.getFecha().format(formatter); // Usamos el formateador
            model.addRow(row);*/
            model.addRow(new Object[]{
            log.getId(),
            log.getAccion(),
            log.getDetalle(),
            log.getUsuario(),
            log.getFecha().format(formatter)
        });
        }
    }

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
        jScrollPane1 = new javax.swing.JScrollPane();
        JTable = new javax.swing.JTable();
        LogsCB = new javax.swing.JComboBox<>();

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
        lblUser.setText("Usuario: Administrador");
        topBar.add(lblUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 150, 30));

        btnLogs.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnLogs.setForeground(new java.awt.Color(228, 68, 28));
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

        btnAdmin.setBackground(new java.awt.Color(228, 68, 28));
        btnAdmin.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnAdmin.setForeground(new java.awt.Color(255, 255, 255));
        btnAdmin.setText("Admin Sistemas");
        btnAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdminActionPerformed(evt);
            }
        });
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

        JTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Acción", "Detalle", "Usuario", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        JTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(JTable);

        LogsCB.setBackground(new java.awt.Color(55, 55, 55));
        LogsCB.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        LogsCB.setForeground(new java.awt.Color(255, 255, 255));
        LogsCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Log Asistencias", "Log Departamentos", "Log Empleados", "Log Nominas", "Log Prestaciones", "Log Puestos" }));
        LogsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogsCBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LogsCB, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1076, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(LogsCB, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogsActionPerformed
        // TODO add your handling code here:
        parent.showPanel("logs");
    }//GEN-LAST:event_btnLogsActionPerformed

    private void btnAsistenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsistenciasActionPerformed
        // TODO add your handling code here:
        parent.showPanel("asistencias");
    }//GEN-LAST:event_btnAsistenciasActionPerformed

    private void btnEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpleadosActionPerformed
        // TODO add your handling code here:
        parent.showPanel("empleados");
    }//GEN-LAST:event_btnEmpleadosActionPerformed

    private void btnNominaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNominaActionPerformed
        // TODO add your handling code here:
        parent.showPanel("nomina");
    }//GEN-LAST:event_btnNominaActionPerformed

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
        // TODO add your handling code here:
        parent.showPanel("adminsistema");
    }//GEN-LAST:event_btnAdminActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        parent.showPanel("home");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void LogsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogsCBActionPerformed
        // TODO add your handling code here:
        String tipoLogSeleccionado = (String) LogsCB.getSelectedItem();
        LogDAO logDAO = new LogDAO();
        List<LogEntry> logs = logDAO.obtenerLogs(tipoLogSeleccionado);
        cargarDatosEnTabla(logs);
    }//GEN-LAST:event_LogsCBActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTable;
    private javax.swing.JComboBox<String> LogsCB;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnAsistencias;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnLogs;
    private javax.swing.JButton btnNomina;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel topBar;
    private javax.swing.JLabel userLogo;
    // End of variables declaration//GEN-END:variables
}

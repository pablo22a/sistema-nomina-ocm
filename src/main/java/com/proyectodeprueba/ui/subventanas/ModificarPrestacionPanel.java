
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.proyectodeprueba.ui.subventanas;

import DAO.AsistenciaDAO;
import DAO.PrestacionDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.proyectodeprueba.ui.subventanas.confirmacion.ConfirmacionDialog;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import models.Asistencia;
import models.Prestacion;


public class ModificarPrestacionPanel extends javax.swing.JPanel {
    
    private ModificarDialog parent;

    private ModificarDialog parentDialog;
    private Prestacion prestacionActual; // Guardará la info de la asistencia a editar
    private Runnable onUpdateSuccess;
    
    public ModificarPrestacionPanel(ModificarDialog parentDialog, Prestacion prestacion, Runnable onUpdate) {
        initComponents();
        this.parentDialog = parentDialog;
        this.prestacionActual = prestacion;
        this.onUpdateSuccess = onUpdate;
        
        // --- LÓGICA PARA RELLENAR LOS CAMPOS ---
        txtIdPrest.setText(String.valueOf(prestacion.getIdPrestacion()));
        txtIdEmpl.setText(String.valueOf(prestacion.getIdEmpleado()));
        txtTipo.setText(prestacion.getTipoPrestacion());
        txtMonto.setText(String.valueOf(prestacion.getMonto()));
        txtDescripcion.setText(prestacion.getDescripcion());
        
        // Hacemos que los IDs no se puedan cambiar
        txtIdPrest.setEditable(false);
        txtIdEmpl.setEditable(false);
        
        // Estilos de los componentes (tu código original)
        txtIdPrest.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtIdEmpl.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtTipo.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtMonto.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtDescripcion.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        btnMod.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        btnMod.putClientProperty(FlatClientProperties.BUTTON_TYPE, "roundRect");
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtIdPrest = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtIdEmpl = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTipo = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        btnMod = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(300, 550));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(55, 55, 55));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Modificar Prestación");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridLayout(6, 1));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Id prestación:");
        jLabel2.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel3.add(jLabel2);

        txtIdPrest.setBackground(new java.awt.Color(216, 216, 216));
        txtIdPrest.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel3.add(txtIdPrest);

        jPanel2.add(jPanel3);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Id empleado:");
        jLabel6.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel9.add(jLabel6);

        txtIdEmpl.setBackground(new java.awt.Color(216, 216, 216));
        txtIdEmpl.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel9.add(txtIdEmpl);

        jPanel2.add(jPanel9);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Tipo de prestación:");
        jLabel3.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel4.add(jLabel3);

        txtTipo.setBackground(new java.awt.Color(216, 216, 216));
        txtTipo.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel4.add(txtTipo);

        jPanel2.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Monto:");
        jLabel4.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel5.add(jLabel4);

        txtMonto.setBackground(new java.awt.Color(216, 216, 216));
        txtMonto.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel5.add(txtMonto);

        jPanel2.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Descripción:");
        jLabel5.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel6.add(jLabel5);

        txtDescripcion.setBackground(new java.awt.Color(216, 216, 216));
        txtDescripcion.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel6.add(txtDescripcion);

        jPanel2.add(jPanel6);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        btnMod.setBackground(new java.awt.Color(55, 55, 55));
        btnMod.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMod.setForeground(new java.awt.Color(255, 255, 255));
        btnMod.setText("Modificar");
        btnMod.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMod.setPreferredSize(new java.awt.Dimension(100, 35));
        btnMod.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnModMouseClicked(evt);
            }
        });
        jPanel8.add(btnMod);

        jPanel2.add(jPanel8);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnModMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnModMouseClicked
        // TODO add your handling code here:
        int respuesta = javax.swing.JOptionPane.showConfirmDialog(
        this, 
        "¿Deseas guardar los cambios realizados?", 
        "Confirmar Modificación", 
        javax.swing.JOptionPane.YES_NO_OPTION
    );

    // 2. VERIFICA SI EL USUARIO PRESIONÓ "SÍ"
    if (respuesta == JOptionPane.YES_OPTION) {
        // --- Si confirma, ejecuta toda la lógica para guardar ---
        try {
            // Lee los nuevos datos de los campos de texto
            String nuevoTipoPrest = txtTipo.getText().trim();
            java.math.BigDecimal nuevoMonto = new java.math.BigDecimal(txtMonto.getText().trim());
            String nuevaDescripcion = txtDescripcion.getText().trim();
            
            // Actualiza el objeto 'prestacionActual'
            prestacionActual.setTipoPrestacion(nuevoTipoPrest);
            prestacionActual.setMonto(nuevoMonto);
            prestacionActual.setDescripcion(nuevaDescripcion);

            // Llama al DAO para guardar en la base de datos
            PrestacionDAO dao = new PrestacionDAO();
            boolean exito = dao.modificarPrestacion(prestacionActual);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Prestacion modificada correctamente.");
                
                // Avisa a la ventana principal que se actualice
                if (onUpdateSuccess != null) {
                    onUpdateSuccess.run();
                }
                
                // Cierra la subventana de modificar
                parentDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar la prestacion.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
        
        
    }//GEN-LAST:event_btnModMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtIdEmpl;
    private javax.swing.JTextField txtIdPrest;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtTipo;
    // End of variables declaration//GEN-END:variables
}

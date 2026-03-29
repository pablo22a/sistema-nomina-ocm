/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.proyectodeprueba.ui.subventanas;

import DAO.DepartamentoDAO;
import DAO.PuestoDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.proyectodeprueba.ui.subventanas.confirmacion.ConfirmacionDialog;
import java.awt.Frame;
import models.Departamento;
import models.Puesto;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author Pablo
 */
public class AgregarEmpleadosPanel extends javax.swing.JPanel {
    
    private AgregarDialog parent;
    private Runnable onUpdateSuccess;

    /**
     * Creates new form AgregarEmpleadosPanel
     */
    public AgregarEmpleadosPanel(AgregarDialog parent, Runnable onUpdate) { // MODIFICAR constructor
        this.parent = parent;
        this.onUpdateSuccess = onUpdate; // AGREGAR esta línea
        initComponents();

        txtNombre.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtPrimerAp.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtSegundoAp.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtSueldoBase.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        cbDepartamento.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        cbPuesto.putClientProperty(FlatClientProperties.STYLE, "arc:15; borderWidth: 0");

        btnAgregar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        btnAgregar.putClientProperty(FlatClientProperties.BUTTON_TYPE, "roundRect");
    }
    
    private boolean validarYGuardarEmpleado() {
        try {
            // Validar campos obligatorios
            String nombre = txtNombre.getText().trim();
            String primerAp = txtPrimerAp.getText().trim();

            if (nombre.isEmpty() || primerAp.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Nombre y Primer Apellido son obligatorios", 
                    "Error de Validación", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (cbDepartamento.getSelectedItem() == null || cbPuesto.getSelectedItem() == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Debe seleccionar Departamento y Puesto", 
                    "Error de Validación", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Mapeo de ComboBoxes String -> IDs de BD
            String departamentoSeleccionado = (String) cbDepartamento.getSelectedItem();
            String puestoSeleccionado = (String) cbPuesto.getSelectedItem();

            // Crear objeto Empleado
            models.Empleado nuevoEmpleado = new models.Empleado();
            nuevoEmpleado.setNombre(nombre);
            nuevoEmpleado.setApellido1(primerAp);
            nuevoEmpleado.setApellido2(txtSegundoAp.getText().trim());

            // Mapear strings a IDs usando métodos auxiliares
            int idDepartamento = mapearDepartamentoStringAId(departamentoSeleccionado);
            int idPuesto = mapearPuestoStringAId(puestoSeleccionado);

            nuevoEmpleado.setIdDepartamento(idDepartamento);
            nuevoEmpleado.setIdPuesto(idPuesto);
            nuevoEmpleado.setFechaIngreso(java.time.LocalDateTime.now());
            nuevoEmpleado.setActivo(true);
            
            // Validar y asignar sueldo base
            String sueldoBaseTexto = txtSueldoBase.getText().trim();
            if (sueldoBaseTexto.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "El sueldo base es obligatorio", 
                    "Error de Validación", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            try {
                java.math.BigDecimal sueldoBase = new java.math.BigDecimal(sueldoBaseTexto);
                if (sueldoBase.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "El sueldo base debe ser mayor a 0", 
                        "Error de Validación", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                nuevoEmpleado.setSueldoBase(sueldoBase);
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "El sueldo base debe ser un número válido", 
                    "Error de Validación", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Guardar en base de datos
            DAO.EmpleadoDAO empleadoDAO = new DAO.EmpleadoDAO();
            boolean exito = empleadoDAO.agregarEmpleado(nuevoEmpleado);

            if (exito) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Empleado agregado correctamente", 
                    "Éxito", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Error al agregar empleado", 
                    "Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    // Reutilizar los mismos métodos auxiliares de ModificarEmpleadosPanel
    private int mapearDepartamentoStringAId(String nombreDepartamento) {
        switch (nombreDepartamento) {
            case "Recursos humanos": return 1;
            case "Ventas": return 2;
            case "Tecnología": return 3;
            case "Finanzas": return 4;
            case "Producción": return 5;
            case "Logística": return 6;
            case "Marketing": return 7;
            case "Compras": return 8;
            case "Atención al cliente": return 9;
            case "Legal": return 10;
            default: return 1;
        }
    }

    private int mapearPuestoStringAId(String nombrePuesto) {
        switch (nombrePuesto) {
            case "albañil": return 1;
            case "Gerente de Ventas": return 2;
            case "Desarrollador de Software": return 3;
            case "Contador": return 4;
            case "Supervisor de Producción": return 5;
            case "Coordinador de Logística": return 6;
            case "Especialista en Marketing Digital": return 7;
            case "Comprador": return 8;
            case "Asesor de Atención al Cliente": return 9;
            case "Abogado Corporativo": return 10;
            case "Especialista en Reclutamiento": return 11;
            default: return 1;
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lblPrimerAp = new javax.swing.JLabel();
        txtPrimerAp = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lblSegundoAp = new javax.swing.JLabel();
        txtSegundoAp = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        lblDepartamento = new javax.swing.JLabel();
        cbDepartamento = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        lblPuesto = new javax.swing.JLabel();
        cbPuesto = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        lblSueldoBase = new javax.swing.JLabel();
        txtSueldoBase = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(300, 600));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(55, 55, 55));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Agregar Empleado");

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

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridLayout(7, 1));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Nombre:");
        jLabel2.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel3.add(jLabel2);

        txtNombre.setBackground(new java.awt.Color(216, 216, 216));
        txtNombre.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel3.add(txtNombre);

        jPanel2.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lblPrimerAp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPrimerAp.setText("Primer Apellido:");
        lblPrimerAp.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel4.add(lblPrimerAp);

        txtPrimerAp.setBackground(new java.awt.Color(216, 216, 216));
        txtPrimerAp.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel4.add(txtPrimerAp);

        jPanel2.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        lblSegundoAp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSegundoAp.setText("Segundo Apellido:");
        lblSegundoAp.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel5.add(lblSegundoAp);

        txtSegundoAp.setBackground(new java.awt.Color(216, 216, 216));
        txtSegundoAp.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel5.add(txtSegundoAp);

        jPanel2.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        lblDepartamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDepartamento.setText("Departamento:");
        lblDepartamento.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel6.add(lblDepartamento);

        cbDepartamento.setBackground(new java.awt.Color(216, 216, 216));
        cbDepartamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recursos humanos", "Ventas", "Tecnología", "Finanzas", "Producción", "Logística", "Marketing", "Compras", "Atención al cliente", "Legal" }));
        cbDepartamento.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel6.add(cbDepartamento);

        jPanel2.add(jPanel6);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        lblPuesto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPuesto.setText("Puesto:");
        lblPuesto.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel7.add(lblPuesto);

        cbPuesto.setBackground(new java.awt.Color(216, 216, 216));
        cbPuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "albañil", "Gerente de Ventas", "Desarrollador de Software", "Contador", "Supervisor de Producción", "Coordinador de Logística", "Especialista en Marketing Digital", "Comprador", "Asesor de Atención al Cliente", "Abogado Corporativo", "Especialista en Reclutamiento" }));
        cbPuesto.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel7.add(cbPuesto);

        jPanel2.add(jPanel7);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        lblSueldoBase.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSueldoBase.setText("Sueldo base:");
        lblSueldoBase.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel9.add(lblSueldoBase);

        txtSueldoBase.setBackground(new java.awt.Color(216, 216, 216));
        txtSueldoBase.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel9.add(txtSueldoBase);

        jPanel2.add(jPanel9);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        btnAgregar.setBackground(new java.awt.Color(55, 55, 55));
        btnAgregar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgregar.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregar.setText("Agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.setPreferredSize(new java.awt.Dimension(80, 35));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        jPanel8.add(btnAgregar);

        jPanel2.add(jPanel8);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Crear la ventana de confirmación personalizada
        ConfirmacionDialog confirmacion = new ConfirmacionDialog((Frame) parent.getParent(), true);

        // Configurar la acción a ejecutar si el usuario confirma
        confirmacion.setOnConfirmar(() -> {
            // AQUÍ va la lógica de validar y guardar (solo si confirma)
            if (validarYGuardarEmpleado()) {
                // Ejecutar callback para actualizar la tabla
                if (onUpdateSuccess != null) {
                    onUpdateSuccess.run();
                }

                // Cerrar el diálogo después de agregar
                parent.dispose();
            }
        });

        // Mostrar el diálogo de confirmación
        confirmacion.mostrar();
    }//GEN-LAST:event_btnAgregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JComboBox<String> cbDepartamento;
    private javax.swing.JComboBox<String> cbPuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblDepartamento;
    private javax.swing.JLabel lblPrimerAp;
    private javax.swing.JLabel lblPuesto;
    private javax.swing.JLabel lblSegundoAp;
    private javax.swing.JLabel lblSueldoBase;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrimerAp;
    private javax.swing.JTextField txtSegundoAp;
    private javax.swing.JTextField txtSueldoBase;
    // End of variables declaration//GEN-END:variables
}

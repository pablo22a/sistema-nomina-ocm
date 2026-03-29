/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.proyectodeprueba.ui.subventanas;

import DAO.EmpleadoDAO;
import DAO.DepartamentoDAO;
import DAO.PuestoDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.proyectodeprueba.ui.subventanas.confirmacion.ConfirmacionDialog;
import java.awt.Frame;
import models.Departamento;
import models.Empleado;
import models.Puesto;

/**
 *
 * @author Pablo
 */
public class ModificarEmpleadosPanel extends javax.swing.JPanel {
    
    private ModificarDialog parent;
    private ModificarDialog parentDialog;
    private int idEmpleado;
    private Runnable onUpdateSuccess;

    
    public ModificarEmpleadosPanel(ModificarDialog parent, int id, String nombre, String apellido1, String apellido2, int idPuesto, int idDepto, boolean activo, java.math.BigDecimal sueldoBase, Runnable onUpdate) {
        initComponents();
        this.parentDialog = parent;
        this.idEmpleado = id; // Guardamos el ID para usarlo después
        this.onUpdateSuccess = onUpdate;

        // Rellenar los campos con los datos recibidos
        txtIdEmpleado.setText(String.valueOf(id));
        txtNombre.setText(nombre);
        txtPrimerAp.setText(apellido1);
        txtSegundoAp.setText(apellido2);
        if (sueldoBase != null) {
            txtSueldoBase.setText(sueldoBase.toString());
        }

        cbActivo.setSelectedItem(activo ? "Si" : "No");
        
        // Configurar combo boxes con los valores correctos
        configurarComboBoxes(idDepto, idPuesto);

        // Hacer que el ID no se pueda editar
        txtIdEmpleado.setEditable(false);
        
        // Aplicar estilos consistentes
        configurarEstilos();
    }
    
    private void configurarEstilos() {
        txtIdEmpleado.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtNombre.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtPrimerAp.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtSegundoAp.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        txtSueldoBase.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        cbDepartamento.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        cbPuesto.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        cbActivo.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:15; borderWidth: 0");
        
        btnMod.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        btnMod.putClientProperty(com.formdev.flatlaf.FlatClientProperties.BUTTON_TYPE, "roundRect");
    }
    
    /**
     * Configura los combo boxes con los valores correctos del empleado
     */
    private void configurarComboBoxes(int idDepto, int idPuesto) {
        try {
            // Configurar departamento
            DepartamentoDAO deptoDAO = new DepartamentoDAO();
            Departamento depto = deptoDAO.obtenerPorId(idDepto);
            if (depto != null) {
                cbDepartamento.setSelectedItem(depto.getNombre());

            } else {
                System.out.println("No se encontró departamento con ID: " + idDepto);
            }
            
            // Configurar puesto
            PuestoDAO puestoDAO = new PuestoDAO();
            Puesto puesto = puestoDAO.obtenerPorId(idPuesto);
            if (puesto != null) {
                cbPuesto.setSelectedItem(puesto.getNombre());

            } else {
                System.out.println("No se encontró puesto con ID: " + idPuesto);
            }
            
        } catch (Exception e) {
            System.err.println("Error configurando combo boxes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para mapear nombres de departamento a IDs
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
            default: return 1; // Valor por defecto
        }
    }
    
    // Método auxiliar para mapear nombres de puesto a IDs
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
            default: return 1; // Valor por defecto
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
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtIdEmpleado = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtPrimerAp = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtSegundoAp = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cbDepartamento = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cbPuesto = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cbActivo = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtSueldoBase = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        btnMod = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(55, 55, 55));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 60));

        jLabel1.setBackground(new java.awt.Color(55, 55, 55));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Modificar Empleado");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 390));
        jPanel2.setLayout(new java.awt.GridLayout(5, 1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Id Empleado:");
        jLabel2.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel4.add(jLabel2);

        txtIdEmpleado.setBackground(new java.awt.Color(216, 216, 216));
        txtIdEmpleado.setToolTipText("");
        txtIdEmpleado.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel4.add(txtIdEmpleado);

        jPanel2.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Nombre:");
        jLabel3.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel5.add(jLabel3);

        txtNombre.setBackground(new java.awt.Color(216, 216, 216));
        txtNombre.setToolTipText("");
        txtNombre.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel5.add(txtNombre);

        jPanel2.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Primer Apellido:");
        jLabel4.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel6.add(jLabel4);

        txtPrimerAp.setBackground(new java.awt.Color(216, 216, 216));
        txtPrimerAp.setToolTipText("");
        txtPrimerAp.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel6.add(txtPrimerAp);

        jPanel2.add(jPanel6);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Segundo Apellido:");
        jLabel5.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel7.add(jLabel5);

        txtSegundoAp.setBackground(new java.awt.Color(216, 216, 216));
        txtSegundoAp.setToolTipText("");
        txtSegundoAp.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel7.add(txtSegundoAp);

        jPanel2.add(jPanel7);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Departamento:");
        jLabel6.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel8.add(jLabel6);

        cbDepartamento.setBackground(new java.awt.Color(216, 216, 216));
        cbDepartamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recursos humanos", "Ventas", "Tecnología", "Finanzas", "Producción", "Logística", "Marketing", "Compras", "Atención al cliente", "Legal" }));
        cbDepartamento.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel8.add(cbDepartamento);

        jPanel2.add(jPanel8);

        add(jPanel2, java.awt.BorderLayout.LINE_START);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(300, 390));
        jPanel3.setLayout(new java.awt.GridLayout(5, 1));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Puesto:");
        jLabel7.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel9.add(jLabel7);

        cbPuesto.setBackground(new java.awt.Color(216, 216, 216));
        cbPuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "albañil", "Gerente de Ventas", "Desarrollador de Software", "Contador", "Supervisor de Producción", "Coordinador de Logística", "Especialista en Marketing Digital", "Comprador", "Asesor de Atención al Cliente", "Abogado Corporativo", "Especialista en Reclutamiento" }));
        cbPuesto.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel9.add(cbPuesto);

        jPanel3.add(jPanel9);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Activo:");
        jLabel8.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel10.add(jLabel8);

        cbActivo.setBackground(new java.awt.Color(216, 216, 216));
        cbActivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Si", "No" }));
        cbActivo.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel10.add(cbActivo);

        jPanel3.add(jPanel10);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Sueldo base:");
        jLabel9.setPreferredSize(new java.awt.Dimension(220, 20));
        jPanel12.add(jLabel9);

        txtSueldoBase.setBackground(new java.awt.Color(216, 216, 216));
        txtSueldoBase.setToolTipText("");
        txtSueldoBase.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel12.add(txtSueldoBase);

        jPanel3.add(jPanel12);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        btnMod.setBackground(new java.awt.Color(55, 55, 55));
        btnMod.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMod.setForeground(new java.awt.Color(255, 255, 255));
        btnMod.setText("Modificar");
        btnMod.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMod.setPreferredSize(new java.awt.Dimension(100, 35));
        btnMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModActionPerformed(evt);
            }
        });
        jPanel11.add(btnMod);

        jPanel3.add(jPanel11);

        add(jPanel3, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        // Crear la ventana de confirmación personalizada
        ConfirmacionDialog confirmacion = new ConfirmacionDialog((Frame) parentDialog.getParent(), true);

        // Configurar la acción a ejecutar si el usuario confirma
        confirmacion.setOnConfirmar(() -> {
            try {
                Empleado empleadoActualizado = new Empleado();
                empleadoActualizado.setIdEmpleado(this.idEmpleado);

                // Campos básicos
                empleadoActualizado.setNombre(txtNombre.getText());
                empleadoActualizado.setApellido1(txtPrimerAp.getText());
                empleadoActualizado.setApellido2(txtSegundoAp.getText());
                empleadoActualizado.setActivo(cbActivo.getSelectedItem().toString().equals("Si"));

                // Mapeo de ComboBoxes String -> IDs de BD
                String departamentoSeleccionado = (String) cbDepartamento.getSelectedItem();
                String puestoSeleccionado = (String) cbPuesto.getSelectedItem();

                // Mapeo Departamento
                int idDepartamento = mapearDepartamentoStringAId(departamentoSeleccionado);
                empleadoActualizado.setIdDepartamento(idDepartamento);

                // Mapeo Puesto  
                int idPuesto = mapearPuestoStringAId(puestoSeleccionado);
                empleadoActualizado.setIdPuesto(idPuesto);
                
                // Validar y asignar sueldo base
                String sueldoBaseTexto = txtSueldoBase.getText().trim();
                if (sueldoBaseTexto.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "El sueldo base es obligatorio", 
                        "Error de Validación", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    java.math.BigDecimal sueldoBase = new java.math.BigDecimal(sueldoBaseTexto);
                    if (sueldoBase.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        javax.swing.JOptionPane.showMessageDialog(this, 
                            "El sueldo base debe ser mayor a 0", 
                            "Error de Validación", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    empleadoActualizado.setSueldoBase(sueldoBase);
                } catch (NumberFormatException nfe) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "El sueldo base debe ser un número válido", 
                        "Error de Validación", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }

                EmpleadoDAO dao = new EmpleadoDAO();
                boolean exito = dao.modificarEmpleado(empleadoActualizado);

                if (exito) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Empleado modificado correctamente.");

                    if (onUpdateSuccess != null) {
                        onUpdateSuccess.run(); // Actualiza la tabla principal
                    }

                    parentDialog.dispose(); // Cierra esta subventana
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error al modificar el empleado.", "Error de Base de Datos", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error en los datos. Revisa los campos.", "Error de Formato", javax.swing.JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        // Mostrar el diálogo de confirmación
        confirmacion.mostrar();
    }//GEN-LAST:event_btnModActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<String> cbActivo;
    private javax.swing.JComboBox<String> cbDepartamento;
    private javax.swing.JComboBox<String> cbPuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField txtIdEmpleado;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrimerAp;
    private javax.swing.JTextField txtSegundoAp;
    private javax.swing.JTextField txtSueldoBase;
    // End of variables declaration//GEN-END:variables
}

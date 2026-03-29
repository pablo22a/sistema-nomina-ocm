package com.proyectodeprueba.ui;

import DAO.DepartamentoDAO;
import DAO.EmpleadoDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import util.SessionManager;
import java.awt.event.ActionListener;
import com.proyectodeprueba.ui.subventanas.AgregarDialog;
import com.proyectodeprueba.ui.subventanas.AgregarEmpleadosPanel;
import com.proyectodeprueba.ui.subventanas.EliminarAsistenciaPanel;
import com.proyectodeprueba.ui.subventanas.EliminarDialog;
import com.proyectodeprueba.ui.subventanas.EliminarEmpleadosPanel;
import com.proyectodeprueba.ui.subventanas.ModificarDialog;
import com.proyectodeprueba.ui.subventanas.ModificarEmpleadosPanel;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import models.Departamento;
import models.Empleado;

/**
 *
 * @author andrisharaizaespinoza
 */
public class EmpleadosPanel extends javax.swing.JPanel {

     private MainFrame parent;
     private DatePicker datePicker; // Hacer DatePicker accesible
     private boolean filtrarPorFecha = false; // Controlar si se debe filtrar por fecha
    
   
    /**
     * Creates new form EmpleadosPanel
     */
    public EmpleadosPanel(MainFrame parent) {
        initComponents();
        this.parent = parent;
        
        configurarEstilos();
        cargarDepartamentos();
        
        // Carga inicial de la tabla con TODOS los registros
        EmpleadoDAO dao = new EmpleadoDAO();
        cargarEmpleadosEnTabla(dao.obtenerEmpleadosFiltrados(0));
    }
    
    private void configurarEstilos() {
        // Estilos del botón
        btnLogs.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAsistencias.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEmpleados.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnNomina.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAdmin.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        //btnMostrar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        OpcionesCB.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAgregar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnModificar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEliminar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        // Hacer DatePicker accesible como variable de instancia
        datePicker = new DatePicker();
        
        panelFecha.setLayout(new java.awt.BorderLayout());
        panelFecha.add(datePicker, java.awt.BorderLayout.CENTER);
        panelFecha.revalidate();
        panelFecha.repaint();
         datePicker.setDate(java.time.LocalDate.now());
        
        // Agregar listener para que se actualice cuando el usuario cambie la fecha manualmente
        datePicker.addDateChangeListener(event -> {
            if (OpcionesCB.getSelectedItem() != null && event.getSource() == datePicker) {
                // Solo activar filtro por fecha cuando el usuario cambie la fecha explícitamente
                filtrarPorFecha = true;
                actualizarTablaConFiltros();
            }
        });
        
        JTableHeader header = JTable.getTableHeader();
        header.putClientProperty(FlatClientProperties.STYLE, "background:#373737; foreground:#ffffff; font: bold 12 Segoe UI");
        
        // NUEVO: Configurar interface según rol del usuario
        configurarInterfazPorRol();
    }
    
    /**
     * Configura la interfaz según el rol del usuario actual
     * - Admin: Ve botón "Admin Sistema"
     * - Capturista: Ve botón "Prestaciones" 
     * - Actualiza lblUser con nombre real
     */
    private void configurarInterfazPorRol() {
        // Actualizar nombre del usuario en lblUser
        if (SessionManager.haySesionActiva()) {
            lblUser.setText("Usuario: " + SessionManager.getNombreUsuario());
        }
        
        // Configurar botón según rol
        if (SessionManager.esAdmin()) {
            // Para admin: asegurar que dice "Admin Sistema" y está visible
            btnAdmin.setText("Admin Sistema");
            btnAdmin.setVisible(true);
            
            // Quitar listeners anteriores
            for (ActionListener listener : btnAdmin.getActionListeners()) {
                btnAdmin.removeActionListener(listener);
            }
            
            // Acción para ir a admin sistema
            btnAdmin.addActionListener(e -> parent.showPanel("adminsistema"));
            
        } else if (SessionManager.esCapturista()) {
            // Para capturistas: cambiar botón Admin por Prestaciones
            btnAdmin.setText("Prestaciones");
            btnAdmin.setVisible(true);
            
            // Quitar listeners anteriores
            for (ActionListener listener : btnAdmin.getActionListeners()) {
                btnAdmin.removeActionListener(listener);
            }
            
            // Agregar nueva acción para ir a prestaciones
            btnAdmin.addActionListener(e -> parent.showPanel("prestaciones"));
            
        } else {
            // Si no es admin ni capturista, ocultar botón
            btnAdmin.setVisible(false);
        }
    }
    
    /**
     * Método público para reconfigurar la interfaz cuando el panel se activa
     */
    public void actualizarInterfaz() {
        configurarInterfazPorRol();
    }
    
    public void actualizarTabla() {
    int idDeptoSeleccionado = 0;
    Object itemSeleccionado = OpcionesCB.getSelectedItem();

    if (itemSeleccionado instanceof Departamento) {
        idDeptoSeleccionado = ((Departamento) itemSeleccionado).getIdDepartamento();
    }
    
    // Resetear filtro de fecha cuando se actualiza la tabla externamente
    filtrarPorFecha = false;
    
    EmpleadoDAO dao = new EmpleadoDAO();
    cargarEmpleadosEnTabla(dao.obtenerEmpleadosFiltrados(idDeptoSeleccionado, null));
    }
    
    // Método público para resetear el filtro de fecha
    public void resetearFiltroFecha() {
        filtrarPorFecha = false;
        actualizarTablaConFiltros();
    }
    
    // Método para actualizar tabla con filtros (reutilizable)
    private void actualizarTablaConFiltros() {
        int idDeptoSeleccionado = 0;
        Object itemSeleccionado = OpcionesCB.getSelectedItem();
        
        if (itemSeleccionado instanceof String && itemSeleccionado.equals("Todos los Departamentos")) {
            idDeptoSeleccionado = 0;
        } else if (itemSeleccionado instanceof Departamento) {
            Departamento depto = (Departamento) itemSeleccionado;
            idDeptoSeleccionado = depto.getIdDepartamento();
        }

        // Solo usar la fecha como filtro si el usuario la cambió explícitamente
        java.time.LocalDate fechaSeleccionada = (filtrarPorFecha && datePicker != null) ? datePicker.getDate() : null;
        

        
        EmpleadoDAO dao = new EmpleadoDAO();
        List<Empleado> empleadosEncontrados = dao.obtenerEmpleadosFiltrados(idDeptoSeleccionado, fechaSeleccionada);
        

        
        cargarEmpleadosEnTabla(empleadosEncontrados);
    }
    
    private void cargarDepartamentos() {
        try {
            DepartamentoDAO deptoDAO = new DepartamentoDAO();
            java.util.List<Departamento> departamentos = deptoDAO.obtenerTodos();

            OpcionesCB.removeAllItems();

            // Crear opción "Todos"
            Departamento todos = new Departamento();
            todos.setIdDepartamento(0);
            todos.setNombre("Todos los Departamentos");
            OpcionesCB.addItem(todos);

            // Agregar departamentos
            for (Departamento depto : departamentos) {
                OpcionesCB.addItem(depto);
            }

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error al cargar departamentos: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
        
    private void cargarEmpleadosEnTabla(List<Empleado> listaEmpleados) {
        DefaultTableModel model = (DefaultTableModel) JTable.getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Empleado e : listaEmpleados) {
            // --- Lógica Corregida: Juntamos los apellidos para mostrarlos ---
            String apellidosCompletos = e.getApellido1() + " " + (e.getApellido2() != null ? e.getApellido2() : "");

            model.addRow(new Object[]{
                e.getIdEmpleado(),
                e.getIdPuesto(), // CAMBIO AQUÍ: Se usa getIdPuesto() en lugar de getPuesto()
                e.getIdDepartamento(),
                e.getNombre(),
                apellidosCompletos.trim(), // CAMBIO AQUÍ: Se muestran los apellidos ya juntos
                e.getFechaIngreso().format(formatter),
                e.getSueldoBase() != null ? e.getSueldoBase() : java.math.BigDecimal.ZERO, // Agregar sueldo base
                e.isActivo()
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
        OpcionesCB = new javax.swing.JComboBox<>();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        panelFecha = new javax.swing.JPanel();

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

        btnEmpleados.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnEmpleados.setForeground(new java.awt.Color(228, 68, 28));
        btnEmpleados.setText("Empleados");
        btnEmpleados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Empleado", "ID Puesto", "ID Departamento", "Nombre", "Apellidos", "Fecha de Ingreso", "Sueldo Base", "Activo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(JTable);

        OpcionesCB.setBackground(new java.awt.Color(55, 55, 55));
        OpcionesCB.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        OpcionesCB.setForeground(new java.awt.Color(255, 255, 255));
        OpcionesCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recursos Humanos", "Ventas", "Tecnología", "Finanzas", "Producción", "Logistica", "Marketing", "Compras", "Atención al Cliente", "Legal" }));
        OpcionesCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpcionesCBActionPerformed(evt);
            }
        });

        btnAgregar.setBackground(new java.awt.Color(55, 55, 55));
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add-circle-outline.png"))); // NOI18N
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgregarMouseClicked(evt);
            }
        });

        btnModificar.setBackground(new java.awt.Color(55, 55, 55));
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pencil-sharp.png"))); // NOI18N
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnModificarMouseClicked(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(55, 55, 55));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash-outline.png"))); // NOI18N
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEliminarMouseClicked(evt);
            }
        });

        panelFecha.setPreferredSize(new java.awt.Dimension(188, 36));

        javax.swing.GroupLayout panelFechaLayout = new javax.swing.GroupLayout(panelFecha);
        panelFecha.setLayout(panelFechaLayout);
        panelFechaLayout.setHorizontalGroup(
            panelFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );
        panelFechaLayout.setVerticalGroup(
            panelFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(OpcionesCB, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(panelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregar)
                        .addGap(18, 18, 18)
                        .addComponent(btnModificar)
                        .addGap(20, 20, 20)
                        .addComponent(btnEliminar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1076, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OpcionesCB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelFecha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
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

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
        // TODO add your handling code here:
                parent.showPanel("adminsistema");

    }//GEN-LAST:event_btnAdminActionPerformed

    private void btnLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogsActionPerformed
        // TODO add your handling code here:
        parent.showPanel("logs");
    }//GEN-LAST:event_btnLogsActionPerformed

    private void OpcionesCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpcionesCBActionPerformed
        // Validar que hay un elemento seleccionado
        if (OpcionesCB.getSelectedItem() == null) {
            return;
        }
        
        // Agregar debug para ver qué se seleccionó
        Object itemSeleccionado = OpcionesCB.getSelectedItem();

        
        try {
            // Resetear el filtro de fecha cuando cambie el departamento
            // para mostrar todos los empleados del departamento seleccionado
            filtrarPorFecha = false;
            
            // Usar el método reutilizable
            actualizarTablaConFiltros();
        } catch (Exception e) {
            System.err.println("Error al actualizar tabla de empleados con filtros: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, mostrar todos los registros
            EmpleadoDAO dao = new EmpleadoDAO();
            cargarEmpleadosEnTabla(dao.obtenerEmpleadosFiltrados(0, null));
        }
    }//GEN-LAST:event_OpcionesCBActionPerformed

    private void btnAgregarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarMouseClicked
        // TODO add your handling code here:
       AgregarDialog agregar = new AgregarDialog(parent, true);
    AgregarEmpleadosPanel agregarEmpleados = new AgregarEmpleadosPanel(agregar, this::actualizarTabla);
    
    agregar.mostrarDialog(agregarEmpleados);
    }//GEN-LAST:event_btnAgregarMouseClicked

    private void btnModificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnModificarMouseClicked
        // TODO add your handling code here:
        int filaSeleccionada = JTable.getSelectedRow();

        if (filaSeleccionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un empleado para modificar.", "Fila no seleccionada", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 1. Obtener datos de la fila (como ya lo tenías) ---
        int idEmpleado = (int) JTable.getValueAt(filaSeleccionada, 0);
        int idPuesto = Integer.parseInt(JTable.getValueAt(filaSeleccionada, 1).toString());
        int idDepto = Integer.parseInt(JTable.getValueAt(filaSeleccionada, 2).toString());
        String nombre = (String) JTable.getValueAt(filaSeleccionada, 3);
        String apellidosCompletos = (String) JTable.getValueAt(filaSeleccionada, 4);
        java.math.BigDecimal sueldoBase = (java.math.BigDecimal) JTable.getValueAt(filaSeleccionada, 6);
        boolean activo = (boolean) JTable.getValueAt(filaSeleccionada, 7);

        // --- 2. Separar los apellidos ---
        String apellido1 = "";
        String apellido2 = "";
        if (apellidosCompletos != null && !apellidosCompletos.trim().isEmpty()) {
            String[] partes = apellidosCompletos.trim().split("\\s+", 2);
            apellido1 = partes[0];
            if (partes.length > 1) {
                apellido2 = partes[1];
            }
        }

        // --- 3. Llamar a la subventana pasando cada dato por separado ---
        ModificarDialog modificar = new ModificarDialog(parent, true);
        ModificarEmpleadosPanel modificarPanel = new ModificarEmpleadosPanel(
     modificar, 
         idEmpleado, 
            nombre, 
            apellido1, 
            apellido2, 
            idPuesto, 
            idDepto, 
            activo, 
            sueldoBase, 
            this::actualizarTabla
        );

        modificar.mostrarDialog(modificarPanel);
     
    }//GEN-LAST:event_btnModificarMouseClicked

    private void btnEliminarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEliminarMouseClicked
        // TODO add your handling code here:
        
        
        int filaSeleccionada = JTable.getSelectedRow();

    // 2. Valida que haya una fila seleccionada
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione una asistencia para eliminar.", "Fila no seleccionada", JOptionPane.WARNING_MESSAGE);
        return; // Si no hay selección, no continúa
    }

    // 3. Captura el ID de la primera columna (índice 0)
    int idEmpleado = (int) JTable.getValueAt(filaSeleccionada, 0);

    // 4. Crea el diálogo y el panel, pasándoles los datos necesarios
    EliminarDialog eliminarDialogo = new EliminarDialog(parent, true);
    EliminarEmpleadosPanel eliminarPanel = new EliminarEmpleadosPanel(eliminarDialogo, idEmpleado, this::actualizarTabla);
    
    // 5. Muestra el diálogo en la pantalla
    eliminarDialogo.mostrarDialog(eliminarPanel);
        
        
    }//GEN-LAST:event_btnEliminarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTable;
    private javax.swing.JComboBox<Object> OpcionesCB;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAsistencias;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnLogs;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNomina;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel panelFecha;
    private javax.swing.JPanel topBar;
    private javax.swing.JLabel userLogo;
    // End of variables declaration//GEN-END:variables

    
}

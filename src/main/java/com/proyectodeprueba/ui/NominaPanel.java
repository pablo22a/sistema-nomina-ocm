/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.proyectodeprueba.ui;
import DAO.DepartamentoDAO;
import DAO.NominaDAO;
import models.Departamento;
import models.Nomina;

import java.util.List;
import java.util.ArrayList; // Necesario para la lista de parámetros en el DAO si modificas esa parte
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal; // Para BigDecimal.ZERO
import java.sql.Connection; // Si usas Connection directamente aquí (no recomendado)
import java.sql.PreparedStatement; // Si usas PreparedStatement directamente aquí (no recomendado)
import java.sql.ResultSet; // Si usas ResultSet directamente aquí (no recomendado)
import java.sql.SQLException; // Para manejo de excepciones SQL
import java.time.LocalDate;
import java.time.LocalDateTime; // Necesario para el método generarPdfNomina
import java.time.format.DateTimeFormatter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import util.SessionManager;
import java.awt.event.ActionListener; // Para tu selector de fecha

// --- Imports para OpenPDF ---
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.proyectodeprueba.ui.subventanas.AgregarDialog;
import com.proyectodeprueba.ui.subventanas.AgregarNominaPanel;
import com.proyectodeprueba.ui.subventanas.EliminarDialog;
import com.proyectodeprueba.ui.subventanas.EliminarNominaPanel;
import com.proyectodeprueba.ui.subventanas.ModificarDialog;
import com.proyectodeprueba.ui.subventanas.ModificarNominaPanel;


/**
 *
 * @author andrisharaizaespinoza
 */
public class NominaPanel extends javax.swing.JPanel {

    private MainFrame parent;
    private DatePicker datePicker; // Hacer DatePicker accesible
    private boolean filtrarPorFecha = false; // Controlar si se debe filtrar por fecha
    /**
     * Creates new form NominaPanel
     */
    public NominaPanel(MainFrame parent) {
        initComponents();
        this.parent = parent;
        
        configurarEstilos();
        cargarDepartamentos();
        
        
        // Carga inicial de la tabla con TODOS los registros
        NominaDAO dao = new NominaDAO();
        cargarNominasEnTabla(dao.obtenerNominasFiltradas(0,null));
        
    }

    public void configurarEstilos() {
        // Estilos del botón
        btnLogs.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAsistencias.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEmpleados.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnNomina.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAdmin.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        OpcionesCB.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        //Opciones2CB.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnAgregar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnModificar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnEliminar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
        btnGenerar.putClientProperty(FlatClientProperties.STYLE, "arc:20; focusWidth:0; borderWidth:0");
        
       
        
        
        
        
        
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
    
    public void actualizarTabla() {
        int idDeptoSeleccionado = 0;
Object itemSeleccionado = OpcionesCB.getSelectedItem(); // Use your ComboBox name

if (itemSeleccionado instanceof Departamento) {
    idDeptoSeleccionado = ((Departamento) itemSeleccionado).getIdDepartamento();
}

// Resetear filtro de fecha cuando se actualiza la tabla externamente
filtrarPorFecha = false;

NominaDAO dao = new NominaDAO();
// Ensure your DAO method is named 'obtenerNominasFiltradas'
cargarNominasEnTabla(dao.obtenerNominasFiltradas(idDeptoSeleccionado, null));
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
        

        
        NominaDAO dao = new NominaDAO();
        List<Nomina> nominasEncontradas = dao.obtenerNominasFiltradas(idDeptoSeleccionado, fechaSeleccionada);
        

        
        cargarNominasEnTabla(nominasEncontradas);
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
    
    private void cargarNominasEnTabla(List<Nomina> listaNominas) {
    DefaultTableModel model = (DefaultTableModel) JTable.getModel(); // Usa el nombre real de tu tabla
    model.setRowCount(0); // Limpia la tabla

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Importa DateTimeFormatter

    // Recorre la lista y añade filas
    for (Nomina nomina : listaNominas) {
        // Asegúrate de que el orden coincida con las columnas de tu JTable
        model.addRow(new Object[]{
        nomina.getIdNomina(),                                                    // Columna 0: ID Nomina
        nomina.getIdEmpleado(),                                                  // Columna 1: ID Empleado
        nomina.getSueldoBase() != null ? nomina.getSueldoBase() : java.math.BigDecimal.ZERO, // Columna 2: Sueldo Base (con null check)
        nomina.getTotalHorasTrabajadas(),                                        // Columna 3: Total Horas
        nomina.getFecha() != null ? nomina.getFecha().format(formatter) : "N/A", // Columna 4: Fecha (formateada)
        nomina.getTotalPrestaciones() != null ? nomina.getTotalPrestaciones() : java.math.BigDecimal.ZERO, // Columna 5: Total Prestaciones (con null check)
        nomina.getTotalNeto() != null ? nomina.getTotalNeto() : java.math.BigDecimal.ZERO      // Columna 6: Total Neto (con null check)
    });
    }
}
    
    private void generarPdfNomina(List<Nomina> nominas, String filePath, String nombreDepto, LocalDate fecha)
        throws IOException, DocumentException {

        Document document = new Document(PageSize.A4.rotate()); // Hoja horizontal
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // --- Título y Encabezados ---
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 9);

        Paragraph title = new Paragraph("Reporte de Nómina", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" ")); // Espacio

        Paragraph subtitle = new Paragraph("Departamento: " + nombreDepto, headerFont);
        document.add(subtitle);
        Paragraph dateInfo = new Paragraph("Fecha: " + (fecha != null ? fecha.format(DateTimeFormatter.ISO_DATE) : "Todas las fechas"), headerFont);
        document.add(dateInfo);
        document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), normalFont));

        document.add(new Paragraph(" ")); // Espacio

        // --- Crear Tabla ---
        // Ajusta el número de columnas según los datos que quieras mostrar
        PdfPTable table = new PdfPTable(7); // ID Nomina, ID Emp, Sueldo B, Hrs Trab, Fecha, Prestac, Neto
        table.setWidthPercentage(100);
        // Definir anchos relativos de columnas (opcional)
        // table.setWidths(new float[]{1, 1, 2, 1, 2, 2, 2});

        // --- Encabezados de Tabla ---
        String[] headers = {"ID Nom", "ID Emp", "Sueldo Base", "Hrs Trab", "Fecha", "Prestaciones", "Total Neto"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            table.addCell(cell);
        }

        // --- Datos de la Tabla ---
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Nomina n : nominas) {
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(n.getIdNomina()), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(n.getIdEmpleado()), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(n.getSueldoBase() != null ? n.getSueldoBase().toPlainString() : "0.00", normalFont)));
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(n.getTotalHorasTrabajadas()), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(n.getFecha() != null ? n.getFecha().format(formatter) : "N/A", normalFont)));
            table.addCell(new PdfPCell(new Paragraph(n.getTotalPrestaciones() != null ? n.getTotalPrestaciones().toPlainString() : "0.00", normalFont)));
            table.addCell(new PdfPCell(new Paragraph(n.getTotalNeto() != null ? n.getTotalNeto().toPlainString() : "0.00", normalFont)));
        }

        document.add(table);
        document.close();
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
        btnGenerar = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

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

        btnNomina.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        btnNomina.setForeground(new java.awt.Color(228, 68, 28));
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
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Nomina", "ID Empleado", "Sueldo Base", "Total Horas Trabajadas", "Fecha Agregado", "Total Prestaciones", "Total Neto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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

        btnGenerar.setBackground(new java.awt.Color(55, 55, 55));
        btnGenerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/document.png"))); // NOI18N
        btnGenerar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(OpcionesCB, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(panelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGenerar)
                        .addGap(18, 18, 18)
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
                            .addComponent(panelFecha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGenerar, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
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

    private void OpcionesCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpcionesCBActionPerformed
        // Validar que hay un elemento seleccionado
        if (OpcionesCB.getSelectedItem() == null) {
            return;
        }
        
        // Agregar debug para ver qué se seleccionó
        Object itemSeleccionado = OpcionesCB.getSelectedItem();

        
        try {
            // Resetear el filtro de fecha cuando cambie el departamento
            // para mostrar todos los registros del departamento seleccionado
            filtrarPorFecha = false;
            
            // Usar el método reutilizable
            actualizarTablaConFiltros();
        } catch (Exception e) {
            System.err.println("Error al actualizar tabla con filtros: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, mostrar todos los registros
            NominaDAO dao = new NominaDAO();
            cargarNominasEnTabla(dao.obtenerNominasFiltradas(0, null));
        }
    }//GEN-LAST:event_OpcionesCBActionPerformed

    private void btnAgregarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarMouseClicked
        // TODO add your handling code here:
        AgregarDialog agregar = new AgregarDialog(parent,true);
        AgregarNominaPanel agregarNomina = new AgregarNominaPanel(agregar, this::actualizarTabla);

        agregar.mostrarDialog(agregarNomina);
    }//GEN-LAST:event_btnAgregarMouseClicked

    private void btnModificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnModificarMouseClicked
        // TODO add your handling code here:
        int filaSeleccionada = JTable.getSelectedRow();

        if (filaSeleccionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione una nomina para modificar.", "Fila no seleccionada", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 1. Obtener datos de la fila (como ya lo tenías) ---
        int idNomina = (int) JTable.getValueAt(filaSeleccionada, 0);
        int idEmpleado = (int) JTable.getValueAt(filaSeleccionada, 1);
        java.math.BigDecimal sueldoBase = (java.math.BigDecimal) JTable.getValueAt(filaSeleccionada, 2);
        int totalHorasTrabajadas = (int) JTable.getValueAt(filaSeleccionada, 3);
        String fecha = (String) JTable.getValueAt(filaSeleccionada, 4);
        java.math.BigDecimal totalPrestaciones = (java.math.BigDecimal) JTable.getValueAt(filaSeleccionada, 5);
        java.math.BigDecimal totalNeto = (java.math.BigDecimal) JTable.getValueAt(filaSeleccionada, 6);

        // --- 2. Llamar a la subventana pasando cada dato por separado ---
        ModificarDialog modificar = new ModificarDialog(parent, true);
        ModificarNominaPanel modificarPanel = new ModificarNominaPanel(
            modificar, 
            idNomina, 
            idEmpleado, 
            totalHorasTrabajadas, 
            fecha, 
            totalPrestaciones != null ? totalPrestaciones.toString() : "0.00", 
            totalNeto != null ? totalNeto.toString() : "0.00", 
            sueldoBase != null ? sueldoBase.toString() : "0.00", 
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
    int idParaEliminar = (int) JTable.getValueAt(filaSeleccionada, 0);

    // 4. Crea el diálogo y el panel, pasándoles los datos necesarios
    EliminarDialog eliminarDialogo = new EliminarDialog(parent, true);
    EliminarNominaPanel eliminarPanel = new EliminarNominaPanel(eliminarDialogo, idParaEliminar, this::actualizarTabla);
    
    // 5. Muestra el diálogo en la pantalla
    eliminarDialogo.mostrarDialog(eliminarPanel);

    }//GEN-LAST:event_btnEliminarMouseClicked

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        // TODO add your handling code here:
        // 1. Obtener Departamento Seleccionado
    int idDeptoSeleccionado = 0;
    String nombreDepto = "Todos los Departamentos";
    Object itemDepto = OpcionesCB.getSelectedItem();
    if (itemDepto instanceof Departamento) {
        idDeptoSeleccionado = ((Departamento) itemDepto).getIdDepartamento();
        nombreDepto = ((Departamento) itemDepto).getNombre();
    } else if (itemDepto instanceof String){
         // Si es el string "Todos", mantenemos id=0 y el nombre
         nombreDepto = (String) itemDepto;
    }


    // 2. Obtener Fecha Seleccionada
    LocalDate fechaSeleccionada = null;
    // Encuentra el componente DatePicker dentro de panelFecha
    DatePicker datePicker = null;
    for (java.awt.Component comp : panelFecha.getComponents()) {
        if (comp instanceof DatePicker) {
            datePicker = (DatePicker) comp;
            break;
        }
    }
    if (datePicker != null) {
        fechaSeleccionada = datePicker.getDate(); // Obtiene LocalDate
    }

    // 3. Obtener Datos Filtrados del DAO
    NominaDAO dao = new NominaDAO();
    List<Nomina> nominasFiltradas = dao.obtenerNominasFiltradas(idDeptoSeleccionado,null);

    if (nominasFiltradas.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No se encontraron nóminas con los filtros seleccionados.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // 4. Preguntar al Usuario Dónde Guardar el PDF
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Guardar Reporte de Nómina");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
    // Sugerir un nombre de archivo
    String nombreArchivoSugerido = "ReporteNomina_" + nombreDepto.replace(" ", "") + "_" + (fechaSeleccionada != null ? fechaSeleccionada.toString() : "Todas") + ".pdf";
    fileChooser.setSelectedFile(new java.io.File(nombreArchivoSugerido));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
        // Asegurarse de que el archivo termine en .pdf
        String filePath = fileToSave.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        // 5. Generar el PDF
        try {
            generarPdfNomina(nominasFiltradas, filePath, nombreDepto, fechaSeleccionada);
            JOptionPane.showMessageDialog(this, "Reporte PDF generado exitosamente en:\n" + filePath, "PDF Generado", JOptionPane.INFORMATION_MESSAGE);
            // Opcional: Abrir el PDF generado
            // try { Desktop.getDesktop().open(new java.io.File(filePath)); } catch (IOException ex) { /* Manejar error */ }
        } catch (IOException | DocumentException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF:\n" + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_btnGenerarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTable;
    private javax.swing.JComboBox<Object> OpcionesCB;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAsistencias;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnGenerar;
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

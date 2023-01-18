package vistas;

import java.awt.Toolkit;
import javax.swing.*;
import modelos.*;

public class editarHorario extends javax.swing.JFrame {

    DefaultListModel<String> listEdit = new DefaultListModel<>();
    horario HR;
    tiempo time;
    Ventana frame;
    
    int pos;
    public editarHorario() {
        initComponents();
        jList2.setModel(listEdit);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/icon.png")));
    }
    
    private void datosHoras(int pos){
        jSlider2.setValue(HR.getHoras().get(pos).getHora());
        jSlider1.setValue(HR.getHoras().get(pos).getMinutos());
        moment1.setSelectedIndex(HR.getHoras().get(pos).getMomento());
        nTimbres1.setSelectedIndex(HR.getHoras().get(pos).getRepeticiones()-1);
        nDuracion1.setSelectedIndex(HR.getHoras().get(pos).getDuracion()-1);
        jList2.setSelectedIndex(pos);
    }
    
    public void aplicarDatos(Ventana fr, horario hr){
        HR = hr;
        frame = fr;
        titulo.setText("EDITAR HORARIO "+hr.getId());
        datosHoras(0);
        listarTiempos();
        jList2.setSelectedIndex(0);
        marcarDias();
    }
    
    
    private void listarTiempos(){
        listEdit.clear();
        for (int i = 0; i < HR.getHoras().len(); i++) {
            listEdit.addElement(HR.getHoras().get(i).toString());
        }
    }
    
    private void marcarDias(){
        for (int i = 0; i < HR.getDias().len(); i++) {
            switch(HR.getDias().get(i)){
                case 1: domingo1.setSelected(true); break;
                case 2: lunes1.setSelected(true); break;
                case 3: martes1.setSelected(true); break;
                case 4: miercoles1.setSelected(true); break;
                case 5: jueves1.setSelected(true); break;
                case 6: viernes1.setSelected(true); break;
                case 7: sabado1.setSelected(true); break;
            }
        }
    }
    
    private void mensaje(String titulo, String texto, int icon){
        JOptionPane.showMessageDialog(null, texto, titulo, icon);
    }
    
    private void guardarTiempo(){
        try {
            time = new tiempo();
            time.setHora((int) jSlider2.getValue());
            time.setMinutos((int) jSlider1.getValue());
            time.setMomento(moment1.getSelectedIndex());
            time.setRepeticiones(nTimbres1.getSelectedIndex()+1);
            time.setDuracion(nDuracion1.getSelectedIndex()+1);
            HR.getHoras().add(time);
            listEdit.addElement(time.toString());
        } catch (Exception e) {
            mensaje("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarDias(){
        HR.getDias().clear();
        if(lunes1.isSelected()) HR.getDias().add(2); 
        if(martes1.isSelected())HR.getDias().add(3); 
        if(miercoles1.isSelected())HR.getDias().add(4); 
        if(jueves1.isSelected())HR.getDias().add(5); 
        if(viernes1.isSelected())HR.getDias().add(6); 
        if(sabado1.isSelected())HR.getDias().add(7); 
        if(domingo1.isSelected()) HR.getDias().add(1); 
    }
    
    private void notCheckAll(JCheckBox ch){
        if(!ch.isSelected()){
            allCheck1.setSelected(false);
        }
    }
    
    private void limpiar(){
       lunes1.setSelected(false);
       martes1.setSelected(false);
       miercoles1.setSelected(false);
       jueves1.setSelected(false);
       viernes1.setSelected(false);
       sabado1.setSelected(false);
       domingo1.setSelected(false);
       allCheck1.setSelected(false);
       listEdit.setSize(0);
       nTimbres1.setSelectedIndex(0);
       HR = null;
       frame = null;
       addCheck.setSelected(false);
   }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listaMenu = new javax.swing.JPopupMenu();
        eliminar = new javax.swing.JMenuItem();
        eliminarTodo = new javax.swing.JMenuItem();
        jPanel5 = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        moment1 = new javax.swing.JComboBox<>();
        agregarEdit = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        lbMin = new javax.swing.JLabel();
        nTimbres1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        addCheck = new javax.swing.JCheckBox();
        nDuracion1 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        lunes1 = new javax.swing.JCheckBox();
        martes1 = new javax.swing.JCheckBox();
        miercoles1 = new javax.swing.JCheckBox();
        jueves1 = new javax.swing.JCheckBox();
        viernes1 = new javax.swing.JCheckBox();
        sabado1 = new javax.swing.JCheckBox();
        domingo1 = new javax.swing.JCheckBox();
        jButton6 = new javax.swing.JButton();
        allCheck1 = new javax.swing.JCheckBox();

        eliminar.setBackground(new java.awt.Color(6, 34, 52));
        eliminar.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        eliminar.setForeground(new java.awt.Color(255, 204, 51));
        eliminar.setText("Eliminar");
        eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarActionPerformed(evt);
            }
        });
        listaMenu.add(eliminar);

        eliminarTodo.setBackground(new java.awt.Color(6, 34, 52));
        eliminarTodo.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        eliminarTodo.setForeground(new java.awt.Color(255, 204, 51));
        eliminarTodo.setText("Eliminar Todo");
        eliminarTodo.setToolTipText("");
        eliminarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarTodoActionPerformed(evt);
            }
        });
        listaMenu.add(eliminarTodo);

        setTitle("Editar Horario");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(1, 34, 57));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titulo.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        titulo.setForeground(new java.awt.Color(255, 255, 255));
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("EDITAR HORARIO");
        jPanel5.add(titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 30));

        jSeparator4.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator4.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator4.setAlignmentY(2.0F);
        jPanel5.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 270, 10));

        moment1.setBackground(new java.awt.Color(15, 65, 98));
        moment1.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        moment1.setForeground(new java.awt.Color(255, 255, 255));
        moment1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AM", "PM" }));
        moment1.setBorder(null);
        moment1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        moment1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moment1ActionPerformed(evt);
            }
        });
        jPanel5.add(moment1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, -1, 30));

        agregarEdit.setBackground(new java.awt.Color(23, 83, 123));
        agregarEdit.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        agregarEdit.setForeground(new java.awt.Color(255, 255, 255));
        agregarEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/agregar.png"))); // NOI18N
        agregarEdit.setText("AGREGAR");
        agregarEdit.setBorder(null);
        agregarEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarEdit.setEnabled(false);
        agregarEdit.setFocusPainted(false);
        agregarEdit.setIconTextGap(8);
        agregarEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarEditActionPerformed(evt);
            }
        });
        jPanel5.add(agregarEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, 170, 40));

        jList2.setBackground(new java.awt.Color(36, 46, 52));
        jList2.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jList2.setForeground(new java.awt.Color(255, 204, 51));
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setComponentPopupMenu(listaMenu);
        jList2.setSelectionBackground(new java.awt.Color(56, 96, 169));
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jList2MouseEntered(evt);
            }
        });
        jScrollPane4.setViewportView(jList2);

        jPanel5.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 160, 190));

        lbMin.setBackground(new java.awt.Color(255, 255, 255));
        lbMin.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        lbMin.setForeground(new java.awt.Color(255, 204, 51));
        lbMin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbMin.setText("30");
        lbMin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbMin.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel5.add(lbMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 30, 20));

        nTimbres1.setBackground(new java.awt.Color(1, 34, 57));
        nTimbres1.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        nTimbres1.setForeground(new java.awt.Color(255, 255, 255));
        nTimbres1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        nTimbres1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nTimbres1ActionPerformed(evt);
            }
        });
        jPanel5.add(nTimbres1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 50, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Sonará: ");
        jPanel5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 60, 30));

        jSlider1.setBackground(new java.awt.Color(1, 34, 57));
        jSlider1.setForeground(new java.awt.Color(255, 204, 51));
        jSlider1.setMajorTickSpacing(1);
        jSlider1.setMaximum(59);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setSnapToTicks(true);
        jSlider1.setToolTipText("");
        jSlider1.setValue(30);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        jPanel5.add(jSlider1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, 170, 20));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 204, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("HORAS");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 280, 30));

        lbHora.setBackground(new java.awt.Color(255, 255, 255));
        lbHora.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        lbHora.setForeground(new java.awt.Color(255, 204, 51));
        lbHora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbHora.setText("12");
        lbHora.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbHora.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel5.add(lbHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 30, 20));

        jSlider2.setBackground(new java.awt.Color(1, 34, 57));
        jSlider2.setForeground(new java.awt.Color(255, 204, 51));
        jSlider2.setMajorTickSpacing(1);
        jSlider2.setMaximum(12);
        jSlider2.setMinimum(1);
        jSlider2.setMinorTickSpacing(1);
        jSlider2.setSnapToTicks(true);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });
        jPanel5.add(jSlider2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 170, 20));

        addCheck.setBackground(new java.awt.Color(1, 34, 57));
        addCheck.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        addCheck.setForeground(new java.awt.Color(255, 255, 255));
        addCheck.setText("Agregar nuevo");
        addCheck.setBorder(null);
        addCheck.setFocusPainted(false);
        addCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        addCheck.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        addCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCheckActionPerformed(evt);
            }
        });
        jPanel5.add(addCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 140, -1));

        nDuracion1.setBackground(new java.awt.Color(1, 34, 57));
        nDuracion1.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        nDuracion1.setForeground(new java.awt.Color(255, 255, 255));
        nDuracion1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        nDuracion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nDuracion1ActionPerformed(evt);
            }
        });
        jPanel5.add(nDuracion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 150, 50, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Duración:");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 70, 30));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 460, 240));

        jPanel6.setBackground(new java.awt.Color(1, 45, 75));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator5.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator5.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel6.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 10, 50));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 204, 51));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("DÍAS");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 50));

        lunes1.setBackground(new java.awt.Color(1, 45, 75));
        lunes1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lunes1.setForeground(new java.awt.Color(255, 255, 255));
        lunes1.setText("Lunes");
        lunes1.setBorder(null);
        lunes1.setFocusPainted(false);
        lunes1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        lunes1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        lunes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lunes1ActionPerformed(evt);
            }
        });
        jPanel6.add(lunes1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 70, -1));

        martes1.setBackground(new java.awt.Color(1, 45, 75));
        martes1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        martes1.setForeground(new java.awt.Color(255, 255, 255));
        martes1.setText("Martes");
        martes1.setBorder(null);
        martes1.setFocusPainted(false);
        martes1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        martes1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        martes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                martes1ActionPerformed(evt);
            }
        });
        jPanel6.add(martes1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 90, -1));

        miercoles1.setBackground(new java.awt.Color(1, 45, 75));
        miercoles1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        miercoles1.setForeground(new java.awt.Color(255, 255, 255));
        miercoles1.setText("Miércoles");
        miercoles1.setBorder(null);
        miercoles1.setFocusPainted(false);
        miercoles1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        miercoles1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        miercoles1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miercoles1ActionPerformed(evt);
            }
        });
        jPanel6.add(miercoles1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 90, -1));

        jueves1.setBackground(new java.awt.Color(1, 45, 75));
        jueves1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jueves1.setForeground(new java.awt.Color(255, 255, 255));
        jueves1.setText("Jueves");
        jueves1.setBorder(null);
        jueves1.setFocusPainted(false);
        jueves1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        jueves1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        jueves1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jueves1ActionPerformed(evt);
            }
        });
        jPanel6.add(jueves1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 80, -1));

        viernes1.setBackground(new java.awt.Color(1, 45, 75));
        viernes1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        viernes1.setForeground(new java.awt.Color(255, 255, 255));
        viernes1.setText("Viernes");
        viernes1.setBorder(null);
        viernes1.setFocusPainted(false);
        viernes1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        viernes1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        viernes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viernes1ActionPerformed(evt);
            }
        });
        jPanel6.add(viernes1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 80, -1));

        sabado1.setBackground(new java.awt.Color(1, 45, 75));
        sabado1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        sabado1.setForeground(new java.awt.Color(255, 255, 255));
        sabado1.setText("Sábado");
        sabado1.setBorder(null);
        sabado1.setFocusPainted(false);
        sabado1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        sabado1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        sabado1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sabado1ActionPerformed(evt);
            }
        });
        jPanel6.add(sabado1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 80, -1));

        domingo1.setBackground(new java.awt.Color(1, 45, 75));
        domingo1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        domingo1.setForeground(new java.awt.Color(255, 255, 255));
        domingo1.setText("Domingo");
        domingo1.setBorder(null);
        domingo1.setFocusPainted(false);
        domingo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        domingo1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        domingo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                domingo1ActionPerformed(evt);
            }
        });
        jPanel6.add(domingo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, 90, -1));

        jButton6.setBackground(new java.awt.Color(255, 204, 51));
        jButton6.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salvar.png"))); // NOI18N
        jButton6.setText("GUARDAR");
        jButton6.setBorder(null);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setFocusPainted(false);
        jButton6.setIconTextGap(8);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 160, 40));

        allCheck1.setBackground(new java.awt.Color(1, 45, 75));
        allCheck1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        allCheck1.setForeground(new java.awt.Color(255, 255, 255));
        allCheck1.setText("Todos");
        allCheck1.setBorder(null);
        allCheck1.setFocusPainted(false);
        allCheck1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        allCheck1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        allCheck1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCheck1ActionPerformed(evt);
            }
        });
        jPanel6.add(allCheck1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 70, -1));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 460, 120));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void moment1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moment1ActionPerformed
        if(!addCheck.isSelected()){
            HR.getHoras().get(pos).setMomento(moment1.getSelectedIndex());
            listarTiempos();
        }
        
    }//GEN-LAST:event_moment1ActionPerformed

    private void agregarEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarEditActionPerformed
        guardarTiempo();
    }//GEN-LAST:event_agregarEditActionPerformed

    private void nTimbres1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nTimbres1ActionPerformed
        if(!addCheck.isSelected()){
            HR.getHoras().get(pos).setRepeticiones(nTimbres1.getSelectedIndex()+1);
            listarTiempos();
        }
    }//GEN-LAST:event_nTimbres1ActionPerformed

    private void addCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCheckActionPerformed
        if(addCheck.isSelected())agregarEdit.setEnabled(true);
        else agregarEdit.setEnabled(false);
    }//GEN-LAST:event_addCheckActionPerformed

    private void lunes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lunes1ActionPerformed
        notCheckAll(lunes1);
    }//GEN-LAST:event_lunes1ActionPerformed

    private void martes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_martes1ActionPerformed
        notCheckAll(martes1);
    }//GEN-LAST:event_martes1ActionPerformed

    private void miercoles1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miercoles1ActionPerformed
        notCheckAll(miercoles1);
    }//GEN-LAST:event_miercoles1ActionPerformed

    private void jueves1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jueves1ActionPerformed
        notCheckAll(jueves1);
    }//GEN-LAST:event_jueves1ActionPerformed

    private void viernes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viernes1ActionPerformed
        notCheckAll(viernes1);
    }//GEN-LAST:event_viernes1ActionPerformed

    private void sabado1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sabado1ActionPerformed
        notCheckAll(sabado1);
    }//GEN-LAST:event_sabado1ActionPerformed

    private void domingo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_domingo1ActionPerformed
        notCheckAll(domingo1);
    }//GEN-LAST:event_domingo1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        guardarDias();
        if(HR.getHoras().len()>0){
            if(HR.getDias().len()>0){
                frame.recibirEdit(HR);
                limpiar();
                setVisible(false);
            } else mensaje("UPS!", "Debe seleccionar días para este horario.", JOptionPane.WARNING_MESSAGE);
        } else mensaje("UPS!", "No puede dejar el horario sin horas.", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        lbHora.setText(jSlider2.getValue()+"");
        if(!addCheck.isSelected()){    
            HR.getHoras().get(pos).setHora(jSlider2.getValue());
            listarTiempos();
        }
    }//GEN-LAST:event_jSlider2StateChanged

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        lbMin.setText(jSlider1.getValue()+"");
        if(!addCheck.isSelected()){
            HR.getHoras().get(pos).setMinutos(jSlider1.getValue());
            listarTiempos();
        }
    }//GEN-LAST:event_jSlider1StateChanged

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        pos = jList2.getSelectedIndex();
        datosHoras(pos);
    }//GEN-LAST:event_jList2MouseClicked

    private void allCheck1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCheck1ActionPerformed
        if(allCheck1.isSelected()){
            lunes1.setSelected(true);
            martes1.setSelected(true);
            miercoles1.setSelected(true);
            jueves1.setSelected(true);
            viernes1.setSelected(true);
            sabado1.setSelected(true);
            domingo1.setSelected(true);
        } else {
            lunes1.setSelected(false);
            martes1.setSelected(false);
            miercoles1.setSelected(false);
            jueves1.setSelected(false);
            viernes1.setSelected(false);
            sabado1.setSelected(false);
            domingo1.setSelected(false);
        }
    }//GEN-LAST:event_allCheck1ActionPerformed

    private void jList2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseEntered

    }//GEN-LAST:event_jList2MouseEntered

    private void eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarActionPerformed
        int p = jList2.getSelectedIndex();
        if(p!=-1){
            HR.getHoras().remove(p);
            listarTiempos();
        } else {
            mensaje("UPS!", "No hay un tiempo seleccionado.", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_eliminarActionPerformed

    private void eliminarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarTodoActionPerformed
        HR.getHoras().clear();
        listarTiempos();
    }//GEN-LAST:event_eliminarTodoActionPerformed

    private void nDuracion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nDuracion1ActionPerformed
        if(!addCheck.isSelected()){
            HR.getHoras().get(pos).setDuracion(nDuracion1.getSelectedIndex()+1);
            listarTiempos();
        }
    }//GEN-LAST:event_nDuracion1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addCheck;
    private javax.swing.JButton agregarEdit;
    private javax.swing.JCheckBox allCheck1;
    private javax.swing.JCheckBox domingo1;
    private javax.swing.JMenuItem eliminar;
    private javax.swing.JMenuItem eliminarTodo;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JCheckBox jueves1;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbMin;
    private javax.swing.JPopupMenu listaMenu;
    private javax.swing.JCheckBox lunes1;
    private javax.swing.JCheckBox martes1;
    private javax.swing.JCheckBox miercoles1;
    private javax.swing.JComboBox<String> moment1;
    private javax.swing.JComboBox<String> nDuracion1;
    private javax.swing.JComboBox<String> nTimbres1;
    private javax.swing.JCheckBox sabado1;
    private javax.swing.JLabel titulo;
    private javax.swing.JCheckBox viernes1;
    // End of variables declaration//GEN-END:variables
}

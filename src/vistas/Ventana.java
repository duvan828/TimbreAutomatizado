package vistas;

import com.panamahitek.PanamaHitek_Arduino;
import com.panamahitek.ArduinoException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import listas.*;
import contror.*;
import java.awt.TextField;
import java.awt.Toolkit;
import modelos.*;

public class Ventana extends javax.swing.JFrame {

    static Ventana frame;
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    DefaultTableModel tb = new DefaultTableModel();
    DefaultListModel<String> list = new DefaultListModel<>();
    listaTiempos times = new listaTiempos();
    listaDias days = new listaDias();
    listaHorarios horarios;
    controlador control = new controlador();
    editarHorario FrameEditar = new editarHorario();
    tiempo time;
    horario hr;
    Calendar cal;
    int pos = -1;
    int sonadas;
    int select = control.getSelect();
    SerialPortEventListener listener = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent spe) {
            try {
                if(ino.isMessageAvailable()) mensaje("ARDUINO DICE", ino.printMessage(), JOptionPane.INFORMATION_MESSAGE);
            } catch (SerialPortException e) {
                mensaje("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
            catch (ArduinoException e) {
                mensaje("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    public Ventana() {
        initComponents();
        tb = (DefaultTableModel) jTable1.getModel();
        jList1.setModel(list);
        sonadas = 0;
        cargarDatos();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/icon.png")));
        if(select>-1) selectRow();
        hilo.start();
    }
    
    private void cargarDatos(){
        listaHorarios lstHr = control.horariosGuardados();
        if(lstHr == null){
            horarios = new listaHorarios();
        } else {
            horarios = lstHr;
        }
        listarHorarios();
        if(select>-1) jTextArea1.setText(horarios.get(select).toString());
    }
    
    private void selectRow(){
        for (int i = 0; i < horarios.len(); i++) {
            if(select==horarios.get(i).getId()){
                jTable1.setRowSelectionInterval(i, i);
            }
        }
    }
    
    private void mensaje(String titulo, String texto, int icon){
        JOptionPane.showMessageDialog(null, texto, titulo, icon);
    }
    
    public void recibirEdit(horario h){
        control.editHorario(h);
        cargarDatos();
    }
    
    private void guardarTiempo(){
        try {
            time = new tiempo();
            time.setHora((int) sp1.getValue());
            time.setMinutos((int) sp2.getValue());
            time.setMomento(moment.getSelectedIndex());
            time.setRepeticiones(nTimbres.getSelectedIndex()+1);
            times.add(time);
            list.addElement(time.toString());
            //listarTiempos();
        } catch (Exception e) {
            mensaje("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listarTiempos(){
        list.clear();
        for (int i = 0; i < times.len(); i++) {
            list.addElement(times.get(i).toString());
        }
    }
    
    private void guardarHorario(){
        try {
            capturarDias();
            if(times.len() == 0) mensaje("UPS!","No hay horas guardadas.", JOptionPane.WARNING_MESSAGE);
            else if (days.len() == 0) mensaje("UPS!","No hay días seleccionados.", JOptionPane.WARNING_MESSAGE);
            else {
                hr = new horario(horarios);
                hr.setHoras(times);
                hr.setDias(days);
                control.saveHorario(hr);
                horarios.add(hr);
                listarHorarios();
                times = new listaTiempos();
                days = new listaDias();
                limpiar();
            }
        } catch (Exception e) {
            mensaje("Error","Error: "+ e, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listarHorarios(){
        tb.setRowCount(horarios.len());
        for (int i = 0; i < horarios.len(); i++) {
            tb.setValueAt(horarios.get(i).getId(), i, 0);
            tb.setValueAt(horarios.get(i).getHoras().listarHoras(), i, 1);
            tb.setValueAt(horarios.get(i).getDias().mostrar(), i, 2);
        }
    }
    
    private void conexionArduino(){
        try {
            if(ino.getSerialPorts().isEmpty()) throw new Exception("No está el dispositivo conectado, verifique la conexión.");
            else {
                String puerto = ino.getSerialPorts().get(0); 
                ino.arduinoRXTX(puerto, 9600, listener);
            }
        } catch (Exception e) {
            mensaje("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

    private void notCheckAll(JCheckBox ch){
        if(!ch.isSelected()){
            allCheck.setSelected(false);
        }
    }
    
    private void capturarDias(){
        if(lunes.isSelected()) days.add(2); 
        if(martes.isSelected())days.add(3); 
        if(miercoles.isSelected())days.add(4); 
        if(jueves.isSelected())days.add(5); 
        if(viernes.isSelected())days.add(6); 
        if(sabado.isSelected())days.add(7); 
        if(domingo.isSelected()) days.add(1); 
    }
    
    private String darDia(int i){
        String salida = "";
        switch(i){
            case 1: salida = "Dom"; break;
            case 2: salida = "Lun"; break;
            case 3: salida = "Mar"; break;
            case 4: salida = "Mié"; break;
            case 5: salida = "Jue"; break;
            case 6: salida = "Vie"; break;
            case 7: salida = "Sáb"; break;
        }
        return salida;
    }
    
    private boolean comprobarHora(int hora, int min, int mom, int dia){
        if(pos>-1){
            listaTiempos tms = horarios.get(pos).getHoras();
            listaDias dys = horarios.get(pos).getDias();
            for(int i = 0; i<tms.len(); i++){
                if(hora==tms.get(i).getHora()&&min==tms.get(i).getMinutos()&&mom==tms.get(i).getMomento()){
                    for (int j = 0; j < dys.len(); j++) {
                        if(dia==dys.get(j)){
                            sonadas = tms.get(i).getRepeticiones();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private void encender(){
        try {
            ino.sendData("1");
        } catch (ArduinoException | SerialPortException ex) {
            mensaje("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void apagar(){
        try {
            ino.sendData("0");
        } catch (ArduinoException | SerialPortException ex) {
            mensaje("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void enviarInformacion() throws Exception{
        try {
            String salida = "2,";
            cal = new GregorianCalendar();
            int hora = cal.get(Calendar.HOUR);
            if(hora==0) hora = 12;
            int min = cal.get(Calendar.MINUTE);
            int seg = cal.get(Calendar.SECOND);
            int mom = cal.get(Calendar.AM_PM);
            int dia = cal.get(Calendar.DAY_OF_WEEK);
            salida += hora+"," + min+"," + seg+","+mom+","+dia+",";
            for (int i = 0; i < horarios.get(pos).getHoras().len(); i++) {
                salida += horarios.get(pos).getHoras().get(i).getHora()+"-"
                       + horarios.get(pos).getHoras().get(i).getMinutos()+"-"
                       + horarios.get(pos).getHoras().get(i).getMomento()+"-"
                       + horarios.get(pos).getHoras().get(i).getRepeticiones()+"-*";
            }
            salida += ",";
            for (int i = 0; i < horarios.get(pos).getDias().len(); i++)
                salida += horarios.get(pos).getDias().get(i)+"*";
            salida += ",";
            //System.out.println(salida);
            ino.sendData(salida);
        } catch (ArduinoException | SerialPortException ex) {
            throw new Exception("No está el dispositivo conectado, verifique la conexión.");
        }
    }
    
    // HILO DE EJECUCIÓN
    Thread hilo = new Thread(){
        @Override
        public void run() {
            try {
                cal = new GregorianCalendar();
                int hora = cal.get(Calendar.HOUR);
                int min = cal.get(Calendar.MINUTE);
                int seg = cal.get(Calendar.SECOND);
                int mm = cal.get(Calendar.AM_PM);
                int dia = cal.get(Calendar.DAY_OF_WEEK);
                int contSeg = 0;
                boolean estado = false;
                String mom = "";
                if(hora==0) hora = 12;
                if(mm==0) mom = "AM";
                else {mom = "PM";}
                while (true){
                    //Reloj
                    if(seg<59) {
                        seg++;
                        if(contSeg<3&&sonadas>0&&estado){
                            contSeg++;
                        }
                        else if(estado&&contSeg==3){
                            contSeg=0;
                            sonadas--;
                        } 
                    }
                    else { 
                        seg = 0;
                        if(min<59){
                            min++;
                            if(comprobarHora(hora, min, mm, dia)) estado = true;
                            else estado = false;
                        }
                        else{ 
                            min = 0;
                            if(comprobarHora(hora, min, mm, dia)) estado = true;
                            else estado = false;
                            if(hora<12) hora++;
                            else {
                               hora = 1;
                               if(Calendar.AM==0)mom = "AM";
                               else mom = "PM";
                               
                            };
                        }
                    }
                    /*if(contSeg==1){
                        //encender();
                        System.out.println("Encendido");
                    } else if (contSeg==3){
                        //apagar();
                        System.out.println("Apagado");
                    }*/
                    

                    //como se mostrará
                    String h = hora+"", m = min+"", s = seg+"";
                    if(seg<10) s = "0"+s;
                    if(min<10) m = "0"+m;
                    if(hora<10) h = "0"+h;

                    tiempo.setText(h+":"+m+":"+s+ " "+ mom + " "+ darDia(dia));
                    sleep(1000);
                }
            } catch (Exception e) {
                mensaje("Error", e.toString(), JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    
   private void limpiar(){
       lunes.setSelected(false);
       martes.setSelected(false);
       miercoles.setSelected(false);
       jueves.setSelected(false);
       viernes.setSelected(false);
       sabado.setSelected(false);
       domingo.setSelected(false);
       allCheck.setSelected(false);
       list.setSize(0);
       nTimbres.setSelectedIndex(0);
   }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        popTabla = new javax.swing.JPopupMenu();
        popEditar = new javax.swing.JMenuItem();
        popEliminar = new javax.swing.JMenuItem();
        popLista = new javax.swing.JPopupMenu();
        eliminarLista = new javax.swing.JMenuItem();
        eliminarTodos = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        sp2 = new javax.swing.JSpinner();
        sp1 = new javax.swing.JSpinner();
        moment = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        nTimbres = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        up = new javax.swing.JButton();
        tiempo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        allCheck = new javax.swing.JCheckBox();
        lunes = new javax.swing.JCheckBox();
        martes = new javax.swing.JCheckBox();
        miercoles = new javax.swing.JCheckBox();
        jueves = new javax.swing.JCheckBox();
        viernes = new javax.swing.JCheckBox();
        sabado = new javax.swing.JCheckBox();
        domingo = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();

        popTabla.setBackground(new java.awt.Color(4, 36, 57));

        popEditar.setBackground(new java.awt.Color(9, 43, 78));
        popEditar.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        popEditar.setForeground(new java.awt.Color(255, 204, 51));
        popEditar.setText("Editar");
        popEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popEditarActionPerformed(evt);
            }
        });
        popTabla.add(popEditar);

        popEliminar.setBackground(new java.awt.Color(9, 43, 78));
        popEliminar.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        popEliminar.setForeground(new java.awt.Color(255, 204, 51));
        popEliminar.setText("Eliminar");
        popEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popEliminarActionPerformed(evt);
            }
        });
        popTabla.add(popEliminar);

        eliminarLista.setBackground(new java.awt.Color(9, 43, 78));
        eliminarLista.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        eliminarLista.setForeground(new java.awt.Color(255, 204, 51));
        eliminarLista.setText("Eliminar");
        eliminarLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarListaActionPerformed(evt);
            }
        });
        popLista.add(eliminarLista);

        eliminarTodos.setBackground(new java.awt.Color(9, 43, 78));
        eliminarTodos.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        eliminarTodos.setForeground(new java.awt.Color(255, 204, 51));
        eliminarTodos.setText("Eliminar Todos");
        eliminarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarTodosActionPerformed(evt);
            }
        });
        popLista.add(eliminarTodos);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Timbre Automático");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(1, 34, 57));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HORARIOS");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 30));

        jSeparator1.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator1.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator1.setAlignmentY(2.0F);
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 170, 10));

        sp2.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        sp2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 5));
        sp2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(sp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 50, 30));

        sp1.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        sp1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 12, 1));
        sp1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sp1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sp1StateChanged(evt);
            }
        });
        sp1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sp1KeyReleased(evt);
            }
        });
        jPanel1.add(sp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 50, 30));

        moment.setBackground(new java.awt.Color(15, 65, 98));
        moment.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        moment.setForeground(new java.awt.Color(255, 204, 51));
        moment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AM", "PM" }));
        moment.setBorder(null);
        moment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        moment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                momentActionPerformed(evt);
            }
        });
        jPanel1.add(moment, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, 30));

        jButton1.setBackground(new java.awt.Color(23, 83, 123));
        jButton1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/agregar.png"))); // NOI18N
        jButton1.setText("AGREGAR");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton1.setIconTextGap(10);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 170, 40));

        jList1.setBackground(new java.awt.Color(36, 46, 52));
        jList1.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jList1.setForeground(new java.awt.Color(255, 204, 51));
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setComponentPopupMenu(popLista);
        jList1.setSelectionBackground(new java.awt.Color(56, 96, 169));
        jScrollPane2.setViewportView(jList1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 170, 220));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 204, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("HORAS");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 190, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("vez.");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 50, 30));

        nTimbres.setBackground(new java.awt.Color(15, 65, 98));
        nTimbres.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        nTimbres.setForeground(new java.awt.Color(255, 204, 51));
        nTimbres.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        nTimbres.setBorder(null);
        nTimbres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nTimbresActionPerformed(evt);
            }
        });
        jPanel1.add(nTimbres, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 50, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Sonará: ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 60, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 450));

        jPanel2.setBackground(new java.awt.Color(36, 46, 52));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 204, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("HORARIOS GUARDADOS");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 30));

        jSeparator3.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator3.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator3.setAlignmentY(2.0F);
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 170, 10));

        jTable1.setBackground(new java.awt.Color(1, 34, 57));
        jTable1.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 204, 51));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "HORAS", "DIAS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setComponentPopupMenu(popTabla);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(35);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(35);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 480, 100));

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(1, 34, 57));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 204, 51));
        jTextArea1.setRows(2);
        jTextArea1.setBorder(null);
        jTextArea1.setFocusable(false);
        jScrollPane3.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 480, 50));

        jButton4.setBackground(new java.awt.Color(255, 204, 51));
        jButton4.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/calendario.png"))); // NOI18N
        jButton4.setText("ESTABLECER");
        jButton4.setBorder(null);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusPainted(false);
        jButton4.setIconTextGap(8);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 160, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 500, 260));

        jPanel3.setBackground(new java.awt.Color(1, 39, 65));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        up.setBackground(new java.awt.Color(63, 188, 78));
        up.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        up.setForeground(new java.awt.Color(255, 255, 255));
        up.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usb.png"))); // NOI18N
        up.setText("CONECTAR DISPOSITIVO");
        up.setBorder(null);
        up.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        up.setFocusPainted(false);
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });
        jPanel3.add(up, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 200, 40));

        tiempo.setBackground(new java.awt.Color(255, 255, 255));
        tiempo.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        tiempo.setForeground(new java.awt.Color(255, 204, 51));
        tiempo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tiempo.setText("00:00:00");
        tiempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tiempo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel3.add(tiempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 40));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 500, 60));

        jPanel4.setBackground(new java.awt.Color(1, 45, 75));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator2.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel4.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 10, 50));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 204, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("DÍAS");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 40, 50));

        allCheck.setBackground(new java.awt.Color(1, 45, 75));
        allCheck.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        allCheck.setForeground(new java.awt.Color(255, 255, 255));
        allCheck.setText("Todos");
        allCheck.setBorder(null);
        allCheck.setContentAreaFilled(false);
        allCheck.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        allCheck.setFocusPainted(false);
        allCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        allCheck.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        allCheck.setRolloverEnabled(true);
        allCheck.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        allCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCheckActionPerformed(evt);
            }
        });
        jPanel4.add(allCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 70, -1));

        lunes.setBackground(new java.awt.Color(1, 45, 75));
        lunes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lunes.setForeground(new java.awt.Color(255, 255, 255));
        lunes.setText("Lunes");
        lunes.setBorder(null);
        lunes.setContentAreaFilled(false);
        lunes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lunes.setFocusPainted(false);
        lunes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        lunes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        lunes.setRolloverEnabled(true);
        lunes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        lunes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lunesActionPerformed(evt);
            }
        });
        jPanel4.add(lunes, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 70, -1));

        martes.setBackground(new java.awt.Color(1, 45, 75));
        martes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        martes.setForeground(new java.awt.Color(255, 255, 255));
        martes.setText("Martes");
        martes.setBorder(null);
        martes.setContentAreaFilled(false);
        martes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        martes.setFocusPainted(false);
        martes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        martes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        martes.setRolloverEnabled(true);
        martes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        martes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                martesActionPerformed(evt);
            }
        });
        jPanel4.add(martes, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 90, -1));

        miercoles.setBackground(new java.awt.Color(1, 45, 75));
        miercoles.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        miercoles.setForeground(new java.awt.Color(255, 255, 255));
        miercoles.setText("Miércoles");
        miercoles.setBorder(null);
        miercoles.setContentAreaFilled(false);
        miercoles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        miercoles.setFocusPainted(false);
        miercoles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        miercoles.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        miercoles.setRolloverEnabled(true);
        miercoles.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        miercoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miercolesActionPerformed(evt);
            }
        });
        jPanel4.add(miercoles, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 90, -1));

        jueves.setBackground(new java.awt.Color(1, 45, 75));
        jueves.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jueves.setForeground(new java.awt.Color(255, 255, 255));
        jueves.setText("Jueves");
        jueves.setBorder(null);
        jueves.setContentAreaFilled(false);
        jueves.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jueves.setFocusPainted(false);
        jueves.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        jueves.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        jueves.setRolloverEnabled(true);
        jueves.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        jueves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juevesActionPerformed(evt);
            }
        });
        jPanel4.add(jueves, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 70, -1));

        viernes.setBackground(new java.awt.Color(1, 45, 75));
        viernes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        viernes.setForeground(new java.awt.Color(255, 255, 255));
        viernes.setText("Viernes");
        viernes.setBorder(null);
        viernes.setContentAreaFilled(false);
        viernes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viernes.setFocusPainted(false);
        viernes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        viernes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        viernes.setRolloverEnabled(true);
        viernes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        viernes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viernesActionPerformed(evt);
            }
        });
        jPanel4.add(viernes, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 80, -1));

        sabado.setBackground(new java.awt.Color(1, 45, 75));
        sabado.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        sabado.setForeground(new java.awt.Color(255, 255, 255));
        sabado.setText("Sábado");
        sabado.setBorder(null);
        sabado.setContentAreaFilled(false);
        sabado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sabado.setFocusPainted(false);
        sabado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        sabado.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        sabado.setRolloverEnabled(true);
        sabado.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        sabado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sabadoActionPerformed(evt);
            }
        });
        jPanel4.add(sabado, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 80, -1));

        domingo.setBackground(new java.awt.Color(1, 45, 75));
        domingo.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        domingo.setForeground(new java.awt.Color(255, 255, 255));
        domingo.setText("Domingo");
        domingo.setBorder(null);
        domingo.setContentAreaFilled(false);
        domingo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        domingo.setFocusPainted(false);
        domingo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        domingo.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        domingo.setRolloverEnabled(true);
        domingo.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        domingo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                domingoActionPerformed(evt);
            }
        });
        jPanel4.add(domingo, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 90, -1));

        jButton5.setBackground(new java.awt.Color(255, 204, 51));
        jButton5.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salvar.png"))); // NOI18N
        jButton5.setText("GUARDAR");
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusPainted(false);
        jButton5.setIconTextGap(8);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, 160, 40));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 500, 130));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void momentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_momentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_momentActionPerformed

    private void martesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_martesActionPerformed
        notCheckAll(martes);
    }//GEN-LAST:event_martesActionPerformed

    private void viernesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viernesActionPerformed
        notCheckAll(viernes);
    }//GEN-LAST:event_viernesActionPerformed

    private void miercolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miercolesActionPerformed
        notCheckAll(miercoles);
    }//GEN-LAST:event_miercolesActionPerformed

    private void allCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCheckActionPerformed
        if(allCheck.isSelected()){
            lunes.setSelected(true);
            martes.setSelected(true);
            miercoles.setSelected(true);
            jueves.setSelected(true);
            viernes.setSelected(true);
            sabado.setSelected(true);
            domingo.setSelected(true);
        } else {
            lunes.setSelected(false);
            martes.setSelected(false);
            miercoles.setSelected(false);
            jueves.setSelected(false);
            viernes.setSelected(false);
            sabado.setSelected(false);
            domingo.setSelected(false);
        }
    }//GEN-LAST:event_allCheckActionPerformed

    private void lunesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lunesActionPerformed
        notCheckAll(lunes);
    }//GEN-LAST:event_lunesActionPerformed

    private void juevesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juevesActionPerformed
        notCheckAll(jueves);
    }//GEN-LAST:event_juevesActionPerformed

    private void sabadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sabadoActionPerformed
        notCheckAll(sabado);
    }//GEN-LAST:event_sabadoActionPerformed

    private void domingoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_domingoActionPerformed
        notCheckAll(domingo);
    }//GEN-LAST:event_domingoActionPerformed

    private void nTimbresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nTimbresActionPerformed
        if(nTimbres.getSelectedIndex()>0) jLabel4.setText("veces.");
        else jLabel4.setText("vez.");
    }//GEN-LAST:event_nTimbresActionPerformed

    private void sp1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sp1StateChanged
        
    }//GEN-LAST:event_sp1StateChanged

    private void sp1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sp1KeyReleased
 
    }//GEN-LAST:event_sp1KeyReleased

    private void upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upActionPerformed
        conexionArduino();
    }//GEN-LAST:event_upActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarTiempo();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        guardarHorario();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jTextArea1.setText(horarios.get(jTable1.getSelectedRow()).toString());
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            if(jTable1.getSelectedRow()>-1){
                pos = jTable1.getSelectedRow();
                enviarInformacion();
                control.setSelect(horarios.get(pos).getId());
                mensaje("INFORMACIÓN", "El timbre ha sido programado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mensaje("ADVERTENCIA", "Debe seleccionar un horarios", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            mensaje("ERROR", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void popEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popEliminarActionPerformed
        int p = jTable1.getSelectedRow();
        int op = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el horario con id: "+horarios.get(p).getId()+"?");
        if(op==0){
            if(p!=-1){
                control.eliminarHorario(p);
                cargarDatos();
                selectRow();
            } else {
                mensaje("UPS!", "No hay horario seleccionado.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_popEliminarActionPerformed

    private void eliminarListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarListaActionPerformed
        int p = jList1.getSelectedIndex();
        if(p!=-1){
            times.remove(p);
            listarTiempos();
        } else {
            mensaje("UPS!", "No hay un tiempo seleccionado.", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_eliminarListaActionPerformed

    private void popEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popEditarActionPerformed
        int p = jTable1.getSelectedRow();
        if(p!=-1){
            FrameEditar.setVisible(true);
            FrameEditar.aplicarDatos(frame, horarios.get(p));
        } else {
            mensaje("UPS!", "No hay horario seleccionado.", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_popEditarActionPerformed

    private void eliminarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarTodosActionPerformed
        times.clear();
        listarTiempos();
    }//GEN-LAST:event_eliminarTodosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        frame = new Ventana();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allCheck;
    private javax.swing.JCheckBox domingo;
    private javax.swing.JMenuItem eliminarLista;
    private javax.swing.JMenuItem eliminarTodos;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JCheckBox jueves;
    private javax.swing.JCheckBox lunes;
    private javax.swing.JCheckBox martes;
    private javax.swing.JCheckBox miercoles;
    private javax.swing.JComboBox<String> moment;
    private javax.swing.JComboBox<String> nTimbres;
    private javax.swing.JMenuItem popEditar;
    private javax.swing.JMenuItem popEliminar;
    private javax.swing.JPopupMenu popLista;
    private javax.swing.JPopupMenu popTabla;
    private javax.swing.JCheckBox sabado;
    private javax.swing.JSpinner sp1;
    private javax.swing.JSpinner sp2;
    private javax.swing.JLabel tiempo;
    private javax.swing.JButton up;
    private javax.swing.JCheckBox viernes;
    // End of variables declaration//GEN-END:variables
}

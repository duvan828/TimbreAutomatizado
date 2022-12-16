import com.panamahitek.PanamaHitek_Arduino;
import com.panamahitek.ArduinoException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import jssc.SerialPortException;

public class Ventana extends javax.swing.JFrame {

    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    DefaultTableModel tb = new DefaultTableModel();
    DefaultListModel<String> list = new DefaultListModel<>();
    listaTiempos times = new listaTiempos();
    listaDias days = new listaDias();
    listaHorarios horarios = new listaHorarios();
    tiempo time;
    horario hr;
    Calendar cal;
    int pos = -1;
    int sonadas;
    public Ventana() {
        initComponents();
        //conexionArduino();
        tb = (DefaultTableModel) jTable1.getModel();
        jList1.setModel(list);
        sonadas = 0;
        hilo.start();
    }
    
    private void mensaje(String titulo, String texto, int icon){
        JOptionPane.showMessageDialog(null, texto, titulo, icon);
    }
    
    private void guardarTiempo(){
        try {
            time = new tiempo();
            time.setHora((int) sp1.getValue());
            time.setMinutos((int) sp2.getValue());
            time.setMomento(moment.getSelectedItem().toString());
            time.setRepeticiones(nTimbres.getSelectedIndex()+1);
            times.add(time);
            list.addElement(time.toString());
            //listarTiempos();
        } catch (Exception e) {
            mensaje("Error", e.toString(), JOptionPane.ERROR_MESSAGE);
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
            hr = new horario();
            hr.setHoras(times);
            hr.setDias(days);
            horarios.add(hr);
            listarHorarios();
            times = new listaTiempos();
            days = new listaDias();
        } catch (Exception e) {
            mensaje("Error", e.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listarHorarios(){
        tb.setRowCount(horarios.len());
        for (int i = 0; i < horarios.len(); i++) {
            tb.setValueAt(i+1, i, 0);
            tb.setValueAt(horarios.get(i).getHoras().listarHoras(), i, 1);
            tb.setValueAt(horarios.get(i).getDias().mostrar(), i, 2);
        }
    }
    
    private void conexionArduino(){
        try {
            ino.arduinoTX("COM10", 9600);
        } catch (ArduinoException e) {
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
    
    private boolean comprobarHora(int hora, int min, String mom, int dia){
        if(pos>-1){
            listaTiempos tms = horarios.get(pos).getHoras();
            listaDias dys = horarios.get(pos).getDias();
            for(int i = 0; i<tms.len(); i++){
                if(hora==tms.get(i).getHora()&&min==tms.get(i).getMinutos()&&mom.equals(tms.get(i).getMomento())){
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
    
    // HILO DE EJECUCIÓN
    Thread hilo = new Thread(){
        @Override
        public void run() {
            try {
                cal = new GregorianCalendar();
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                int seg = cal.get(Calendar.SECOND);
                int dia = cal.get(Calendar.DAY_OF_WEEK);
                int contSeg = 0;
                boolean estado = false;
                String mom = "";
                if(hora>12){ mom = "PM"; hora = hora-12;}
                else {mom = "AM"; hora = 1;}
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
                            if(comprobarHora(hora, min, mom, dia)) estado = true;
                            else estado = false;
                        }
                        else{ 
                            min = 0;
                            if(hora<12) hora++;
                            else { 
                               if(hora>12){ mom = "PM"; hora = hora-12;}
                               else {mom = "AM"; hora = 1;}
                               
                            };
                        }
                    }
                    if(contSeg==1){
                        encender();
                        System.out.println("Encendido");
                    } else if (contSeg==3){
                        apagar();
                        System.out.println("Apagado");
                    }
                    

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
    
   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
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
        back = new javax.swing.JButton();
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
        sp2.setBorder(null);
        jPanel1.add(sp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 50, 30));

        sp1.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        sp1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 12, 1));
        sp1.setBorder(null);
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
        moment.setForeground(new java.awt.Color(255, 255, 255));
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
        jButton1.setText("AGREGAR");
        jButton1.setBorder(null);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        nTimbres.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        nTimbres.setForeground(new java.awt.Color(255, 255, 255));
        nTimbres.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
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
        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 204, 51));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "HORAS", "DIAS"
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
        jButton4.setText("ESTABLECER");
        jButton4.setBorder(null);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 160, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 500, 260));

        jPanel3.setBackground(new java.awt.Color(1, 39, 65));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        back.setBackground(new java.awt.Color(219, 24, 41));
        back.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        back.setForeground(new java.awt.Color(255, 255, 255));
        back.setText("APAGAR");
        back.setBorder(null);
        back.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel3.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 80, 40));

        up.setBackground(new java.awt.Color(63, 188, 78));
        up.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        up.setForeground(new java.awt.Color(255, 255, 255));
        up.setText("ENCENDER");
        up.setBorder(null);
        up.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });
        jPanel3.add(up, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 90, 40));

        tiempo.setBackground(new java.awt.Color(255, 255, 255));
        tiempo.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        tiempo.setForeground(new java.awt.Color(255, 204, 51));
        tiempo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tiempo.setText("00:00:00");
        tiempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tiempo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel3.add(tiempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 190, 40));

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

        allCheck.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        allCheck.setForeground(new java.awt.Color(255, 255, 255));
        allCheck.setText("Todos");
        allCheck.setBorder(null);
        allCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCheckActionPerformed(evt);
            }
        });
        jPanel4.add(allCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, -1, -1));

        lunes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lunes.setForeground(new java.awt.Color(255, 255, 255));
        lunes.setText("Lunes");
        lunes.setBorder(null);
        lunes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lunesActionPerformed(evt);
            }
        });
        jPanel4.add(lunes, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 70, -1));

        martes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        martes.setForeground(new java.awt.Color(255, 255, 255));
        martes.setText("Martes");
        martes.setBorder(null);
        martes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                martesActionPerformed(evt);
            }
        });
        jPanel4.add(martes, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 90, -1));

        miercoles.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        miercoles.setForeground(new java.awt.Color(255, 255, 255));
        miercoles.setText("Miércoles");
        miercoles.setBorder(null);
        miercoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miercolesActionPerformed(evt);
            }
        });
        jPanel4.add(miercoles, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, -1, -1));

        jueves.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jueves.setForeground(new java.awt.Color(255, 255, 255));
        jueves.setText("Jueves");
        jueves.setBorder(null);
        jueves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juevesActionPerformed(evt);
            }
        });
        jPanel4.add(jueves, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        viernes.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        viernes.setForeground(new java.awt.Color(255, 255, 255));
        viernes.setText("Viernes");
        viernes.setBorder(null);
        viernes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viernesActionPerformed(evt);
            }
        });
        jPanel4.add(viernes, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, -1, -1));

        sabado.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        sabado.setForeground(new java.awt.Color(255, 255, 255));
        sabado.setText("Sábado");
        sabado.setBorder(null);
        sabado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sabadoActionPerformed(evt);
            }
        });
        jPanel4.add(sabado, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, -1, -1));

        domingo.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        domingo.setForeground(new java.awt.Color(255, 255, 255));
        domingo.setText("Domingo");
        domingo.setBorder(null);
        domingo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                domingoActionPerformed(evt);
            }
        });
        jPanel4.add(domingo, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 90, -1));

        jButton5.setBackground(new java.awt.Color(255, 204, 51));
        jButton5.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setText("GUARDAR");
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        encender();
    }//GEN-LAST:event_upActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        apagar();
    }//GEN-LAST:event_backActionPerformed

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
        if(jTable1.getSelectedRow()>-1){
            pos = jTable1.getSelectedRow();
            mensaje("INFORMACIÓN", "El timbre ha sido programado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mensaje("ADVERTENCIA", "Debe seleccionar un horarios", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allCheck;
    private javax.swing.JButton back;
    private javax.swing.JCheckBox domingo;
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
    private javax.swing.JCheckBox sabado;
    private javax.swing.JSpinner sp1;
    private javax.swing.JSpinner sp2;
    private javax.swing.JLabel tiempo;
    private javax.swing.JButton up;
    private javax.swing.JCheckBox viernes;
    // End of variables declaration//GEN-END:variables
}

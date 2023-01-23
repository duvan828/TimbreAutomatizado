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
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.table.*;
import modelos.*;

public class Ventana extends javax.swing.JFrame {

    static Ventana frame;
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    DefaultTableModel tb, tbHr = new DefaultTableModel();
    DefaultListModel<String> list = new DefaultListModel<>();
    listaTiempos times = new listaTiempos();
    listaDias days = new listaDias();
    listaHorarios horarios;
    controlador control = new controlador();
    editarHorario FrameEditar = new editarHorario();
    JTableHeader tableHeader;
    TableColumnModel tableColumnModel;
    TableColumn tableColumn;
    tiempo time;
    horario hr;
    Calendar cal;
    int pos = -1;
    int sonadas;
    int contAgregados = 0;
    boolean sonar = false;
    int select = control.getSelect();
    SerialPortEventListener listener = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent spe) {
            try {
                if(ino.isMessageAvailable()) mensaje("TIMBRE DICE", ino.printMessage(), JOptionPane.INFORMATION_MESSAGE);
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
        try {
            tb = (DefaultTableModel) jTable1.getModel();
            tbHr = (DefaultTableModel) tbHorario.getModel();
            tableHeader = tbHorario.getTableHeader();
            tableColumnModel = tableHeader.getColumnModel();
            tableColumn = tableColumnModel.getColumn(0);
            tableColumn.setHeaderValue("NO HAY HORARIO SELECCIONADO");
            tableHeader.repaint();
            tableColumn.setMaxWidth(tableHeader.getWidth());
            tableColumn.setWidth(tableHeader.getWidth());
            jList1.setModel(list);
            sonadas = 0;
            cargarDatos();
            timbre.setBackground(Color.decode("#1BE03F"));
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/icon.png")));
            hilo.start();
        } catch (Exception e) {
            mensaje("ERROR", "Ha ocurrido un error inesperado!\nError: "+e.getMessage(), pos);
        }
    }
    
    private void cargarDatos(){
        listaHorarios lstHr = control.horariosGuardados();
        if(lstHr == null){
            horarios = new listaHorarios();
        } else {
            horarios = lstHr;
        }
        listarHorarios();
        if(select>-1){
            selectRow();
            listarHorario(pos);
        }
    }
    
    private void selectRow(){
        for (int i = 0; i < horarios.len(); i++) {
            if(select==horarios.get(i).getId()){
                jTable1.setRowSelectionInterval(i, i);
                pos = i;
            }
        }
    }
    
    private void mensaje(String titulo, String texto, int icon){
        JOptionPane.showMessageDialog(null, texto, titulo, icon);
    }
    
    public void recibirEdit(horario h){
        control.editHorario(h);
        cargarDatos();
        listarHorarios();
    }
    
    private void guardarTiempo(){
        try {
            if(contAgregados<20){
                time = new tiempo();
                time.setHora(Integer.parseInt(sp1.getText()));
                time.setMinutos(Integer.parseInt(sp2.getText()));
                time.setMomento(moment.getSelectedIndex());
                time.setRepeticiones(nTimbres.getSelectedIndex()+1);
                time.setDuracion(nDuracion.getSelectedIndex()+1);
                times.add(time);
                list.addElement(time.toString());
                contAgregados++;
                lbContador.setText(contAgregados+" de 20");
            } else throw new Exception("Solo es posible programar 20 horas para un horario.");
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
                hr.setNombre(lbNombre.getText());
                hr.setHoras(times);
                hr.setDias(days);
                control.saveHorario(hr);
                horarios.add(hr);
                listarHorarios();
                times = new listaTiempos();
                days = new listaDias();
                limpiar();
                lbContador.setText("0 de 20");
            }
        } catch (Exception e) {
            mensaje("Error","Error: "+ e, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listarHorarios(){
        tb.setRowCount(horarios.len());
        for (int i = 0; i < horarios.len(); i++) {
            tb.setValueAt(horarios.get(i).getId(), i, 0);
            tb.setValueAt(horarios.get(i).getNombre(), i, 1);
        }
    }
    
    private void listarHorario(int ps){
        horario obj  = horarios.get(ps);
        tableColumn.setHeaderValue(obj.getDias().mostrar());
        tableHeader.repaint();
        tbHr.setRowCount(obj.getHoras().len());
        lbId.setText(obj.getId()+"");
        lbLenHoras.setText(obj.getHoras().len()+"");
        lbLenDias.setText(obj.getDias().len()+"");
        for (int i = 0; i < obj.getHoras().len(); i++) {
            tbHr.setValueAt((i+1)+". "+obj.getHoras().get(i).toString(), i, 0);
        }
    }
    
    private void conexionArduino(){
        try {
            
            if(ino.getSerialPorts().isEmpty()) throw new Exception("No está el dispositivo conectado, verifique la conexión.");
            else {
                String puerto = ino.getSerialPorts().get(0); 
                ino.arduinoRXTX(puerto, 9600, listener);
                mensaje("CONFIRMACIÓN", "Conectado correctamente.", JOptionPane.INFORMATION_MESSAGE);
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
            timbre.setText("APAGAR");
            timbre.setBackground(Color.decode("#E01B1B"));
            timbre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/off.png")));
            sonar = true;
        } catch (ArduinoException | SerialPortException ex) {
            mensaje("Error", "No está el dispositivo conectado, verifique la conexión.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void apagar(){
        try {
            ino.sendData("0");
            timbre.setText("ENCENDER");
            timbre.setBackground(Color.decode("#1BE03F"));
            timbre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/on.png")));
            sonar = false;
        } catch (ArduinoException | SerialPortException ex) {
            mensaje("Error", "No está el dispositivo conectado, verifique la conexión.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void enviarInformacion() throws Exception{
        try {
            pos = jTable1.getSelectedRow();
            int id = horarios.get(pos).getId();
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
                       + horarios.get(pos).getHoras().get(i).getRepeticiones()+"-"
                       + horarios.get(pos).getHoras().get(i).getDuracion()+"-*";
            }
            salida += ",";
            for (int i = 0; i < horarios.get(pos).getDias().len(); i++)
                salida += horarios.get(pos).getDias().get(i)+"*";
            salida += ",";
            //System.out.println(salida);
            ino.sendData(salida);
            control.setSelect(id);
            select = id;
        } catch (ArduinoException | SerialPortException ex) {
            throw new Exception("No se pudo programar el dispositivo, verifique inicio la conexión al dispositivo.");
        }
    }
    
    private String colocaCero(int n){
        if(n<10) return "0"+n;
        else return ""+n;
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
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        moment = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        lbContador = new javax.swing.JLabel();
        nTimbres = new javax.swing.JComboBox<>();
        nDuracion = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        sp2 = new javax.swing.JTextField();
        sp1 = new javax.swing.JTextField();
        spDown1 = new javax.swing.JLabel();
        spUp1 = new javax.swing.JLabel();
        spUp2 = new javax.swing.JLabel();
        spDown2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbHorario = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lbLenDias = new javax.swing.JLabel();
        lbId = new javax.swing.JLabel();
        lbLenHoras = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        timbre = new javax.swing.JButton();
        tiempo = new javax.swing.JLabel();
        conector = new javax.swing.JButton();
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
        jSeparator5 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        lbNombre = new javax.swing.JTextField();

        popTabla.setBackground(new java.awt.Color(4, 36, 57));

        popEditar.setBackground(new java.awt.Color(9, 43, 78));
        popEditar.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        popEditar.setForeground(new java.awt.Color(255, 204, 51));
        popEditar.setText("Editar");
        popEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popEditarActionPerformed(evt);
            }
        });
        popTabla.add(popEditar);

        popEliminar.setBackground(new java.awt.Color(9, 43, 78));
        popEliminar.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        popEliminar.setForeground(new java.awt.Color(255, 204, 51));
        popEliminar.setText("Eliminar");
        popEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popEliminarActionPerformed(evt);
            }
        });
        popTabla.add(popEliminar);

        eliminarLista.setBackground(new java.awt.Color(9, 43, 78));
        eliminarLista.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        eliminarLista.setForeground(new java.awt.Color(255, 204, 51));
        eliminarLista.setText("Eliminar");
        eliminarLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarListaActionPerformed(evt);
            }
        });
        popLista.add(eliminarLista);

        eliminarTodos.setBackground(new java.awt.Color(9, 43, 78));
        eliminarTodos.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        eliminarTodos.setForeground(new java.awt.Color(255, 204, 51));
        eliminarTodos.setText("Eliminar Todos");
        eliminarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarTodosActionPerformed(evt);
            }
        });
        popLista.add(eliminarTodos);

        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Duración:");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Timbre Automático 1.0.0v");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(1, 34, 57));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HORARIOS");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 310, -1));

        jSeparator1.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator1.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator1.setAlignmentY(2.0F);
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 290, 10));

        moment.setBackground(new java.awt.Color(15, 65, 98));
        moment.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        moment.setForeground(new java.awt.Color(255, 204, 51));
        moment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AM", "PM" }));
        moment.setBorder(null);
        moment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        moment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                momentActionPerformed(evt);
            }
        });
        jPanel1.add(moment, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 80, 40));

        jButton1.setBackground(new java.awt.Color(23, 83, 123));
        jButton1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
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
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 50));

        jList1.setBackground(new java.awt.Color(36, 46, 52));
        jList1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jList1.setForeground(new java.awt.Color(255, 204, 51));
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setComponentPopupMenu(popLista);
        jList1.setSelectionBackground(new java.awt.Color(56, 96, 169));
        jScrollPane2.setViewportView(jList1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 290, 250));

        lbContador.setBackground(new java.awt.Color(255, 255, 255));
        lbContador.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        lbContador.setForeground(new java.awt.Color(255, 204, 51));
        lbContador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbContador.setText("0 de 20");
        lbContador.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbContador.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(lbContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, 310, 40));

        nTimbres.setBackground(new java.awt.Color(15, 65, 98));
        nTimbres.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        nTimbres.setForeground(new java.awt.Color(255, 204, 51));
        nTimbres.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        nTimbres.setBorder(null);
        nTimbres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nTimbresActionPerformed(evt);
            }
        });
        jPanel1.add(nTimbres, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 110, 40));

        nDuracion.setBackground(new java.awt.Color(15, 65, 98));
        nDuracion.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        nDuracion.setForeground(new java.awt.Color(255, 204, 51));
        nDuracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        nDuracion.setBorder(null);
        nDuracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nDuracionActionPerformed(evt);
            }
        });
        jPanel1.add(nDuracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 110, 40));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 204, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("HORAS");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 310, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Tiempo (s):");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 100, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("N° Toques:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 120, 30));

        sp2.setBackground(new java.awt.Color(15, 65, 98));
        sp2.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        sp2.setForeground(new java.awt.Color(255, 204, 51));
        sp2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sp2.setText("00");
        sp2.setBorder(null);
        sp2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sp2KeyTyped(evt);
            }
        });
        jPanel1.add(sp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 60, 40));

        sp1.setEditable(false);
        sp1.setBackground(new java.awt.Color(15, 65, 98));
        sp1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        sp1.setForeground(new java.awt.Color(255, 204, 51));
        sp1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sp1.setText("01");
        sp1.setBorder(null);
        sp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sp1ActionPerformed(evt);
            }
        });
        sp1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sp1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sp1KeyTyped(evt);
            }
        });
        jPanel1.add(sp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 60, 40));

        spDown1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spDown1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/down.png"))); // NOI18N
        spDown1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spDown1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spDown1MouseClicked(evt);
            }
        });
        jPanel1.add(spDown1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, -1, 20));

        spUp1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spUp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/up.png"))); // NOI18N
        spUp1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spUp1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spUp1MouseClicked(evt);
            }
        });
        jPanel1.add(spUp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 90, -1, 20));

        spUp2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spUp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/up.png"))); // NOI18N
        spUp2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spUp2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spUp2MouseClicked(evt);
            }
        });
        jPanel1.add(spUp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, -1, 20));

        spDown2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spDown2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/down.png"))); // NOI18N
        spDown2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spDown2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spDown2MouseClicked(evt);
            }
        });
        jPanel1.add(spDown2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, -1, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 310, 560));

        jPanel2.setBackground(new java.awt.Color(36, 46, 52));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 204, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("HORARIOS GUARDADOS");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 300, -1));

        jSeparator3.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator3.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator3.setAlignmentY(2.0F);
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 280, 10));

        jTable1.setBackground(new java.awt.Color(1, 34, 57));
        jTable1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 204, 51));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOMBRE O DESCRIPCIÓN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setComponentPopupMenu(popTabla);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTable1.setRowHeight(30);
        jTable1.setSelectionBackground(new java.awt.Color(255, 204, 51));
        jTable1.setSelectionForeground(new java.awt.Color(1, 34, 57));
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(50);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 610, 160));

        jButton4.setBackground(new java.awt.Color(255, 204, 51));
        jButton4.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/calendario.png"))); // NOI18N
        jButton4.setText("PROGRAMAR");
        jButton4.setBorder(null);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusPainted(false);
        jButton4.setIconTextGap(8);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 370, 190, 40));

        tbHorario.setBackground(new java.awt.Color(1, 34, 57));
        tbHorario.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        tbHorario.setForeground(new java.awt.Color(255, 204, 51));
        tbHorario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "---"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbHorario.setFocusable(false);
        tbHorario.setRowHeight(20);
        tbHorario.setSelectionBackground(new java.awt.Color(255, 204, 51));
        tbHorario.setSelectionForeground(new java.awt.Color(1, 34, 57));
        tbHorario.setShowHorizontalLines(false);
        tbHorario.getTableHeader().setResizingAllowed(false);
        tbHorario.getTableHeader().setReorderingAllowed(false);
        tbHorario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbHorarioMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbHorarioMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(tbHorario);
        if (tbHorario.getColumnModel().getColumnCount() > 0) {
            tbHorario.getColumnModel().getColumn(0).setMinWidth(50);
            tbHorario.getColumnModel().getColumn(0).setPreferredWidth(50);
            tbHorario.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jPanel2.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 410, 150));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 204, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("N° DÍAS:");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 80, 20));

        jSeparator4.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator4.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator4.setAlignmentY(2.0F);
        jPanel2.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 260, 10));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 204, 51));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("DETALLES DE HORARIO");
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 280, 30));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 204, 51));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("ID:");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 30, 20));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 204, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("N° HORAS:");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 280, 100, 20));

        lbLenDias.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        lbLenDias.setForeground(new java.awt.Color(255, 255, 255));
        lbLenDias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbLenDias.setText("0");
        jPanel2.add(lbLenDias, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 300, 20, 20));

        lbId.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        lbId.setForeground(new java.awt.Color(255, 255, 255));
        lbId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbId.setText("0");
        jPanel2.add(lbId, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 260, 20, 20));

        lbLenHoras.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        lbLenHoras.setForeground(new java.awt.Color(255, 255, 255));
        lbLenHoras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbLenHoras.setText("0");
        jPanel2.add(lbLenHoras, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 280, 20, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 220, 630, 420));

        jPanel3.setBackground(new java.awt.Color(1, 39, 65));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        timbre.setBackground(new java.awt.Color(63, 188, 78));
        timbre.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        timbre.setForeground(new java.awt.Color(255, 255, 255));
        timbre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/on.png"))); // NOI18N
        timbre.setText("ENCENDER");
        timbre.setBorder(null);
        timbre.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        timbre.setFocusPainted(false);
        timbre.setIconTextGap(10);
        timbre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timbreActionPerformed(evt);
            }
        });
        jPanel3.add(timbre, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 10, 160, 60));

        tiempo.setBackground(new java.awt.Color(255, 255, 255));
        tiempo.setFont(new java.awt.Font("Segoe UI Symbol", 1, 36)); // NOI18N
        tiempo.setForeground(new java.awt.Color(255, 204, 51));
        tiempo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tiempo.setText("00:00:00");
        tiempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tiempo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel3.add(tiempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 380, 60));

        conector.setBackground(new java.awt.Color(0, 102, 204));
        conector.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        conector.setForeground(new java.awt.Color(255, 255, 255));
        conector.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usb.png"))); // NOI18N
        conector.setText("CONECTAR");
        conector.setBorder(null);
        conector.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        conector.setFocusPainted(false);
        conector.setIconTextGap(10);
        conector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conectorActionPerformed(evt);
            }
        });
        jPanel3.add(conector, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, 160, 60));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 80));

        jPanel4.setBackground(new java.awt.Color(1, 45, 75));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator2.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel4.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 10, 60));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 204, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("DÍAS");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 70, 50));

        allCheck.setBackground(new java.awt.Color(1, 45, 75));
        allCheck.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        allCheck.setForeground(new java.awt.Color(255, 255, 255));
        allCheck.setText("Todos");
        allCheck.setBorder(null);
        allCheck.setContentAreaFilled(false);
        allCheck.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        allCheck.setFocusPainted(false);
        allCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        allCheck.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        allCheck.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        allCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCheckActionPerformed(evt);
            }
        });
        jPanel4.add(allCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 80, -1));

        lunes.setBackground(new java.awt.Color(1, 45, 75));
        lunes.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lunes.setForeground(new java.awt.Color(255, 255, 255));
        lunes.setText("Lunes");
        lunes.setBorder(null);
        lunes.setContentAreaFilled(false);
        lunes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lunes.setFocusPainted(false);
        lunes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        lunes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        lunes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        lunes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lunesActionPerformed(evt);
            }
        });
        jPanel4.add(lunes, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 80, -1));

        martes.setBackground(new java.awt.Color(1, 45, 75));
        martes.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        martes.setForeground(new java.awt.Color(255, 255, 255));
        martes.setText("Martes");
        martes.setBorder(null);
        martes.setContentAreaFilled(false);
        martes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        martes.setFocusPainted(false);
        martes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        martes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        martes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        martes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                martesActionPerformed(evt);
            }
        });
        jPanel4.add(martes, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 90, -1));

        miercoles.setBackground(new java.awt.Color(1, 45, 75));
        miercoles.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        miercoles.setForeground(new java.awt.Color(255, 255, 255));
        miercoles.setText("Miércoles");
        miercoles.setBorder(null);
        miercoles.setContentAreaFilled(false);
        miercoles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        miercoles.setFocusPainted(false);
        miercoles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        miercoles.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        miercoles.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        miercoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miercolesActionPerformed(evt);
            }
        });
        jPanel4.add(miercoles, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 110, -1));

        jueves.setBackground(new java.awt.Color(1, 45, 75));
        jueves.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jueves.setForeground(new java.awt.Color(255, 255, 255));
        jueves.setText("Jueves");
        jueves.setBorder(null);
        jueves.setContentAreaFilled(false);
        jueves.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jueves.setFocusPainted(false);
        jueves.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        jueves.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        jueves.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        jueves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juevesActionPerformed(evt);
            }
        });
        jPanel4.add(jueves, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 80, -1));

        viernes.setBackground(new java.awt.Color(1, 45, 75));
        viernes.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        viernes.setForeground(new java.awt.Color(255, 255, 255));
        viernes.setText("Viernes");
        viernes.setBorder(null);
        viernes.setContentAreaFilled(false);
        viernes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viernes.setFocusPainted(false);
        viernes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        viernes.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        viernes.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        viernes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viernesActionPerformed(evt);
            }
        });
        jPanel4.add(viernes, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 90, -1));

        sabado.setBackground(new java.awt.Color(1, 45, 75));
        sabado.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        sabado.setForeground(new java.awt.Color(255, 255, 255));
        sabado.setText("Sábado");
        sabado.setBorder(null);
        sabado.setContentAreaFilled(false);
        sabado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sabado.setFocusPainted(false);
        sabado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        sabado.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        sabado.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        sabado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sabadoActionPerformed(evt);
            }
        });
        jPanel4.add(sabado, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, -1));

        domingo.setBackground(new java.awt.Color(1, 45, 75));
        domingo.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        domingo.setForeground(new java.awt.Color(255, 255, 255));
        domingo.setText("Domingo");
        domingo.setBorder(null);
        domingo.setContentAreaFilled(false);
        domingo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        domingo.setFocusPainted(false);
        domingo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        domingo.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        domingo.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cheque.png"))); // NOI18N
        domingo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                domingoActionPerformed(evt);
            }
        });
        jPanel4.add(domingo, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 110, -1));

        jButton5.setBackground(new java.awt.Color(255, 204, 51));
        jButton5.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/salvar.png"))); // NOI18N
        jButton5.setText("GUARDAR HORARIO");
        jButton5.setBorder(null);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusPainted(false);
        jButton5.setIconTextGap(8);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 230, 50));

        jSeparator5.setBackground(new java.awt.Color(255, 204, 51));
        jSeparator5.setForeground(new java.awt.Color(255, 204, 51));
        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel4.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 10, 50));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 204, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("NOMBRE");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 120, 50));

        lbNombre.setBackground(new java.awt.Color(15, 65, 98));
        lbNombre.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lbNombre.setForeground(new java.awt.Color(255, 255, 255));
        lbNombre.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lbNombre.setBorder(null);
        lbNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbNombreActionPerformed(evt);
            }
        });
        lbNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lbNombreKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lbNombreKeyTyped(evt);
            }
        });
        jPanel4.add(lbNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 250, 50));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, 630, 140));

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

    }//GEN-LAST:event_nTimbresActionPerformed

    private void timbreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timbreActionPerformed
        if(!sonar){
            encender();
        } else {
            apagar();
        }
               
    }//GEN-LAST:event_timbreActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarTiempo();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        guardarHorario();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        listarHorario(jTable1.getSelectedRow());
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            if(jTable1.getSelectedRow()>-1){
                enviarInformacion();
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
                if(pos == p) control.setSelect(-1);
                else selectRow();
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
            contAgregados--;
            lbContador.setText(contAgregados+" de 20");
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
        contAgregados = 0;
        lbContador.setText(contAgregados+" de 20");
    }//GEN-LAST:event_eliminarTodosActionPerformed

    private void nDuracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nDuracionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nDuracionActionPerformed

    private void tbHorarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHorarioMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHorarioMouseClicked

    private void tbHorarioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHorarioMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHorarioMousePressed

    private void conectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conectorActionPerformed
        conexionArduino();
    }//GEN-LAST:event_conectorActionPerformed

    private void spUp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spUp1MouseClicked
        int n =  Integer.parseInt(sp1.getText());
        if(n<12) sp1.setText(colocaCero(n+1));
    }//GEN-LAST:event_spUp1MouseClicked

    private void sp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sp1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sp1ActionPerformed

    private void spUp2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spUp2MouseClicked
        int n =  Integer.parseInt(sp2.getText());
        if(n<59) sp2.setText(colocaCero(n+5));
        if(Integer.parseInt(sp2.getText())>59) sp2.setText(""+59);
    }//GEN-LAST:event_spUp2MouseClicked

    private void spDown1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spDown1MouseClicked
        int n =  Integer.parseInt(sp1.getText());
        if(n>1) sp1.setText(colocaCero(n-1));
    }//GEN-LAST:event_spDown1MouseClicked

    private void spDown2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spDown2MouseClicked
        int n =  Integer.parseInt(sp2.getText());
        if(n>=0&&n-5>=0) sp2.setText(colocaCero(n-5));
    }//GEN-LAST:event_spDown2MouseClicked

    private void sp1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sp1KeyReleased

    }//GEN-LAST:event_sp1KeyReleased

    private void sp1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sp1KeyTyped
        int key = evt.getKeyChar();
        boolean soloNumeros = key >= 48 && key <= 57;
        if(!soloNumeros){
            evt.consume();
        }
        if(sp1.getText().length()>1){
            evt.consume();
        }
    }//GEN-LAST:event_sp1KeyTyped

    private void sp2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sp2KeyTyped
        int key = evt.getKeyChar();
        boolean soloNumeros = key >= 48 && key <= 57;
        if(!soloNumeros){
            evt.consume();
        }
        if(sp2.getText().length()>1) evt.consume();
        else if (sp2.getText().length()<1) sp2.setText("00");
    }//GEN-LAST:event_sp2KeyTyped

    private void lbNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbNombreActionPerformed

    private void lbNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbNombreKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_lbNombreKeyReleased

    private void lbNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbNombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_lbNombreKeyTyped

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
    private javax.swing.JButton conector;
    private javax.swing.JCheckBox domingo;
    private javax.swing.JMenuItem eliminarLista;
    private javax.swing.JMenuItem eliminarTodos;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTable jTable1;
    private javax.swing.JCheckBox jueves;
    private javax.swing.JLabel lbContador;
    private javax.swing.JLabel lbId;
    private javax.swing.JLabel lbLenDias;
    private javax.swing.JLabel lbLenHoras;
    private javax.swing.JTextField lbNombre;
    private javax.swing.JCheckBox lunes;
    private javax.swing.JCheckBox martes;
    private javax.swing.JCheckBox miercoles;
    private javax.swing.JComboBox<String> moment;
    private javax.swing.JComboBox<String> nDuracion;
    private javax.swing.JComboBox<String> nTimbres;
    private javax.swing.JMenuItem popEditar;
    private javax.swing.JMenuItem popEliminar;
    private javax.swing.JPopupMenu popLista;
    private javax.swing.JPopupMenu popTabla;
    private javax.swing.JCheckBox sabado;
    private javax.swing.JTextField sp1;
    private javax.swing.JTextField sp2;
    private javax.swing.JLabel spDown1;
    private javax.swing.JLabel spDown2;
    private javax.swing.JLabel spUp1;
    private javax.swing.JLabel spUp2;
    private javax.swing.JTable tbHorario;
    private javax.swing.JLabel tiempo;
    private javax.swing.JButton timbre;
    private javax.swing.JCheckBox viernes;
    // End of variables declaration//GEN-END:variables
}

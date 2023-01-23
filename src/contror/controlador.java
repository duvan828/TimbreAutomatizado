package contror;

import java.io.*;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import modelos.*;
import listas.*;

public class controlador {
    String ruta = System.getProperty("user.dir");
    FileWriter file;
    JSONObject cuerpo;
    String sep = "\\";
    String rt = ruta.replace(sep, "/");
    String datos[] = rt.split("/");
    String path = datos[0]+sep+datos[1]+sep+datos[2]+sep+"Documents\\HorarioTimbreAutomatico";

    public controlador() {
        try {
            crearArchivo();
            cuerpo = (JSONObject) new JSONParser().parse(new FileReader(path+"\\db.json"));
        } catch (ParseException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    private void crearArchivo(){
        try {
            File dir = new File(path);
            File json = new File(path+"\\db.json");
            if(!dir.exists()){
                if(dir.mkdir()) JOptionPane.showMessageDialog(null, "Directorios creados para guardar los horarios", "INFORMACIÓN", JOptionPane.INFORMATION_MESSAGE);
                else JOptionPane.showMessageDialog(null, "No se pudo crear los directorios necesarios.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if(!json.exists()){
                json.createNewFile();
                FileWriter escribir = new FileWriter(path+"\\db.json");
                escribir.write("{\"horarios\":[], \"select\":\"-1\"}");
                escribir.flush();
                escribir.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: "+e, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void guardarDatos(){
        try {
            FileWriter file = new FileWriter(path+"\\db.json");
            file.write(cuerpo.toJSONString());
            file.flush();
            file.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Error: "+ ex);
        }
    }
    
    public JSONArray getHotarios(){
        return (JSONArray) cuerpo.get("horarios");
    }
    
    private void setHorario(int id, String nombre, JSONArray horas, JSONArray dias){
        JSONObject horario = new JSONObject();
        JSONArray horarios = (JSONArray) cuerpo.get("horarios");
        horario.put("id", id+"");
        horario.put("nombre", nombre);
        horario.put("horas", horas);
        horario.put("dias", dias);
        horarios.add(horario);
    }
    
    public void saveHorario(horario hr){
        JSONArray horas = new JSONArray();
        JSONArray dias = new JSONArray();
        for (int i = 0; i < hr.getHoras().len(); i++) {
            tiempo tm = hr.getHoras().get(i);
            JSONObject obj = new JSONObject();
            obj.put("hora", tm.getHora()+"");
            obj.put("minutos", tm.getMinutos()+"");
            obj.put("momento", tm.getMomento()+"");
            obj.put("repeticiones", tm.getRepeticiones()+"");
            obj.put("duracion", tm.getDuracion()+"");
            horas.add(obj);
        }
        for (int i = 0; i < hr.getDias().len(); i++) {
            int dia = hr.getDias().get(i);
            dias.add(dia+"");
        }
        setHorario(hr.getId(),hr.getNombre(), horas, dias);
        guardarDatos();
    }
    
    private void reescribirHorarios(int id, String nombre, JSONArray horas, JSONArray dias){
        JSONArray datos = getHotarios();
        JSONArray hr = new JSONArray();
        cuerpo.clear();
        cuerpo.put("horarios", hr);
        for (int i = 0; i < datos.size(); i++) {
            JSONObject obj = (JSONObject) datos.get(i);
            if(Integer.parseInt((String)obj.get("id")) == id){
                JSONObject nuevo = new JSONObject();
                nuevo.put("id", id+"");
                nuevo.put("nombre", nombre);
                nuevo.put("horas", horas);
                nuevo.put("dias", dias);
                hr.add(nuevo);
            } else hr.add(obj);
        }
    }
    
    public void editHorario(horario hr){
        JSONArray horas = new JSONArray();
        JSONArray dias = new JSONArray();
        for (int i = 0; i < hr.getHoras().len(); i++) {
            tiempo tm = hr.getHoras().get(i);
            JSONObject obj = new JSONObject();
            obj.put("hora", tm.getHora()+"");
            obj.put("minutos", tm.getMinutos()+"");
            obj.put("momento", tm.getMomento()+"");
            obj.put("repeticiones", tm.getRepeticiones()+"");
            obj.put("duracion", tm.getDuracion()+"");
            horas.add(obj);
        }
        for (int i = 0; i < hr.getDias().len(); i++) {
            int dia = hr.getDias().get(i);
            dias.add(dia+"");
        }
        reescribirHorarios(hr.getId(), hr.getNombre(), horas, dias);
        guardarDatos();
    }
    
    public int getSelect(){
        if(cuerpo.get("select")!=null){
            int s = Integer.parseInt((String) cuerpo.get("select"));
            if(s>-1){
                return s;
            }
        } 
        return -1;
    }
    
    public void setSelect(int n){
        cuerpo.put("select", n+"");
        guardarDatos();
    }
    
    private int number(String n){
        return Integer.parseInt(n);
    }
    
    public listaHorarios horariosGuardados(){
        JSONArray hrs = getHotarios();
        JSONObject hr;
        int id;
        String nombre;
        JSONArray TIMES;
        JSONObject TIME;
        JSONArray DAYS;
        tiempo tm;
        horario horario;
        listaTiempos tms;
        listaDias days;
        listaHorarios horarios;
        if(hrs.size()>0){
            horarios = new listaHorarios();
            for (int i = 0; i < hrs.size(); i++) {
                hr = (JSONObject) hrs.get(i);
                horario = new horario();
                id = number((String) hr.get("id"));
                nombre = (String) hr.get("nombre");
                 TIMES = (JSONArray) hr.get("horas");
                DAYS = (JSONArray) hr.get("dias");
                tms = new listaTiempos();
                days = new listaDias();
                for (int j = 0; j < TIMES.size(); j++) {
                    TIME = (JSONObject) TIMES.get(j);
                    tm = new tiempo();
                    tm.setHora(number((String) TIME.get("hora")));
                    tm.setMinutos(number((String) TIME.get("minutos")));
                    tm.setMomento(number((String) TIME.get("momento")));
                    tm.setRepeticiones(number((String) TIME.get("repeticiones")));
                    tm.setDuracion(number((String) TIME.get("duracion")));
                    tms.add(tm);
                }
                for (int j = 0; j < DAYS.size(); j++) {
                    days.add(number((String) DAYS.get(j)));
                }
                horario.setId(id);
                horario.setNombre(nombre);
                horario.setHoras(tms);
                horario.setDias(days);
                horarios.add(horario);
            }
            return horarios;
        } else return null;
    }
    
    public void eliminarHorario(int pos){
        getHotarios().remove(pos);
        guardarDatos();
    }
}

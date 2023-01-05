import java.io.*;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

public class controlador {
    String ruta;
    FileWriter file;
    JSONObject cuerpo;

    public controlador() {
        try {
            ruta = System.getProperty("user.dir");
            cuerpo = (JSONObject) new JSONParser().parse(new FileReader(ruta+"/src/db.json"));
        } catch (ParseException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void guardarDatos(){
        try {
            FileWriter file = new FileWriter(ruta+"/src/db.json");
            file.write(cuerpo.toString());
            file.flush();
            file.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Error: "+ ex);
        }
    }
    
    public JSONArray getHotarios(){
        return (JSONArray) cuerpo.get("horarios");
    }
    
    private void setHorario(int id, JSONArray horas, JSONArray dias){
        JSONObject horario = new JSONObject();
        JSONArray horarios = (JSONArray) cuerpo.get("horarios");
        horario.put("id", id+"");
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
            horas.add(obj);
        }
        for (int i = 0; i < hr.getDias().len(); i++) {
            int dia = hr.getDias().get(i);
            dias.add(dia+"");
        }
        setHorario(hr.getId(), horas, dias);
        guardarDatos();
    }
    
    public int getSelect(){
        if(cuerpo.get("select")!=null) return Integer.parseInt((String) cuerpo.get("select")); 
        else return -1;
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
                    tms.add(tm);
                }
                for (int j = 0; j < DAYS.size(); j++) {
                    days.add(number((String) DAYS.get(j)));
                }
                horario.setId(id);
                horario.setHoras(tms);
                horario.setDias(days);
                horarios.add(horario);
            }
        } else horarios = null;
        return horarios;
    }
    
    public void eliminarHorario(int pos){
        getHotarios().remove(pos);
        guardarDatos();
    }
}

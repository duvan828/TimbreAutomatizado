
import java.util.ArrayList;

public class listaTiempos {
    ArrayList<tiempo> lista;

    public listaTiempos() {
        lista = new ArrayList<tiempo>();
    }
    
    public int len(){
        return lista.size();
    }
    
    public void add(tiempo tm){
        lista.add(tm);
    }
 
    public void remove(int pos){
        lista.remove(pos);
    }
    
    public tiempo get(int pos){
        return lista.get(pos);
    }
    
    public String listarHoras(){
        String salida = "";
        for (int i = 0; i < lista.size(); i++) {
            salida += lista.get(i).toString();
            if(i<lista.size()-1) salida +=  ", ";
        }
        return salida;
    }
    
}

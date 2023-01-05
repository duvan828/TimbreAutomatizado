
import java.util.ArrayList;

public class listaHorarios {
    
    ArrayList<horario> lista;

    public listaHorarios() {
        lista = new ArrayList<horario>();;
    }
    
    public int len(){
         return lista.size();
     }

     public void add(horario hr){
         lista.add(hr);
     }

     public void remove(int pos){
         lista.remove(pos);
     }

     public horario get(int pos){
         return lista.get(pos);
     }
     
     public boolean vacio(){
         return lista.isEmpty();
     }
}

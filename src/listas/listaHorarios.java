package listas;

import modelos.horario;
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

     public horario getForId(int id){
         horario hr;
         for (int i = 0; i < lista.size(); i++) {
             hr = lista.get(i);
             if(hr.getId()==id){
                 return hr;
             }
         }
         return null;
     }
     
     public boolean vacio(){
         return lista.isEmpty();
     }
}

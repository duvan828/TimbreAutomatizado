
import java.util.ArrayList;

public class listaDias {
    ArrayList<Integer> lista;

    public listaDias() {
        lista = new ArrayList<>();
    }
    
    public int len(){
        return lista.size();
    }

    public void add(int d){
        lista.add(d);
    }

    public void remove(int pos){
        lista.remove(pos);
    }

    public int get(int pos){
        return lista.get(pos);
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
    
    public String mostrar(){
        String salida = "";
        for (int i = 0; i < lista.size(); i++) {
            salida += darDia(lista.get(i));
            if(i<lista.size()-1) salida +=  ", ";
        }
        return salida;
    }
    
}

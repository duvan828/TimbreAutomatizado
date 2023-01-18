package modelos;

public class tiempo {
    private int hora;
    private int minutos;
    private int momento;
    private int repeticiones;
    private int duracion;

    public tiempo() {
        hora = 0;
        minutos = 0;
        momento = 0;
        repeticiones = 0;
        duracion = 0;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getMomento() {
        return momento;
    }

    public void setMomento(int momento) {
        this.momento = momento;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    
    
    public String toString(){
       String h = ""+hora, m = ""+minutos, mom = "";
       if(hora<10){
           h = "0"+h;
       }
       if(minutos<10){
           m = "0"+m;
       }
       if(momento==0) mom = "AM";
       else mom = "PM";
       
        return h+":"+m+mom+" ("+repeticiones+","+duracion+")";
    }
}

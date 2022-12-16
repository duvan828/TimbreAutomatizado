public class tiempo {
    private int hora;
    private int minutos;
    private String momento;
    private int repeticiones;

    public tiempo() {
        hora = 0;
        minutos = 0;
        momento = "AM";
        repeticiones = 0;
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

    public String getMomento() {
        return momento;
    }

    public void setMomento(String momento) {
        this.momento = momento;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
    
    public String toString(){
       String h = ""+hora, m = ""+minutos;
       if(hora<10){
           h = "0"+h;
       }
       if(minutos<10){
           m = "0"+m;
       }
        return h+":"+m+"-"+momento+"-"+repeticiones;
    }
}

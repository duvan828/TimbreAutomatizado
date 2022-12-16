public class horario {
    
    private listaTiempos horas;
    private listaDias dias;

    public horario() {
        horas = new listaTiempos();
        dias = new listaDias();
    }

    public listaTiempos getHoras() {
        return horas;
    }

    public void setHoras(listaTiempos horas) {
        this.horas = horas;
    }

    public listaDias getDias() {
        return dias;
    }

    public void setDias(listaDias dias) {
        this.dias = dias;
    }
    
    public String toString(){
        return "Horas: " + horas.listarHoras()
              +"\nDías: " + dias.mostrar();
    }
}

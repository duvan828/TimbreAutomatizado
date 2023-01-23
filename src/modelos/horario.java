package modelos;

import listas.*;
public class horario {
    
    private int id;
    private String nombre;
    private listaTiempos horas;
    private listaDias dias;

    public horario(){
        id = 0;
        nombre = "";
        horas = new listaTiempos();
        dias = new listaDias();
    }
    
    public horario(listaHorarios lst) {
        id = generarID(lst);
        horas = new listaTiempos();
        dias = new listaDias();
    }

    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if(!nombre.equals(""))  this.nombre = nombre;
        else this.nombre = "Horario "+ id;
    }
    
    
    
    private int generarID(listaHorarios lst) {
        int idGen = 1;
        if(!lst.vacio()){
            int idMax = lst.get(0).getId();
            for (int i = 0; i < lst.len(); i++) {
                if(lst.get(i).getId()>idMax) idMax = lst.get(i).getId();
            }
            idGen = idMax + 1;
        }
        return idGen;
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

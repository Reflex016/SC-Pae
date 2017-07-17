package pae.estadisticas.models;

import java.util.HashMap;

public class Registro  extends HashMap<String, Object> {

    private String ci;
    private String fecha;

    public  Registro(String ci, String fecha) {
        this.ci = ci;
        this.fecha = fecha;
    }

    public Registro(){

    }

    public String getCi() {
        return ci;
    }

    public String getFecha() {
        return fecha;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString(){
        return "CI: " + getCi() + " Fecha: " + getFecha();
    }
}
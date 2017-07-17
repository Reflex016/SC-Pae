package pae.alumnos.models;

public class Alumno {

    private String ci;
    private String nombres;
    private String apellidos;
    private String grado;
    private String seccion;
    private String carrera;

    public Alumno(String ci, String nombres, String apellidos, String grado, String seccion, String carrera) {
        this.ci = ci;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.grado = grado;
        this. seccion = seccion;
        this.carrera = carrera;
    }

    public Alumno(String nombres, String grado, String seccion) {
        this.nombres = nombres;
        this.grado = grado;
        this.seccion = seccion;
    }

    public Alumno(String data, String option) {
        if (option == "grade")
            this.grado = data;
        else
            this.seccion = data;
    }

    public void setCi() {

    }

    public String getCi() {
        return ci;
    }

    public void setNombres() {

    }

    public String getNombres() {
        return nombres;
    }

    public void setApellidos() {

    }

    public String getApellidos() {
        return apellidos;
    }

    public void setGrado() {

    }

    public String getGrado() {
        return grado;
    }

    public void setSeccion() {

    }

    public String getSeccion() {
        return seccion;
    }

    public void setCarrera() {

    }

    public String getCarrera() {
        return carrera;
    }

    @Override
    public String toString() {

        String alumno =
                "Cedula: " + getCi() +
                " Nombre: " + getNombres() +
                " Apellidos: " + getApellidos() +
                " Grado: " + getGrado() +
                " Seccion: " + getSeccion() +
                " Carrera: " + getCarrera();

        return alumno;
    }
}
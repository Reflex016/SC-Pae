package pae.alumnos.models;

import java.sql.Date;

public class RegistroComida {
    String CI;
    Date fecha;

    public RegistroComida (String CI, Date fecha)
    {
        this.CI = CI;
        this.fecha = fecha;
    }

    public String getCI ()
    {
        return this.CI;
    }

    public Date getFecha ()
    {
        return this.fecha;
    }

    public void setCI (String CI)
    {
        this.CI = CI;
    }

    public void setFecha (Date fecha)
    {
        this.fecha = fecha;
    }

    @Override
    public String toString ()
    {
        return "CI: " + this.CI + ", fecha: " + this.fecha;
    }
}

package pae.alumnos.dbconnections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pae.alumnos.models.Alumno;
import pae.alumnos.models.RegistroComida;
import pae.comedor.controllers.ComedorController;
import pae.dbconnections.DbException;
import pae.dbconnections.IDbConnection;
import pae.dbconnections.PostgresDbConnection;

public class AlumnoDbAdapter implements IDataAdapter<Alumno>{
    //flag to drive open/close connection in local methods
    boolean localOpen;

    @Override
    public List<Alumno> getList(IDbConnection db,
                                  HashMap<String, Object> options) {
        List<Alumno> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.alumnos WHERE 1=1";

            if (options != null){
                String ci = (String) options.get("CI");
                if (ci != null)
                    sql += " AND \"CI\" = '" + ci + "'";
                String grado = (String) options.get("grado");
                if (grado != null)
                    sql += " AND grado = '" + grado + "'";
                String seccion = (String) options.get("seccion");
                if (seccion != null)
                    sql += " AND seccion = '" + seccion + "'";
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Alumno al = new Alumno(rs.getString("CI"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("grado"),
                        rs.getString("seccion"),
                        rs.getString("carrera"));
                list.add(al);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }

    public boolean hasStudentEaten (IDbConnection db,
                                    HashMap<String, Object> options) {
        String sql = "SELECT * FROM public.registro_comidas WHERE \"CI\" = '"
                + options.get("CI") + "' AND fecha = '" + options.get("fecha") + "'";
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        try {
            ResultSet rs = postgresDb.getResultSet(sql);
            RegistroComida rg = null;
            while (rs.next()) {
                rg = new RegistroComida(rs.getString("CI"), rs.getDate("fecha"));
            }
            if (rg != null)
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean hasStudentEaten (IDbConnection db,
                                    HashMap<String, Object> options, String FOOD) {
        String sql = "SELECT * FROM public.registro_comidas WHERE \"CI\" = '"
                + options.get("CI") + "' AND fecha = '" + options.get("fecha") + "'"
                + "AND comida = '" + FOOD + "'";
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        try {
            ResultSet rs = postgresDb.getResultSet(sql);
            RegistroComida rg = null;
            while (rs.next()) {
                rg = new RegistroComida(rs.getString("CI"), rs.getDate("fecha"));
            }
            if (rg != null)
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void studentEat (IDbConnection db,
                            HashMap<String, Object> options) {
        if (options != null) {
            String sql = "INSERT INTO public.registro_comidas(\"CI\", fecha)"
            + " VALUES ('" + options.get("CI") + "', '" + options.get("fecha") + "')";
            PostgresDbConnection postgresDb = (PostgresDbConnection) db;
            try
            {
                postgresDb.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void studentEat (IDbConnection db,
                            HashMap<String, Object> options, String FOOD) {
        if (options != null) {
            String sql = "INSERT INTO public.registro_comidas(\"CI\", fecha, comida)"
                    + " VALUES ('" + options.get("CI") + "', '" + options.get("fecha") + "', '" + FOOD + "')";
            PostgresDbConnection postgresDb = (PostgresDbConnection) db;
            try
            {
                postgresDb.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<String> getCIsWhoAte (IDbConnection db,
                                HashMap<String, Object> options) {
        List<String> cis = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        try {
            String sql = "select \"CI\" from public.registro_comidas WHERE fecha = '" + options.get("fecha") + "' " +
                    "intersect " +
                    "select \"CI\" from public.alumnos WHERE grado = '"
                    + options.get("grado") + "' AND seccion = '" + options.get("seccion") + "'";
            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                String ci = rs.getString("CI");
                cis.add(ci);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cis;
    }

    public List<String> getCIsWhoAte (IDbConnection db,
                                      HashMap<String, Object> options, String FOOD) {
        List<String> cis = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        try {
            String sql = "select \"CI\" from public.registro_comidas WHERE fecha = '"
                    + options.get("fecha") + "' AND comida = '" + FOOD + "'" +
                    "intersect " +
                    "select \"CI\" from public.alumnos WHERE grado = '"
                    + options.get("grado") + "' AND seccion = '" + options.get("seccion") + "'";
            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                String ci = rs.getString("CI");
                cis.add(ci);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cis;
    }

    @Override
    public Alumno getRecord(IDbConnection db, HashMap<String, Object> options) {
        List<Alumno> list = getList(db, options);
        if (list.size()>0) return list.get(0);
        return null;
    }

    @Override
    public boolean insertRecord(IDbConnection db, Alumno record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int insertedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "INSERT INTO public.alumnos(CI,nombres,apellidos,grado,seccion,carrera)"
                    + "VALUES ('"
                    + record.getCi() + "', "
                    + record.getNombres() + "', "
                    + record.getApellidos() + ", '"
                    + record.getGrado() + "')"
                    + record.getSeccion() + "', "
                    + record.getCarrera() + "', ";
            System.out.println("INSERT SQL:" + sql);

            insertedRecords = postgresDb.executeUpdate(sql);

        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (insertedRecords>0);
    }

    @Override
    public boolean updateRecord(IDbConnection db, Alumno record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.alumnos "
                    + "SET nombres = " + record.getNombres()
                    + "SET apellidos = " + record.getApellidos()
                    + "SET grado = " + record.getGrado()
                    + "SET seccion = " + record.getSeccion()
                    + "SET carrera = " + record.getCarrera()
                    + "WHERE CI = '" + record.getCi();

            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    @Override
    public boolean deleteRecord(IDbConnection db, Alumno record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int deletedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "DELETE FROM public.alumnos "
                    + "WHERE CI = '" + record.getCi() + "'";

            System.out.println("DELETE SQL:" + sql);

            deletedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlumnoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (deletedRecords > 0);
    }
}
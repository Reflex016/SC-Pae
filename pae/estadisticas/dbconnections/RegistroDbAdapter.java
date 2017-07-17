package pae.estadisticas.dbconnections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pae.dbconnections.DbException;
import pae.dbconnections.IDbConnection;
import pae.dbconnections.PostgresDbConnection;
import pae.estadisticas.models.Registro;

public class RegistroDbAdapter implements IDataAdapter<Registro> {
    //flag to drive open/close connection in local methods
    boolean localOpen;

    @Override
    public List<Registro> getList(IDbConnection db,
                                 HashMap<String, Object> options) {
        List<pae.estadisticas.models.Registro> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.registro_comidas WHERE 1=1";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Registro registro = new Registro(rs.getString("ci"),
                        rs.getString("fecha"));
                list.add(registro);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }

    @Override
    public Registro getRecord(IDbConnection db, HashMap<String, Object> options) {
        List<Registro> list = getList(db, options);
        if (list.size()>0) return list.get(0);
        return null;
    }

    @Override
    public boolean insertRecord(IDbConnection db, Registro record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int insertedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "INSERT INTO public.registro_comidas(CI,fecha)"
                    + "VALUES ('"
                    + record.getCi() + "', '"
                    + record.getFecha() + "') ";
            System.out.println("INSERT SQL:" + sql);

            insertedRecords = postgresDb.executeUpdate(sql);

        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (insertedRecords>0);
    }

    @Override
    public boolean updateRecord(IDbConnection db, Registro record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.users ";
            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);
    }

    @Override
    public boolean deleteRecord(IDbConnection db, Registro record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int deletedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "DELETE FROM public.registro_comidas";

            System.out.println("DELETE SQL:" + sql);

            deletedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (deletedRecords > 0);
    }


    public List<Registro> getByDate(IDbConnection db,
                                  HashMap<String, Object> options, String date) {
        List<pae.estadisticas.models.Registro> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.registro_comidas WHERE fecha = '" + date +"'";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Registro registro = new Registro(rs.getString("ci"),
                        rs.getString("fecha"));
                list.add(registro);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }
}
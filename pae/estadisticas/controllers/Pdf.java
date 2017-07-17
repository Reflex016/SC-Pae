package pae.estadisticas.controllers;


import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.ConditionalStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;

import pae.dbconnections.PostgresDbConnection;
import pae.estadisticas.models.Registro;

import java.awt.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class Pdf {

    static String db = "Alimentos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;
    Connection connection = null;


    public void buildGlobales(String fechaI, String fechaF) throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alumnos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<Date> fechaColumn = col.column("Fecha", "fecha", type.dateType());
        TextColumnBuilder<Integer> cantidadColumn = col.column("Cantidad", "cantidad", type.integerType());

        report.columns(fechaColumn, cantidadColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                                "Estadísticas Globales de Almuerzos\n" +
                                String.valueOf(localDate) + "\n " +
                                time + "\n").setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setDataSource("SELECT fecha as fecha," +
                        " COUNT(*) as cantidad FROM public.registro_comidas" +
                        " WHERE fecha BETWEEN '" +
                        fechaI+"' AND '"+fechaF+"'" +
                        " GROUP BY fecha ORDER BY fecha",connection);
        report.show(false);
    }

    public void buildIndividuales(String fechaI, String fechaF, String nombre) throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alumnos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<Date> fechaColumn = col.column("Fecha", "fecha", type.dateType());

        report.columns(fechaColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                                "Estadísticas Individuales de Almuerzos\n" +
                                String.valueOf(localDate) + "\n " +
                                time + "\n"+"CI: "+nombre).setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setDataSource("SELECT fecha FROM public.registro_comidas " +
                        "WHERE fecha BETWEEN '" +
                        fechaI + "' AND '"+fechaF+"' " +
                        "AND "+"\"CI\""+" = '"+nombre+"'",connection);
        report.show(false);
    }

    public void buildSection(String fechaI, String fechaF, String grado, String seccion) throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alumnos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        TextColumnBuilder<Date> fechaColumn = col.column("Fecha", "fecha", type.dateType());
        TextColumnBuilder<Integer> cantidadColumn = col.column("Cantidad", "cantidad", type.integerType());

        report.columns(fechaColumn, cantidadColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                                "Estadísticas de Almuerzos por Sección\n" +
                                String.valueOf(localDate) + "\n " +
                                time + "\n"+"Grado: "+grado+" Sección: "+seccion).setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setDataSource("SELECT registro_comidas.fecha as fecha, COUNT(*) as cantidad FROM public.registro_comidas, public.alumnos " +
                        "WHERE registro_comidas.\"CI\" = alumnos.\"CI\" AND alumnos.grado = '" +
                        grado+"' AND alumnos.seccion = '"+seccion+"' AND fecha BETWEEN '" +
                        fechaI+"' AND '"+fechaF+"' GROUP BY fecha",connection);
        report.show(false);
    }

    public void buildReport(String fechaInicial, String fechaFinal) throws DRException {

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Alumnos_AndresBello","postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JasperReportBuilder report = DynamicReports.report();
        LocalDate localDate = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());

        TextColumnBuilder<String> insumoColumn = col.column("Insumo", "insumo", type.stringType());
        TextColumnBuilder<String> accionColumn = col.column("Acción", "accion", type.stringType());
        TextColumnBuilder<Double> cantidadColumn = col.column("Cantidad", "cantidad", type.doubleType());
        TextColumnBuilder<Date> fechaColumn = col.column("Fecha", "fecha", type.dateType());

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        StyleBuilder titleStyle = stl.style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setFontSize(16);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        ConditionalStyleBuilder rowStyle1 = stl.conditionalStyle(cnd.equal(accionColumn,"Abastecer"))
                .setBackgroundColor(new Color(210,255,210));
        ConditionalStyleBuilder rowStyle2 = stl.conditionalStyle(cnd.equal(accionColumn,"Consumir"))
                .setBackgroundColor(new Color(255,210,210));
        ConditionalStyleBuilder rowStyle3 = stl.conditionalStyle(cnd.equal(accionColumn,"Crear"))
                .setBackgroundColor(new Color(210,210,255));
        StyleBuilder rowStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        Bar3DChartBuilder bar3DChartBuilder = cht.bar3DChart().setTitle("\n\nReporte de Movimiento de Insumos")
                .setCategory(insumoColumn)
                .addSerie(
                        cht.serie(cantidadColumn)
                );

        report.columns(insumoColumn, accionColumn, cantidadColumn, fechaColumn).setColumnTitleStyle(columnTitleStyle)
                .highlightDetailEvenRows().detailRowHighlighters(rowStyle1,rowStyle2,rowStyle3)
                .setColumnStyle(rowStyle)
                .title(Components.horizontalFlowList().add(
                        cmp.image(getClass().getResourceAsStream("/pae/utils/img/andresbello.png")).setFixedDimension(125,125)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER),
                        Components.text("\nEscuela Técnica Comercial Andres Bello\n" +
                                "Reporte de Movimiento de Insumos\n" +
                                String.valueOf(localDate) + "\n " +
                                time + "\n").setStyle(titleStyle)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .summary(bar3DChartBuilder)
                .setDataSource("SELECT alimentos.nombre as insumo, " +
                        "inventarios.accion, " +
                        "inventarios.cantidad, " +
                        "inventarios.fecha " +
                        "FROM public.inventarios, public.alimentos " +
                        "WHERE inventarios.id_alimento = alimentos.id " +
                        "AND inventarios.fecha BETWEEN '"+fechaInicial+"' AND '"+fechaFinal+"' " +
                        "ORDER BY inventarios.fecha",connection);
        report.show(false);
    }
}
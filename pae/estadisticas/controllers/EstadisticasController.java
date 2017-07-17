package pae.estadisticas.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import net.sf.dynamicreports.report.exception.DRException;
import pae.app.controllers.AlertMaker;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;
import pae.estadisticas.dbconnections.RegistroDbAdapter;
import pae.estadisticas.models.Registro;

public class EstadisticasController extends Application implements Initializable{

    @FXML private JFXHamburger menu;
    @FXML private JFXDrawer side;
    @FXML private JFXTabPane pane;
    @FXML private JFXTextField cedulaTextField;
    @FXML private JFXButton individualesButton;
    @FXML private JFXButton globalesButton;
    @FXML private JFXButton estadisticasButton;
    @FXML private JFXDatePicker individualInicialDatePicker;
    @FXML private JFXDatePicker individualFinalDatePicker;
    @FXML private JFXDatePicker globalesInicialDatePicker;
    @FXML private JFXDatePicker globalesFinalDatePicker;
    @FXML private JFXDatePicker seccionInicialDatePicker;
    @FXML private JFXDatePicker seccionFinalDatePicker;
    @FXML private JFXComboBox gradoComboBox;
    @FXML private JFXComboBox seccionComboBox;
    @FXML private StackPane stackPane;
    @FXML private JFXSpinner globalSpinner;
    @FXML private JFXSpinner individualSpinner;
    @FXML private JFXSpinner seccionSpinner;
    @FXML private Label diariasLabel;

    static String db = "Alumnos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;
    List<Registro> registros;

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection (url, db, user, pass);
        conn.open(true);

        Parent estadisticas = FXMLLoader.load(getClass().getResource("/pae/estadisticas/views/EstadisticasView.fxml"));
        Scene estadisticas_scene = new Scene(estadisticas);
        primaryStage.setScene(estadisticas_scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        mostrarMenu();
        globalSpinner.setVisible(false);
        individualSpinner.setVisible(false);
        seccionSpinner.setVisible(false);
        setSeccionComboBox();
        setGradoComboBox();
        showDaily();

    }

    public void showDaily() {

        LocalDate now = LocalDate.now();
        String fecha = String.valueOf(now.getYear()) + "-"
                + String.valueOf(now.getMonthValue()) + "-"
                + String.valueOf(now.getDayOfMonth());
        RegistroDbAdapter registroDbAdapter = new RegistroDbAdapter();
        registros = registroDbAdapter.getByDate(conn,null,fecha);

        diariasLabel.setText(String.valueOf(registros.size()));
    }

    public void getGlobal() throws DRException {

        if (globalesInicialDatePicker.getValue() != null && globalesFinalDatePicker.getValue() != null) {

            if (globalesInicialDatePicker.getValue().isBefore(globalesFinalDatePicker.getValue())) {

                globalSpinner.setVisible(true);
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,"Generando Reporte","Por favor, espere mientras se genera el reporte solicitado.");
                disableAll();
                GlobalesThread globalesThread = new GlobalesThread();
                globalesThread.start();
            }
            else {

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Estadísticas Globales",
                        "La fecha inicial debe ser anterior a la fecha final.");
            }
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Estadísticas Globales",
                    "Por favor, ingrese ambas fechas para generar el reporte");
        }
    }

    public class GlobalesThread extends Thread {

        public void run() {
            Pdf pdf = new Pdf();
            try {
                pdf.buildGlobales(String.valueOf(globalesInicialDatePicker.getValue()),
                        String.valueOf(globalesFinalDatePicker.getValue()));
            } catch (DRException e) {
                e.printStackTrace();
            }
            globalSpinner.setVisible(false);
            enableAll();
        }
    }

    public void getIndividual() throws DRException {

        if (individualInicialDatePicker.getValue() != null
                && individualInicialDatePicker.getValue() != null
                && !cedulaTextField.getText().isEmpty()) {

            if (individualInicialDatePicker.getValue().isBefore(individualFinalDatePicker.getValue())) {

                individualSpinner.setVisible(true);
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,"Generando Reporte","Por favor, espere mientras se genera el reporte solicitado.");
                disableAll();
                IndividualesThread individualesThread = new IndividualesThread();
                individualesThread.start();
            }
            else {

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Estadísticas Individuales",
                        "La fecha inicial debe ser anterior a la fecha final.");
            }
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Estadísticas Individuales",
                    "Por favor, ingrese ambas fechas y la cédula para generar el reporte");
        }
    }

    public class IndividualesThread extends Thread {

        public void run() {
            Pdf pdf = new Pdf();
            try {
                pdf.buildIndividuales(String.valueOf(individualInicialDatePicker.getValue()),
                        String.valueOf(individualFinalDatePicker.getValue()),cedulaTextField.getText());
            } catch (DRException e) {
                e.printStackTrace();
            }
            individualSpinner.setVisible(false);
            enableAll();
        }
    }

    public void getSeccion() throws DRException {

        if (!gradoComboBox.getSelectionModel().isEmpty() && !seccionComboBox.getSelectionModel().isEmpty()
                && (seccionInicialDatePicker.getValue()!= null) && (seccionFinalDatePicker.getValue()!= null)) {

            if (seccionInicialDatePicker.getValue().isBefore(seccionFinalDatePicker.getValue())) {

                seccionSpinner.setVisible(true);
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,"Generando Reporte","Por favor, espere mientras se genera el reporte solicitado.");
                disableAll();
                SeccionThread seccionThread = new SeccionThread();
                seccionThread.start();
            }
            else {

                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,
                        "Estadísticas por Sección",
                        "La fecha inicial debe ser anterior a la fecha final.");
            }
        }
        else {

            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,
                    "Estadísticas por Sección",
                    "Por favor, ingrese ambas fechas, además seleccione grado y sección.");
        }
    }

    public class SeccionThread extends Thread {

        public void run() {
            Pdf pdf = new Pdf();
            try {
                pdf.buildSection(String.valueOf(seccionInicialDatePicker.getValue()),
                        String.valueOf(seccionFinalDatePicker.getValue()),
                        String.valueOf(gradoComboBox.getSelectionModel().getSelectedItem()),
                        String.valueOf(seccionComboBox.getSelectionModel().getSelectedItem()));
            } catch (DRException e) {
                e.printStackTrace();
            }
            seccionSpinner.setVisible(false);
            enableAll();
        }
    }

    public void setGradoComboBox(){
        gradoComboBox.getItems().removeAll(gradoComboBox.getItems());
        gradoComboBox.getItems().addAll("7mo", "8vo", "9no", "4to", "5to", "6to");
    }

    public void setSeccionComboBox(){
        seccionComboBox.getItems().removeAll(seccionComboBox.getItems());
        seccionComboBox.getItems().addAll("A", "B", "C", "D", "U");
    }

    public void disableAll() {

        menu.setDisable(true);
        cedulaTextField.setDisable(true);
        individualesButton.setDisable(true);
        globalesButton.setDisable(true);
        estadisticasButton.setDisable(true);
        individualInicialDatePicker.setDisable(true);
        individualFinalDatePicker.setDisable(true);
        globalesInicialDatePicker.setDisable(true);
        globalesFinalDatePicker.setDisable(true);
        seccionInicialDatePicker.setDisable(true);
        seccionFinalDatePicker.setDisable(true);
        gradoComboBox.setDisable(true);
        seccionComboBox.setDisable(true);
        pane.setDisable(true);
    }

    public void enableAll() {

        menu.setDisable(false);
        cedulaTextField.setDisable(false);
        individualesButton.setDisable(false);
        globalesButton.setDisable(false);
        estadisticasButton.setDisable(false);
        individualInicialDatePicker.setDisable(false);
        individualFinalDatePicker.setDisable(false);
        globalesInicialDatePicker.setDisable(false);
        globalesFinalDatePicker.setDisable(false);
        seccionInicialDatePicker.setDisable(false);
        seccionFinalDatePicker.setDisable(false);
        gradoComboBox.setDisable(false);
        seccionComboBox.setDisable(false);
        pane.setDisable(false);
    }

    public void mostrarMenu(){

        try {
            VBox box = FXMLLoader.load(getClass().getResource("/pae/app/views/MenuView.fxml"));
            side.setSidePane(box);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(menu);
        transition.setRate(-1);
        menu.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (event -> {
            transition.setRate(transition.getRate()*-1);
            transition.play();

            if (side.isShown())
                side.close();
            else
                side.open();
        }));
    }
}
package pae.comedor.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.util.Callback;

import pae.alumnos.dbconnections.AlumnoDbAdapter;
import pae.alumnos.models.Alumno;
import pae.app.controllers.AlertMaker;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;

import javafx.scene.image.ImageView;

public class ComedorController extends Application implements Initializable{

    @FXML JFXHamburger menu;
    @FXML JFXDrawer side;
    @FXML JFXComboBox<String> gradoComboBox = new JFXComboBox<String>();
    @FXML JFXComboBox<String> seccionComboBox = new JFXComboBox<String>();
    @FXML JFXTreeTableView<AlumnoProperty> comedorTreeTableView;
    @FXML Label nombreLabel;
    @FXML Label gradoLabel;
    @FXML Label seccionLabel;
    @FXML Label estadoLabel;
    @FXML JFXTextField cedulaTextField;
    @FXML JFXButton btnProcesar;
    @FXML JFXButton btnEmpezar;
    @FXML JFXDialog advertenciaDialog;
    @FXML ImageView currentFrame;
    @FXML StackPane stackPane;

    static String db = "Alumnos_AndresBello";
    static String user = "postgres";
    static String pass = "1234";
    static String url = "jdbc:postgresql://localhost:5432/" + db;
    static PostgresDbConnection conn;

    static CameraController cmrCtrl;
    static Scene scene;

    ObservableList<AlumnoProperty> alsPrpt = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        conn = new PostgresDbConnection(url, db, user, pass);
        conn.open(true);

        Parent comedor = FXMLLoader.load(getClass().getResource("/pae/comedor/views/ComedorView.fxml"));
        Scene comedor_scene = new Scene(comedor);

        this.scene = comedor_scene;

        primaryStage.setScene(comedor_scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        mostrarMenu();
        setGradoComboBox();
        setSeccionComboBox();
        setComedorTreeTableView();
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

    public void buscarAlumno(ActionEvent event){
        String cedula = cedulaTextField.getText();

        if (!cedula.isEmpty()) {
            procesarAlumno(cedula);
        }
        else {
            clearFields();
            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,"Control Individual","Por favor, introduzca un número de cédula.");
        }

    }

    public void procesarAlumno (String CI)
    {
        AlumnoDbAdapter adapter = new AlumnoDbAdapter();
        HashMap<String, Object> opt = new HashMap<String, Object>();
        opt.put("CI", CI);
        Alumno a = adapter.getRecord(conn, opt);
        if (a != null)
        {
            Platform.runLater(() -> {
                nombreLabel.setText(a.getNombres());
                gradoLabel.setText(a.getGrado());
                seccionLabel.setText(a.getSeccion());
            });

            Date fecha = new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis());
            opt.put ("fecha", fecha);

            if (adapter.hasStudentEaten(conn, opt))
            {
                Platform.runLater(() -> {
                    estadoLabel.setTextFill(Color.web("#d50000"));
                    estadoLabel.setText("INHABIILITADO");
                });
            }
            else
            {
                Platform.runLater(() -> {
                    estadoLabel.setTextFill(Color.web("#64dd17"));
                    estadoLabel.setText("HABILITADO");
                });
                adapter.studentEat(conn, opt);
            }
        }
        else
        {
            Platform.runLater(() -> {
                AlertMaker alertMaker = new AlertMaker();
                alertMaker.loadInfoDialog(stackPane,"Control Individual","No se encontró el alumno." +
                        "\n Por favor, ingrese un número de cédula válido.");
                clearFields();
            });
        }
    }

    public void clearFields(){

        cedulaTextField.setText("");
        nombreLabel.setText("");
        gradoLabel.setText("");
        seccionLabel.setText("");
        estadoLabel.setText("");
    }

    public void setGradoComboBox(){
        gradoComboBox.getItems().removeAll(gradoComboBox.getItems());
        gradoComboBox.getItems().addAll("7mo", "8vo", "9no", "4to", "5to", "6to");
        gradoComboBox.setOnAction(e -> updateTabView());
    }

    public void setSeccionComboBox(){
        seccionComboBox.getItems().removeAll(seccionComboBox.getItems());
        seccionComboBox.getItems().addAll("A", "B", "C", "D", "U");
        seccionComboBox.setOnAction(e -> updateTabView());
    }

    //Actualiza el tabView
    private void updateTabView ()
    {
        if (gradoComboBox.getSelectionModel().getSelectedItem() != null &&
                seccionComboBox.getSelectionModel().getSelectedItem() != null)
        {
            AlumnoDbAdapter ad = new AlumnoDbAdapter();
            HashMap<String, Object> opt = new HashMap<>();
            opt.put("grado", gradoComboBox.getSelectionModel().getSelectedItem().toString());
            opt.put("seccion", seccionComboBox.getSelectionModel().getSelectedItem().toString());
            List<Alumno> als = ad.getList(conn, opt);
            alsPrpt.clear();
            for (Alumno al : als) {
                alsPrpt.add(new AlumnoProperty(al.getCi(), al.getNombres(), al.getApellidos(), true));
            }
            Date fecha = new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis());
            opt.put ("fecha", fecha);
            setDisps (ad.getCIsWhoAte(conn, opt));
        }
    }

    public void setDisps (List<String> cis) {
        for (ComedorController.AlumnoProperty s: alsPrpt) {
            for (String ci: cis) {
                if (s.getCI().equals((ci)))
                {
                    s.setHabilitado(false);
                    break;
                }
            }
        }
    }

    public void procesarAlumnos(ActionEvent event) {
        AlumnoDbAdapter ad = new AlumnoDbAdapter();
        HashMap<String, Object> opt = new HashMap<>();
        List<String> err = new ArrayList<>();
        Date fecha = new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis());
        for (AlumnoProperty s: alsPrpt) {
            opt.put("CI", s.getCI());
            opt.put ("fecha", fecha);
            if (s.habilitado.getValue()) {
                if (!ad.hasStudentEaten(conn, opt)) {
                    s.setHabilitado(false);
                    ad.studentEat(conn, opt);
                    opt.clear();
                }
                else
                    err.add(s.ci.get() + ", " + s.names.get() + ", " + s.lastNames.get());
            }
        }
        updateTabView();
        if (err.size() > 0){
            String txtShow = "";
            for (String str: err) {
                txtShow += "\n" + str;
            }
            AlertMaker alertMaker = new AlertMaker();
            alertMaker.loadInfoDialog(stackPane,"Control de Almuerzos",
                    "No se puede comer repetidas veces, solo una vez al día:\n" + txtShow +"\n");
        }
    }

    public class AlumnoProperty extends RecursiveTreeObject<AlumnoProperty> {

        StringProperty names;
        StringProperty lastNames;
        StringProperty ci;
        BooleanProperty habilitado;

        public AlumnoProperty (String ci, String names, String lastNames, Boolean hab) {
            this.names = new SimpleStringProperty(names);
            this.lastNames = new SimpleStringProperty(lastNames);
            this.ci = new SimpleStringProperty(ci);
            this.habilitado = new javafx.beans.property.SimpleBooleanProperty(hab);
        }

        public String getCI() { return this.ci.getValue(); }
        public void setHabilitado(boolean hab) {
            this.habilitado = new javafx.beans.property.SimpleBooleanProperty(hab);
        }
    }

    public void setComedorTreeTableView(){

        JFXTreeTableColumn<AlumnoProperty,String> ciColumn = new JFXTreeTableColumn<>("CI");
        ciColumn.setPrefWidth(110.0);
        ciColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AlumnoProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AlumnoProperty, String> param) {
                return param.getValue().getValue().ci;
            }
        });

        JFXTreeTableColumn<AlumnoProperty,String> namesColumn = new JFXTreeTableColumn<>("Nombres");
        namesColumn.setPrefWidth(190.0);
        namesColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AlumnoProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AlumnoProperty, String> param) {
                 return param.getValue().getValue().names;
            }
        });

        JFXTreeTableColumn<AlumnoProperty,String> lastNamesColumn = new JFXTreeTableColumn<>("Apellidos");
        lastNamesColumn.setPrefWidth(190.0);
        lastNamesColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AlumnoProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AlumnoProperty, String> param) {
                return param.getValue().getValue().lastNames;
            }
        });

        JFXTreeTableColumn<AlumnoProperty,Boolean> stateColumn = new JFXTreeTableColumn<>("Almuerzo");
        stateColumn.setPrefWidth(100.0);
        stateColumn.setCellValueFactory(param -> param.getValue().getValue().habilitado);
        stateColumn.setCellFactory(new Callback<TreeTableColumn<AlumnoProperty,Boolean>,TreeTableCell<AlumnoProperty,Boolean>>() {
            @Override
            public TreeTableCell<AlumnoProperty,Boolean> call( TreeTableColumn<AlumnoProperty,Boolean> p ) {
                javafx.scene.control.cell.CheckBoxTreeTableCell<AlumnoProperty,Boolean> cell = new javafx.scene.control.cell.CheckBoxTreeTableCell<AlumnoProperty,Boolean>();
                cell.setAlignment(javafx.geometry.Pos.CENTER);
                return  cell;
            }
        });

        final TreeItem<AlumnoProperty> root = new RecursiveTreeItem<AlumnoProperty>(alsPrpt, RecursiveTreeObject::getChildren);

        comedorTreeTableView.getColumns().setAll(ciColumn, namesColumn,lastNamesColumn,stateColumn);
        comedorTreeTableView.setRoot(root);
        comedorTreeTableView.setEditable(true);
        comedorTreeTableView.setShowRoot(false);

    }

    @FXML
    protected void startCamera(ActionEvent event) {
        if (btnEmpezar.getText().equals("Empezar"))
            cmrCtrl = new CameraController(this, this.scene);
        this.cmrCtrl.startCamera();
    }
}
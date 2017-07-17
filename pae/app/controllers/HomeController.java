package pae.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import pae.comedor.controllers.ComedorController;
import pae.dbconnections.DbException;
import pae.estadisticas.controllers.EstadisticasController;

public class HomeController extends Application implements Initializable{

    @FXML private JFXHamburger menu;
    @FXML private JFXDrawer side;
    @FXML private JFXButton comedorButton;
    @FXML private JFXButton estadisticasButton;
    @FXML private JFXButton actualizarButton;
    @FXML private JFXSpinner updateSpinner;
    @FXML private Label avisoLabel;

    private String migrationPath;
    @Override
    public void start(Stage primaryStage) throws DbException, IOException {

        Parent home = FXMLLoader.load(getClass().getResource("/pae/app/views/HomeView.fxml"));
        Scene home_scene = new Scene(home);
        primaryStage.setScene(home_scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mostrarMenu();
        avisoLabel.setVisible(false);

    }

    public void mostrarMenu() {
        try {
            VBox box = FXMLLoader.load(getClass().getResource("/pae/app/views/MenuView.fxml"));
            side.setSidePane(box);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateSpinner.setVisible(false);
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

    public void mostrarComedor(ActionEvent event) throws Exception {

        ComedorController comedorController = new ComedorController();
        comedorController.init();
        comedorController.start((Stage) ((Node) event.getSource()).getScene().getWindow());

    }

    public void mostrarEstadisticas(ActionEvent event) throws Exception {

        EstadisticasController estadisticasController = new EstadisticasController();
        estadisticasController.init();
        estadisticasController.start((Stage) ((Node) event.getSource()).getScene().getWindow());

    }

    public void actualizarDB(ActionEvent event) {
        migrationPath = "\"" + java.nio.file.Paths.get(".").
                toAbsolutePath().normalize().toString() + "\\Migration\\Servicio_Migracion_ExcelToPostgres.exe\"";

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccione la carpeta con los archivos Excel");
        java.io.File defaultDirectory = new java.io.File(java.nio.file.Paths.get(".").
                toAbsolutePath().normalize().toString() + "\\Migration");
        chooser.setInitialDirectory(defaultDirectory);
        java.io.File selectedDirectory = chooser.showDialog((Stage) ((Node) event.getSource()).getScene().getWindow());

        Thread thread = new Thread() {
            public void run() {
                ProcessBuilder p = new ProcessBuilder();
                if (selectedDirectory != null) {
                    migrationPath += " \"" + selectedDirectory.getAbsolutePath().toString() + "\"";
                }
                comedorButton.setDisable(true);
                estadisticasButton.setDisable(true);
                actualizarButton.setDisable(true);
                menu.setDisable(true);
                avisoLabel.setVisible(true);
                updateSpinner.setVisible(true);
                p.command(migrationPath);
                final Process shell;
                try {
                    shell = p.start();
                    if(shell.waitFor() == 0 ){
                        comedorButton.setDisable(false);
                        estadisticasButton.setDisable(false);
                        actualizarButton.setDisable(false);
                        menu.setDisable(false);
                        avisoLabel.setVisible(false);
                        updateSpinner.setVisible(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
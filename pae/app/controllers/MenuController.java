package pae.app.controllers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import pae.comedor.controllers.ComedorController;
import pae.estadisticas.controllers.EstadisticasController;


public class MenuController implements Initializable {

    @FXML JFXButton homeButton;
    @FXML JFXButton comedorButton;
    @FXML JFXButton estadisticasButton;
    @FXML JFXButton perfilButton;
    @FXML JFXButton ayudaButton;
    @FXML Label nombreUsuarioLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        nombreUsuarioLabel.setText(CurrentUser.currentUserFullName);
    }

    public void mostrarHome(ActionEvent event) throws Exception {

        HomeController homeController = new HomeController();
        homeController.init();
        homeController.start((Stage) ((Node) event.getSource()).getScene().getWindow());

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

    public void mostrarAyuda() throws IOException {
        File file = new File("C:\\Users\\Isrrael\\IdeaProjects\\SC-Pae\\src\\pae\\utils\\help\\Home.html");
        Desktop.getDesktop().browse(file.toURI());
    }

    public void mostrarPerfil(ActionEvent event) throws Exception {

        PerfilController perfilController = new PerfilController();
        perfilController.init();
        perfilController.start((Stage) ((Node) event.getSource()).getScene().getWindow());

    }

    public void salir(ActionEvent event) throws Exception {

        System.exit(0);

    }
}
package pae.comedor.controllers;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import pae.utils.Utils;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Miguel on 16/06/2017.
 */
public class CameraController {

    @FXML ImageView currentFrame;
    @FXML JFXButton btnEmpezar;
    @FXML JFXTextField cedulaTextField;

    private ScheduledExecutorService timerCam;
    private ScheduledExecutorService timerBCod;
    // the OpenCV object that realizes the video capture
    private final VideoCapture capture = new VideoCapture();
    // a flag to change the startButton behavior
    private boolean cameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    public static Mat fm = null;
    private BufferedImage grabbedImage;
    private ComedorController ctr = null;

    public CameraController (ComedorController ctr, Scene scn)
    {
        this.ctr = ctr;
        this.currentFrame = (ImageView) scn.lookup("#currentFrame");
        this.btnEmpezar = (JFXButton) scn.lookup("#btnEmpezar");
        this.cedulaTextField = (JFXTextField) scn.lookup("#cedulaTextField");
    }

    public void startCamera ()
    {
        if (!this.cameraActive) {
            //Iniciamos la camara para capturar video
            this.capture.open(cameraId);
            //Preguntamos si el video esta disponible
            if (this.capture.isOpened()) {
                this.cameraActive = true;
                //Hilo de video (30 frames/sec)
                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        //Se recibe un frame
                        Mat frame = grabFrame();
                        //Se convierte a imagen
                        Image imageToShow = Utils.mat2Image(frame);
                        //Actualizamos la imagen en pantalla
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timerCam = Executors.newSingleThreadScheduledExecutor();
                this.timerCam.scheduleAtFixedRate(frameGrabber,
                        0, 33, TimeUnit.MILLISECONDS);

                //Hilo de reconocimiento de matriculas
                Runnable recogFrame = () -> {
                    try {

                        if ((fm != null)) {
                            //Platform.runLater(() -> {
                            grabbedImage = getBufferedImage();
                            Result decodedBar = getDecodedImage();
                            try {
                                validateBarcode(decodedBar);
                            } catch (ParseException ex) {
                                Logger.getLogger(ComedorController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ComedorController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                };

                this.timerBCod = Executors.newSingleThreadScheduledExecutor();
                this.timerBCod.scheduleAtFixedRate(recogFrame, 0, 33, TimeUnit.MILLISECONDS);

                btnEmpezar.setText("Detener");

            } else {
                System.err.println("No se puede conectar a la camara...");
            }
        } else {
            this.cameraActive = false;

            //Se detienen los procesos
            this.stopAcquisition();
            btnEmpezar.setText("Empezar");
        }
    }

    private void validateBarcode(Result decodedBar) throws ParseException, InterruptedException {
        if (decodedBar != null) {
            System.err.println("validando");
            Platform.runLater(() -> {
                cedulaTextField.setText(decodedBar.getText());
            });
            String cedula = decodedBar.getText();//.replace("V","");
            Platform.runLater(() -> {
                ctr.procesarAlumno(cedula);
            });
            Thread.sleep(5000);
        }
    }

    private Mat grabFrame() {
        //Iniciamos el frme
        Mat frame = new Mat();

        //Verificamos si la camara esta activa para grabar
        if (this.capture.isOpened()) {
            try {
                //leemos el frame actual
                this.capture.read(frame);

                //si el frame no esta vacio lo procesa
                if (!frame.empty()) {
                    fm = frame;
                }

            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

    private void updateImageView(javafx.scene.image.ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    private Result getDecodedImage() {
        LuminanceSource source = new BufferedImageLuminanceSource(grabbedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result decodedBar = null;
        try {
            decodedBar = new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {

            //No hay código en la imágen
        }
        return decodedBar;
    }

    BufferedImage getBufferedImage(){

        return
                Utils.matToBufferedImage(fm);

    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timerCam != null && !this.timerCam.isShutdown()) {
            try {
                //detenemos el hilo de grabacion
                this.timerCam.shutdown();
                this.timerCam.awaitTermination(33, TimeUnit.MILLISECONDS);
                //detenemos el hilo de reconocimiento de codigo de barra
                this.timerBCod.shutdown();
                this.timerBCod.awaitTermination(33, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                System.err.println("problema al detener la captura de video,"
                        + " intentando cerrar la camara" + e);
            }
        }
        if (this.capture.isOpened()) {
            //liberamos la camara
            this.capture.release();
        }
    }
}

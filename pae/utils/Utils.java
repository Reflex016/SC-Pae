package pae.utils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import pae.dbconnections.DbException;
import pae.dbconnections.PostgresDbConnection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;

/**
 * Created by Miguel on 11/06/2017.
 */
public class Utils {
    public static Image mat2Image(Mat frame) {
        if (frame == null) {
            System.out.println("Frame nulo...");
            return null;
        }
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> {
            property.set(value);
        });
    }

    public static BufferedImage matToBufferedImage(Mat original) {
        // init
        if (original == null) {
            System.out.println("Frame nulo...");
            return null;
        }
        BufferedImage image = null;
        int width = original.width(), height = original.height(),
                channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels
                = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }
}

package com.group4.macromanager.util;

// This class contains utility methods for image handling across the application.

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class ImageUtil {

    // Set placeholder image
    public static void setPlaceholderImage(ImageView imageView) {
        try {
            imageView.setImage(new Image(ImageUtil.class.getResource("/images/placeholder.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Could not load placeholder image: " + e.getMessage());
        }
    }

    // Open file chooser to select image file
    public static File selectImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(null);
    }

    // Set image from file to ImageView
    public static void setImageFromFile(ImageView imageView, File imageFile) {
        if (imageFile != null) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        }
    }
}

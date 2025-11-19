package com.group4.macromanager.util;

// This class contains utility methods for image handling across the application.

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageUtil {

    // use a runtime directory instead of resources
    private static final String UPLOADS_DIR = "uploads/"; // Root of project, not in resources
    private static final String UPLOADS_RESOURCE_PREFIX = "file:uploads/";

    // Set placeholder image
    public static void setPlaceholderImage(ImageView imageView) {
        try {
            imageView.setImage(new Image(ImageUtil.class.getResource("/images/placeholder.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Could not load placeholder image: " + e.getMessage());
        }
    }

    // Select and copy image file to uploads directory
    public static File selectImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Food Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return copyToUploads(selectedFile);
        }
        return null;
    }

    // Copy file to uploads directory with unique name
    private static File copyToUploads(File sourceFile) {
        try {
            // Create uploads directory if it doesn't exist
            Path uploadsPath = Paths.get(UPLOADS_DIR);
            Files.createDirectories(uploadsPath);

            // Generate unique filename to avoid conflicts
            String extension = getFileExtension(sourceFile.getName());
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            Path targetPath = uploadsPath.resolve(uniqueFileName);

            // Copy file synchronously
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Validate the copy was successful
            if (!Files.exists(targetPath)) {
                throw new IOException("File copy validation failed - target file does not exist");
            }

            // Additional validation - check file size
            long originalSize = Files.size(sourceFile.toPath());
            long copiedSize = Files.size(targetPath);
            if (originalSize != copiedSize) {
                throw new IOException("File copy validation failed - size mismatch");
            }

            System.out.println("Successfully copied image to: " + targetPath);
            return targetPath.toFile();
        }
        catch (IOException e) {
            System.err.println("Failed to copy image to uploads directory: " + e.getMessage());
            return null;
        }
    }

    // Get file extension
    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        // default to .png if no extension found
        return lastDot > 0 ? fileName.substring(lastDot) : ".png";
    }

    // Set image from file path (handles both uploads and resources)
    public static void setImageFromFile(ImageView imageView, File imageFile) {
        if (imageFile == null) {
            setPlaceholderImage(imageView);
            return;
        }

        try {
            Image image = new Image(imageFile.toURI().toString());
            if (image.isError()) {
                setPlaceholderImage(imageView);
            } else {
                imageView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
            setPlaceholderImage(imageView);
        }
    }

    // getResourcePath returns file URLs
    public static String getResourcePath(File imageFile) {
        if (imageFile == null) return null;

        String absolutePath = imageFile.getAbsolutePath().replace("\\", "/");
        if (absolutePath.contains("/uploads/")) {
            String fileName = imageFile.getName();
            return UPLOADS_RESOURCE_PREFIX + fileName; // Returns "file:uploads/filename.jpg"
        }
        return null;
    }

    // Load image from resource path
    public static void setImageFromResourcePath(ImageView imageView, String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            setPlaceholderImage(imageView);
            return;
        }

        try {
            Image image;
            if (resourcePath.startsWith("file:uploads/")) {
                // Load from runtime uploads directory
                String fileName = resourcePath.substring("file:uploads/".length());
                File imageFile = new File("uploads/" + fileName);
                image = new Image(imageFile.toURI().toString());
            } else {
                // Load from resources (placeholders, etc.)
                var resource = ImageUtil.class.getResource(resourcePath);
                if (resource != null) {
                    image = new Image(resource.toExternalForm());
                } else {
                    setPlaceholderImage(imageView);
                    return;
                }
            }
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load image from resource path: " + resourcePath);
            setPlaceholderImage(imageView);
        }
    }

    // method to delete uploaded image file
    public static boolean deleteUploadedImage(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith("file:uploads/")) {
            return false;
        }

        try {
            String fileName = resourcePath.substring("file:uploads/".length());
            Path filePath = Paths.get("uploads/" + fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted image file: " + fileName);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to delete image file: " + e.getMessage());
        }

        return false;
    }
}

package com.furniture.visualizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set essential system properties for macOS
        System.setProperty("apple.awt.application.name", "Furniture Visualizer");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("javafx.macosx.embedded", "true");
        
        // Load the login FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        // Set up the stage
        Scene scene = new Scene(root, 400, 300);
        
        // Add application CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        primaryStage.setTitle("Furniture Visualizer - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        
        // Set application icon if available
        try {
            Image appIcon = new Image(getClass().getResourceAsStream("/icons/AppIcon.icns"));
            if (!appIcon.isError()) {
                primaryStage.getIcons().add(appIcon);
            }
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        // Show the primary stage
        Platform.runLater(() -> {
            try {
                // Ensure we're on the JavaFX Application Thread
                if (!Platform.isFxApplicationThread()) {
                    throw new IllegalStateException("Not on JavaFX Application Thread");
                }
                primaryStage.show();
            } catch (Exception e) {
                System.err.println("Error showing stage: " + e.getMessage());
                e.printStackTrace();
                Platform.exit();
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

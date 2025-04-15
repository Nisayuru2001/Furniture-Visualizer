package com.furniture.visualizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set essential system properties for macOS
        System.setProperty("apple.awt.application.name", "Furniture Visualizer");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        // Load the login FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        // Create scene with explicit dimensions
        Scene scene = new Scene(root, 400, 300);
        
        // Configure stage with minimal decorations
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Furniture Visualizer");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.setResizable(false);
        
        // Center the window on screen
        primaryStage.centerOnScreen();
        
        // Show the stage using Platform.runLater to ensure proper initialization
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
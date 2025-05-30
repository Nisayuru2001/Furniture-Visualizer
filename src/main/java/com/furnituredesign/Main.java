package com.furnituredesign;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            URL fxmlUrl = getClass().getClassLoader().getResource("fxml/login.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Could not find login.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);            // Load CSS styles
            URL mainCssUrl = getClass().getClassLoader().getResource("styles/main.css");
            URL themeCssUrl = getClass().getClassLoader().getResource("styles/theme.css");
            
            if (mainCssUrl != null) {
                scene.getStylesheets().add(mainCssUrl.toExternalForm());
            }
            if (themeCssUrl != null) {      
                scene.getStylesheets().add(themeCssUrl.toExternalForm());
            }primaryStage.setTitle("Furniture Designer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
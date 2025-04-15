package com.furniture.visualizer.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.furniture.visualizer.db.SQLiteManager;
import com.furniture.visualizer.model.User;

import java.io.IOException;

/**
 * Controller for the login screen
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private BorderPane rootPane;
    
    @FXML
    private VBox loginFormContainer;
    
    private SQLiteManager dbManager;
    
    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        // Initialize database connection
        dbManager = new SQLiteManager();
        
        // Set error label initially invisible
        errorLabel.setVisible(false);
        
        // Add key event handlers
        setupKeyHandlers();
        
        // Add focus animations
        setupFocusAnimations();
        
        // Play entrance animations
        Platform.runLater(this::playEntranceAnimations);
    }
    
    /**
     * Sets up focus animations for input fields
     */
    private void setupFocusAnimations() {
        // Add subtle style effect when fields get focus
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle("-fx-background-color: white; -fx-border-color: #3f51b5; -fx-border-width: 1px;");
            } else {
                usernameField.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: transparent;");
            }
        });
        
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: white; -fx-border-color: #3f51b5; -fx-border-width: 1px;");
            } else {
                passwordField.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: transparent;");
            }
        });
        
        // Button press effect
        loginButton.setOnMousePressed(e -> {
            loginButton.setStyle("-fx-background-color: #1a237e; -fx-effect: none;");
        });
        
        loginButton.setOnMouseReleased(e -> {
            loginButton.setStyle("-fx-background-color: #3f51b5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        });
    }
    
    /**
     * Plays entrance animations for login form elements
     */
    private void playEntranceAnimations() {
        double delay = 0;
        double staggerDelay = 100; // milliseconds between each element
        
        for (int i = 0; i < loginFormContainer.getChildren().size(); i++) {
            var node = loginFormContainer.getChildren().get(i);
            
            // Start with node invisible
            node.setOpacity(0);
            node.setTranslateY(20);
            
            // Create fade in transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(delay));
            
            // Create move up transition
            TranslateTransition moveUp = new TranslateTransition(Duration.millis(300), node);
            moveUp.setFromY(20);
            moveUp.setToY(0);
            moveUp.setDelay(Duration.millis(delay));
            
            // Play both animations
            fadeIn.play();
            moveUp.play();
            
            // Increase delay for next element
            delay += staggerDelay;
        }
        
        // Focus username field after animations
        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> usernameField.requestFocus());
        pause.play();
    }
    
    /**
     * Sets up key event handlers for input fields
     */
    private void setupKeyHandlers() {
        // Set up enter key handling on username field
        usernameField.setOnAction(event -> {
            if (usernameField.getText().isEmpty()) {
                showError("Please enter your username");
            } else {
                passwordField.requestFocus();
            }
        });
        
        // Set up enter key handling on password field
        passwordField.setOnAction(event -> handleLogin());
    }
    
    /**
     * Handles the login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate inputs
        if (username.isEmpty() && password.isEmpty()) {
            showError("Please enter both username and password");
            usernameField.requestFocus();
            return;
        } else if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        } else if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Clear error if previously shown
        errorLabel.setVisible(false);
        
        // Disable the form controls during login
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        loginButton.setDisable(true);
        
        // Show loading state
        loginButton.setText("LOGGING IN...");
        
        // Add small delay to show the loading state (simulates network request)
        PauseTransition pause = new PauseTransition(Duration.millis(800));
        pause.setOnFinished(e -> {
            // Attempt authentication
            try {
                User user = dbManager.authenticateUser(username, password);
                
                if (user != null) {
                    // Authentication successful - fade out and transition
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), rootPane);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> loadMainApplication());
                    fadeOut.play();
                } else {
                    // Authentication failed - reset form and show error
                    resetForm();
                    showError("Invalid username or password");
                }
            } catch (Exception ex) {
                // Handle error
                resetForm();
                showError("Error during login: " + ex.getMessage());
            }
        });
        pause.play();
    }
    
    /**
     * Resets the form after an error
     */
    private void resetForm() {
        // Re-enable form controls
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        loginButton.setDisable(false);
        
        // Reset button text
        loginButton.setText("LOGIN");
        
        // Clear password field
        passwordField.clear();
        passwordField.requestFocus();
    }
    
    /**
     * Shows an error message with animation
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        
        // Make sure it's visible
        errorLabel.setVisible(true);
        
        // Create shake animation
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), errorLabel);
        shake.setFromX(0);
        shake.setByX(5);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
    
    /**
     * Loads the main application window
     */
    private void loadMainApplication() {
        try {
            // Load main application FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            
            // Create and set up the new scene
            Scene scene = new Scene(root, 1200, 800);
            
            // Apply styles
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Set up the stage
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Furniture Visualizer");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Close the login window and show the main window
            currentStage.close();
            stage.show();
            
            // Maximize window on startup
            stage.setMaximized(true);
        } catch (IOException e) {
            // If we can't load the main window, re-enable the login form
            resetForm();
            showError("Error loading main application: " + e.getMessage());
        }
    }
}
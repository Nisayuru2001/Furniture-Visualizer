package com.furnituredesign.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.furnituredesign.services.AuthService;
import java.io.IOException;
import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label forgotPasswordLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Set up click handler for forgot password label
        forgotPasswordLabel.setOnMouseClicked(event -> handleForgotPassword());
    }

    @FXML
    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both Username and Password");
                return;
            }

            if (authService.authenticate(username, password)) {
                try {
                    //  Main application
                    URL fxmlUrl = getClass().getClassLoader().getResource("fxml/main.fxml");
                    if (fxmlUrl == null) {
                        throw new IOException("Could not find main.fxml");
                    }

                    FXMLLoader loader = new FXMLLoader(fxmlUrl);
                    Parent root = loader.load();

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    Scene scene = new Scene(root);

                    // Load designing parts
                    URL cssUrl = getClass().getClassLoader().getResource("styles/main.css");
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                    }

                    stage.setScene(scene);
                    stage.setTitle("Furniture Designer - Main");
                } catch (IOException e) {
                    errorLabel.setText("Error loading main view: " + e.getMessage());
                    System.err.println("Error loading FXML: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                errorLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            errorLabel.setText("An unexpected error occurred: " + e.getMessage());
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        // First step: Ask for the username
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Password Recovery");
        usernameDialog.setHeaderText("Enter your username");
        usernameDialog.setContentText("Username:");
        
        Optional<String> usernameResult = usernameDialog.showAndWait();
        
        if (usernameResult.isPresent() && !usernameResult.get().isEmpty()) {
            String username = usernameResult.get();
            
            // Check if the user exists
            if (!authService.userExists(username)) {
                showError("Username not found in our records.");
                return;
            }
            
            // Get the security question for this user
            String securityQuestion = authService.getSecurityQuestion(username);
            if (securityQuestion == null) {
                securityQuestion = "What is your favorite furniture piece?"; // Default if not found
            }
            
            // Show security question dialog
            TextInputDialog securityDialog = new TextInputDialog();
            securityDialog.setTitle("Security Verification");
            securityDialog.setHeaderText("Please answer your security question");
            securityDialog.setContentText(securityQuestion);
            
            Optional<String> securityResult = securityDialog.showAndWait();
            
            if (securityResult.isPresent() && !securityResult.get().isEmpty()) {
                String answer = securityResult.get();
                
                // Verify the security answer
                if (authService.verifySecurityAnswer(username, answer)) {
                    // Security answer is correct, set a new password
                    String newPassword = "password123"; // In production, generate a secure password
                    boolean success = authService.resetPassword(username, newPassword);
                    
                    if (success) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Password Reset");
                        successAlert.setHeaderText("Password Reset Success");
                        successAlert.setContentText("Your password has been reset to '" + newPassword + "'.\nPlease change it after logging in.");
                        successAlert.showAndWait();
                        
                        // Pre-fill the username field to make it easier for the user
                        usernameField.setText(username);
                        passwordField.setText(newPassword); // For demonstration only, normally don't pre-fill passwords
                        passwordField.requestFocus();
                    } else {
                        showError("Failed to reset password. Please contact support.");
                    }
                } else {
                    showError("Incorrect security answer. Please try again.");
                }
            } else {
                showError("Security verification cancelled.");
            }
        } else {
            showError("Username required for password recovery.");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
}

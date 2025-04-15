package com.furniture.visualizer.view;

import com.furniture.visualizer.controller.ColorMaterialController;
import com.furniture.visualizer.model.Furniture;
import com.furniture.visualizer.model.FurnitureType;
import com.furniture.visualizer.model.MaterialType;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.SplitPane;
import javafx.util.Callback;

import java.io.IOException;

public class FurnitureDialog extends Dialog<Furniture> {
    
    private Furniture furniture;
    private TextField widthField;
    private TextField heightField;
    private TextField depthField;
    private TextField positionXField;
    private TextField positionYField;
    private TextField positionZField;
    private TextField rotationField;
    private ComboBox<FurnitureType> typeComboBox;
    private ColorMaterialController colorMaterialController;
    private Furniture3DModel furniturePreview;
    
    public FurnitureDialog(Furniture furniture, boolean isNew) {
        this.furniture = furniture;
        
        setTitle(isNew ? "Add Furniture" : "Edit Furniture");
        setHeaderText(isNew ? "Add new furniture item" : "Edit furniture item");
        
        // Create buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create form
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(20));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        // Add fields
        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(FurnitureType.values());
        typeComboBox.setValue(furniture.getType());
        
        // Add dimension fields
        widthField = new TextField(String.valueOf(furniture.getWidth()));
        heightField = new TextField(String.valueOf(furniture.getHeight()));
        depthField = new TextField(String.valueOf(furniture.getDepth()));
        
        // Add position fields
        positionXField = new TextField(String.valueOf(furniture.getPositionX()));
        positionYField = new TextField(String.valueOf(furniture.getPositionY()));
        positionZField = new TextField(String.valueOf(furniture.getPositionZ()));
        
        // Add rotation field
        rotationField = new TextField(String.valueOf(furniture.getRotation()));
        
        // Load color and material controller
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/color_material_dialog.fxml"));
            VBox colorMaterialPane = loader.load();
            colorMaterialController = loader.getController();
            colorMaterialController.setMaterial(furniture.getMaterial());
            colorMaterialController.setColor(Color.valueOf(furniture.getColor()));
            
            // Add labels and fields to grid
            formGrid.add(new Label("Type:"), 0, 0);
            formGrid.add(typeComboBox, 1, 0);
            
            formGrid.add(new Label("Width:"), 0, 1);
            formGrid.add(widthField, 1, 1);
            
            formGrid.add(new Label("Height:"), 0, 2);
            formGrid.add(heightField, 1, 2);
            
            formGrid.add(new Label("Depth:"), 0, 3);
            formGrid.add(depthField, 1, 3);
            
            formGrid.add(new Label("Position X:"), 0, 4);
            formGrid.add(positionXField, 1, 4);
            
            formGrid.add(new Label("Position Y:"), 0, 5);
            formGrid.add(positionYField, 1, 5);
            
            formGrid.add(new Label("Position Z:"), 0, 6);
            formGrid.add(positionZField, 1, 6);
            
            formGrid.add(new Label("Rotation:"), 0, 7);
            formGrid.add(rotationField, 1, 7);
            
            // Add 3D preview
            furniturePreview = new Furniture3DModel(furniture);
            VBox previewContainer = new VBox(10);
            previewContainer.setPadding(new Insets(10));
            previewContainer.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
            previewContainer.getChildren().add(new Label("3D Preview:"));
            previewContainer.getChildren().add(furniturePreview.getPreviewPane());
            
            // Type selection updates dimensions
            typeComboBox.setOnAction(e -> {
                FurnitureType selectedType = typeComboBox.getValue();
                if (selectedType != null) {
                    widthField.setText(String.valueOf(selectedType.getDefaultWidth()));
                    heightField.setText(String.valueOf(selectedType.getDefaultHeight()));
                    depthField.setText(String.valueOf(selectedType.getDefaultDepth()));
                    
                    // Update preview
                    furniture.setType(selectedType);
                    furniture.setWidth(selectedType.getDefaultWidth());
                    furniture.setHeight(selectedType.getDefaultHeight());
                    furniture.setDepth(selectedType.getDefaultDepth());
                    furniturePreview.updateFurniture(furniture);
                }
            });
            
            // Update preview when color or material changes
            colorMaterialController.colorPicker.setOnAction(e -> {
                furniture.setColor(colorMaterialController.getSelectedColor().toString());
                furniturePreview.updateFurniture(furniture);
            });
            
            colorMaterialController.materialComboBox.setOnAction(e -> {
                furniture.setMaterial(colorMaterialController.getSelectedMaterial());
                furniturePreview.updateFurniture(furniture);
            });
            
            // Put it all together
            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(formGrid, colorMaterialPane, previewContainer);
            splitPane.setDividerPositions(0.4, 0.7);
            
            mainContainer.getChildren().add(splitPane);
            getDialogPane().setContent(mainContainer);
            getDialogPane().setPrefWidth(800);
            getDialogPane().setPrefHeight(550);
            
        } catch (IOException e) {
            e.printStackTrace();
            GridPane fallbackGrid = new GridPane();
            fallbackGrid.setHgap(10);
            fallbackGrid.setVgap(10);
            fallbackGrid.setPadding(new Insets(20));
            
            // Add basic fields in fallback mode
            fallbackGrid.add(new Label("Type:"), 0, 0);
            fallbackGrid.add(typeComboBox, 1, 0);
            
            fallbackGrid.add(new Label("Width:"), 0, 1);
            fallbackGrid.add(widthField, 1, 1);
            
            fallbackGrid.add(new Label("Height:"), 0, 2);
            fallbackGrid.add(heightField, 1, 2);
            
            fallbackGrid.add(new Label("Depth:"), 0, 3);
            fallbackGrid.add(depthField, 1, 3);
            
            fallbackGrid.add(new Label("Position X:"), 0, 4);
            fallbackGrid.add(positionXField, 1, 4);
            
            fallbackGrid.add(new Label("Position Y:"), 0, 5);
            fallbackGrid.add(positionYField, 1, 5);
            fallbackGrid.add(new Label("Position Z:"), 0, 6);
            fallbackGrid.add(positionZField, 1, 6);
            
            fallbackGrid.add(new Label("Rotation:"), 0, 7);
            fallbackGrid.add(rotationField, 1, 7);
            
            ComboBox<MaterialType> materialComboBox = new ComboBox<>();
            materialComboBox.getItems().addAll(MaterialType.values());
            materialComboBox.setValue(furniture.getMaterial());
            
            ColorPicker colorPicker = new ColorPicker(Color.valueOf(furniture.getColor()));
            
            fallbackGrid.add(new Label("Material:"), 0, 8);
            fallbackGrid.add(materialComboBox, 1, 8);
            
            fallbackGrid.add(new Label("Color:"), 0, 9);
            fallbackGrid.add(colorPicker, 1, 9);
            
            getDialogPane().setContent(fallbackGrid);
        }
        
        // Convert result to Furniture when save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    furniture.setType(typeComboBox.getValue());
                    furniture.setWidth(Double.parseDouble(widthField.getText()));
                    furniture.setHeight(Double.parseDouble(heightField.getText()));
                    furniture.setDepth(Double.parseDouble(depthField.getText()));
                    furniture.setPositionX(Double.parseDouble(positionXField.getText()));
                    furniture.setPositionY(Double.parseDouble(positionYField.getText()));
                    furniture.setPositionZ(Double.parseDouble(positionZField.getText()));
                    furniture.setRotation(Double.parseDouble(rotationField.getText()));
                    
                    if (colorMaterialController != null) {
                        furniture.setMaterial(colorMaterialController.getSelectedMaterial());
                        furniture.setColor(colorMaterialController.getSelectedColor().toString());
                    }
                    
                    return furniture;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for dimensions and positions.");
                    return null;
                }
            }
            return null;
        });
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

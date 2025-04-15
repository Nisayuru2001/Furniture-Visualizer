package com.furniture.visualizer.controller;

import com.furniture.visualizer.model.MaterialType;
import com.furniture.visualizer.view.MaterialPreview;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorMaterialController {
    
    @FXML
    public ComboBox<MaterialType> materialComboBox;
    
    @FXML
    public ColorPicker colorPicker;
    
    @FXML
    private VBox previewContainer;
    
    private MaterialPreview materialPreview;
    
    @FXML
    public void initialize() {
        // Set up material combo box
        materialComboBox.getItems().addAll(MaterialType.values());
        materialComboBox.setValue(MaterialType.WOOD);
        
        // Set up color picker
        colorPicker.setValue(Color.BURLYWOOD);
        
        // Set up material preview
        materialPreview = new MaterialPreview();
        previewContainer.getChildren().add(materialPreview.getContainer());
        
        // Add listeners
        materialComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                materialPreview.setMaterial(newVal);
            }
        });
        
        colorPicker.setOnAction(e -> {
            materialPreview.setColor(colorPicker.getValue());
        });
    }
    
    public MaterialType getSelectedMaterial() {
        return materialComboBox.getValue();
    }
    
    public Color getSelectedColor() {
        return colorPicker.getValue();
    }
    
    public void setMaterial(MaterialType material) {
        materialComboBox.setValue(material);
        materialPreview.setMaterial(material);
    }
    
    public void setColor(Color color) {
        colorPicker.setValue(color);
        materialPreview.setColor(color);
    }
}

package com.furniture.visualizer.view;

import com.furniture.visualizer.model.MaterialType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MaterialPreview {
    private VBox container;
    private Rectangle previewRect;
    private Label nameLabel;
    private MaterialType material;
    private Color color;
    
    public MaterialPreview() {
        container = new VBox(5);
        container.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-background-radius: 5;");
        
        previewRect = new Rectangle(150, 100);
        previewRect.setStroke(Color.BLACK);
        previewRect.setStrokeWidth(1);
        
        nameLabel = new Label("Material Preview");
        nameLabel.setStyle("-fx-font-weight: bold;");
        
        container.getChildren().addAll(previewRect, nameLabel);
        
        // Default values
        material = MaterialType.WOOD;
        color = Color.BURLYWOOD;
        updatePreview();
    }
    
    public void setMaterial(MaterialType material) {
        this.material = material;
        updatePreview();
    }
    
    public void setColor(Color color) {
        this.color = color;
        updatePreview();
    }
    
    private void updatePreview() {
        if (nameLabel != null) {
            nameLabel.setText(material.getDisplayName());
        }
        
        if (previewRect != null) {
            previewRect.setFill(color);
            
            // Apply material-specific effects
            switch (material) {
                case WOOD:
                    previewRect.setStyle("-fx-effect: dropshadow(gaussian, #8B4513, 10, 0.5, 0, 0);");
                    break;
                case METAL:
                    previewRect.setStyle("-fx-effect: dropshadow(gaussian, #C0C0C0, 10, 0.7, 0, 0);");
                    break;
                case FABRIC:
                    previewRect.setStyle("-fx-effect: dropshadow(gaussian, #A0522D, 5, 0.3, 0, 0);");
                    break;
                case GLASS:
                    previewRect.setStyle("-fx-effect: dropshadow(gaussian, #87CEEB, 15, 0.8, 0, 0);");
                    break;
                case PLASTIC:
                    previewRect.setStyle("-fx-effect: dropshadow(gaussian, #D3D3D3, 8, 0.6, 0, 0);");
                    break;
                default:
                    previewRect.setStyle("");
                    break;
            }
        }
    }
    
    public VBox getContainer() {
        return container;
    }
} 
package com.furniture.visualizer.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ScrollPane;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

import java.util.function.Consumer;

public class RoomConfigPanel extends ScrollPane { // Change to extend ScrollPane
    private final GridPane grid; // Create a GridPane to hold the controls

    private final TextField widthField;
    private final TextField heightField;
    private final TextField depthField;
    private final ColorPicker wallColorPicker;
    private final Slider roomWidthSlider;
    private final Slider roomHeightSlider;
    private final Slider roomDepthSlider;
    
    private Consumer<Double> onWidthChange;
    private Consumer<Double> onHeightChange;
    private Consumer<Double> onDepthChange;
    private Consumer<Color> onColorChange;
    
    public RoomConfigPanel() {
        grid = new GridPane(); // Initialize the GridPane
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        // Room width controls
        grid.add(new Label("Width (m):"), 0, 0);
        widthField = new TextField("3.0");
        widthField.setPrefWidth(60);
        grid.add(widthField, 1, 0);
        
        roomWidthSlider = new Slider(1.0, 10.0, 3.0);
        roomWidthSlider.setShowTickMarks(true);
        roomWidthSlider.setShowTickLabels(true);
        roomWidthSlider.setMajorTickUnit(1.0);
        roomWidthSlider.setMinorTickCount(4);
        roomWidthSlider.setBlockIncrement(0.1);
        roomWidthSlider.setPrefWidth(150);
        grid.add(roomWidthSlider, 2, 0);
        
        // Room height controls
        grid.add(new Label("Height (m):"), 0, 1);
        heightField = new TextField("2.5");
        heightField.setPrefWidth(60);
        grid.add(heightField, 1, 1);
        
        roomHeightSlider = new Slider(1.0, 5.0, 2.5);
        roomHeightSlider.setShowTickMarks(true);
        roomHeightSlider.setShowTickLabels(true);
        roomHeightSlider.setMajorTickUnit(0.5);
        roomHeightSlider.setMinorTickCount(4);
        roomHeightSlider.setBlockIncrement(0.1);
        roomHeightSlider.setPrefWidth(150);
        grid.add(roomHeightSlider, 2, 1);
        
        // Room depth controls
        grid.add(new Label("Depth (m):"), 0, 2);
        depthField = new TextField("4.0");
        depthField.setPrefWidth(60);
        grid.add(depthField, 1, 2);
        
        roomDepthSlider = new Slider(1.0, 10.0, 4.0);
        roomDepthSlider.setShowTickMarks(true);
        roomDepthSlider.setShowTickLabels(true);
        roomDepthSlider.setMajorTickUnit(1.0);
        roomDepthSlider.setMinorTickCount(4);
        roomDepthSlider.setBlockIncrement(0.1);
        roomDepthSlider.setPrefWidth(150);
        grid.add(roomDepthSlider, 2, 2);
        
        // Wall color control
        grid.add(new Label("Wall Color:"), 0, 3);
        wallColorPicker = new ColorPicker(Color.WHITE);
        grid.add(wallColorPicker, 1, 3, 2, 1);
        
        // Bind text fields and sliders
        bindTextFieldToSlider(widthField, roomWidthSlider);
        bindTextFieldToSlider(heightField, roomHeightSlider);
        bindTextFieldToSlider(depthField, roomDepthSlider);
        
        // Add listeners with validation
        addInputValidation(widthField, onWidthChange);
        addInputValidation(heightField, onHeightChange);
        addInputValidation(depthField, onDepthChange);
        
        wallColorPicker.setOnAction(e -> {
            if (onColorChange != null) {
                onColorChange.accept(wallColorPicker.getValue());
            }
        });
        
        // Set the content of the ScrollPane to the GridPane
        setContent(grid);
        setFitToWidth(true); // Allow the ScrollPane to fit the width of the parent
        setFitToHeight(true); // Allow the ScrollPane to fit the height of the parent
    }
    
    private void bindTextFieldToSlider(TextField textField, Slider slider) {
        // Bind text field to slider
        textField.textProperty().bindBidirectional(slider.valueProperty(), new NumberStringConverter("0.0"));
        
        // Update slider when text field changes
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                if (value >= slider.getMin() && value <= slider.getMax()) {
                    slider.setValue(value);
                }
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
    }
    
    private void addInputValidation(TextField textField, Consumer<Double> onChange) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                textField.setStyle("-fx-border-color: red;"); // Highlight empty input
                return;
            }
            try {
                double value = Double.parseDouble(newVal);
                if (value > 0 && onChange != null) {
                    onChange.accept(value);
                    textField.setStyle(""); // Reset style on valid input
                } else {
                    textField.setStyle("-fx-border-color: red;"); // Highlight invalid input
                }
            } catch (NumberFormatException e) {
                textField.setText(oldVal); // Reset to previous valid value
                textField.setStyle("-fx-border-color: red;"); // Highlight invalid input
            }
        });
    }
    
    public void setOnWidthChange(Consumer<Double> consumer) {
        this.onWidthChange = consumer;
    }
    
    public void setOnHeightChange(Consumer<Double> consumer) {
        this.onHeightChange = consumer;
    }
    
    public void setOnDepthChange(Consumer<Double> consumer) {
        this.onDepthChange = consumer;
    }
    
    public void setOnColorChange(Consumer<Color> consumer) {
        this.onColorChange = consumer;
    }
    
    public double getRoomWidth() {
        try {
            return Double.parseDouble(widthField.getText());
        } catch (NumberFormatException e) {
            return 3.0;
        }
    }
    
    public double getRoomHeight() {
        try {
            return Double.parseDouble(heightField.getText());
        } catch (NumberFormatException e) {
            return 2.5;
        }
    }
    
    public double getDepth() {
        try {
            return Double.parseDouble(depthField.getText());
        } catch (NumberFormatException e) {
            return 4.0;
        }
    }
    
    public Color getWallColor() {
        return wallColorPicker.getValue();
    }
    
    public void setWidth(double width) {
        widthField.setText(String.format("%.1f", width));
        roomWidthSlider.setValue(width);
    }
    
    public void setHeight(double height) {
        heightField.setText(String.format("%.1f", height));
        roomHeightSlider.setValue(height);
    }
    
    public void setDepth(double depth) {
        depthField.setText(String.format("%.1f", depth));
        roomDepthSlider.setValue(depth);
    }
    
    public void setWallColor(Color color) {
        wallColorPicker.setValue(color);
    }
}

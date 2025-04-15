package com.furniture.visualizer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.furniture.visualizer.view.Visualization2D;
import com.furniture.visualizer.view.Visualization3D;
import com.furniture.visualizer.view.FurnitureDialog;
import com.furniture.visualizer.view.RoomConfigPanel;
import com.furniture.visualizer.db.SQLiteManager;
import com.furniture.visualizer.model.FurnitureDesign;
import com.furniture.visualizer.model.Furniture;
import com.furniture.visualizer.model.FurnitureType;
import com.furniture.visualizer.model.MaterialType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML
    private StackPane visualizationPane;
    
    @FXML
    private ListView<FurnitureDesign> designListView;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private ComboBox<FurnitureType> typeComboBox;
    
    @FXML
    private ComboBox<MaterialType> materialComboBox;
    
    @FXML
    private ListView<Furniture> furnitureListView;
    
    @FXML
    private VBox roomConfigContainer;
    
    @FXML
    private CheckBox showGridCheckbox;
    
    @FXML
    private CheckBox showDimensionsCheckbox;
    
    @FXML
    private CheckBox showLabelsCheckbox;
    
    @FXML
    private ToggleButton view2DToggle;
    
    @FXML
    private ToggleButton view3DToggle;
    
    private Visualization2D visualization2D;
    private Visualization3D visualization3D;
    private RoomConfigPanel roomConfigPanel;
    private boolean is3DView = false;
    private SQLiteManager dbManager;
    private ObservableList<FurnitureDesign> designs;
    private ObservableList<Furniture> furnitureItems;
    private FurnitureDesign currentDesign;
    private Furniture selectedFurniture;
    
    @FXML
    public void initialize() {
        dbManager = new SQLiteManager();
        designs = FXCollections.observableArrayList();
        furnitureItems = FXCollections.observableArrayList();
        
        // Initialize visualization components
        visualization2D = new Visualization2D();
        visualization3D = new Visualization3D();
        
        // Initialize room config panel
        roomConfigPanel = new RoomConfigPanel();
        roomConfigContainer.getChildren().add(roomConfigPanel);
        
        // Set up design list
        designListView.setItems(designs);
        designListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDesignDetails(newVal);
            }
        });
        
        // Set up furniture list
        furnitureListView.setItems(furnitureItems);
        furnitureListView.setCellFactory(param -> new ListCell<Furniture>() {
            @Override
            protected void updateItem(Furniture item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getType().getDisplayName() + " (" + 
                          item.getMaterial().getDisplayName() + ")");
                    
                    if (item == selectedFurniture) {
                        setStyle("-fx-background-color: lightyellow;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        furnitureListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedFurniture = newVal;
            if (is3DView) {
                visualization3D.setSelectedFurniture(newVal);
            } else {
                visualization2D.setSelectedFurniture(newVal);
            }
        });
        
        // Initialize combo boxes
        typeComboBox.setItems(FXCollections.observableArrayList(FurnitureType.values()));
        materialComboBox.setItems(FXCollections.observableArrayList(MaterialType.values()));
        
        // Add listeners for room dimension changes
        roomConfigPanel.setOnWidthChange(width -> {
            if (is3DView) {
                visualization3D.updateRoomDimensions(width, roomConfigPanel.getDepth(), roomConfigPanel.getRoomHeight());
            } else {
                visualization2D.updateRoomDimensions(width, roomConfigPanel.getDepth());
            }
        });
        
        roomConfigPanel.setOnDepthChange(depth -> {
            if (is3DView) {
                visualization3D.updateRoomDimensions(roomConfigPanel.getRoomWidth(), depth, roomConfigPanel.getRoomHeight());
            } else {
                visualization2D.updateRoomDimensions(roomConfigPanel.getRoomWidth(), depth);
            }
        });
        
        roomConfigPanel.setOnHeightChange(height -> {
            if (is3DView) {
                visualization3D.updateRoomDimensions(roomConfigPanel.getRoomWidth(), roomConfigPanel.getDepth(), height);
            }
        });
        
        // Add listener for color changes
        roomConfigPanel.setOnColorChange(color -> {
            if (is3DView) {
                visualization3D.updateRoomColor(color);
            } else {
                visualization2D.updateRoomColor(color);
            }
        });
        
        // Set up visualization options
        showGridCheckbox.setSelected(true);
        showDimensionsCheckbox.setSelected(true);
        showLabelsCheckbox.setSelected(true);
        
        showGridCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            visualization2D.setShowGrid(newVal);
        });
        
        showDimensionsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            visualization2D.setShowDimensions(newVal);
        });
        
        showLabelsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            visualization2D.setShowLabels(newVal);
        });
        
        // Set up view toggle buttons
        view2DToggle.setSelected(true);
        view2DToggle.setOnAction(e -> {
            if (view2DToggle.isSelected()) {
                view3DToggle.setSelected(false);
                setView(false);
            } else {
                view2DToggle.setSelected(true); // Keep at least one toggle selected
            }
        });
        
        view3DToggle.setOnAction(e -> {
            if (view3DToggle.isSelected()) {
                view2DToggle.setSelected(false);
                setView(true);
            } else {
                view3DToggle.setSelected(true); // Keep at least one toggle selected
            }
        });
        
        // Set default view to 2D
        setView(false);
        
        // Load existing designs
        loadDesigns();
        
        // If no designs exist, create a default design with furniture items
        if (designs.isEmpty()) {
            handleNewDesign();
        }
    }
    
    private void setView(boolean use3D) {
        is3DView = use3D;
        visualizationPane.getChildren().clear();
        
        if (is3DView) {
            visualizationPane.getChildren().add(visualization3D);
            
            // Update 3D visualization with current room dimensions
            visualization3D.updateRoomDimensions(
                roomConfigPanel.getRoomWidth(), 
                roomConfigPanel.getDepth(), 
                roomConfigPanel.getRoomHeight()
            );
            visualization3D.updateRoomColor(roomConfigPanel.getWallColor());
            visualization3D.setFurniture(furnitureItems);
            
            // Set selected furniture in 3D view
            if (selectedFurniture != null) {
                visualization3D.setSelectedFurniture(selectedFurniture);
            }
            
            // Show/hide relevant controls
            showGridCheckbox.setDisable(true);
            showDimensionsCheckbox.setDisable(true);
            showLabelsCheckbox.setDisable(true);
        } else {
            visualizationPane.getChildren().add(visualization2D);
            
            // Update 2D visualization with current room dimensions
            visualization2D.updateRoomDimensions(
                roomConfigPanel.getRoomWidth(), 
                roomConfigPanel.getDepth()
            );
            visualization2D.updateRoomColor(roomConfigPanel.getWallColor());
            visualization2D.setFurniture(furnitureItems);
            
            // Set selected furniture in 2D view
            if (selectedFurniture != null) {
                visualization2D.setSelectedFurniture(selectedFurniture);
            }
            
            // Show relevant controls
            showGridCheckbox.setDisable(false);
            showDimensionsCheckbox.setDisable(false);
            showLabelsCheckbox.setDisable(false);
        }
    }
    
    private void addDefaultFurnitureItems(int designId) {
        // Create a modern chair
        Furniture modernChair = new Furniture();
        modernChair.setDesignId(designId);
        modernChair.setType(FurnitureType.CHAIR);
        modernChair.setMaterial(MaterialType.METAL);
        modernChair.setWidth(0.6);
        modernChair.setHeight(0.9);
        modernChair.setDepth(0.6);
        modernChair.setPositionX(1.5);
        modernChair.setPositionY(1.0);
        modernChair.setPositionZ(0.0);
        modernChair.setRotation(0.0);
        modernChair.setColor(Color.SILVER.toString());
        dbManager.addFurniture(modernChair);

        // Create a coffee table
        Furniture coffeeTable = new Furniture();
        coffeeTable.setDesignId(designId);
        coffeeTable.setType(FurnitureType.TABLE);
        coffeeTable.setMaterial(MaterialType.WOOD);
        coffeeTable.setWidth(1.2);
        coffeeTable.setHeight(0.45);
        coffeeTable.setDepth(0.8);
        coffeeTable.setPositionX(1.5);
        coffeeTable.setPositionY(2.0);
        coffeeTable.setPositionZ(0.0);
        coffeeTable.setRotation(0.0);
        coffeeTable.setColor(Color.BURLYWOOD.toString());
        dbManager.addFurniture(coffeeTable);

        // Create a sofa
        Furniture sofa = new Furniture();
        sofa.setDesignId(designId);
        sofa.setType(FurnitureType.SOFA);
        sofa.setMaterial(MaterialType.FABRIC);
        sofa.setWidth(2.0);
        sofa.setHeight(0.85);
        sofa.setDepth(0.9);
        sofa.setPositionX(1.5);
        sofa.setPositionY(3.0);
        sofa.setPositionZ(0.0);
        sofa.setRotation(0.0);
        sofa.setColor(Color.INDIANRED.toString());
        dbManager.addFurniture(sofa);
        
        // Create a bed
        Furniture bed = new Furniture();
        bed.setDesignId(designId);
        bed.setType(FurnitureType.BED);
        bed.setMaterial(MaterialType.WOOD);
        bed.setWidth(2.0);
        bed.setHeight(0.5);
        bed.setDepth(1.8);
        bed.setPositionX(1.5);
        bed.setPositionY(0.5);
        bed.setPositionZ(0.0);
        bed.setRotation(0.0);
        bed.setColor(Color.LIGHTSTEELBLUE.toString());
        dbManager.addFurniture(bed);
        
        // Create a cabinet
        Furniture cabinet = new Furniture();
        cabinet.setDesignId(designId);
        cabinet.setType(FurnitureType.CABINET);
        cabinet.setMaterial(MaterialType.WOOD);
        cabinet.setWidth(0.8);
        cabinet.setHeight(1.8);
        cabinet.setDepth(0.5);
        cabinet.setPositionX(0.5);
        cabinet.setPositionY(0.9);
        cabinet.setPositionZ(0.0);
        cabinet.setRotation(0.0);
        cabinet.setColor(Color.TAN.toString());
        dbManager.addFurniture(cabinet);
        
        // Create a desk
        Furniture desk = new Furniture();
        desk.setDesignId(designId);
        desk.setType(FurnitureType.DESK);
        desk.setMaterial(MaterialType.WOOD);
        desk.setWidth(1.4);
        desk.setHeight(0.75);
        desk.setDepth(0.7);
        desk.setPositionX(2.5);
        desk.setPositionY(0.375);
        desk.setPositionZ(0.0);
        desk.setRotation(0.0);
        desk.setColor(Color.DARKKHAKI.toString());
        dbManager.addFurniture(desk);
    }

    @FXML
    private void handleNewDesign() {
        FurnitureDesign newDesign = new FurnitureDesign();
        newDesign.setName("New Design");
        newDesign.setDescription("A new furniture design");
        newDesign.setFurnitureType(FurnitureType.CHAIR.name());
        newDesign.setWidth(3.0);
        newDesign.setHeight(2.5);
        newDesign.setDepth(4.0);
        newDesign.setColor(Color.WHITE.toString());
        newDesign.setMaterial(MaterialType.WOOD.name());
        
        dbManager.createDesign(newDesign);
        
        // Add default furniture items
        addDefaultFurnitureItems(newDesign.getId());
        
        loadDesigns();
        designListView.getSelectionModel().select(newDesign);
    }
    
    @FXML
    private void handleSaveDesign() {
        if (currentDesign != null) {
            currentDesign.setName(nameField.getText());
            currentDesign.setDescription(descriptionField.getText());
            currentDesign.setFurnitureType(typeComboBox.getValue().getDisplayName());
            
            // Save current room dimensions
            currentDesign.setWidth(roomConfigPanel.getRoomWidth());
            currentDesign.setHeight(roomConfigPanel.getRoomHeight());
            currentDesign.setDepth(roomConfigPanel.getDepth());
            
            currentDesign.setColor(roomConfigPanel.getWallColor().toString());
            currentDesign.setMaterial(materialComboBox.getValue().getDisplayName());
            
            dbManager.updateDesign(currentDesign);
            loadDesigns();
        }
    }
    
    @FXML
    private void handleDeleteDesign() {
        if (currentDesign != null) {
            // Confirm deletion
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Deletion");
            confirmDialog.setHeaderText("Delete Design");
            confirmDialog.setContentText("Are you sure you want to delete this design? This action cannot be undone.");
            
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dbManager.deleteDesign(currentDesign.getId());
                loadDesigns();
                currentDesign = null;
                clearDesignDetails();
                
                // Update visualizations
                furnitureItems.clear();
                if (is3DView) {
                    visualization3D.setFurniture(furnitureItems);
                } else {
                    visualization2D.setFurniture(furnitureItems);
                }
            }
        }
    }
    
    @FXML
    private void handleAddFurniture() {
        if (currentDesign == null) {
            showAlert("Error", "Please create or select a design first!");
            return;
        }
        
        // Create a new furniture with default values
        Furniture furniture = Furniture.createDefault(FurnitureType.CHAIR, currentDesign.getId());
        
        // Position in the center of the room by default
        furniture.setPositionX(roomConfigPanel.getRoomWidth() / 2);
        furniture.setPositionY(0); // Bottom of the furniture on the floor
        furniture.setPositionZ(roomConfigPanel.getDepth() / 2);
        
        // Use the current selected material and color if available
        if (materialComboBox.getValue() != null) {
            furniture.setMaterial(materialComboBox.getValue());
        }
        
        // Show dialog to customize
        FurnitureDialog dialog = new FurnitureDialog(furniture, true);
        Optional<Furniture> result = dialog.showAndWait();
        
        result.ifPresent(f -> {
            dbManager.addFurniture(f);
            loadFurnitureForDesign(currentDesign.getId());
            
            // Update visualizations
            if (is3DView) {
                visualization3D.setFurniture(furnitureItems);
            } else {
                visualization2D.setFurniture(furnitureItems);
            }
            
            // Select the newly added furniture
            furnitureListView.getSelectionModel().select(f);
        });
    }
    
    @FXML
    private void handleEditFurniture() {
        if (selectedFurniture == null) {
            showAlert("Error", "Please select a furniture item to edit!");
            return;
        }
        
        FurnitureDialog dialog = new FurnitureDialog(selectedFurniture, false);
        Optional<Furniture> result = dialog.showAndWait();
        
        result.ifPresent(f -> {
            dbManager.updateFurniture(f);
            loadFurnitureForDesign(currentDesign.getId());
        });
    }
    
    @FXML
    private void handleDeleteFurniture() {
        if (selectedFurniture == null) {
            showAlert("Error", "Please select a furniture item to delete!");
            return;
        }
        
        // Confirm deletion
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Furniture");
        confirmDialog.setContentText("Are you sure you want to delete this furniture item?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbManager.deleteFurniture(selectedFurniture.getId());
            loadFurnitureForDesign(currentDesign.getId());
        }
    }
    
    @FXML
    private void handleScaleDesign() {
        if (currentDesign == null) {
            showAlert("Error", "Please create or select a design first!");
            return;
        }
        
        // Create a dialog for scaling
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Scale Design");
        dialog.setHeaderText("Enter scale factor (0.1 to 10.0):");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form
        TextField scaleField = new TextField("1.0");
        dialog.getDialogPane().setContent(scaleField);
        
        // Convert the result to a scale factor when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    double scale = Double.parseDouble(scaleField.getText());
                    if (scale >= 0.1 && scale <= 10.0) {
                        return scale;
                    } else {
                        showAlert("Error", "Scale factor must be between 0.1 and 10.0");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter a valid number");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(scale -> {
            // Apply scaling to all furniture items
            for (Furniture furniture : furnitureItems) {
                furniture.setWidth(furniture.getWidth() * scale);
                furniture.setHeight(furniture.getHeight() * scale);
                furniture.setDepth(furniture.getDepth() * scale);
                dbManager.updateFurniture(furniture);
            }
            
            // Update visualizations
            loadFurnitureForDesign(currentDesign.getId());
        });
    }
    
    @FXML
    private void handleChangeColor() {
        if (currentDesign == null) {
            showAlert("Error", "Please create or select a design first!");
            return;
        }
        
        // Create a dialog for color selection
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Change Color");
        dialog.setHeaderText("Select a new color for the design:");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the color picker
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        dialog.getDialogPane().setContent(colorPicker);
        
        // Convert the result to a color when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return colorPicker.getValue();
            }
            return null;
        });
        
        Optional<Color> result = dialog.showAndWait();
        result.ifPresent(color -> {
            // Apply color to all furniture items
            for (Furniture furniture : furnitureItems) {
                furniture.setColor(color.toString());
                dbManager.updateFurniture(furniture);
            }
            
            // Update visualizations
            loadFurnitureForDesign(currentDesign.getId());
        });
    }
    
    @FXML
    private void handleAddShade() {
        if (currentDesign == null) {
            showAlert("Error", "Please create or select a design first!");
            return;
        }
        
        // Create a dialog for shade selection
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Add Shade");
        dialog.setHeaderText("Select shade intensity (0.0 to 1.0):");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the slider
        Slider shadeSlider = new Slider(0.0, 1.0, 0.5);
        shadeSlider.setShowTickLabels(true);
        shadeSlider.setShowTickMarks(true);
        shadeSlider.setMajorTickUnit(0.25);
        shadeSlider.setBlockIncrement(0.1);
        dialog.getDialogPane().setContent(shadeSlider);
        
        // Convert the result to a shade value when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return shadeSlider.getValue();
            }
            return null;
        });
        
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(shade -> {
            // Apply shade to all furniture items
            for (Furniture furniture : furnitureItems) {
                // Get current color
                Color currentColor = Color.valueOf(furniture.getColor());
                
                // Apply shade (darken the color)
                Color shadedColor = currentColor.darker().darker();
                
                // Set the new color
                furniture.setColor(shadedColor.toString());
                dbManager.updateFurniture(furniture);
            }
            
            // Update visualizations
            loadFurnitureForDesign(currentDesign.getId());
        });
    }
    
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    private void loadDesigns() {
        List<FurnitureDesign> designList = dbManager.getAllDesigns();
        designs.setAll(designList);
    }
    
    private void loadDesignDetails(FurnitureDesign design) {
        currentDesign = design;
        nameField.setText(design.getName());
        descriptionField.setText(design.getDescription());
        
        // Set furniture type
        FurnitureType furnitureType = FurnitureType.fromString(design.getFurnitureType());
        typeComboBox.setValue(furnitureType);
        
        // Set material type
        MaterialType materialType = MaterialType.fromString(design.getMaterial());
        materialComboBox.setValue(materialType);
        
        // Update room config panel
        roomConfigPanel.setWidth(design.getWidth());
        roomConfigPanel.setHeight(design.getHeight());
        roomConfigPanel.setDepth(design.getDepth());
        
        try {
            Color wallColor = Color.valueOf(design.getColor());
            roomConfigPanel.setWallColor(wallColor);
        } catch (Exception e) {
            roomConfigPanel.setWallColor(Color.WHITE);
        }
        
        // Update visualizations with new room dimensions
        if (is3DView) {
            visualization3D.updateRoomDimensions(
                design.getWidth(), 
                design.getDepth(), 
                design.getHeight()
            );
            visualization3D.updateRoomColor(roomConfigPanel.getWallColor());
        } else {
            visualization2D.updateRoomDimensions(
                design.getWidth(), 
                design.getDepth()
            );
            visualization2D.updateRoomColor(roomConfigPanel.getWallColor());
        }
        
        loadFurnitureForDesign(design.getId());
    }
    
    private void loadFurnitureForDesign(int designId) {
        List<Furniture> furnitureList = dbManager.getFurnitureForDesign(designId);
        furnitureItems.setAll(furnitureList);
        
        // Update visualizations
        if (is3DView) {
            visualization3D.setFurniture(furnitureItems);
            if (selectedFurniture != null) {
                visualization3D.setSelectedFurniture(selectedFurniture);
            }
        } else {
            visualization2D.setFurniture(furnitureItems);
            if (selectedFurniture != null) {
                visualization2D.setSelectedFurniture(selectedFurniture);
            }
        }
        
        // Update the furniture list view
        furnitureListView.setItems(furnitureItems);
    }
    
    private void clearDesignDetails() {
        nameField.clear();
        descriptionField.clear();
        typeComboBox.setValue(null);
        materialComboBox.setValue(null); 
        furnitureItems.clear();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
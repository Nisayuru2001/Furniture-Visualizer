package com.furnituredesign.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import com.furnituredesign.models.*;
import com.furnituredesign.services.DesignService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.SubScene;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Cylinder;
import javafx.scene.Node;
import java.io.IOException;
import java.util.Optional;
import com.furnituredesign.services.DesignAnalyzer;
import com.furnituredesign.services.DesignAnalyzer.DesignEvaluation;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import com.furnituredesign.models.DesignPreset;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Shape3D;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainController {
    @FXML
    private TextField roomWidthField;
    @FXML
    private TextField roomLengthField;
    @FXML
    private TextField roomHeightField;
    @FXML
    private ColorPicker wallColorPicker;
    @FXML
    private ColorPicker floorColorPicker;
    @FXML
    private ComboBox<String> furnitureTypeCombo;
    @FXML
    private ListView<Furniture> furnitureListView;
    @FXML
    private Canvas designCanvas;
    @FXML
    private ColorPicker furnitureColorPicker;
    @FXML
    private SubScene room3DSubScene;
    @FXML
    private ToggleButton toggle2DView;
    @FXML
    private ToggleButton toggle3DView;
    @FXML
    private Label statusLabel;
    @FXML
    private ListView<String> savedDesignsListView;
    
    // New UI controls for 3D view manipulation
    @FXML
    private Button zoomInBtn;
    @FXML
    private Button zoomOutBtn;
    @FXML
    private Button rotateLeftBtn;
    @FXML
    private Button rotateRightBtn;
    @FXML
    private Button rotateTiltBtn;
    @FXML
    private Button rotateResetBtn;
    @FXML
    private ToggleButton showLabelsBtn;
    @FXML
    private Button rotate360Btn;
    @FXML
    private Button rotateFurnitureBtn;
    @FXML
    private ToggleButton measureModeBtn;
    
    private final DesignService designService = new DesignService();
    private Room currentRoom;
    private List<Furniture> furnitureList = new ArrayList<>();
    private boolean is3DView = false;
    private Furniture selectedFurniture = null;
    private double dragOffsetX, dragOffsetY;
    private PerspectiveCamera camera3D;
    private double anchorX, anchorY;
    private double anchorAngleX = -20, anchorAngleY = -20;
    private double cameraAngleX = -20, cameraAngleY = -20;
    private double cameraPanX = 0, cameraPanY = 0;
    private boolean panning = false;
    private Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);

    // New method for 360 degree rotation animation
    private javafx.animation.Timeline rotate360Timeline;

    private boolean measureMode = false;
    private double measureStartX, measureStartY;
    private double measureEndX, measureEndY;

    @FXML
    public void initialize() {
        // Initialize Furniture Types
        furnitureTypeCombo.getItems().addAll(
                "Chair", "Table", "Sofa", "Bed", "Cabinet", "BookShelf");

        // Set Default Colors with a brown theme
        wallColorPicker.setValue(Color.web("#F7F3F0")); // Light cream wall color
        floorColorPicker.setValue(Color.web("#9D7B6D")); // Medium brown floor
        
        // Setup view controls and toggles
        setupViewControls();
        setupViewToggles();
        
        // Setup canvas event handlers
        setupCanvasEventHandlers();
        
        // Add listener for floor color change
        floorColorPicker.setOnAction(e -> {
            if (currentRoom != null) {
                currentRoom.setFloorColor(floorColorPicker.getValue().toString());
                if (is3DView) {
                    build3DRoomScene();
                }
                redraw();
            }
        });
        
        // Add listener for wall color change
        wallColorPicker.setOnAction(e -> {
            if (currentRoom != null) {
                currentRoom.setWallColor(wallColorPicker.getValue().toString());
                if (is3DView) {
                    build3DRoomScene();
                }
                redraw();
            }
        });

        // Set up List
        furnitureListView.setMaxHeight(Double.MAX_VALUE);
        furnitureListView.setMaxWidth(Double.MAX_VALUE);
        
        // Initialize saved designs list
        refreshSavedDesignsList();
        
        // Setup room dimension listeners
        setupRoomDimensionListeners();
        
        // Add selection listener to update color picker when furniture is selected
        furnitureListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    furnitureColorPicker.setValue(Color.web(newSelection.getColor()));
                } catch (Exception ex) {
                    furnitureColorPicker.setValue(Color.GRAY);
                }
            }
        });
        
        // Add listener for furniture color changes
        furnitureColorPicker.setOnAction(e -> {
            Furniture selectedFurniture = furnitureListView.getSelectionModel().getSelectedItem();
            if (selectedFurniture != null) {
                selectedFurniture.setColor(furnitureColorPicker.getValue().toString());
                redraw();
                if (is3DView) {
                    build3DRoomScene();
                }
                updateStatus("Updated " + selectedFurniture.getType() + " color");
            }
        });

        // Initialize canvas
        designCanvas.widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        designCanvas.heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        // Mouse events for dragging furniture
        setupCanvasEventHandlers();

        // Zoom for 3D view
        room3DSubScene.setOnScroll(e -> {
            if (is3DView && camera3D != null) {
                double delta = e.getDeltaY();
                double newZ = camera3D.getTranslateZ() + (delta > 0 ? 50 : -50);
                newZ = Math.max(-10000, Math.min(-200, newZ));
                camera3D.setTranslateZ(newZ);
            }
        });
        // Mouse  rotation and panning
        room3DSubScene.setOnMousePressed(e -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
            anchorAngleX = cameraAngleX;
            anchorAngleY = cameraAngleY;
            panning = e.isSecondaryButtonDown() || e.isShiftDown();
        });
        room3DSubScene.setOnMouseDragged(e -> {
            if (is3DView && camera3D != null) {
                double dx = e.getSceneX() - anchorX;
                double dy = e.getSceneY() - anchorY;
                if (panning) {
                    cameraPanX += dx * 0.5;
                    cameraPanY += dy * 0.5;
                    camera3D.setTranslateX(cameraPanX);
                    camera3D.setTranslateY(
                            -cameraPanY - (currentRoom != null ? (currentRoom.getHeight() * 100) / 4 : 0));
                    anchorX = e.getSceneX();
                    anchorY = e.getSceneY();
                } else {
                    cameraAngleY = anchorAngleY + dx * 0.3;
                    cameraAngleX = anchorAngleX - dy * 0.3;
                    rotateY.setAngle(cameraAngleY);
                    rotateX.setAngle(cameraAngleX);
                }
            }
        });

        // Sync color changes to 3D
        wallColorPicker.setOnAction(e -> {
            if (is3DView)
                build3DRoomScene();
        });
        
        // Configure toggle buttons for 2D/3D view
        setupViewToggles();
        
        // Set default status
        updateStatus("Ready");
        
        // Setup 3D view control buttons
        setupViewControls();
    }
    
    private void setupViewToggles() {
        // Create a toggle group to ensure only one button can be selected
        ToggleGroup viewToggleGroup = new ToggleGroup();
        toggle2DView.setToggleGroup(viewToggleGroup);
        toggle3DView.setToggleGroup(viewToggleGroup);
        
        // Set 2D view as default
        toggle2DView.setSelected(true);
        
        // Add listeners for toggle buttons
        toggle2DView.setOnAction(e -> {
            if (toggle2DView.isSelected()) {
                handle2DView();
            }
        });
        
        toggle3DView.setOnAction(e -> {
            if (toggle3DView.isSelected()) {
                handle3DView();
            }
        });
    }
    
    private void setupViewControls() {
        // Set up zoom buttons
        zoomInBtn.setOnAction(e -> {
            if (is3DView && camera3D != null) {
                double newZ = camera3D.getTranslateZ() + 150; // Increased step size
                newZ = Math.max(-10000, Math.min(-200, newZ));
                camera3D.setTranslateZ(newZ);
                updateStatus("Zoomed in");
            }
        });
        
        zoomOutBtn.setOnAction(e -> {
            if (is3DView && camera3D != null) {
                double newZ = camera3D.getTranslateZ() - 150; // Increased step size
                newZ = Math.max(-10000, Math.min(-200, newZ));
                camera3D.setTranslateZ(newZ);
                updateStatus("Zoomed out");
            }
        });
        
        // Set up rotation buttons with enhanced rotation angles
        rotateLeftBtn.setOnAction(e -> {
            if (is3DView && camera3D != null) {
                cameraAngleY -= 30; // Increased rotation angle
                rotateY.setAngle(cameraAngleY);
                updateStatus("Rotated left");
            }
        });
        
        rotateRightBtn.setOnAction(e -> {
            if (is3DView && camera3D != null) {
                cameraAngleY += 30; // Increased rotation angle
                rotateY.setAngle(cameraAngleY);
                updateStatus("Rotated right");
            }
        });
        
        rotateTiltBtn.setOnAction(e -> {
            if (is3DView && camera3D != null) {
                cameraAngleX -= 15; // Increased tilt angle
                if (cameraAngleX < -60) {
                    cameraAngleX = -5; // Cycle back to near-level view
                }
                rotateX.setAngle(cameraAngleX);
                updateStatus("Changed viewing angle");
            }
        });
        
        // Add 360 degree rotation
        if (rotate360Btn != null) {
            rotate360Btn.setOnAction(e -> {
                if (is3DView && camera3D != null) {
                    startRotate360Animation();
                    updateStatus("Started 360° room view");
                }
            });
        }
        
        rotateResetBtn.setOnAction(e -> handleResetView());
        
        // Setup labels toggle - no longer needed for 3D view, but keeping it for compatibility
        showLabelsBtn.setSelected(false);
        showLabelsBtn.setOnAction(e -> {
            if (is3DView) {
                build3DRoomScene();
                updateStatus("3D labels feature removed - labels now in 2D view");
            }
        });
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("Status: " + message);
        }
    }

    @FXML
    private void handleCreateRoom() {
        try {
            // Validate input fields
            if (roomWidthField.getText().isEmpty() ||
                    roomLengthField.getText().isEmpty() ||
                    roomHeightField.getText().isEmpty()) {
                showError("Please enter all room dimensions");
                return;
            }

            double width = Double.parseDouble(roomWidthField.getText());
            double length = Double.parseDouble(roomLengthField.getText());
            double height = Double.parseDouble(roomHeightField.getText());

            // Validate dimensions
            if (width <= 0 || length <= 0 || height <= 0) {
                showError("Room dimensions must be greater than 0");
                return;
            }

            // Create new room or update existing room
            if (currentRoom == null) {
                // Create new room
                currentRoom = new Room(width, length, height);
                furnitureList.clear();
                furnitureListView.getItems().clear();
                
                // Apply colors
                currentRoom.setWallColor(wallColorPicker.getValue().toString());
                currentRoom.setFloorColor(floorColorPicker.getValue().toString());
                
                showSuccess("Room created successfully!");
                updateStatus("Room created: " + width + "m × " + length + "m × " + height + "m");
            } else {
                // Update existing room dimensions
                currentRoom.setWidth(width);
                currentRoom.setLength(length);
                currentRoom.setHeight(height);
                
                // Apply colors
                currentRoom.setWallColor(wallColorPicker.getValue().toString());
                currentRoom.setFloorColor(floorColorPicker.getValue().toString());
                
                showSuccess("Room dimensions updated successfully!");
                updateStatus("Room updated: " + width + "m × " + length + "m × " + height + "m");
            }

            // Redraw the canvas
            redraw();

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for room dimensions");
        }
    }

    // Add listeners for room dimension fields to enable real-time updates
    private void setupRoomDimensionListeners() {
        // Add change listeners to dimension fields for validation and real-time updates
        roomWidthField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRoom != null && !newVal.isEmpty()) {
                try {
                    double width = Double.parseDouble(newVal);
                    if (width > 0) {
                        currentRoom.setWidth(width);
                        redraw();
                        
                        // Update 3D view if active
                        if (is3DView) {
                            build3DRoomScene();
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid input
                }
            }
        });
        
        roomLengthField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRoom != null && !newVal.isEmpty()) {
                try {
                    double length = Double.parseDouble(newVal);
                    if (length > 0) {
                        currentRoom.setLength(length);
                        redraw();
                        
                        // Update 3D view if active
                        if (is3DView) {
                            build3DRoomScene();
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid input
                }
            }
        });
        
        roomHeightField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRoom != null && !newVal.isEmpty()) {
                try {
                    double height = Double.parseDouble(newVal);
                    if (height > 0) {
                        currentRoom.setHeight(height);
                        redraw();
                        
                        // Update 3D view if active
                        if (is3DView) {
                            build3DRoomScene();
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid input
                }
            }
        });
    }

    @FXML
    private void handleAddFurniture() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        String type = furnitureTypeCombo.getValue();
        if (type == null) {
            showError("Please select a furniture type");
            return;
        }
        Furniture furniture = new Furniture(type);
        // Place in center of room area (in pixels)
        furniture.setX(50 + (designCanvas.getWidth() - 100) / 2 - 25);
        furniture.setY(50 + (designCanvas.getHeight() - 100) / 2 - 25);
        // Set color from color picker
        if (furnitureColorPicker != null && furnitureColorPicker.getValue() != null) {
            furniture.setColor(furnitureColorPicker.getValue().toString());
        }
        furnitureList.add(furniture);
        furnitureListView.getItems().add(furniture);
        // Select the newly added furniture
        furnitureListView.getSelectionModel().select(furniture);
        redraw();
        updateStatus("Added " + type + " to the room");
    }

    @FXML
    private void handleRemoveFurniture() {
        Furniture selected = furnitureListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            furnitureList.remove(selected);
            furnitureListView.getItems().remove(selected);
            redraw();
            updateStatus("Removed " + selected.getType() + " from the room");
        }
    }

    @FXML
    private void handleSaveDesign() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("My Design");
        dialog.setTitle("Save Design");
        dialog.setHeaderText("Enter a name for your design");
        dialog.setContentText("Design name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String designName = result.get();
            String filename = designName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            
            try {
                // Create a Design object
                DesignService.Design design = new DesignService.Design(designName, currentRoom, furnitureList);
                designService.saveDesign(design, filename);
                refreshSavedDesignsList();
                showSuccess("Design saved successfully!");
            } catch (IOException e) {
                showError("Failed to save design: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLoadSelectedDesign() {
        String filename = savedDesignsListView.getSelectionModel().getSelectedItem();
        if (filename == null) {
            showError("Please select a design to load");
            return;
        }
        
        try {
            DesignService.Design design = designService.loadDesign(filename);
            if (design != null) {
                currentRoom = design.getRoom();
                furnitureList = design.getFurniture();
                
                // Update UI fields
                roomWidthField.setText(String.valueOf(currentRoom.getWidth()));
                roomLengthField.setText(String.valueOf(currentRoom.getLength()));
                roomHeightField.setText(String.valueOf(currentRoom.getHeight()));
                
                try {
                    wallColorPicker.setValue(Color.web(currentRoom.getWallColor()));
                    floorColorPicker.setValue(Color.web(currentRoom.getFloorColor()));
                } catch (Exception ignored) {
                    // Default values if colors can't be parsed
                    wallColorPicker.setValue(Color.web("#F7F3F0"));
                    floorColorPicker.setValue(Color.web("#9D7B6D"));
                }
                
                furnitureListView.getItems().setAll(furnitureList);
                redraw();
                
                showSuccess("Design '" + filename + "' loaded successfully!");
                updateStatus("Loaded design: " + filename);
            }
        } catch (IOException e) {
            showError("Failed to load design: " + e.getMessage());
        }
    }

    @FXML
    private void handle2DView() {
        is3DView = false;
        designCanvas.setVisible(true);
        room3DSubScene.setVisible(false);
        toggle2DView.setSelected(true);
        toggle3DView.setSelected(false);
        redraw();
        updateStatus("Switched to 2D View with furniture labels");
    }

    @FXML
    private void handle3DView() {
        is3DView = true;
        designCanvas.setVisible(false);
        room3DSubScene.setVisible(true);
        toggle2DView.setSelected(false);
        toggle3DView.setSelected(true);
        build3DRoomScene();
        updateStatus("Switched to 3D View");
    }

    @FXML
    private void handleApplyShading() {
        if (is3DView) {
            // Apply advanced shading to 3D view
            build3DRoomScene();
            updateStatus("Applied shading to 3D view");
        }
    }

    @FXML
    private void handleResetView() {
        if (is3DView && camera3D != null) {
            // Reset camera position and rotation
            cameraAngleX = -20;
            cameraAngleY = -20;
            cameraPanX = 0;
            cameraPanY = 0;
            rotateX.setAngle(cameraAngleX);
            rotateY.setAngle(cameraAngleY);
            camera3D.setTranslateX(cameraPanX);
            camera3D.setTranslateY(cameraPanY);
            camera3D.setTranslateZ(-1000);
            updateStatus("View reset");
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleNewDesign() {
        // Clear the current room and furniture
        currentRoom = null;
        furnitureList.clear();
        furnitureListView.getItems().clear();
        roomWidthField.clear();
        roomLengthField.clear();
        roomHeightField.clear();
        wallColorPicker.setValue(Color.web("#F7F3F0"));
        floorColorPicker.setValue(Color.web("#9D7B6D"));
        redraw();
        
        // Refresh saved designs list
        refreshSavedDesignsList();
        
        updateStatus("Created new design");
    }

    @FXML
    private void handleAnalyzeDesign() {
        if (currentRoom == null || furnitureList.isEmpty()) {
            showError("Please create a room and add furniture before analyzing");
            return;
        }
        
        // Create a design object
        DesignService.Design design = new DesignService.Design("Current Design", currentRoom, furnitureList);
        
        // Analyze the design
        DesignAnalyzer analyzer = new DesignAnalyzer();
        DesignEvaluation evaluation = analyzer.evaluateDesign(design);
        
        // Create the evaluation dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Design Analysis");
        dialog.setHeaderText("Design Evaluation Report");
        dialog.setResizable(true);
        
        // Create layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        
        // Add overall score with progress bar
        Label overallScoreLabel = new Label("Overall Score:");
        ProgressBar overallProgress = createProgressBar(evaluation.getOverallScore());
        Label overallScoreValueLabel = new Label(evaluation.getOverallScore() + "/100");
        
        grid.add(overallScoreLabel, 0, 0);
        grid.add(overallProgress, 1, 0);
        grid.add(overallScoreValueLabel, 2, 0);
        
        // Add category scores
        addCategoryScore(grid, "Functionality", evaluation.getFunctionalityScore(), 1);
        addCategoryScore(grid, "Aesthetics", evaluation.getAestheticsScore(), 2);
        addCategoryScore(grid, "Ergonomics", evaluation.getErgonomicsScore(), 3);
        addCategoryScore(grid, "Lighting", evaluation.getLightingScore(), 4);
        addCategoryScore(grid, "Flow", evaluation.getFlowScore(), 5);
        
        // Add recommendations section
        Label recommendationsLabel = new Label("Recommendations:");
        recommendationsLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        grid.add(recommendationsLabel, 0, 6, 3, 1);
        
        VBox recommendationsBox = new VBox(5);
        List<DesignAnalyzer.Recommendation> recommendations = evaluation.getRecommendations();
        
        if (recommendations.isEmpty()) {
            recommendationsBox.getChildren().add(new Label("No recommendations - your design looks great!"));
        } else {
            for (DesignAnalyzer.Recommendation rec : recommendations) {
                HBox recommendationRow = new HBox(10);
                
                // Icon based on recommendation type
                Label icon = new Label("•");
                icon.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 18;");
                
                Label description = new Label(rec.getDescription());
                description.setWrapText(true);
                
                recommendationRow.getChildren().addAll(icon, description);
                recommendationsBox.getChildren().add(recommendationRow);
            }
        }
        
        grid.add(recommendationsBox, 0, 7, 3, 1);
        
        // Add cost estimate
        double cost = designService.calculateEstimatedCost(design);
        Label costLabel = new Label("Estimated Implementation Cost:");
        costLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        grid.add(costLabel, 0, 8, 2, 1);
        
        Label costValueLabel = new Label(String.format("$%.2f", cost));
        costValueLabel.setStyle("-fx-font-weight: bold;");
        grid.add(costValueLabel, 2, 8);
        
        // Set the dialog content
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons to the dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        
        // Show the dialog
        dialog.showAndWait();
    }
    
    private ProgressBar createProgressBar(int score) {
        ProgressBar progressBar = new ProgressBar(score / 100.0);
        progressBar.setMinWidth(200);
        progressBar.setPrefHeight(20);
        
        // Set color based on score
        if (score >= 80) {
            progressBar.setStyle("-fx-accent: #4CAF50;"); // Green for high scores
        } else if (score >= 60) {
            progressBar.setStyle("-fx-accent: #FFC107;"); // Yellow for medium scores
        } else {
            progressBar.setStyle("-fx-accent: #F44336;"); // Red for low scores
        }
        
        return progressBar;
    }
    
    private void addCategoryScore(GridPane grid, String category, int score, int row) {
        Label categoryLabel = new Label(category + ":");
        ProgressBar progress = createProgressBar(score);
        Label scoreValueLabel = new Label(score + "/100");
        
        grid.add(categoryLabel, 0, row);
        grid.add(progress, 1, row);
        grid.add(scoreValueLabel, 2, row);
    }

    private void redraw() {
        GraphicsContext gc = designCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, designCanvas.getWidth(), designCanvas.getHeight());
        
        if (is3DView) {
            draw3DView(gc);
        } else {
            draw2DView(gc);
        }
        
        // Draw measurement line if in measure mode
        if (measureMode) {
            if (measureStartX >= 0) {
                // Draw a more visible measurement line
                gc.setStroke(Color.RED);
                gc.setLineWidth(2.5);
                gc.strokeLine(measureStartX, measureStartY, measureEndX, measureEndY);
                
                // Add end points
                double dotRadius = 5.0;
                gc.setFill(Color.RED);
                gc.fillOval(measureStartX - dotRadius/2, measureStartY - dotRadius/2, dotRadius, dotRadius);
                gc.fillOval(measureEndX - dotRadius/2, measureEndY - dotRadius/2, dotRadius, dotRadius);
                
                // Calculate and display the distance
                double pixelDist = Math.sqrt(Math.pow(measureEndX - measureStartX, 2) + 
                                             Math.pow(measureEndY - measureStartY, 2));
                
                // Convert pixel distance to meters based on room scale
                double scale = 100.0; // Pixels per meter
                if (currentRoom != null) {
                    // Calculate scale based on room size
                    double roomWidthInPixels = designCanvas.getWidth() - 100;
                    scale = roomWidthInPixels / currentRoom.getWidth();
                }
                
                double meterDist = pixelDist / scale;
                
                // Draw a more visible measurement label
                double labelX = (measureStartX + measureEndX) / 2;
                double labelY = (measureStartY + measureEndY) / 2;
                
                // Background for text
                gc.setFill(Color.WHITE);
                gc.fillRoundRect(labelX - 40, labelY - 15, 80, 30, 10, 10);
                gc.setStroke(Color.RED);
                gc.strokeRoundRect(labelX - 40, labelY - 15, 80, 30, 10, 10);
                
                // Display value with better formatting
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                gc.fillText(String.format("%.2f m", meterDist), labelX - 25, labelY + 5);
            } else {
                // Draw a "measurement mode active" indicator
                gc.setFill(new Color(1, 0, 0, 0.2)); // Translucent red
                gc.fillRect(10, 10, 200, 30);
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                gc.fillText("MEASUREMENT MODE ACTIVE", 20, 30);
            }
        }
    }

    private void draw2DView(GraphicsContext gc) {
        // Fill background with white
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, designCanvas.getWidth(), designCanvas.getHeight());

        // Calculate room dimensions to match 3D view
        double scale = 100; // Same scale as 3D view
        double roomW = currentRoom.getWidth() * scale;
        double roomL = currentRoom.getLength() * scale;

        // Center the room in the canvas
        double startX = (designCanvas.getWidth() - roomW) / 2;
        double startY = (designCanvas.getHeight() - roomL) / 2;

        // Fill room with wall color if a room exists
        if (currentRoom != null) {
            Color wallColor = Color.WHITE;
            try {
                wallColor = Color.web(currentRoom.getWallColor());
            } catch (Exception ignored) {
            }
            gc.setFill(wallColor);
            gc.fillRect(startX, startY, roomW, roomL);
        }

        // Draw room outline
        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, roomW, roomL);

        // Draw furniture at their (x, y) with specific shapes
        for (Furniture furniture : furnitureList) {
            Color color = Color.GRAY;
            try {
                color = Color.web(furniture.getColor());
            } catch (Exception ignored) {
            }
            gc.setFill(color);
            String type = furniture.getType().toLowerCase();
            double x = furniture.getX();
            double y = furniture.getY();
            
            // Save the current state of the graphics context
            gc.save();
            
            // Apply rotation if needed
            double rotation = furniture.getRotation();
            if (rotation != 0) {
                // Translate to center of furniture for rotation
                double furnitureWidth = getFurnitureWidth(type);
                double furnitureHeight = getFurnitureHeight(type);
                gc.translate(x + furnitureWidth/2, y + furnitureHeight/2);
                gc.rotate(rotation);
                gc.translate(-(furnitureWidth/2), -(furnitureHeight/2));
                // Reset x and y to 0 since we've translated to center of furniture
                x = 0;
                y = 0;
            }
            
            // Draw the appropriate shape based on furniture type
            switch (type) {
                case "chair":
                    // Circle for chair
                    gc.fillOval(x, y, 30, 30);
                    gc.setStroke(Color.WHITE);
                    gc.strokeOval(x, y, 30, 30);
                    break;
                case "table":
                    // Standard rectangle 50x30
                    gc.fillRect(x, y, 50, 30);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(x, y, 50, 30);
                    break;
                case "sofa":
                    // Rounded rectangle 60x30 with rounded edges
                    gc.fillRoundRect(x, y, 60, 30, 15, 15);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRoundRect(x, y, 60, 30, 15, 15);
                    break;
                case "bed":
                    // Large rectangle 70x40
                    gc.fillRect(x, y, 70, 40);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(x, y, 70, 40);
                    break;
                case "cabinet":
                    // Tall rectangle 30x50
                    gc.fillRect(x, y, 30, 50);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(x, y, 30, 50);
                    break;
                case "bookshelf":
                    // Very narrow and tall rectangle (1:5 ratio)
                    gc.fillRect(x, y, 15, 75);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(x, y, 15, 75);
                    break;
                default:
                    // Default square 40x40
                    gc.fillRect(x, y, 40, 40);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(x, y, 40, 40);
            }
            
            // Add labels for furniture in 2D view
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            
            // Position labels based on furniture dimensions
            double labelX, labelY;
            switch (type) {
                case "chair":
                    labelX = x + 15 - (type.length() * 2.5); // Center in chair
                    labelY = y + 20;
                    break;
                case "table":
                    labelX = x + 25 - (type.length() * 2.5); // Center in table
                    labelY = y + 20;
                    break;
                case "sofa":
                    labelX = x + 30 - (type.length() * 2.5); // Center in sofa
                    labelY = y + 20;
                    break;
                case "bed":
                    labelX = x + 35 - (type.length() * 2.5); // Center in bed
                    labelY = y + 25;
                    break;
                case "cabinet":
                    labelX = x + 15 - (type.length() * 2.5); // Center in cabinet
                    labelY = y + 25;
                    break;
                case "bookshelf":
                    labelX = x + 7.5 - (type.length() * 2.5); // Center in bookshelf
                    labelY = y + 38;
                    break;
                default:
                    labelX = x + 20 - (type.length() * 2.5); // Center in default shape
                    labelY = y + 25;
            }
            
            gc.fillText(type.toUpperCase(), labelX, labelY);
            
            // Restore the graphics context to its original state
            gc.restore();
        }
    }
    
    // Helper method to get furniture width based on type
    private double getFurnitureWidth(String type) {
        switch (type.toLowerCase()) {
            case "chair": return 30;
            case "table": return 50;
            case "sofa": return 60;
            case "bed": return 70;
            case "cabinet": return 30;
            case "bookshelf": return 15;
            default: return 40;
        }
    }
    
    // Helper method to get furniture height based on type
    private double getFurnitureHeight(String type) {
        switch (type.toLowerCase()) {
            case "chair": return 30;
            case "table": return 30;
            case "sofa": return 30;
            case "bed": return 40;
            case "cabinet": return 50;
            case "bookshelf": return 75;
            default: return 40;
        }
    }

    private void draw3DView(GraphicsContext gc) {
        // Basic 3D representation using perspective projection
        // This is a simplified version - in a real application, you'd use JavaFX 3D or
        // JMonkeyEngine
        gc.setFill(wallColorPicker.getValue());
        gc.fillRect(50, 50, designCanvas.getWidth() - 100, designCanvas.getHeight() - 100);

        // Draw furniture in 3D
        for (Furniture furniture : furnitureList) {
            // Simplified 3D furniture representation
            gc.setFill(Color.GRAY);
            gc.fillRect(100, 100, 50, 50);
        }
    }

    private void build3DRoomScene() {
        if (currentRoom == null)
            return;
        double width = currentRoom.getWidth();
        double length = currentRoom.getLength();
        double height = currentRoom.getHeight();
        double scale = 100;
        double roomW = width * scale;
        double roomL = length * scale;
        double roomH = height * scale;

        Group root3D = new Group();

        // Floor with selected floor color
        Box floor = new Box(roomW, 5, roomL);
        PhongMaterial floorMat = new PhongMaterial();
        Color selectedFloorColor = floorColorPicker.getValue();
        floorMat.setDiffuseColor(selectedFloorColor);
        floor.setMaterial(floorMat);
        floor.setTranslateY(roomH / 2);
        root3D.getChildren().add(floor);

        // Furniture
        for (Furniture furniture : furnitureList) {
            String type = furniture.getType().toLowerCase();
            double fw = 40, fl = 40, fh = 40;

            // Create appropriate 3D shape based on furniture type
            javafx.scene.Node furnitureShape;
            Group furnitureGroup = new Group(); // Group to hold furniture and label
            
            switch (type) {
                case "chair":
                    // Create a group for the chair
                    Group chairGroup = new Group();

                    // Seat - a flat cylinder
                    Cylinder seat = new Cylinder(15, 5);
                    PhongMaterial seatMat = new PhongMaterial();
                    try {
                        seatMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        seatMat.setDiffuseColor(Color.BROWN);
                    }
                    seat.setMaterial(seatMat);
                    seat.setTranslateY(0); // Seat height from center

                    // Legs - 4 cylinders
                    int legHeight = 25;
                    double legOffset = 10;

                    Cylinder leg1 = new Cylinder(2, legHeight);
                    leg1.setMaterial(new PhongMaterial(Color.SADDLEBROWN));
                    leg1.setTranslateX(-legOffset);
                    leg1.setTranslateZ(-legOffset);
                    leg1.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg2 = new Cylinder(2, legHeight);
                    leg2.setMaterial(new PhongMaterial(Color.SADDLEBROWN));
                    leg2.setTranslateX(legOffset);
                    leg2.setTranslateZ(-legOffset);
                    leg2.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg3 = new Cylinder(2, legHeight);
                    leg3.setMaterial(new PhongMaterial(Color.SADDLEBROWN));
                    leg3.setTranslateX(-legOffset);
                    leg3.setTranslateZ(legOffset);
                    leg3.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg4 = new Cylinder(2, legHeight);
                    leg4.setMaterial(new PhongMaterial(Color.SADDLEBROWN));
                    leg4.setTranslateX(legOffset);
                    leg4.setTranslateZ(legOffset);
                    leg4.setTranslateY(legHeight / 2.0 + 2.5);

                    // Optional: simple backrest (Box or Cylinder)
                    Box backrest = new Box(30, 20, 2);
                    backrest.setMaterial(new PhongMaterial(Color.SADDLEBROWN));
                    backrest.setTranslateY(-10);
                    backrest.setTranslateZ(-13); // Move behind the seat

                    // Add parts to the group
                    chairGroup.getChildren().addAll(seat, leg1, leg2, leg3, leg4, backrest);

                    // Set shape and dimensions
                    furnitureShape = chairGroup;
                    fw = fl = 30;
                    fh = 35; // 30 (legs + seat) + small backrest
                    break;
                case "table":
                    // Create a group for the table
                    Group tableGroup = new Group();

                    // Table top with beveled edges
                    Box tableTop = new Box(50, 4, 30);
                    PhongMaterial tableMat = new PhongMaterial();
                    try {
                        tableMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        tableMat.setDiffuseColor(Color.GRAY);
                    }
                    tableTop.setMaterial(tableMat);
                    tableTop.setTranslateY(-10); // Raise it up to rest on legs

                    // Add beveled edges using thin boxes
                    double bevelSize = 1;
                    Box topBevel = new Box(52, bevelSize, 32);
                    Box bottomBevel = new Box(52, bevelSize, 32);
                    topBevel.setMaterial(tableMat);
                    bottomBevel.setMaterial(tableMat);
                    topBevel.setTranslateY(-12);
                    bottomBevel.setTranslateY(-8);

                    // Table legs with more detail
                    PhongMaterial tableLegMat = new PhongMaterial(Color.DARKGRAY);
                    tableLegMat.setSpecularColor(Color.TRANSPARENT); // Remove shadows
                    double tableLegHeight = 20;
                    double halfWidth = 23; // Slightly inset from edges
                    double halfLength = 13;

                    // Create decorative leg tops
                    double legTopSize = 4;
                    Box frontLeftLegTop = new Box(legTopSize, 2, legTopSize);
                    Box frontRightLegTop = new Box(legTopSize, 2, legTopSize);
                    Box backLeftLegTop = new Box(legTopSize, 2, legTopSize);
                    Box backRightLegTop = new Box(legTopSize, 2, legTopSize);

                    frontLeftLegTop.setMaterial(tableLegMat);
                    frontRightLegTop.setMaterial(tableLegMat);
                    backLeftLegTop.setMaterial(tableLegMat);
                    backRightLegTop.setMaterial(tableLegMat);

                    // Position leg tops
                    frontLeftLegTop.setTranslateX(-halfWidth);
                    frontLeftLegTop.setTranslateZ(-halfLength);
                    frontLeftLegTop.setTranslateY(-1);

                    frontRightLegTop.setTranslateX(halfWidth);
                    frontRightLegTop.setTranslateZ(-halfLength);
                    frontRightLegTop.setTranslateY(-1);

                    backLeftLegTop.setTranslateX(-halfWidth);
                    backLeftLegTop.setTranslateZ(halfLength);
                    backLeftLegTop.setTranslateY(-1);

                    backRightLegTop.setTranslateX(halfWidth);
                    backRightLegTop.setTranslateZ(halfLength);
                    backRightLegTop.setTranslateY(-1);

                    // Create legs with slightly tapered design
                    Cylinder frontLeftLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder frontRightLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder backLeftLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder backRightLeg = new Cylinder(1.5, tableLegHeight);

                    frontLeftLeg.setMaterial(tableLegMat);
                    frontRightLeg.setMaterial(tableLegMat);
                    backLeftLeg.setMaterial(tableLegMat);
                    backRightLeg.setMaterial(tableLegMat);

                    // Position legs
                    frontLeftLeg.setTranslateX(-halfWidth);
                    frontLeftLeg.setTranslateZ(-halfLength);
                    frontLeftLeg.setTranslateY(tableLegHeight / 2);

                    frontRightLeg.setTranslateX(halfWidth);
                    frontRightLeg.setTranslateZ(-halfLength);
                    frontRightLeg.setTranslateY(tableLegHeight / 2);

                    backLeftLeg.setTranslateX(-halfWidth);
                    backLeftLeg.setTranslateZ(halfLength);
                    backLeftLeg.setTranslateY(tableLegHeight / 2);

                    backRightLeg.setTranslateX(halfWidth);
                    backRightLeg.setTranslateZ(halfLength);
                    backRightLeg.setTranslateY(tableLegHeight / 2);

                    // Add all parts to the group
                    tableGroup.getChildren().addAll(
                            tableTop, topBevel, bottomBevel,
                            frontLeftLegTop, frontRightLegTop, backLeftLegTop, backRightLegTop,
                            frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);

                    // Set the table as the furniture shape
                    furnitureShape = tableGroup;
                    fw = 50;
                    fl = 30;
                    fh = 25;
                    break;
                case "sofa":
                    Group sofaGroup = new Group();

                    // Seat base
                    Box sofaBase = new Box(60, 15, 30);
                    sofaBase.setTranslateY(5);

                    // Backrest
                    Box sofaBackrest = new Box(60, 15, 5);
                    sofaBackrest.setTranslateY(-5);
                    sofaBackrest.setTranslateZ(-12.5);

                    // Armrests
                    Box leftArm = new Box(5, 15, 30);
                    leftArm.setTranslateX(-27.5);
                    leftArm.setTranslateY(5);

                    Box rightArm = new Box(5, 15, 30);
                    rightArm.setTranslateX(27.5);
                    rightArm.setTranslateY(5);

                    PhongMaterial sofaMat = new PhongMaterial();
                    try {
                        sofaMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        sofaMat.setDiffuseColor(Color.DARKSLATEGRAY);
                    }
                    sofaBase.setMaterial(sofaMat);
                    sofaBackrest.setMaterial(sofaMat);
                    leftArm.setMaterial(sofaMat);
                    rightArm.setMaterial(sofaMat);

                    sofaGroup.getChildren().addAll(sofaBase, sofaBackrest, leftArm, rightArm);
                    furnitureShape = sofaGroup;
                    fw = 60;
                    fl = 30;
                    fh = 25;
                    break;
                case "bed":
                    Group bedGroup = new Group();

                    // Bed base
                    Box bedBase = new Box(70, 10, 40);
                    bedBase.setTranslateY(5);

                    // Headboard
                    Box headboard = new Box(70, 15, 3);
                    headboard.setTranslateZ(-18.5);
                    headboard.setTranslateY(-2.5);

                    // Pillow area
                    Box pillow = new Box(60, 5, 10);
                    pillow.setTranslateZ(-10);
                    pillow.setTranslateY(-7.5);

                    PhongMaterial bedMat = new PhongMaterial();
                    try {
                        bedMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        bedMat.setDiffuseColor(Color.LIGHTGRAY);
                    }

                    bedBase.setMaterial(bedMat);
                    headboard.setMaterial(bedMat);

                    PhongMaterial pillowMat = new PhongMaterial(Color.WHITE);
                    pillow.setMaterial(pillowMat);

                    bedGroup.getChildren().addAll(bedBase, headboard, pillow);
                    furnitureShape = bedGroup;
                    fw = 70;
                    fl = 40;
                    fh = 20;
                    break;
                case "cabinet":
                    Group cabinetGroup = new Group();

                    // Main box
                    Box cabinetBody = new Box(30, 50, 20);

                    // Vertical divider line (visual)
                    Box divider = new Box(1, 48, 1);
                    divider.setTranslateZ(-9.5);
                    divider.setMaterial(new PhongMaterial(Color.BLACK));

                    PhongMaterial cabinetMat = new PhongMaterial();
                    try {
                        cabinetMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        cabinetMat.setDiffuseColor(Color.SADDLEBROWN);
                    }

                    cabinetBody.setMaterial(cabinetMat);
                    cabinetGroup.getChildren().addAll(cabinetBody, divider);
                    furnitureShape = cabinetGroup;
                    fw = 30;
                    fl = 20;
                    fh = 50;
                    break;
                case "bookshelf":
                    Group shelfGroup = new Group();

                    // Frame
                    Box frame = new Box(15, 60, 20);

                    // Add 3 horizontal shelves
                    for (int i = -1; i <= 1; i++) {
                        Box shelf = new Box(13, 1, 18);
                        shelf.setTranslateY(i * 15); // evenly spaced
                        shelfGroup.getChildren().add(shelf);
                    }

                    PhongMaterial shelfMat = new PhongMaterial();
                    try {
                        shelfMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        shelfMat.setDiffuseColor(Color.BURLYWOOD);
                    }

                    frame.setMaterial(shelfMat);
                    for (Node node : shelfGroup.getChildren()) {
                        if (node instanceof Box && node != frame) {
                            ((Box) node).setMaterial(shelfMat);
                        }
                    }

                    shelfGroup.getChildren().add(frame);
                    furnitureShape = shelfGroup;
                    fw = 15;
                    fl = 20;
                    fh = 60;
                    break;
                default:
                    // Default box
                    Box defaultBox = new Box(40, 40, 40);
                    PhongMaterial defaultMat = new PhongMaterial();
                    try {
                        defaultMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        defaultMat.setDiffuseColor(Color.GRAY);
                    }
                    defaultBox.setMaterial(defaultMat);
                    furnitureShape = defaultBox;
                    fw = fl = fh = 40;
            }

            // Calculate furniture position to match 2D view
            double canvasWidth = designCanvas.getWidth();
            double canvasHeight = designCanvas.getHeight();
            double startX = (canvasWidth - roomW) / 2;
            double startY = (canvasHeight - roomL) / 2;

            // Map 2D coordinates to 3D space
            double px = furniture.getX() - startX - (roomW / 2) + fw / 2;
            double pz = furniture.getY() - startY - (roomL / 2) + fl / 2;

            // Place furniture exactly on the floor surface
            furnitureShape.setTranslateX(px);
            furnitureShape.setTranslateY(roomH / 2 - fh / 2 + 2.5); // +2.5 to place on floor surface
            furnitureShape.setTranslateZ(pz);
            
            // IMPORTANT: Apply the furniture rotation
            Rotate rotateY = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureGroup.getTransforms().add(rotateY);
            
            // Add furniture to the furniture group
            furnitureGroup.getChildren().add(furnitureShape);
            
            // Add the furniture group to the scene
            root3D.getChildren().add(furnitureGroup);
        }

        // Camera setup with improved angles and position for better navigation
        camera3D = new PerspectiveCamera(true);
        camera3D.setTranslateZ(-roomL * 1.8); // Moved camera further back for better initial view
        camera3D.setTranslateY(-roomH / 3); // Adjusted height
        camera3D.setTranslateX(roomW / 4); // Moved slightly to the right
        camera3D.setNearClip(0.1);
        camera3D.setFarClip(10000.0);
        camera3D.setFieldOfView(40); // Slightly narrower field of view for more detail

        // Reset rotation and pan with better initial angles
        cameraAngleX = -25; // Less dramatic looking down
        cameraAngleY = -40; // Less dramatic side angle
        cameraPanX = 0;
        cameraPanY = 0;
        rotateX = new Rotate(cameraAngleX, Rotate.X_AXIS);
        rotateY = new Rotate(cameraAngleY, Rotate.Y_AXIS);
        camera3D.getTransforms().setAll(rotateY, rotateX);

        // Add multiple lights for better visibility
        PointLight light1 = new PointLight(Color.WHITE);
        light1.setTranslateX(0);
        light1.setTranslateY(-roomH / 2);
        light1.setTranslateZ(-roomL / 2);
        root3D.getChildren().add(light1);

        PointLight light2 = new PointLight(Color.WHITE);
        light2.setTranslateX(roomW / 4);
        light2.setTranslateY(-roomH / 3);
        light2.setTranslateZ(-roomL / 4);
        root3D.getChildren().add(light2);

        // Set up SubScene with better background
        room3DSubScene.setRoot(root3D);
        room3DSubScene.setCamera(camera3D);
        room3DSubScene.setFill(Color.rgb(240, 240, 240)); // Lighter background
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle loading a Living Room template
     */
    @FXML
    private void handleLivingRoomTemplate() {
        currentRoom = Room.createLivingRoom();
        updateRoomFields();
        redraw();
        
        // Apply the brown theme colors to the room
        currentRoom.setWallColor("#F7F3F0");
        currentRoom.setFloorColor("#9D7B6D");
        wallColorPicker.setValue(Color.web("#F7F3F0"));
        floorColorPicker.setValue(Color.web("#9D7B6D"));
        
        showSuccess("Living Room template applied");
    }
    
    /**
     * Handle loading a Bedroom template
     */
    @FXML
    private void handleBedroomTemplate() {
        currentRoom = Room.createBedroom();
        updateRoomFields();
        redraw();
        showSuccess("Bedroom template applied");
    }
    
    /**
     * Handle loading a Kitchen template
     */
    @FXML
    private void handleKitchenTemplate() {
        currentRoom = Room.createKitchen();
        updateRoomFields();
        redraw();
        showSuccess("Kitchen template applied");
    }
    
    /**
     * Handle loading an Office template
     */
    @FXML
    private void handleOfficeTemplate() {
        currentRoom = Room.createOffice();
        updateRoomFields();
        redraw();
        showSuccess("Office template applied");
    }
    
    /**
     * Handle loading a Dining Room template
     */
    @FXML
    private void handleDiningRoomTemplate() {
        currentRoom = Room.createDiningRoom();
        updateRoomFields();
        redraw();
        showSuccess("Dining Room template applied");
    }
    
    /**
     * Update UI fields based on current room
     */
    private void updateRoomFields() {
        roomWidthField.setText(String.valueOf(currentRoom.getWidth()));
        roomLengthField.setText(String.valueOf(currentRoom.getLength()));
        roomHeightField.setText(String.valueOf(currentRoom.getHeight()));
        
        try {
            wallColorPicker.setValue(Color.web(currentRoom.getWallColor()));
            floorColorPicker.setValue(Color.web(currentRoom.getFloorColor()));
        } catch (Exception ignored) {
            // Default values if colors can't be parsed
            wallColorPicker.setValue(Color.WHITE);
            floorColorPicker.setValue(Color.LIGHTGRAY);
        }
    }
    
    /**
     * Handle loading a Living Room furniture preset
     */
    @FXML
    private void handleLivingRoomPreset() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        
        DesignPreset preset = DesignPreset.createLivingRoomPreset();
        
        // Copy furniture to our list
        furnitureList.clear();
        for (Furniture furniture : preset.getFurnitureList()) {
            furnitureList.add(furniture);
        }
        
        // Update the list view
        furnitureListView.getItems().setAll(furnitureList);
        redraw();
        
        showSuccess("Living Room furniture preset applied");
    }
    
    /**
     * Handle loading a Bedroom furniture preset
     */
    @FXML
    private void handleBedroomPreset() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        
        DesignPreset preset = DesignPreset.createBedroomPreset();
        
        // Copy furniture to our list
        furnitureList.clear();
        for (Furniture furniture : preset.getFurnitureList()) {
            furnitureList.add(furniture);
        }
        
        // Update the list view
        furnitureListView.getItems().setAll(furnitureList);
        redraw();
        
        showSuccess("Bedroom furniture preset applied");
    }
    
    /**
     * Handle loading an Office furniture preset
     */
    @FXML
    private void handleOfficePreset() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        
        DesignPreset preset = DesignPreset.createOfficePreset();
        
        // Copy furniture to our list
        furnitureList.clear();
        for (Furniture furniture : preset.getFurnitureList()) {
            furnitureList.add(furniture);
        }
        
        // Update the list view
        furnitureListView.getItems().setAll(furnitureList);
        redraw();
        
        showSuccess("Office furniture preset applied");
    }
    
    /**
     * Handle loading a Dining Room furniture preset
     */
    @FXML
    private void handleDiningPreset() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        
        DesignPreset preset = DesignPreset.createDiningRoomPreset();
        
        // Copy furniture to our list
        furnitureList.clear();
        for (Furniture furniture : preset.getFurnitureList()) {
            furnitureList.add(furniture);
        }
        
        // Update the list view
        furnitureListView.getItems().setAll(furnitureList);
        redraw();
        
        showSuccess("Dining Room furniture preset applied");
    }
    
    /**
     * Handle loading a Kitchen furniture preset
     */
    @FXML
    private void handleKitchenPreset() {
        if (currentRoom == null) {
            showError("Please create a room first");
            return;
        }
        
        DesignPreset preset = DesignPreset.createKitchenPreset();
        
        // Copy furniture to our list
        furnitureList.clear();
        for (Furniture furniture : preset.getFurnitureList()) {
            furnitureList.add(furniture);
        }
        
        // Update the list view
        furnitureListView.getItems().setAll(furnitureList);
        redraw();
        
        showSuccess("Kitchen furniture preset applied");
    }
    
    /**
     * Export the current design to OBJ format
     */
    @FXML
    private void handleExportToOBJ() {
        if (currentRoom == null || furnitureList.isEmpty()) {
            showError("Please create a room and add furniture before exporting");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("MyDesign");
        dialog.setTitle("Export to OBJ");
        dialog.setHeaderText("Enter a name for the export file");
        dialog.setContentText("File name:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String filename = result.get();
            
            try {
                // Create a design object
                DesignService.Design design = new DesignService.Design(filename, currentRoom, furnitureList);
                
                // Export to OBJ
                designService.exportToOBJ(design, filename);
                
                showSuccess("Design exported to " + filename + ".obj");
            } catch (IOException e) {
                showError("Failed to export design: " + e.getMessage());
            }
        }
    }
    
    /**
     * Calculate and display the estimated cost of implementation
     */
    @FXML
    private void handleCalculateCost() {
        if (currentRoom == null || furnitureList.isEmpty()) {
            showError("Please create a room and add furniture before calculating cost");
            return;
        }
        
        // Create a design object
        DesignService.Design design = new DesignService.Design("Current Design", currentRoom, furnitureList);
        
        // Calculate cost
        double cost = designService.calculateEstimatedCost(design);
        
        // Display the cost
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cost Estimate");
        alert.setHeaderText("Implementation Cost Estimate");
        alert.setContentText(String.format("The estimated cost to implement this design is: $%.2f", cost));
        
        alert.showAndWait();
    }

    @FXML
    private void refreshSavedDesignsList() {
        List<String> savedDesigns = designService.getSavedDesigns();
        savedDesignsListView.getItems().clear();
        savedDesignsListView.getItems().addAll(savedDesigns);
        updateStatus("Found " + savedDesigns.size() + " saved designs");
    }

    @FXML
    private void handleAbout() {
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About Furniture Designer Pro");
        aboutDialog.setHeaderText(null); // Remove default header
        
        // Create premium styled content with app information
        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 30, 25, 30));
        content.setStyle("-fx-background-color: #F7F3F0;"); // Light cream background
        
        // App title with larger, more premium font
        Label titleLabel = new Label("Furniture Designer Pro");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #7D5A4F;");
        
        // Version with subtle styling
        Label versionLabel = new Label("Version 2.0");
        versionLabel.setStyle("-fx-font-weight: normal; -fx-font-style: italic; -fx-text-fill: #9D7B6D;");
        
        // Separator for visual division
        Separator titleSeparator = new Separator();
        titleSeparator.setStyle("-fx-background-color: #9D7B6D;");
        
        // Description with improved typography
        Label descriptionLabel = new Label(
            "Furniture Designer Pro is a professional tool for designing and " +
            "visualizing interior layouts. Create rooms, add furniture, and " +
            "view your designs in both 2D and 3D."
        );
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 13px; -fx-text-fill: #333333; -fx-line-spacing: 5px;");
        
        // Premium section headers
        Label featuresLabel = new Label("Key Features");
        featuresLabel.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7D5A4F; -fx-padding: 10 0 5 0;");
        
        // Features with custom bullet points and improved spacing
        VBox featuresList = new VBox(8);
        featuresList.setPadding(new Insets(0, 0, 0, 10));
        
        String[] features = {
            "2D and 3D visualization",
            "Multiple furniture types",
            "Room dimension customization",
            "Save and load designs",
            "Color customization",
            "Design analysis"
        };
        
        for (String feature : features) {
            HBox featureRow = new HBox(10);
            Label bulletPoint = new Label("•");
            bulletPoint.setStyle("-fx-text-fill: #9D7B6D; -fx-font-size: 16px;");
            
            Label featureText = new Label(feature);
            featureText.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 13px;");
            
            featureRow.getChildren().addAll(bulletPoint, featureText);
            featuresList.getChildren().add(featureRow);
        }
        
        // Separator before developers section
        Separator devSeparator = new Separator();
        devSeparator.setStyle("-fx-background-color: #9D7B6D;");
        
        // Developers section with premium styling
        Label developersLabel = new Label("Development Team");
        developersLabel.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7D5A4F; -fx-padding: 10 0 5 0;");
        
        VBox developersList = new VBox(8);
        developersList.setPadding(new Insets(0, 0, 0, 10));
        
        String[] developers = {
            "Murukkuwadura Mendis 10898558",
            "Munasiri Sandaneth 10898646",
            "Rajapaksha Rajapaksha 10899673",
            "Wickramaarachchige Sayuranga 10899688",
            "Weerappulli Dewmi 10899503"
        };
        
        for (String developer : developers) {
            HBox devRow = new HBox(10);
            Label bulletPoint = new Label("•");
            bulletPoint.setStyle("-fx-text-fill: #9D7B6D; -fx-font-size: 16px;");
            
            Label devText = new Label(developer);
            devText.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 13px;");
            
            devRow.getChildren().addAll(bulletPoint, devText);
            developersList.getChildren().add(devRow);
        }
        
        // Bottom separator
        Separator bottomSeparator = new Separator();
        bottomSeparator.setStyle("-fx-background-color: #9D7B6D;");
        
        // Copyright with premium styling
        Label copyrightLabel = new Label("© 2025 Furniture Designer Pro");
        copyrightLabel.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: #9D7B6D; -fx-padding: 5 0 0 0;");
        
        // Add all elements to content
        content.getChildren().addAll(
            titleLabel,
            versionLabel,
            titleSeparator,
            descriptionLabel,
            featuresLabel,
            featuresList,
            devSeparator,
            developersLabel,
            developersList,
            bottomSeparator,
            copyrightLabel
        );
        
        // Set dialog content with premium size and styling
        aboutDialog.getDialogPane().setContent(content);
        aboutDialog.getDialogPane().setPrefWidth(450);
        aboutDialog.getDialogPane().setStyle("-fx-background-color: #F7F3F0;");
        
        // Add buttons
        aboutDialog.getButtonTypes().setAll(ButtonType.OK);
        
        // Show dialog
        aboutDialog.showAndWait();
    }
    
    @FXML
    private void handleUserGuide() {
        Alert userGuideDialog = new Alert(Alert.AlertType.INFORMATION);
        userGuideDialog.setTitle("User Guide");
        userGuideDialog.setHeaderText("Furniture Designer Pro - User Guide");
        
        // Create scrollable content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(10, 20, 10, 20));
        content.setPrefWidth(450);
        
        // Getting Started section
        Label gettingStartedLabel = new Label("Getting Started");
        gettingStartedLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label gettingStartedContent = new Label(
            "1. Create a room using the dimensions in the sidebar\n" +
            "2. Choose wall and floor colors\n" +
            "3. Add furniture using the dropdown and color picker\n" +
            "4. Save your design when finished"
        );
        gettingStartedContent.setWrapText(true);
        
        // Room Dimensions section
        Label dimensionsLabel = new Label("Room Dimensions");
        dimensionsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label dimensionsContent = new Label(
            "Enter room dimensions in meters. You can update dimensions at any time " +
            "by changing the values and clicking CREATE/UPDATE ROOM."
        );
        dimensionsContent.setWrapText(true);
        
        // Furniture Management section
        Label furnitureLabel = new Label("Furniture Management");
        furnitureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label furnitureContent = new Label(
            "• Add furniture: Select a type, choose a color, and click ADD TO ROOM\n" +
            "• Move furniture: Click and drag items in the 2D view\n" +
            "• Edit color: Select furniture from the list and use the color picker\n" +
            "• Remove furniture: Select from the list and click REMOVE SELECTED"
        );
        furnitureContent.setWrapText(true);
        
        // Views section
        Label viewsLabel = new Label("2D and 3D Views");
        viewsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label viewsContent = new Label(
            "Toggle between 2D and 3D views using the buttons in the top right.\n\n" +
            "In 3D view:\n" +
            "• Use the zoom buttons to move closer or further\n" +
            "• Use rotation buttons to change viewing angle\n" +
            "• Click RESET VIEW to return to default position\n" +
            "• You can also use the mouse: click and drag to rotate, scroll to zoom"
        );
        viewsContent.setWrapText(true);
        
        // Saving and Loading section
        Label savingLabel = new Label("Saving and Loading Designs");
        savingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label savingContent = new Label(
            "• Save design: File > Save Design\n" +
            "• View saved designs: In the Saved Designs panel\n" +
            "• Load a design: Select from the list and click LOAD SELECTED\n" +
            "• Refresh designs list: File > Refresh Designs"
        );
        savingContent.setWrapText(true);
        
        // Add all sections to content
        content.getChildren().addAll(
            gettingStartedLabel, gettingStartedContent,
            dimensionsLabel, dimensionsContent,
            furnitureLabel, furnitureContent,
            viewsLabel, viewsContent,
            savingLabel, savingContent
        );
        
        scrollPane.setContent(content);
        scrollPane.setPrefHeight(400);
        
        // Set dialog content
        userGuideDialog.getDialogPane().setContent(scrollPane);
        userGuideDialog.getDialogPane().setPrefWidth(500);
        
        // Add buttons
        userGuideDialog.getButtonTypes().setAll(ButtonType.OK);
        
        // Show dialog
        userGuideDialog.showAndWait();
    }

    // New method for 360 degree rotation animation
    private void startRotate360Animation() {
        // Stop any existing animation
        if (rotate360Timeline != null) {
            rotate360Timeline.stop();
        }
        
        // Create a new timeline for smooth 360 rotation
        rotate360Timeline = new javafx.animation.Timeline();
        rotate360Timeline.setCycleCount(1); // Run once
        
        // Create 360 keyframes (one degree at a time)
        javafx.animation.KeyValue startValue = new javafx.animation.KeyValue(
                rotateY.angleProperty(), cameraAngleY);
        javafx.animation.KeyValue endValue = new javafx.animation.KeyValue(
                rotateY.angleProperty(), cameraAngleY + 360);
        
        javafx.animation.KeyFrame startFrame = new javafx.animation.KeyFrame(
                javafx.util.Duration.ZERO, startValue);
        javafx.animation.KeyFrame endFrame = new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(10), endValue); // 10 seconds for a full rotation
        
        rotate360Timeline.getKeyFrames().addAll(startFrame, endFrame);
        
        // Update angle when done
        rotate360Timeline.setOnFinished(event -> {
            cameraAngleY = (cameraAngleY + 360) % 360; // Keep angle within 0-360 range
        });
        
        // Start the animation
        rotate360Timeline.play();
    }

    // Add method to remove shadows
    @FXML
    private void handleRemoveShadows() {
        if (is3DView) {
            // Rebuild 3D scene without shadows
            build3DRoomSceneWithoutShadows();
            updateStatus("Removed shadows from furniture items");
        }
    }

    private void build3DRoomSceneWithoutShadows() {
        if (currentRoom == null)
            return;
        double width = currentRoom.getWidth();
        double length = currentRoom.getLength();
        double height = currentRoom.getHeight();
        double scale = 100;
        double roomW = width * scale;
        double roomL = length * scale;
        double roomH = height * scale;

        Group root3D = new Group();

        // Floor with selected floor color (no specular highlights)
        Box floor = new Box(roomW, 5, roomL);
        PhongMaterial floorMat = new PhongMaterial();
        Color selectedFloorColor = floorColorPicker.getValue();
        floorMat.setDiffuseColor(selectedFloorColor);
        floorMat.setSpecularColor(Color.TRANSPARENT); // Remove specular highlights (shiny reflections)
        floor.setMaterial(floorMat);
        floor.setTranslateY(roomH / 2);
        root3D.getChildren().add(floor);

        // Furniture - same logic as build3DRoomScene but with shadow removal
        for (Furniture furniture : furnitureList) {
            String type = furniture.getType().toLowerCase();
            double fw = 40, fl = 40, fh = 40;

            // Create appropriate 3D shape based on furniture type
            javafx.scene.Node furnitureShape;
            Group furnitureGroup = new Group(); // Group to hold furniture and label
            
            switch (type) {
                case "chair":
                    // Create a group for the chair
                    Group chairGroup = new Group();

                    // Seat - a flat cylinder with no shadows
                    Cylinder seat = new Cylinder(15, 5);
                    PhongMaterial seatMat = new PhongMaterial();
                    try {
                        seatMat.setDiffuseColor(Color.web(furniture.getColor()));
                        seatMat.setSpecularColor(Color.TRANSPARENT); // Remove specular highlights
                    } catch (Exception e) {
                        seatMat.setDiffuseColor(Color.BROWN);
                        seatMat.setSpecularColor(Color.TRANSPARENT);
                    }
                    seat.setMaterial(seatMat);
                    seat.setTranslateY(0);

                    // Legs - 4 cylinders with no shadows
                    int legHeight = 25;
                    double legOffset = 10;

                    // Create a shadow-free material for legs
                    PhongMaterial legMat = new PhongMaterial(Color.SADDLEBROWN);
                    legMat.setSpecularColor(Color.TRANSPARENT);

                    Cylinder leg1 = new Cylinder(2, legHeight);
                    leg1.setMaterial(legMat);
                    leg1.setTranslateX(-legOffset);
                    leg1.setTranslateZ(-legOffset);
                    leg1.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg2 = new Cylinder(2, legHeight);
                    leg2.setMaterial(legMat);
                    leg2.setTranslateX(legOffset);
                    leg2.setTranslateZ(-legOffset);
                    leg2.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg3 = new Cylinder(2, legHeight);
                    leg3.setMaterial(legMat);
                    leg3.setTranslateX(-legOffset);
                    leg3.setTranslateZ(legOffset);
                    leg3.setTranslateY(legHeight / 2.0 + 2.5);

                    Cylinder leg4 = new Cylinder(2, legHeight);
                    leg4.setMaterial(legMat);
                    leg4.setTranslateX(legOffset);
                    leg4.setTranslateZ(legOffset);
                    leg4.setTranslateY(legHeight / 2.0 + 2.5);

                    // Optional: simple backrest (Box or Cylinder)
                    Box backrest = new Box(30, 20, 2);
                    backrest.setMaterial(legMat);
                    backrest.setTranslateY(-10);
                    backrest.setTranslateZ(-13); // Move behind the seat

                    // Add parts to the group
                    chairGroup.getChildren().addAll(seat, leg1, leg2, leg3, leg4, backrest);

                    // Set shape and dimensions
                    furnitureShape = chairGroup;
                    fw = fl = 30;
                    fh = 35; // 30 (legs + seat) + small backrest
                    break;
                case "table":
                    // Create a group for the table
                    Group tableGroup = new Group();

                    // Table top with beveled edges
                    Box tableTop = new Box(50, 4, 30);
                    PhongMaterial tableMat = new PhongMaterial();
                    try {
                        tableMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        tableMat.setDiffuseColor(Color.GRAY);
                    }
                    tableTop.setMaterial(tableMat);
                    tableTop.setTranslateY(-10); // Raise it up to rest on legs

                    // Add beveled edges using thin boxes
                    double bevelSize = 1;
                    Box topBevel = new Box(52, bevelSize, 32);
                    Box bottomBevel = new Box(52, bevelSize, 32);
                    topBevel.setMaterial(tableMat);
                    bottomBevel.setMaterial(tableMat);
                    topBevel.setTranslateY(-12);
                    bottomBevel.setTranslateY(-8);

                    // Table legs with more detail
                    PhongMaterial tableLegMat = new PhongMaterial(Color.DARKGRAY);
                    tableLegMat.setSpecularColor(Color.TRANSPARENT); // Remove shadows
                    double tableLegHeight = 20;
                    double halfWidth = 23; // Slightly inset from edges
                    double halfLength = 13;

                    // Create decorative leg tops
                    double legTopSize = 4;
                    Box frontLeftLegTop = new Box(legTopSize, 2, legTopSize);
                    Box frontRightLegTop = new Box(legTopSize, 2, legTopSize);
                    Box backLeftLegTop = new Box(legTopSize, 2, legTopSize);
                    Box backRightLegTop = new Box(legTopSize, 2, legTopSize);

                    frontLeftLegTop.setMaterial(tableLegMat);
                    frontRightLegTop.setMaterial(tableLegMat);
                    backLeftLegTop.setMaterial(tableLegMat);
                    backRightLegTop.setMaterial(tableLegMat);

                    // Position leg tops
                    frontLeftLegTop.setTranslateX(-halfWidth);
                    frontLeftLegTop.setTranslateZ(-halfLength);
                    frontLeftLegTop.setTranslateY(-1);

                    frontRightLegTop.setTranslateX(halfWidth);
                    frontRightLegTop.setTranslateZ(-halfLength);
                    frontRightLegTop.setTranslateY(-1);

                    backLeftLegTop.setTranslateX(-halfWidth);
                    backLeftLegTop.setTranslateZ(halfLength);
                    backLeftLegTop.setTranslateY(-1);

                    backRightLegTop.setTranslateX(halfWidth);
                    backRightLegTop.setTranslateZ(halfLength);
                    backRightLegTop.setTranslateY(-1);

                    // Create legs with slightly tapered design
                    Cylinder frontLeftLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder frontRightLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder backLeftLeg = new Cylinder(1.5, tableLegHeight);
                    Cylinder backRightLeg = new Cylinder(1.5, tableLegHeight);

                    frontLeftLeg.setMaterial(tableLegMat);
                    frontRightLeg.setMaterial(tableLegMat);
                    backLeftLeg.setMaterial(tableLegMat);
                    backRightLeg.setMaterial(tableLegMat);

                    // Position legs
                    frontLeftLeg.setTranslateX(-halfWidth);
                    frontLeftLeg.setTranslateZ(-halfLength);
                    frontLeftLeg.setTranslateY(tableLegHeight / 2);

                    frontRightLeg.setTranslateX(halfWidth);
                    frontRightLeg.setTranslateZ(-halfLength);
                    frontRightLeg.setTranslateY(tableLegHeight / 2);

                    backLeftLeg.setTranslateX(-halfWidth);
                    backLeftLeg.setTranslateZ(halfLength);
                    backLeftLeg.setTranslateY(tableLegHeight / 2);

                    backRightLeg.setTranslateX(halfWidth);
                    backRightLeg.setTranslateZ(halfLength);
                    backRightLeg.setTranslateY(tableLegHeight / 2);

                    // Add all parts to the group
                    tableGroup.getChildren().addAll(
                            tableTop, topBevel, bottomBevel,
                            frontLeftLegTop, frontRightLegTop, backLeftLegTop, backRightLegTop,
                            frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);

                    // Set the table as the furniture shape
                    furnitureShape = tableGroup;
                    fw = 50;
                    fl = 30;
                    fh = 25;
                    break;
                case "sofa":
                    Group sofaGroup = new Group();

                    // Seat base
                    Box sofaBase = new Box(60, 15, 30);
                    sofaBase.setTranslateY(5);

                    // Backrest
                    Box sofaBackrest = new Box(60, 15, 5);
                    sofaBackrest.setTranslateY(-5);
                    sofaBackrest.setTranslateZ(-12.5);

                    // Armrests
                    Box leftArm = new Box(5, 15, 30);
                    leftArm.setTranslateX(-27.5);
                    leftArm.setTranslateY(5);

                    Box rightArm = new Box(5, 15, 30);
                    rightArm.setTranslateX(27.5);
                    rightArm.setTranslateY(5);

                    PhongMaterial sofaMat = new PhongMaterial();
                    try {
                        sofaMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        sofaMat.setDiffuseColor(Color.DARKSLATEGRAY);
                    }
                    sofaBase.setMaterial(sofaMat);
                    sofaBackrest.setMaterial(sofaMat);
                    leftArm.setMaterial(sofaMat);
                    rightArm.setMaterial(sofaMat);

                    sofaGroup.getChildren().addAll(sofaBase, sofaBackrest, leftArm, rightArm);
                    furnitureShape = sofaGroup;
                    fw = 60;
                    fl = 30;
                    fh = 25;
                    break;
                case "bed":
                    Group bedGroup = new Group();

                    // Bed base
                    Box bedBase = new Box(70, 10, 40);
                    bedBase.setTranslateY(5);

                    // Headboard
                    Box headboard = new Box(70, 15, 3);
                    headboard.setTranslateZ(-18.5);
                    headboard.setTranslateY(-2.5);

                    // Pillow area
                    Box pillow = new Box(60, 5, 10);
                    pillow.setTranslateZ(-10);
                    pillow.setTranslateY(-7.5);

                    PhongMaterial bedMat = new PhongMaterial();
                    try {
                        bedMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        bedMat.setDiffuseColor(Color.LIGHTGRAY);
                    }

                    bedBase.setMaterial(bedMat);
                    headboard.setMaterial(bedMat);

                    PhongMaterial pillowMat = new PhongMaterial(Color.WHITE);
                    pillow.setMaterial(pillowMat);

                    bedGroup.getChildren().addAll(bedBase, headboard, pillow);
                    furnitureShape = bedGroup;
                    fw = 70;
                    fl = 40;
                    fh = 20;
                    break;
                case "cabinet":
                    Group cabinetGroup = new Group();

                    // Main box
                    Box cabinetBody = new Box(30, 50, 20);

                    // Vertical divider line (visual)
                    Box divider = new Box(1, 48, 1);
                    divider.setTranslateZ(-9.5);
                    divider.setMaterial(new PhongMaterial(Color.BLACK));

                    PhongMaterial cabinetMat = new PhongMaterial();
                    try {
                        cabinetMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        cabinetMat.setDiffuseColor(Color.SADDLEBROWN);
                    }

                    cabinetBody.setMaterial(cabinetMat);
                    cabinetGroup.getChildren().addAll(cabinetBody, divider);
                    furnitureShape = cabinetGroup;
                    fw = 30;
                    fl = 20;
                    fh = 50;
                    break;
                case "bookshelf":
                    Group shelfGroup = new Group();

                    // Frame
                    Box frame = new Box(15, 60, 20);

                    // Add 3 horizontal shelves
                    for (int i = -1; i <= 1; i++) {
                        Box shelf = new Box(13, 1, 18);
                        shelf.setTranslateY(i * 15); // evenly spaced
                        shelfGroup.getChildren().add(shelf);
                    }

                    PhongMaterial shelfMat = new PhongMaterial();
                    try {
                        shelfMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        shelfMat.setDiffuseColor(Color.BURLYWOOD);
                    }

                    frame.setMaterial(shelfMat);
                    for (Node node : shelfGroup.getChildren()) {
                        if (node instanceof Box && node != frame) {
                            ((Box) node).setMaterial(shelfMat);
                        }
                    }

                    shelfGroup.getChildren().add(frame);
                    furnitureShape = shelfGroup;
                    fw = 15;
                    fl = 20;
                    fh = 60;
                    break;
                default:
                    // Default box with no shadows
                    Box defaultBox = new Box(40, 40, 40);
                    PhongMaterial defaultMat = new PhongMaterial();
                    try {
                        defaultMat.setDiffuseColor(Color.web(furniture.getColor()));
                    } catch (Exception e) {
                        defaultMat.setDiffuseColor(Color.GRAY);
                    }
                    defaultMat.setSpecularColor(Color.TRANSPARENT); // Remove specular highlights
                    defaultBox.setMaterial(defaultMat);
                    furnitureShape = defaultBox;
                    fw = fl = fh = 40;
            }

            // Calculate furniture position to match 2D view
            double canvasWidth = designCanvas.getWidth();
            double canvasHeight = designCanvas.getHeight();
            double startX = (canvasWidth - roomW) / 2;
            double startY = (canvasHeight - roomL) / 2;

            double px = furniture.getX() - startX - (roomW / 2) + fw / 2;
            double pz = furniture.getY() - startY - (roomL / 2) + fl / 2;

            furnitureShape.setTranslateX(px);
            furnitureShape.setTranslateY(roomH / 2 - fh / 2 + 2.5);
            furnitureShape.setTranslateZ(pz);
            
            // IMPORTANT: Apply the furniture rotation here too
            Rotate rotateY = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureGroup.getTransforms().add(rotateY);
            
            // Add furniture to the furniture group
            furnitureGroup.getChildren().add(furnitureShape);
            
            // Add the furniture group to the scene
            root3D.getChildren().add(furnitureGroup);
        }

        // Camera setup - similar to original method
        camera3D = new PerspectiveCamera(true);
        camera3D.setTranslateZ(-roomL * 1.8);
        camera3D.setTranslateY(-roomH / 3);
        camera3D.setTranslateX(roomW / 4);
        camera3D.setNearClip(0.1);
        camera3D.setFarClip(10000.0);
        camera3D.setFieldOfView(40);

        cameraAngleX = -25;
        cameraAngleY = -40;
        cameraPanX = 0;
        cameraPanY = 0;
        rotateX = new Rotate(cameraAngleX, Rotate.X_AXIS);
        rotateY = new Rotate(cameraAngleY, Rotate.Y_AXIS);
        camera3D.getTransforms().setAll(rotateY, rotateX);

        // Room lighting without shadows
        PointLight light1 = new PointLight(Color.WHITE);
        light1.setTranslateX(0);
        light1.setTranslateY(-roomH / 2);
        light1.setTranslateZ(-roomL / 2);
        root3D.getChildren().add(light1);

        PointLight light2 = new PointLight(Color.WHITE);
        light2.setTranslateX(roomW / 4);
        light2.setTranslateY(-roomH / 3);
        light2.setTranslateZ(-roomL / 4);
        root3D.getChildren().add(light2);

        // Set up SubScene
        room3DSubScene.setRoot(root3D);
        room3DSubScene.setCamera(camera3D);
        room3DSubScene.setFill(Color.rgb(240, 240, 240));
    }

    /**
     * Rotates the selected furniture item by 45 degrees
     */
    @FXML
    private void handleRotateFurniture() {
        Furniture selectedFurniture = furnitureListView.getSelectionModel().getSelectedItem();
        if (selectedFurniture != null) {
            try {
                // Rotate by 45 degrees each click
                double currentRotation = selectedFurniture.getRotation();
                currentRotation = (currentRotation + 45) % 360;
                selectedFurniture.setRotation(currentRotation);
                
                // Update 3D rotation values as well
                selectedFurniture.setRotationY(currentRotation);
                
                // Always redraw regardless of current view
                redraw();
                
                // Also rebuild 3D scene if in 3D view
                if (is3DView) {
                    build3DRoomScene();
                }
                
                updateStatus("Rotated " + selectedFurniture.getType() + " to " + currentRotation + "°");
            } catch (Exception e) {
                System.err.println("Error rotating furniture: " + e.getMessage());
                e.printStackTrace();
                showError("Error rotating furniture");
            }
        } else {
            showError("Select a furniture item first");
        }
    }

    /**
     * Handles toggling the measurement mode
     */
    @FXML
    private void handleMeasureMode() {
        // Toggle the measurement mode state
        measureMode = !measureMode;
        
        // Update the button state if it exists
        if (measureModeBtn != null) {
            measureModeBtn.setSelected(measureMode);
        }
        
        if (measureMode) {
            updateStatus("Measurement mode active - click and drag to measure distances");
            // Reset measurement points
            measureStartX = -1;
            measureStartY = -1;
            measureEndX = -1;
            measureEndY = -1;
        } else {
            updateStatus("Measurement mode disabled");
            redraw(); // Clear any measurement lines
        }
    }

    // Modify the mouse event handlers to support measurement mode and rotation properly
    private void setupCanvasEventHandlers() {
        designCanvas.setOnMousePressed(e -> {
            if (measureMode) {
                measureStartX = e.getX();
                measureStartY = e.getY();
                measureEndX = measureStartX;
                measureEndY = measureStartY;
                redraw();
                return;
            }
            
            // Existing furniture selection code
            double mouseX = e.getX();
            double mouseY = e.getY();
            selectedFurniture = null;
            
            // Enhanced furniture selection logic to account for rotation
            for (Furniture furniture : furnitureList) {
                double fx = furniture.getX();
                double fy = furniture.getY();
                String type = furniture.getType().toLowerCase();
                double fw = getFurnitureWidth(type);
                double fh = getFurnitureHeight(type);
                
                boolean isSelected = false;
                
                // For simplicity with rotation, we'll use a simple bounding box check
                // A more accurate check would involve rotation matrix math
                if (mouseX >= fx && mouseX <= fx + fw && mouseY >= fy && mouseY <= fy + fh) {
                    selectedFurniture = furniture;
                    dragOffsetX = mouseX - fx;
                    dragOffsetY = mouseY - fy;
                    break;
                }
            }
        });
        
        designCanvas.setOnMouseDragged(e -> {
            if (measureMode) {
                measureEndX = e.getX();
                measureEndY = e.getY();
                redraw();
                return;
            }
            
            // Existing furniture dragging code
            if (selectedFurniture != null) {
                double mouseX = e.getX();
                double mouseY = e.getY();
                // Room bounds in pixels
                double minX = 50, minY = 50;
                double maxX = designCanvas.getWidth() - 100;
                double maxY = designCanvas.getHeight() - 100;
                
                // Apply drag, considering furniture dimensions
                String type = selectedFurniture.getType().toLowerCase();
                double fw = getFurnitureWidth(type);
                double fh = getFurnitureHeight(type);
                
                double newX = mouseX - dragOffsetX;
                double newY = mouseY - dragOffsetY;
                
                // Clamp to room boundaries with padding
                newX = Math.max(minX, Math.min(newX, minX + maxX - fw));
                newY = Math.max(minY, Math.min(newY, minY + maxY - fh));
                
                selectedFurniture.setX(newX);
                selectedFurniture.setY(newY);
                redraw();
            }
        });
        
        designCanvas.setOnMouseReleased(e -> {
            if (!measureMode) {
                selectedFurniture = null;
            }
        });
    }

    /**
     * Handles aligning furniture to the left side of the room
     */
    @FXML
    private void handleAlignLeft() {
        alignSelectedFurniture("left");
    }
    
    /**
     * Handles aligning furniture to the center of the room
     */
    @FXML
    private void handleAlignCenter() {
        alignSelectedFurniture("center");
    }
    
    /**
     * Handles aligning furniture to the right side of the room
     */
    @FXML
    private void handleAlignRight() {
        alignSelectedFurniture("right");
    }
    
    /**
     * Aligns the selected furniture according to the specified alignment
     */
    private void alignSelectedFurniture(String alignment) {
        Furniture selectedFurniture = furnitureListView.getSelectionModel().getSelectedItem();
        if (selectedFurniture == null) {
            showError("Select a furniture item first");
            return;
        }
        
        if (currentRoom == null) {
            showError("Create a room first");
            return;
        }
        
        // Calculate room boundaries in pixels
        double roomLeft = 50;
        double roomWidth = designCanvas.getWidth() - 100;
        double roomRight = roomLeft + roomWidth;
        double furnitureWidth = 50; // Assuming standard furniture width
        
        // Apply alignment
        if (alignment.equals("left")) {
            selectedFurniture.setX(roomLeft + 10); // Add small margin
            updateStatus("Aligned furniture to left");
        } else if (alignment.equals("center")) {
            selectedFurniture.setX(roomLeft + (roomWidth / 2) - (furnitureWidth / 2));
            updateStatus("Aligned furniture to center");
        } else if (alignment.equals("right")) {
            selectedFurniture.setX(roomRight - furnitureWidth - 10); // Subtract width and margin
            updateStatus("Aligned furniture to right");
        }
        
        redraw();
        if (is3DView) {
            build3DRoomScene();
        }
    }

    /**
     * Saves a snapshot of the current room design
     */
    @FXML
    private void handleSaveSnapshot() {
        if (currentRoom == null) {
            showError("Create a room first");
            return;
        }
        
        try {
            // Capture the current view
            WritableImage snapshot = designCanvas.snapshot(new SnapshotParameters(), null);
            
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Room Snapshot");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
            );
            
            // Show save dialog
            File file = fileChooser.showSaveDialog(designCanvas.getScene().getWindow());
            if (file != null) {
                // Use a direct approach to save image
                // We can show a success message even if we don't actually save the file
                // in this demo implementation
                
                // In a full implementation, you would need to include the proper JavaFX 
                // extensions to save the image or use another approach
                
                updateStatus("Snapshot would be saved as " + file.getName() + " (demo)");
            }
        } catch (Exception e) {
            showError("Failed to save snapshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

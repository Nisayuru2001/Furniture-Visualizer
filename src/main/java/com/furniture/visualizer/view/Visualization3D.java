package com.furniture.visualizer.view;

import com.furniture.visualizer.model.Furniture;
import javafx.application.Platform;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visualization3D extends Pane {
    // 3D scene components
    private SubScene subScene;
    private Group root;
    private Box floor;
    private Box wall1, wall2, wall3, wall4;
    private Group roomGroup;
    private PerspectiveCamera camera;
    
    // Room properties
    private double roomWidth = 0;
    private double roomLength = 0;
    private double roomHeight = 0;
    private Color roomColor = Color.WHITE;
    
    // Mouse interaction properties
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    
    // Furniture
    private List<Furniture> furnitureItems = new ArrayList<>();
    private Map<Integer, Furniture3DModel> furnitureModels = new HashMap<>();
    private Furniture selectedFurniture = null;
    private Furniture3DModel selectedModel = null;
    
    // Camera properties
    private double cameraDistance = -2000;
    private double cameraHeight = -1000;
    private double cameraAngle = 30;
    
    // Movement flags for keyboard navigation
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveUp = false;
    private boolean moveDown = false;
    
    // UI elements
    private Rectangle selectionRectangle;
    private Text helpText;
    private Text statsText;
    
    // Fallback 2D visualization
    private VBox fallbackContainer;
    private boolean useFallback = false;
    
    // Add these fields to the class
    private Label fallbackRoomDimensionsLabel;
    private StackPane fallbackRoomRepresentation;
    private VBox fallbackFurnitureList;
    
    public Visualization3D() {
        try {
            // Initialize 3D scene components
            root = new Group();
            camera = new PerspectiveCamera(true);
            camera.setNearClip(0.1);
            camera.setFarClip(10000.0);
            
            // Create subscene with antialiasing
            subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
            subScene.setCamera(camera);
            
            // Set up lighting
            setupLighting();
            
            // Initialize room properties
            roomWidth = 5.0;
            roomLength = 5.0;
            roomHeight = 3.0;
            roomColor = Color.LIGHTBLUE;
            
            // Initialize camera properties
            cameraDistance = -1000;
            cameraHeight = -500;
            camera.setTranslateZ(cameraDistance);
            camera.setTranslateY(cameraHeight);
            
            // Initialize mouse interaction properties
            mousePosX = 0;
            mousePosY = 0;
            mouseOldX = 0;
            mouseOldY = 0;
            
            // Initialize movement flags
            moveForward = false;
            moveBackward = false;
            moveLeft = false;
            moveRight = false;
            moveUp = false;
            moveDown = false;
            
            // Create UI elements
            selectionRectangle = new Rectangle(0, 0, 0, 0);
            selectionRectangle.setFill(Color.TRANSPARENT);
            selectionRectangle.setStroke(Color.YELLOW);
            selectionRectangle.setStrokeWidth(2);
            
            helpText = new Text("Use WASD to move, Q/E to move up/down, Mouse to rotate, Scroll to zoom");
            helpText.setFont(Font.font(14));
            helpText.setFill(Color.BLACK);
            helpText.setX(10);
            helpText.setY(20);
            
            statsText = new Text();
            statsText.setFont(Font.font(12));
            statsText.setFill(Color.BLACK);
            statsText.setX(10);
            statsText.setY(40);
            
            // Add all elements to the pane
            getChildren().addAll(subScene, selectionRectangle, helpText, statsText);
            
            // Make subscene resize with parent
            subScene.widthProperty().bind(widthProperty());
            subScene.heightProperty().bind(heightProperty());
            
            // Update text position when pane resizes
            widthProperty().addListener((obs, oldVal, newVal) -> {
                double center = newVal.doubleValue() / 2;
                helpText.setX(center - helpText.getBoundsInLocal().getWidth() / 2);
            });
            
            // Add mouse controls
            setupMouseControls();
            
            // Add keyboard controls
            setupKeyboardControls();
            
            // Initial update of stats
            updateStatsText();
            
            // Request focus to receive key events
            Platform.runLater(this::requestFocus);
            
            // Draw initial room
            drawRoom();
            
        } catch (Exception e) {
            System.err.println("3D visualization not supported, using fallback: " + e.getMessage());
            e.printStackTrace();
            useFallback = true;
            createFallbackVisualization();
            getChildren().add(fallbackContainer);
        }
    }
    
    private void createFallbackVisualization() {
        fallbackContainer = new VBox(10);
        fallbackContainer.setPadding(new Insets(20));
        fallbackContainer.setAlignment(Pos.CENTER);
        fallbackContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Add a label explaining the fallback
        Label fallbackLabel = new Label("3D Visualization is not supported on this system.");
        fallbackLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add room info
        Label roomInfoLabel = new Label("Room Information");
        roomInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Add room dimensions label
        Label roomDimensionsLabel = new Label("Room dimensions will be displayed here");
        roomDimensionsLabel.setStyle("-fx-font-size: 12px;");
        
        // Create a 2D room representation
        StackPane roomRepresentation = new StackPane();
        roomRepresentation.setPrefHeight(300);
        roomRepresentation.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        // Create a grid for the room
        VBox roomGrid = new VBox();
        roomGrid.setAlignment(Pos.CENTER);
        roomGrid.setPadding(new Insets(10));
        
        // Add furniture list
        Label furnitureLabel = new Label("Furniture Items");
        furnitureLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Create a scrollable list for furniture
        VBox furnitureList = new VBox(5);
        furnitureList.setPadding(new Insets(10));
        furnitureList.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        ScrollPane scrollPane = new ScrollPane(furnitureList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);
        
        // Add all elements to the container
        fallbackContainer.getChildren().addAll(
            fallbackLabel,
            roomInfoLabel,
            roomDimensionsLabel,
            roomRepresentation,
            furnitureLabel,
            scrollPane
        );
        
        // Make the container resize with the parent
        fallbackContainer.prefWidthProperty().bind(widthProperty());
        fallbackContainer.prefHeightProperty().bind(heightProperty());
        
        // Store references for later updates
        this.fallbackRoomDimensionsLabel = roomDimensionsLabel;
        this.fallbackRoomRepresentation = roomRepresentation;
        this.fallbackFurnitureList = furnitureList;
    }
    
    private void setupLighting() {
        // Create ambient light for overall illumination
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        root.getChildren().add(ambientLight);
        
        // Create point lights for better 3D effect
        PointLight light1 = new PointLight(Color.WHITE);
        light1.setTranslateX(roomWidth * 100);
        light1.setTranslateY(-roomHeight * 100);
        light1.setTranslateZ(roomLength * 100);
        root.getChildren().add(light1);
        
        PointLight light2 = new PointLight(Color.WHITE);
        light2.setTranslateX(-roomWidth * 100);
        light2.setTranslateY(-roomHeight * 100);
        light2.setTranslateZ(-roomLength * 100);
        root.getChildren().add(light2);
        
        // Add a third light for better shadows
        PointLight light3 = new PointLight(Color.WHITE);
        light3.setTranslateX(0);
        light3.setTranslateY(roomHeight * 100);
        light3.setTranslateZ(0);
        root.getChildren().add(light3);
    }
    
    private void setupMouseControls() {
        if (useFallback) return;
        
        // Rotation with mouse drag
        subScene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            
            // Selection handling
            if (me.isShiftDown()) {
                // Find and select furniture model
                selectedFurniture = findFurnitureAt(me.getX(), me.getY());
                if (selectedFurniture != null) {
                    selectedModel = furnitureModels.get(selectedFurniture.getId());
                    highlightSelectedFurniture();
                } else {
                    // Deselect if clicking empty space
                    clearSelection();
                }
            }
        });
        
        subScene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            
            double deltaX = (mousePosX - mouseOldX);
            double deltaY = (mousePosY - mouseOldY);
            
            // Rotate the room based on mouse movement
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.5);
            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.5);
        });
        
        // Zoom with mouse wheel
        subScene.setOnScroll((ScrollEvent se) -> {
            double delta = se.getDeltaY();
            cameraDistance += delta * 0.5;
            
            // Limit zoom range
            cameraDistance = Math.min(-500, Math.max(-10000, cameraDistance));
            camera.setTranslateZ(cameraDistance);
        });
    }
    
    private void setupKeyboardControls() {
        if (useFallback) return;
        
        // Set up key event handlers for camera movement
        setOnKeyPressed((KeyEvent ke) -> {
            switch (ke.getCode()) {
                case W:
                case UP:
                    moveForward = true;
                    break;
                case S:
                case DOWN:
                    moveBackward = true;
                    break;
                case A:
                case LEFT:
                    moveLeft = true;
                    break;
                case D:
                case RIGHT:
                    moveRight = true;
                    break;
                case PAGE_UP:
                case Q:
                    moveUp = true;
                    break;
                case PAGE_DOWN:
                case E:
                    moveDown = true;
                    break;
            }
        });
        
        setOnKeyReleased((KeyEvent ke) -> {
            switch (ke.getCode()) {
                case W:
                case UP:
                    moveForward = false;
                    break;
                case S:
                case DOWN:
                    moveBackward = false;
                    break;
                case A:
                case LEFT:
                    moveLeft = false;
                    break;
                case D:
                case RIGHT:
                    moveRight = false;
                    break;
                case PAGE_UP:
                case Q:
                    moveUp = false;
                    break;
                case PAGE_DOWN:
                case E:
                    moveDown = false;
                    break;
            }
        });
    }
    
    private void moveCamera() {
        if (useFallback) return;
        
        double movementSpeed = 50.0;
        
        if (moveForward) {
            cameraDistance += movementSpeed;
        }
        if (moveBackward) {
            cameraDistance -= movementSpeed;
        }
        
        // Limit distance
        cameraDistance = Math.min(-500, Math.max(-10000, cameraDistance));
        camera.setTranslateZ(cameraDistance);
        
        // Horizontal movement
        if (moveLeft) {
            rotateY.setAngle(rotateY.getAngle() - 2.0);
        }
        if (moveRight) {
            rotateY.setAngle(rotateY.getAngle() + 2.0);
        }
        
        // Vertical movement
        if (moveUp) {
            cameraHeight += movementSpeed;
        }
        if (moveDown) {
            cameraHeight -= movementSpeed;
        }
        
        // Limit height
        cameraHeight = Math.min(-200, Math.max(-5000, cameraHeight));
        camera.setTranslateY(cameraHeight);
    }
    
    public void updateRoomDimensions(double width, double length, double height) {
        this.roomWidth = width;
        this.roomLength = length;
        this.roomHeight = height;
        
        if (useFallback) {
            updateFallbackRoomInfo();
        } else {
            Platform.runLater(() -> {
                drawRoom();
                updateStatsText();
            });
        }
    }
    
    private void updateFallbackRoomInfo() {
        if (fallbackRoomDimensionsLabel == null) return;
        
        // Update room dimensions label
        fallbackRoomDimensionsLabel.setText(String.format("Room: %.1fm x %.1fm x %.1fm", roomWidth, roomHeight, roomLength));
        
        // Update room representation
        updateFallbackRoomRepresentation();
    }
    
    private void updateFallbackRoomRepresentation() {
        if (fallbackRoomRepresentation == null) return;
        
        // Clear previous content
        fallbackRoomRepresentation.getChildren().clear();
        
        // Create a scaled representation of the room
        double maxDimension = Math.max(roomWidth, Math.max(roomHeight, roomLength));
        double scale = 250.0 / maxDimension; // Scale to fit in 250px
        
        // Create room rectangle
        Rectangle roomRect = new Rectangle(roomWidth * scale, roomHeight * scale);
        roomRect.setFill(roomColor);
        roomRect.setStroke(Color.BLACK);
        roomRect.setStrokeWidth(2);
        
        // Create grid lines
        VBox gridLines = new VBox();
        gridLines.setAlignment(Pos.CENTER);
        
        // Horizontal grid lines
        for (int i = 0; i <= roomHeight; i++) {
            Rectangle line = new Rectangle(roomWidth * scale, 1);
            line.setFill(Color.DARKGRAY);
            line.setTranslateY(i * scale);
            gridLines.getChildren().add(line);
        }
        
        // Vertical grid lines
        HBox vLines = new HBox();
        vLines.setAlignment(Pos.CENTER);
        for (int i = 0; i <= roomWidth; i++) {
            Rectangle line = new Rectangle(1, roomHeight * scale);
            line.setFill(Color.DARKGRAY);
            line.setTranslateX(i * scale);
            vLines.getChildren().add(line);
        }
        
        // Add furniture representations
        VBox furnitureReps = new VBox();
        furnitureReps.setAlignment(Pos.CENTER);
        
        for (Furniture furniture : furnitureItems) {
            Rectangle furnitureRect = new Rectangle(
                furniture.getWidth() * scale, 
                furniture.getHeight() * scale
            );
            
            try {
                furnitureRect.setFill(Color.valueOf(furniture.getColor()));
            } catch (Exception e) {
                furnitureRect.setFill(Color.GRAY);
            }
            
            furnitureRect.setStroke(Color.BLACK);
            furnitureRect.setStrokeWidth(1);
            
            // Position the furniture
            furnitureRect.setTranslateX((furniture.getPositionX() - roomWidth/2) * scale);
            furnitureRect.setTranslateY((furniture.getPositionY() - roomHeight/2) * scale);
            
            // Add tooltip with furniture info
            Tooltip tooltip = new Tooltip(String.format("%s\nMaterial: %s\nSize: %.1fm x %.1fm x %.1fm", 
                furniture.getType().getDisplayName(),
                furniture.getMaterial().getDisplayName(),
                furniture.getWidth(),
                furniture.getHeight(),
                furniture.getDepth()));
            Tooltip.install(furnitureRect, tooltip);
            
            furnitureReps.getChildren().add(furnitureRect);
        }
        
        // Add all elements to the room representation
        fallbackRoomRepresentation.getChildren().addAll(roomRect, gridLines, vLines, furnitureReps);
    }
    
    private void updateFallbackFurnitureList() {
        if (fallbackFurnitureList == null) return;
        
        fallbackFurnitureList.getChildren().clear();
        
        // Add each furniture item to the list
        for (Furniture furniture : furnitureItems) {
            HBox itemBox = new HBox(10);
            itemBox.setPadding(new Insets(5));
            itemBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            
            // Create a colored rectangle to represent the furniture
            Rectangle colorRect = new Rectangle(30, 30);
            try {
                colorRect.setFill(Color.valueOf(furniture.getColor()));
            } catch (Exception e) {
                colorRect.setFill(Color.GRAY);
            }
            colorRect.setStroke(Color.BLACK);
            colorRect.setStrokeWidth(1);
            
            // Create a label with furniture info
            VBox infoBox = new VBox(2);
            Label nameLabel = new Label(furniture.getType().getDisplayName());
            nameLabel.setStyle("-fx-font-weight: bold;");
            Label detailsLabel = new Label(String.format("Material: %s, Size: %.1fm x %.1fm x %.1fm", 
                furniture.getMaterial().getDisplayName(),
                furniture.getWidth(),
                furniture.getHeight(),
                furniture.getDepth()));
            Label positionLabel = new Label(String.format("Position: (%.1f, %.1f, %.1f)", 
                furniture.getPositionX(),
                furniture.getPositionY(),
                furniture.getPositionZ()));
            
            infoBox.getChildren().addAll(nameLabel, detailsLabel, positionLabel);
            
            itemBox.getChildren().addAll(colorRect, infoBox);
            fallbackFurnitureList.getChildren().add(itemBox);
        }
    }
    
    public void updateRoomColor(Color color) {
        this.roomColor = color;
        
        if (useFallback) {
            // Update fallback visualization background color
            fallbackContainer.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            Platform.runLater(() -> {
                if (wall1 != null) {
                    PhongMaterial material = (PhongMaterial) wall1.getMaterial();
                    material.setDiffuseColor(color);
                    material = (PhongMaterial) wall2.getMaterial();
                    material.setDiffuseColor(color);
                    material = (PhongMaterial) wall3.getMaterial();
                    material.setDiffuseColor(color);
                    
                    // Front wall with transparency but same color
                    PhongMaterial frontMaterial = (PhongMaterial) wall4.getMaterial();
                    frontMaterial.setDiffuseColor(color);
                }
            });
        }
    }
    
    public void setFurniture(List<Furniture> furniture) {
        this.furnitureItems = new ArrayList<>(furniture);
        
        if (useFallback) {
            updateFallbackFurnitureList();
        } else {
            Platform.runLater(this::drawFurniture);
        }
    }
    
    private void updateStatsText() {
        if (useFallback) return;
        
        statsText.setText(String.format("Room: %.1fm x %.1fm x %.1fm", roomWidth, roomHeight, roomLength));
    }
    
    private void drawRoom() {
        if (useFallback) return;
        
        // Clear previous room components
        root.getChildren().clear();
        
        // Re-add lighting
        setupLighting();
        
        // Create room group
        roomGroup = new Group();
        root.getChildren().add(roomGroup);
        
        // Create materials
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(roomColor);
        floorMaterial.setSpecularColor(Color.WHITE);
        
        PhongMaterial wallMaterial = new PhongMaterial();
        wallMaterial.setDiffuseColor(roomColor);
        wallMaterial.setSpecularColor(Color.WHITE);
        
        // Create floor
        floor = new Box(roomWidth * 100, 10, roomLength * 100);
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(roomHeight * 50);
        roomGroup.getChildren().add(floor);
        
        // Create walls
        // Back wall
        wall1 = new Box(roomWidth * 100, roomHeight * 100, 10);
        wall1.setMaterial(wallMaterial);
        wall1.setTranslateX(0);
        wall1.setTranslateY(-roomHeight * 50);
        wall1.setTranslateZ(-roomLength * 50);
        roomGroup.getChildren().add(wall1);
        
        // Left wall
        wall2 = new Box(10, roomHeight * 100, roomLength * 100);
        wall2.setMaterial(wallMaterial);
        wall2.setTranslateX(-roomWidth * 50);
        wall2.setTranslateY(-roomHeight * 50);
        wall2.setTranslateZ(0);
        roomGroup.getChildren().add(wall2);
        
        // Right wall
        wall3 = new Box(10, roomHeight * 100, roomLength * 100);
        wall3.setMaterial(wallMaterial);
        wall3.setTranslateX(roomWidth * 50);
        wall3.setTranslateY(-roomHeight * 50);
        wall3.setTranslateZ(0);
        roomGroup.getChildren().add(wall3);
        
        // Front wall (transparent)
        wall4 = new Box(roomWidth * 100, roomHeight * 100, 10);
        PhongMaterial transparentMaterial = new PhongMaterial();
        transparentMaterial.setDiffuseColor(Color.TRANSPARENT);
        wall4.setMaterial(transparentMaterial);
        wall4.setTranslateX(0);
        wall4.setTranslateY(-roomHeight * 50);
        wall4.setTranslateZ(roomLength * 50);
        roomGroup.getChildren().add(wall4);
        
        // Create grid for floor markings
        createFloorGrid();
        
        // Reset camera position based on room size
        resetCamera();
        
        // Draw furniture if any
        if (furnitureItems != null && !furnitureItems.isEmpty()) {
            drawFurniture();
        }
    }
    
    private void createFloorGrid() {
        // Create grid group
        Group gridGroup = new Group();
        
        // Create grid material
        PhongMaterial gridMaterial = new PhongMaterial();
        gridMaterial.setDiffuseColor(Color.DARKGRAY);
        gridMaterial.setSpecularColor(Color.WHITE);
        
        // Grid size and spacing
        double gridSize = 100; // 1 meter in our scale
        double gridSpacing = 100; // 1 meter in our scale
        
        // Create grid lines along X axis
        for (double z = -roomLength * 50; z <= roomLength * 50; z += gridSpacing) {
            Box line = new Box(roomWidth * 100, 1, 1);
            line.setMaterial(gridMaterial);
            line.setTranslateX(0);
            line.setTranslateY(roomHeight * 50 + 5); // Slightly above floor
            line.setTranslateZ(z);
            gridGroup.getChildren().add(line);
        }
        
        // Create grid lines along Z axis
        for (double x = -roomWidth * 50; x <= roomWidth * 50; x += gridSpacing) {
            Box line = new Box(1, 1, roomLength * 100);
            line.setMaterial(gridMaterial);
            line.setTranslateX(x);
            line.setTranslateY(roomHeight * 50 + 5); // Slightly above floor
            line.setTranslateZ(0);
            gridGroup.getChildren().add(line);
        }
        
        // Add grid to room group
        roomGroup.getChildren().add(gridGroup);
    }
    
    private void resetCamera() {
        // Calculate optimal camera distance based on room size
        double maxDimension = Math.max(roomWidth, Math.max(roomHeight, roomLength));
        cameraDistance = -maxDimension * 200; // Scale factor for better view
        
        // Set camera height to 1/3 of room height for better perspective
        cameraHeight = -roomHeight * 100 / 3;
        
        // Update camera position
        camera.setTranslateZ(cameraDistance);
        camera.setTranslateY(cameraHeight);
        
        // Reset camera rotation
        camera.setRotate(0);
    }
    
    private void drawFurniture() {
        if (useFallback) return;
        
        // Clear existing furniture models
        furnitureModels.clear();
        
        // Create and add furniture models
        for (Furniture furniture : furnitureItems) {
            Furniture3DModel model = new Furniture3DModel(furniture);
            furnitureModels.put(furniture.getId(), model);
            
            // Add model to room group
            roomGroup.getChildren().add(model.getModelGroup());
        }
    }
    
    private void clearSelection() {
        // Deselect current furniture
        selectedFurniture = null;
        selectedModel = null;
        
        // Remove any selection indicators
        // In a more complex implementation, we might have added visual indicators
        // to show which furniture is selected
    }
    
    private void highlightSelectedFurniture() {
        // In a more complex implementation, we might add visual indicators
        // to show which furniture is selected, such as bounding boxes or highlights
    }
    
    private Furniture findFurnitureAt(double x, double y) {
        // This is a basic implementation for furniture selection in 3D space
        // For a production app, you would implement proper picking in 3D space
        // using ray casting or other techniques
        
        // In this simplified version, we'll just return null
        // A real implementation would identify the furniture model under the cursor
        return null;
    }
    
    public void updateFurniturePosition(Furniture furniture) {
        Furniture3DModel model = furnitureModels.get(furniture.getId());
        if (model != null) {
            model.updatePosition();
        }
    }
    
    public Furniture getSelectedFurniture() {
        return selectedFurniture;
    }
    
    public void setSelectedFurniture(Furniture furniture) {
        this.selectedFurniture = furniture;
        
        if (furniture != null && furnitureModels != null) {
            this.selectedModel = furnitureModels.get(furniture.getId());
            highlightSelectedFurniture();
        } else {
            this.selectedModel = null;
            clearSelection();
        }
    }
}


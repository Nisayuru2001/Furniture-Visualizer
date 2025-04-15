package com.furniture.visualizer.view;

import com.furniture.visualizer.model.Furniture;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

/**
 * Class representing a 3D furniture model in the scene
 */
public class Furniture3DModel {
    private Furniture furniture;
    private Group modelGroup;
    private Group rootGroup;
    private RotateTransition rotateTransition;
    private SubScene previewSubScene;
    
    /**
     * Constructor for creating a 3D furniture model
     * 
     * @param furniture The furniture data
     */
    public Furniture3DModel(Furniture furniture) {
        this.furniture = furniture;
        this.rootGroup = new Group();
        this.modelGroup = FurnitureModelFactory.createFurnitureModel(furniture);
        this.rootGroup.getChildren().add(modelGroup);
        setupRotation();
        updatePosition();
    }
    
    /**
     * Sets up rotation animation for preview mode
     */
    private void setupRotation() {
        rotateTransition = new RotateTransition(Duration.seconds(10), rootGroup);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.play();
    }
    
    /**
     * Creates a subscene for previewing the furniture in dialogs
     * 
     * @return A pane containing the 3D preview
     */
    public Pane getPreviewPane() {
        VBox container = new VBox(5);
        container.setPrefSize(300, 300);
        
        // Create preview scene
        Group previewRoot = new Group();
        previewRoot.getChildren().add(rootGroup);
        
        // Add lighting for better 3D effect
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.setLightOn(true);
        
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-500);
        pointLight.setTranslateZ(-500);
        
        previewRoot.getChildren().addAll(ambientLight, pointLight);
        
        // Create subscene with hardware acceleration
        previewSubScene = new SubScene(previewRoot, 300, 300, true, SceneAntialiasing.BALANCED);
        
        // Set up camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        camera.setTranslateY(-500);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(30);
        previewSubScene.setCamera(camera);
        
        // Add descriptive label
        Label label = new Label(furniture.getType().getDisplayName() + " Preview");
        label.setStyle("-fx-font-weight: bold;");
        
        container.getChildren().addAll(label, previewSubScene);
        
        // Start rotation
        rotateTransition.play();
        
        return container;
    }
    
    /**
     * Updates the position of the furniture model
     */
    public void updatePosition() {
        // Scale the model to match furniture dimensions
        double scaleX = furniture.getWidth() * 100;
        double scaleY = furniture.getHeight() * 100;
        double scaleZ = furniture.getDepth() * 100;
        
        // Apply scaling
        modelGroup.setScaleX(scaleX);
        modelGroup.setScaleY(scaleY);
        modelGroup.setScaleZ(scaleZ);
        
        // Position the model
        double posX = (furniture.getPositionX() - 5.0) * 100; // Center at origin
        double posY = furniture.getPositionY() * 100;
        double posZ = (furniture.getPositionZ() - 5.0) * 100; // Center at origin
        
        rootGroup.setTranslateX(posX);
        rootGroup.setTranslateY(posY);
        rootGroup.setTranslateZ(posZ);
        
        // Apply rotation if needed
        if (furniture.getRotation() != 0) {
            rootGroup.setRotate(furniture.getRotation());
        }
    }
    
    /**
     * Updates the furniture data and rebuilds the model
     * 
     * @param furniture The updated furniture data
     */
    public void updateFurniture(Furniture furniture) {
        // Store rotation state
        boolean wasRotating = false;
        if (rotateTransition != null && rotateTransition.getStatus() == Animation.Status.RUNNING) {
            rotateTransition.pause();
            wasRotating = true;
        }
        
        this.furniture = furniture;
        
        // Replace the model
        Group newModel = FurnitureModelFactory.createFurnitureModel(furniture);
        rootGroup.getChildren().clear();
        rootGroup.getChildren().add(newModel);
        this.modelGroup = newModel;
        
        updatePosition();
        
        // Resume rotation if it was active
        if (wasRotating) {
            rotateTransition.play();
        }
    }
    
    /**
     * Gets the model group for adding to the scene
     * 
     * @return The JavaFX Group representing this furniture in 3D
     */
    public Group getModelGroup() {
        return rootGroup;
    }
    
    /**
     * Gets the furniture data
     * 
     * @return The furniture data
     */
    public Furniture getFurniture() {
        return furniture;
    }
    
    /**
     * Sets new furniture data and updates the model
     * 
     * @param furniture The new furniture data
     */
    public void setFurniture(Furniture furniture) {
        this.furniture = furniture;
        updateFurniture(furniture);
    }
    
    /**
     * Stops any animations on this model
     */
    public void dispose() {
        if (rotateTransition != null) {
            rotateTransition.stop();
        }
    }
}

package com.furnituredesign.utils;

import com.furnituredesign.models.Furniture;
import com.furnituredesign.models.Material;
import com.furnituredesign.models.Room;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.scene.image.Image;

/**
 * Utility class for advanced 3D rendering operations
 * including lighting, materials, and scene construction.
 */
public class RenderUtils {

    /**
     * Converts a hex color string to a JavaFX Color object
     */
    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        
        if (hex.length() == 6) {
            return Color.rgb(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
            );
        } else {
            return Color.WHITE;
        }
    }
    
    /**
     * Creates a PhongMaterial from our custom Material class
     */
    public static PhongMaterial createPhongMaterial(Material material) {
        PhongMaterial phongMaterial = new PhongMaterial();
        
        // Set base color
        Color baseColor = hexToColor(material.getBaseColor());
        phongMaterial.setDiffuseColor(baseColor);
        
        // Apply specular effect based on material properties
        double specularPower = (1.0 - material.getRoughness()) * 128.0 + 1.0;
        Color specularColor = Color.WHITE;
        
        // Metallic materials reflect their own color
        if (material.getMetallic() > 0.5) {
            specularColor = baseColor.brighter();
        }
        
        // Apply specular properties
        phongMaterial.setSpecularColor(specularColor);
        phongMaterial.setSpecularPower(specularPower);
        
        return phongMaterial;
    }
    
    /**
     * Create a 3D representation of a room with walls, floor, and ceiling
     */
    public static Group createRoom3D(Room room) {
        Group roomGroup = new Group();
        
        double width = room.getWidth() * 100;  // Scale to scene units
        double length = room.getLength() * 100;
        double height = room.getHeight() * 100;
        
        // Create floor
        Box floor = new Box(width, length, 1);
        floor.setTranslateX(width / 2);
        floor.setTranslateY(length / 2);
        floor.setTranslateZ(0.5);
        PhongMaterial floorMaterial = createPhongMaterial(room.getFloorMaterial());
        floor.setMaterial(floorMaterial);
        
        // Create ceiling
        Box ceiling = new Box(width, length, 1);
        ceiling.setTranslateX(width / 2);
        ceiling.setTranslateY(length / 2);
        ceiling.setTranslateZ(height - 0.5);
        PhongMaterial ceilingMaterial = createPhongMaterial(room.getCeilingMaterial());
        ceiling.setMaterial(ceilingMaterial);
        
        // Create walls
        Box wallNorth = new Box(width, 1, height);
        wallNorth.setTranslateX(width / 2);
        wallNorth.setTranslateY(0.5);
        wallNorth.setTranslateZ(height / 2);
        
        Box wallSouth = new Box(width, 1, height);
        wallSouth.setTranslateX(width / 2);
        wallSouth.setTranslateY(length - 0.5);
        wallSouth.setTranslateZ(height / 2);
        
        Box wallEast = new Box(1, length, height);
        wallEast.setTranslateX(width - 0.5);
        wallEast.setTranslateY(length / 2);
        wallEast.setTranslateZ(height / 2);
        
        Box wallWest = new Box(1, length, height);
        wallWest.setTranslateX(0.5);
        wallWest.setTranslateY(length / 2);
        wallWest.setTranslateZ(height / 2);
        
        PhongMaterial wallMaterial = createPhongMaterial(room.getWallMaterial());
        wallNorth.setMaterial(wallMaterial);
        wallSouth.setMaterial(wallMaterial);
        wallEast.setMaterial(wallMaterial);
        wallWest.setMaterial(wallMaterial);
        
        roomGroup.getChildren().addAll(floor, ceiling, wallNorth, wallSouth, wallEast, wallWest);
        
        // Add lights
        for (Room.Light roomLight : room.getLights()) {
            PointLight light = new PointLight();
            light.setColor(hexToColor(roomLight.getColor()));
            
            // Scale light position to scene units
            double lightX = roomLight.getX() * 100;
            double lightY = roomLight.getY() * 100;
            double lightZ = roomLight.getZ() * 100;
            
            light.setTranslateX(lightX);
            light.setTranslateY(lightY);
            light.setTranslateZ(lightZ);
            
            // Create a visual representation of the light
            Cylinder lightSphere = new Cylinder(roomLight.getRadius() * 100, roomLight.getRadius() * 100 * 2);
            lightSphere.setTranslateX(lightX);
            lightSphere.setTranslateY(lightY);
            lightSphere.setTranslateZ(lightZ);
            
            PhongMaterial lightMaterial = new PhongMaterial();
            lightMaterial.setDiffuseColor(hexToColor(roomLight.getColor()));
            lightMaterial.setSpecularColor(Color.WHITE);
            
            // Create self-illumination effect (glow)
            try {
                String glowPath = "/images/light_glow.png";
                Image glowImage = new Image(RenderUtils.class.getResourceAsStream(glowPath));
                if (glowImage != null) {
                    lightMaterial.setSelfIlluminationMap(glowImage);
                }
            } catch (Exception e) {
                // If image fails to load, just use a glowing diffuse color
                lightMaterial.setDiffuseColor(hexToColor(roomLight.getColor()).brighter().brighter());
            }
            
            lightSphere.setMaterial(lightMaterial);
            
            // Add light sources
            roomGroup.getChildren().addAll(light, lightSphere);
        }
        
        return roomGroup;
    }
    
    /**
     * Create a 3D representation of a furniture item
     */
    public static Shape3D createFurniture3D(Furniture furniture) {
        Group furnitureGroup = new Group();
        
        // For simplicity, we're creating basic shapes. In a real implementation,
        // this would load actual 3D models from the furniture.getModelPath()
        Shape3D primaryShape;
        switch (furniture.getType().toLowerCase()) {
            case "table":
            case "coffee_table":
            case "dining_table":
                addTable3D(furnitureGroup, furniture);
                break;
            case "chair":
                addChair3D(furnitureGroup, furniture);
                break;
            case "sofa":
                addSofa3D(furnitureGroup, furniture);
                break;
            case "bed":
                addBed3D(furnitureGroup, furniture);
                break;
            case "lamp":
                addLamp3D(furnitureGroup, furniture);
                break;
            default:
                // Default to a simple box for any other furniture type
                double width = furniture.getWidth() * 100;
                double length = furniture.getLength() * 100;
                double height = furniture.getHeight() * 100;
                Box box = new Box(width, length, height);
                box.setTranslateZ(height / 2); // Set the base at Z=0
                furnitureGroup.getChildren().add(box);
                primaryShape = box; // Set the primary shape for material
        }
        
        // Create a "handle" box to apply the material to all shapes in the group
        // This box is invisible but helps apply transformations to the whole group
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        Box handle = new Box(width, length, height);
        handle.setVisible(false);
        furnitureGroup.getChildren().add(handle);
        
        // Apply material to all shapes in the group
        PhongMaterial material = createPhongMaterial(furniture.getMaterial());
        for (javafx.scene.Node node : furnitureGroup.getChildren()) {
            if (node instanceof Shape3D) {
                ((Shape3D) node).setMaterial(material);
            }
        }
        
        // Position and rotate the entire group
        double x = furniture.getX() * 100;
        double y = furniture.getY() * 100;
        double z = furniture.getZ() * 100;
        
        furnitureGroup.setTranslateX(x);
        furnitureGroup.setTranslateY(y);
        furnitureGroup.setTranslateZ(z);
        
        // Apply rotations
        Rotate rotateX = new Rotate(furniture.getRotationX(), Rotate.X_AXIS);
        Rotate rotateY = new Rotate(furniture.getRotationY(), Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(furniture.getRotationZ(), Rotate.Z_AXIS);
        
        furnitureGroup.getTransforms().addAll(rotateX, rotateY, rotateZ);
        
        // Apply scale
        furnitureGroup.setScaleX(furniture.getScale());
        furnitureGroup.setScaleY(furniture.getScale());
        furnitureGroup.setScaleZ(furniture.getScale());
        
        return handle; // Return the handle as the primary shape
    }
    
    /**
     * Create and add a 3D table to the given group
     */
    private static void addTable3D(Group group, Furniture furniture) {
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        double legThickness = 5.0;
        
        // Table top
        Box top = new Box(width, length, 5);
        top.setTranslateZ(height - 2.5);
        
        // Table legs
        Box leg1 = new Box(legThickness, legThickness, height - 5);
        leg1.setTranslateX(width/2 - legThickness/2 - 5);
        leg1.setTranslateY(length/2 - legThickness/2 - 5);
        leg1.setTranslateZ((height - 5) / 2);
        
        Box leg2 = new Box(legThickness, legThickness, height - 5);
        leg2.setTranslateX(width/2 - legThickness/2 - 5);
        leg2.setTranslateY(-length/2 + legThickness/2 + 5);
        leg2.setTranslateZ((height - 5) / 2);
        
        Box leg3 = new Box(legThickness, legThickness, height - 5);
        leg3.setTranslateX(-width/2 + legThickness/2 + 5);
        leg3.setTranslateY(length/2 - legThickness/2 - 5);
        leg3.setTranslateZ((height - 5) / 2);
        
        Box leg4 = new Box(legThickness, legThickness, height - 5);
        leg4.setTranslateX(-width/2 + legThickness/2 + 5);
        leg4.setTranslateY(-length/2 + legThickness/2 + 5);
        leg4.setTranslateZ((height - 5) / 2);
        
        group.getChildren().addAll(top, leg1, leg2, leg3, leg4);
    }
    
    /**
     * Create and add a 3D chair to the given group
     */
    private static void addChair3D(Group group, Furniture furniture) {
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        double seatHeight = height * 0.4;
        double legThickness = 4.0;
        
        // Seat
        Box seat = new Box(width, length, 4);
        seat.setTranslateZ(seatHeight);
        
        // Back
        Box back = new Box(width, 4, height - seatHeight);
        back.setTranslateY(-length/2 + 2);
        back.setTranslateZ(seatHeight + (height - seatHeight) / 2);
        
        // Legs
        Box leg1 = new Box(legThickness, legThickness, seatHeight);
        leg1.setTranslateX(width/2 - legThickness/2 - 2);
        leg1.setTranslateY(length/2 - legThickness/2 - 2);
        leg1.setTranslateZ(seatHeight/2);
        
        Box leg2 = new Box(legThickness, legThickness, seatHeight);
        leg2.setTranslateX(width/2 - legThickness/2 - 2);
        leg2.setTranslateY(-length/2 + legThickness/2 + 2);
        leg2.setTranslateZ(seatHeight/2);
        
        Box leg3 = new Box(legThickness, legThickness, seatHeight);
        leg3.setTranslateX(-width/2 + legThickness/2 + 2);
        leg3.setTranslateY(length/2 - legThickness/2 - 2);
        leg3.setTranslateZ(seatHeight/2);
        
        Box leg4 = new Box(legThickness, legThickness, seatHeight);
        leg4.setTranslateX(-width/2 + legThickness/2 + 2);
        leg4.setTranslateY(-length/2 + legThickness/2 + 2);
        leg4.setTranslateZ(seatHeight/2);
        
        group.getChildren().addAll(seat, back, leg1, leg2, leg3, leg4);
    }
    
    /**
     * Create and add a 3D sofa to the given group
     */
    private static void addSofa3D(Group group, Furniture furniture) {
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        double seatHeight = height * 0.4;
        double armWidth = 20;
        
        // Base
        Box base = new Box(width, length, seatHeight);
        base.setTranslateZ(seatHeight/2);
        
        // Back
        Box back = new Box(width - 2*armWidth, 20, height - seatHeight);
        back.setTranslateY(-length/2 + 10);
        back.setTranslateZ(seatHeight + (height - seatHeight)/2);
        
        // Arms
        Box leftArm = new Box(armWidth, length, height);
        leftArm.setTranslateX(-width/2 + armWidth/2);
        leftArm.setTranslateZ(height/2);
        
        Box rightArm = new Box(armWidth, length, height);
        rightArm.setTranslateX(width/2 - armWidth/2);
        rightArm.setTranslateZ(height/2);
        
        group.getChildren().addAll(base, back, leftArm, rightArm);
    }
    
    /**
     * Create and add a 3D bed to the given group
     */
    private static void addBed3D(Group group, Furniture furniture) {
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        double frameHeight = height * 0.3;
        double headboardHeight = height * 2;
        
        // Base frame
        Box frame = new Box(width, length, frameHeight);
        frame.setTranslateZ(frameHeight/2);
        
        // Mattress
        Box mattress = new Box(width - 10, length - 10, height - frameHeight);
        mattress.setTranslateZ(frameHeight + (height - frameHeight)/2);
        
        // Headboard
        Box headboard = new Box(width, 10, headboardHeight);
        headboard.setTranslateY(-length/2 - 5);
        headboard.setTranslateZ(headboardHeight/2);
        
        group.getChildren().addAll(frame, mattress, headboard);
    }
    
    /**
     * Create and add a 3D lamp to the given group
     */
    private static void addLamp3D(Group group, Furniture furniture) {
        double width = furniture.getWidth() * 100;
        double length = furniture.getLength() * 100;
        double height = furniture.getHeight() * 100;
        double baseHeight = 10;
        double poleRadius = 3;
        double shadeRadius = width * 0.7;
        double shadeHeight = 30;
        
        // Base
        Cylinder base = new Cylinder(width/2, baseHeight);
        base.setTranslateZ(baseHeight/2);
        
        // Pole
        Cylinder pole = new Cylinder(poleRadius, height - baseHeight - shadeHeight);
        pole.setTranslateZ(baseHeight + (height - baseHeight - shadeHeight)/2);
        
        // Shade
        Cylinder shade = new Cylinder(shadeRadius, shadeHeight);
        shade.setTranslateZ(height - shadeHeight/2);
        
        // Light source
        PointLight light = new PointLight();
        light.setColor(Color.YELLOW);
        light.setTranslateZ(height - shadeHeight/2);
        
        group.getChildren().addAll(base, pole, shade, light);
    }
    
    /**
     * Calculate shadows for objects in the scene
     * This is a simplified version - real shadow calculation is more complex
     */
    public static void calculateShadows(Group scene, Room room) {
        // This would be implemented with more complex algorithms
        // For JavaFX, we'd typically use external libraries or more advanced techniques
        // In this simplified version, we'll just mention what would happen
        
        // 1. For each light source, cast rays to objects
        // 2. Calculate shadow polygons based on occluded light
        // 3. Project shadows onto floor and other surfaces
        // 4. Adjust color/alpha of shadow based on light properties
    }
} 
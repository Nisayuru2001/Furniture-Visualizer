package com.furniture.visualizer.view;

import com.furniture.visualizer.model.Furniture;
import com.furniture.visualizer.model.FurnitureType;
import com.furniture.visualizer.model.MaterialType;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

/**
 * Factory class for generating 3D furniture models
 */
public class FurnitureModelFactory {
    
    /**
     * Creates a 3D model for the specified furniture type
     * 
     * @param furniture The furniture item to create a model for
     * @return A JavaFX Group containing the 3D model
     */
    public static Group createFurnitureModel(Furniture furniture) {
        FurnitureType type = furniture.getType();
        
        if (type == null) {
            return createGenericModel(furniture);
        }
        
        switch (type) {
            case CHAIR:
                return createChairModel(furniture);
            case TABLE:
                return createTableModel(furniture);
            case SOFA:
                return createSofaModel(furniture);
            case BED:
                return createBedModel(furniture);
            case CABINET:
                return createCabinetModel(furniture);
            case DESK:
                return createDeskModel(furniture);
            default:
                // Default to a simple box for unknown types
                return createGenericModel(furniture);
        }
    }
    
    /**
     * Creates a PhongMaterial for the furniture based on its color and material type
     */
    public static PhongMaterial createMaterial(Furniture furniture) {
        PhongMaterial material = new PhongMaterial();
        
        try {
            material.setDiffuseColor(Color.valueOf(furniture.getColor()));
        } catch (Exception e) {
            material.setDiffuseColor(Color.GRAY);
        }
        
        material.setSpecularColor(Color.WHITE);
        
        return material;
    }
    
    /**
     * Creates a 3D chair model
     */
    private static Group createChairModel(Furniture furniture) {
        Group group = new Group();
        
        // Create materials
        PhongMaterial seatMaterial = createMaterial(furniture);
        PhongMaterial legMaterial = createMaterial(furniture);
        
        // Create seat
        Box seat = new Box(1, 0.1, 1);
        seat.setMaterial(seatMaterial);
        seat.setTranslateY(-0.5);
        
        // Create legs
        Box leg1 = new Box(0.1, 0.5, 0.1);
        leg1.setMaterial(legMaterial);
        leg1.setTranslateX(0.4);
        leg1.setTranslateY(-0.25);
        leg1.setTranslateZ(0.4);
        
        Box leg2 = new Box(0.1, 0.5, 0.1);
        leg2.setMaterial(legMaterial);
        leg2.setTranslateX(-0.4);
        leg2.setTranslateY(-0.25);
        leg2.setTranslateZ(0.4);
        
        Box leg3 = new Box(0.1, 0.5, 0.1);
        leg3.setMaterial(legMaterial);
        leg3.setTranslateX(0.4);
        leg3.setTranslateY(-0.25);
        leg3.setTranslateZ(-0.4);
        
        Box leg4 = new Box(0.1, 0.5, 0.1);
        leg4.setMaterial(legMaterial);
        leg4.setTranslateX(-0.4);
        leg4.setTranslateY(-0.25);
        leg4.setTranslateZ(-0.4);
        
        // Create back
        Box back = new Box(1, 0.5, 0.1);
        back.setMaterial(seatMaterial);
        back.setTranslateY(-0.75);
        back.setTranslateZ(-0.45);
        
        group.getChildren().addAll(seat, leg1, leg2, leg3, leg4, back);
        return group;
    }
    
    /**
     * Creates a 3D table model
     */
    private static Group createTableModel(Furniture furniture) {
        Group tableGroup = new Group();
        PhongMaterial material = createMaterial(furniture);
        
        // Table dimensions
        double width = furniture.getWidth();
        double height = furniture.getHeight();
        double depth = furniture.getDepth();
        
        // Tabletop
        Box tabletop = new Box(width, height * 0.05, depth);
        tabletop.setTranslateY(-height * 0.475);
        tabletop.setMaterial(material);
        
        // Legs
        double legThickness = width * 0.05;
        double legHeight = height * 0.95;
        
        // Create a slightly darker material for the legs
        Color baseColor = Color.valueOf(furniture.getColor());
        Color legColor = baseColor.darker();
        PhongMaterial legMaterial = new PhongMaterial(legColor);
        
        // Apply texture to leg material if available
        if (material.getDiffuseMap() != null) {
            legMaterial.setDiffuseMap(material.getDiffuseMap());
        }
        
        // Front-left leg
        Box frontLeftLeg = new Box(legThickness, legHeight, legThickness);
        frontLeftLeg.setTranslateX(-width * 0.45);
        frontLeftLeg.setTranslateY(-height * 0.025);
        frontLeftLeg.setTranslateZ(-depth * 0.45);
        frontLeftLeg.setMaterial(legMaterial);
        
        // Front-right leg
        Box frontRightLeg = new Box(legThickness, legHeight, legThickness);
        frontRightLeg.setTranslateX(width * 0.45);
        frontRightLeg.setTranslateY(-height * 0.025);
        frontRightLeg.setTranslateZ(-depth * 0.45);
        frontRightLeg.setMaterial(legMaterial);
        
        // Back-left leg
        Box backLeftLeg = new Box(legThickness, legHeight, legThickness);
        backLeftLeg.setTranslateX(-width * 0.45);
        backLeftLeg.setTranslateY(-height * 0.025);
        backLeftLeg.setTranslateZ(depth * 0.45);
        backLeftLeg.setMaterial(legMaterial);
        
        // Back-right leg
        Box backRightLeg = new Box(legThickness, legHeight, legThickness);
        backRightLeg.setTranslateX(width * 0.45);
        backRightLeg.setTranslateY(-height * 0.025);
        backRightLeg.setTranslateZ(depth * 0.45);
        backRightLeg.setMaterial(legMaterial);
        
        tableGroup.getChildren().addAll(tabletop, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
        return tableGroup;
    }
    
    /**
     * Creates a 3D sofa model
     */
    private static Group createSofaModel(Furniture furniture) {
        Group sofaGroup = new Group();
        PhongMaterial material = createMaterial(furniture);
        
        // Sofa dimensions
        double width = furniture.getWidth();
        double height = furniture.getHeight();
        double depth = furniture.getDepth();
        
        // Base
        Box base = new Box(width, height * 0.3, depth);
        base.setTranslateY(-height * 0.35);
        base.setMaterial(material);
        
        // Back
        Box back = new Box(width, height * 0.7, depth * 0.2);
        back.setTranslateY(-height * 0.65);
        back.setTranslateZ(depth * 0.4);
        back.setMaterial(material);
        
        // Left armrest
        Box leftArm = new Box(width * 0.1, height * 0.4, depth);
        leftArm.setTranslateX(-width * 0.45);
        leftArm.setTranslateY(-height * 0.5);
        leftArm.setMaterial(material);
        
        // Right armrest
        Box rightArm = new Box(width * 0.1, height * 0.4, depth);
        rightArm.setTranslateX(width * 0.45);
        rightArm.setTranslateY(-height * 0.5);
        rightArm.setMaterial(material);
        
        // Cushions (using a slightly different color)
        Color baseColor = Color.valueOf(furniture.getColor());
        Color cushionColor = baseColor.brighter();
        PhongMaterial cushionMaterial = new PhongMaterial(cushionColor);
        
        // Apply texture to cushion material if available
        if (material.getDiffuseMap() != null) {
            cushionMaterial.setDiffuseMap(material.getDiffuseMap());
        }
        
        // Left cushion
        Box leftCushion = new Box(width * 0.4, height * 0.15, depth * 0.8);
        leftCushion.setTranslateX(-width * 0.25);
        leftCushion.setTranslateY(-height * 0.425);
        leftCushion.setTranslateZ(-depth * 0.1);
        leftCushion.setMaterial(cushionMaterial);
        
        // Right cushion
        Box rightCushion = new Box(width * 0.4, height * 0.15, depth * 0.8);
        rightCushion.setTranslateX(width * 0.25);
        rightCushion.setTranslateY(-height * 0.425);
        rightCushion.setTranslateZ(-depth * 0.1);
        rightCushion.setMaterial(cushionMaterial);
        
        // Legs
        double legHeight = height * 0.1;
        double legRadius = width * 0.02;
        
        // Create a slightly darker material for the legs
        Color legColor = baseColor.darker();
        PhongMaterial legMaterial = new PhongMaterial(legColor);
        
        // Front-left leg
        Cylinder frontLeftLeg = new Cylinder(legRadius, legHeight);
        frontLeftLeg.setTranslateX(-width * 0.4);
        frontLeftLeg.setTranslateY(-height * 0.1);
        frontLeftLeg.setTranslateZ(-depth * 0.4);
        frontLeftLeg.setMaterial(legMaterial);
        
        // Front-right leg
        Cylinder frontRightLeg = new Cylinder(legRadius, legHeight);
        frontRightLeg.setTranslateX(width * 0.4);
        frontRightLeg.setTranslateY(-height * 0.1);
        frontRightLeg.setTranslateZ(-depth * 0.4);
        frontRightLeg.setMaterial(legMaterial);
        
        // Back-left leg
        Cylinder backLeftLeg = new Cylinder(legRadius, legHeight);
        backLeftLeg.setTranslateX(-width * 0.4);
        backLeftLeg.setTranslateY(-height * 0.1);
        backLeftLeg.setTranslateZ(depth * 0.4);
        backLeftLeg.setMaterial(legMaterial);
        
        // Back-right leg
        Cylinder backRightLeg = new Cylinder(legRadius, legHeight);
        backRightLeg.setTranslateX(width * 0.4);
        backRightLeg.setTranslateY(-height * 0.1);
        backRightLeg.setTranslateZ(depth * 0.4);
        backRightLeg.setMaterial(legMaterial);
        
        sofaGroup.getChildren().addAll(
            base, back, leftArm, rightArm, leftCushion, rightCushion,
            frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg
        );
        
        return sofaGroup;
    }
    
    /**
     * Creates a 3D bed model
     */
    private static Group createBedModel(Furniture furniture) {
        Group bedGroup = new Group();
        PhongMaterial material = createMaterial(furniture);
        
        // Bed dimensions
        double width = furniture.getWidth();
        double height = furniture.getHeight();
        double depth = furniture.getDepth();
        
        // Base/Frame
        Box frame = new Box(width, height * 0.2, depth);
        frame.setTranslateY(-height * 0.4);
        frame.setMaterial(material);
        
        // Mattress (slightly smaller than the frame)
        Box mattress = new Box(width * 0.95, height * 0.1, depth * 0.95);
        mattress.setTranslateY(-height * 0.55);
        
        // Use a different color for the mattress
        PhongMaterial mattressMaterial = new PhongMaterial(Color.WHITESMOKE);
        mattress.setMaterial(mattressMaterial);
        
        // Pillow 1
        Box pillow1 = new Box(width * 0.3, height * 0.05, depth * 0.2);
        pillow1.setTranslateX(-width * 0.3);
        pillow1.setTranslateY(-height * 0.575);
        pillow1.setTranslateZ(depth * 0.35);
        PhongMaterial pillowMaterial = new PhongMaterial(Color.WHITE);
        pillow1.setMaterial(pillowMaterial);
        
        // Pillow 2
        Box pillow2 = new Box(width * 0.3, height * 0.05, depth * 0.2);
        pillow2.setTranslateX(width * 0.3);
        pillow2.setTranslateY(-height * 0.575);
        pillow2.setTranslateZ(depth * 0.35);
        pillow2.setMaterial(pillowMaterial);
        
        // Blanket
        Box blanket = new Box(width * 0.9, height * 0.03, depth * 0.6);
        blanket.setTranslateY(-height * 0.565);
        blanket.setTranslateZ(-depth * 0.15);
        
        // Use furniture color for blanket, but slightly different
        Color baseColor = Color.valueOf(furniture.getColor());
        PhongMaterial blanketMaterial = new PhongMaterial(baseColor.brighter());
        blanket.setMaterial(blanketMaterial);
        
        // Headboard
        Box headboard = new Box(width, height * 0.6, depth * 0.1);
        headboard.setTranslateY(-height * 0.7);
        headboard.setTranslateZ(depth * 0.45);
        headboard.setMaterial(material);
        
        // Legs
        double legThickness = width * 0.05;
        double legHeight = height * 0.2;
        
        // Create a slightly darker material for the legs
        Color legColor = baseColor.darker();
        PhongMaterial legMaterial = new PhongMaterial(legColor);
        
        // Front-left leg
        Box frontLeftLeg = new Box(legThickness, legHeight, legThickness);
        frontLeftLeg.setTranslateX(-width * 0.45);
        frontLeftLeg.setTranslateY(-height * 0.3);
        frontLeftLeg.setTranslateZ(-depth * 0.45);
        frontLeftLeg.setMaterial(legMaterial);
        
        // Front-right leg
        Box frontRightLeg = new Box(legThickness, legHeight, legThickness);
        frontRightLeg.setTranslateX(width * 0.45);
        frontRightLeg.setTranslateY(-height * 0.3);
        frontRightLeg.setTranslateZ(-depth * 0.45);
        frontRightLeg.setMaterial(legMaterial);
        
        // Back-left leg
        Box backLeftLeg = new Box(legThickness, legHeight, legThickness);
        backLeftLeg.setTranslateX(-width * 0.45);
        backLeftLeg.setTranslateY(-height * 0.3);
        backLeftLeg.setTranslateZ(depth * 0.45);
        backLeftLeg.setMaterial(legMaterial);
        
        // Back-right leg
        Box backRightLeg = new Box(legThickness, legHeight, legThickness);
        backRightLeg.setTranslateX(width * 0.45);
        backRightLeg.setTranslateY(-height * 0.3);
        backRightLeg.setTranslateZ(depth * 0.45);
        backRightLeg.setMaterial(legMaterial);
        
        bedGroup.getChildren().addAll(
            frame, mattress, pillow1, pillow2, blanket, headboard,
            frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg
        );
        
        return bedGroup;
    }
    
    /**
     * Creates a 3D cabinet model
     */
    private static Group createCabinetModel(Furniture furniture) {
        Group cabinetGroup = new Group();
        PhongMaterial material = createMaterial(furniture);
        
        // Cabinet dimensions
        double width = furniture.getWidth();
        double height = furniture.getHeight();
        double depth = furniture.getDepth();
        
        // Main body
        Box body = new Box(width, height, depth);
        body.setTranslateY(-height * 0.5);
        body.setMaterial(material);
        
        // Doors (slightly smaller than the body)
        double doorDepth = 0.05;
        double doorHeight = height * 0.9;
        double doorWidth = width * 0.475;
        
        // Left door
        Box leftDoor = new Box(doorWidth, doorHeight, doorDepth);
        leftDoor.setTranslateX(-width * 0.24);
        leftDoor.setTranslateY(-height * 0.5);
        leftDoor.setTranslateZ(-depth * 0.5 + doorDepth/2);
        
        // Right door
        Box rightDoor = new Box(doorWidth, doorHeight, doorDepth);
        rightDoor.setTranslateX(width * 0.24);
        rightDoor.setTranslateY(-height * 0.5);
        rightDoor.setTranslateZ(-depth * 0.5 + doorDepth/2);
        
        // Use a slightly different color for the doors
        Color baseColor = Color.valueOf(furniture.getColor());
        Color doorColor = baseColor.brighter();
        PhongMaterial doorMaterial = new PhongMaterial(doorColor);
        
        // Apply texture to door material if available
        if (material.getDiffuseMap() != null) {
            doorMaterial.setDiffuseMap(material.getDiffuseMap());
        }
        
        leftDoor.setMaterial(doorMaterial);
        rightDoor.setMaterial(doorMaterial);
        
        // Handles
        double handleRadius = width * 0.02;
        double handleLength = height * 0.1;
        
        // Left handle
        Cylinder leftHandle = new Cylinder(handleRadius, handleLength);
        leftHandle.setTranslateX(-width * 0.4);
        leftHandle.setTranslateY(-height * 0.5);
        leftHandle.setTranslateZ(-depth * 0.5 - handleRadius);
        leftHandle.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // Right handle
        Cylinder rightHandle = new Cylinder(handleRadius, handleLength);
        rightHandle.setTranslateX(width * 0.4);
        rightHandle.setTranslateY(-height * 0.5);
        rightHandle.setTranslateZ(-depth * 0.5 - handleRadius);
        rightHandle.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // Use a metallic color for the handles
        PhongMaterial handleMaterial = new PhongMaterial(Color.SILVER);
        handleMaterial.setSpecularColor(Color.WHITE);
        handleMaterial.setSpecularPower(32.0);
        leftHandle.setMaterial(handleMaterial);
        rightHandle.setMaterial(handleMaterial);
        
        // Shelves (inside the cabinet - slightly visible from certain angles)
        double shelfDepth = depth * 0.9;
        double shelfHeight = 0.02;
        double shelfWidth = width * 0.95;
        
        // Middle shelf
        Box middleShelf = new Box(shelfWidth, shelfHeight, shelfDepth);
        middleShelf.setTranslateY(-height * 0.5);
        middleShelf.setMaterial(material);
        
        // Upper shelf
        Box upperShelf = new Box(shelfWidth, shelfHeight, shelfDepth);
        upperShelf.setTranslateY(-height * 0.75);
        upperShelf.setMaterial(material);
        
        // Lower shelf
        Box lowerShelf = new Box(shelfWidth, shelfHeight, shelfDepth);
        lowerShelf.setTranslateY(-height * 0.25);
        lowerShelf.setMaterial(material);
        
        cabinetGroup.getChildren().addAll(
            body, leftDoor, rightDoor, leftHandle, rightHandle,
            middleShelf, upperShelf, lowerShelf
        );
        
        return cabinetGroup;
    }
    
    /**
     * Creates a 3D desk model
     */
    private static Group createDeskModel(Furniture furniture) {
        Group deskGroup = new Group();
        PhongMaterial material = createMaterial(furniture);
        
        // Desk dimensions
        double width = furniture.getWidth();
        double height = furniture.getHeight();
        double depth = furniture.getDepth();
        
        // Desktop
        Box desktop = new Box(width, height * 0.05, depth);
        desktop.setTranslateY(-height * 0.475);
        desktop.setMaterial(material);
        
        // Left side panel
        Box leftPanel = new Box(width * 0.05, height * 0.95, depth);
        leftPanel.setTranslateX(-width * 0.45);
        leftPanel.setTranslateY(-height * 0.025);
        leftPanel.setMaterial(material);
        
        // Right side panel
        Box rightPanel = new Box(width * 0.05, height * 0.95, depth * 0.5);
        rightPanel.setTranslateX(width * 0.45);
        rightPanel.setTranslateY(-height * 0.025);
        rightPanel.setTranslateZ(-depth * 0.25);
        rightPanel.setMaterial(material);
        
        // Back panel
        Box backPanel = new Box(width, height * 0.3, depth * 0.05);
        backPanel.setTranslateY(-height * 0.35);
        backPanel.setTranslateZ(depth * 0.475);
        backPanel.setMaterial(material);
        
        // Drawers
        double drawerWidth = width * 0.3;
        double drawerHeight = height * 0.15;
        double drawerDepth = depth * 0.8;
        
        // Top drawer
        Box topDrawer = new Box(drawerWidth, drawerHeight, drawerDepth);
        topDrawer.setTranslateX(width * 0.15);
        topDrawer.setTranslateY(-height * 0.15);
        
        // Middle drawer
        Box middleDrawer = new Box(drawerWidth, drawerHeight, drawerDepth);
        middleDrawer.setTranslateX(width * 0.15);
        middleDrawer.setTranslateY(-height * 0.325);
        
        // Bottom drawer
        Box bottomDrawer = new Box(drawerWidth, drawerHeight, drawerDepth);
        bottomDrawer.setTranslateX(width * 0.15);
        bottomDrawer.setTranslateY(-height * 0.5);
        
        // Use a slightly different color for drawers
        Color baseColor = Color.valueOf(furniture.getColor());
        Color drawerColor = baseColor.brighter();
        PhongMaterial drawerMaterial = new PhongMaterial(drawerColor);
        
        // Apply texture to drawer material if available
        if (material.getDiffuseMap() != null) {
            drawerMaterial.setDiffuseMap(material.getDiffuseMap());
        }
        
        topDrawer.setMaterial(drawerMaterial);
        middleDrawer.setMaterial(drawerMaterial);
        bottomDrawer.setMaterial(drawerMaterial);
        
        // Drawer handles
        double handleWidth = width * 0.2;
        double handleHeight = height * 0.02;
        double handleDepth = depth * 0.02;
        
        // Top drawer handle
        Box topHandle = new Box(handleWidth, handleHeight, handleDepth);
        topHandle.setTranslateX(width * 0.15);
        topHandle.setTranslateY(-height * 0.15);
        topHandle.setTranslateZ(-depth * 0.4);
        
        // Middle drawer handle
        Box middleHandle = new Box(handleWidth, handleHeight, handleDepth);
        middleHandle.setTranslateX(width * 0.15);
        middleHandle.setTranslateY(-height * 0.325);
        middleHandle.setTranslateZ(-depth * 0.4);
        
        // Bottom drawer handle
        Box bottomHandle = new Box(handleWidth, handleHeight, handleDepth);
        bottomHandle.setTranslateX(width * 0.15);
        bottomHandle.setTranslateY(-height * 0.5);
        bottomHandle.setTranslateZ(-depth * 0.4);
        
        // Use a metallic color for handles
        PhongMaterial handleMaterial = new PhongMaterial(Color.SILVER);
        handleMaterial.setSpecularColor(Color.WHITE);
        handleMaterial.setSpecularPower(32.0);
        
        topHandle.setMaterial(handleMaterial);
        middleHandle.setMaterial(handleMaterial);
        bottomHandle.setMaterial(handleMaterial);
        
        deskGroup.getChildren().addAll(
            desktop, leftPanel, rightPanel, backPanel,
            topDrawer, middleDrawer, bottomDrawer,
            topHandle, middleHandle, bottomHandle
        );
        
        return deskGroup;
    }
    
    /**
     * Creates a generic 3D model (simple box) for any unspecified furniture type
     */
    private static Group createGenericModel(Furniture furniture) {
        Group group = new Group();
        
        // Create a simple box
        Box box = new Box(1, 1, 1);
        box.setMaterial(createMaterial(furniture));
        
        group.getChildren().add(box);
        return group;
    }
}

package com.furnituredesign.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage design presets for different room types with predefined furniture arrangements.
 */
public class DesignPreset {
    private String name;
    private String description;
    private String roomType;
    private Room roomTemplate;
    private List<Furniture> furnitureList;
    private String previewImagePath;
    
    public DesignPreset(String name, String roomType) {
        this.name = name;
        this.roomType = roomType;
        this.furnitureList = new ArrayList<>();
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public Room getRoomTemplate() {
        return roomTemplate;
    }
    
    public void setRoomTemplate(Room roomTemplate) {
        this.roomTemplate = roomTemplate;
    }
    
    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }
    
    public void setFurnitureList(List<Furniture> furnitureList) {
        this.furnitureList = furnitureList;
    }
    
    public void addFurniture(Furniture furniture) {
        this.furnitureList.add(furniture);
    }
    
    public String getPreviewImagePath() {
        return previewImagePath;
    }
    
    public void setPreviewImagePath(String previewImagePath) {
        this.previewImagePath = previewImagePath;
    }
    
    // Factory methods for predefined presets
    
    public static DesignPreset createLivingRoomPreset() {
        DesignPreset preset = new DesignPreset("Modern Living Room", "living_room");
        preset.setDescription("A modern living room with a sofa, coffee table, and TV stand");
        preset.setRoomTemplate(Room.createLivingRoom());
        preset.setPreviewImagePath("/images/presets/living_room.jpg");
        
        // Add a sofa
        Furniture sofa = new Furniture("sofa");
        sofa.setX(1.5);
        sofa.setY(1.0);
        sofa.setZ(0);
        sofa.setRotationY(0);
        sofa.setColor("#6B8E23");  // Olive green
        preset.addFurniture(sofa);
        
        // Add a coffee table
        Furniture coffeeTable = new Furniture("coffee_table");
        coffeeTable.setX(3.0);
        coffeeTable.setY(2.0);
        coffeeTable.setZ(0);
        coffeeTable.setMaterial(Material.wood());
        preset.addFurniture(coffeeTable);
        
        // Add a TV stand
        Furniture tvStand = new Furniture("tv_stand");
        tvStand.setX(5.0);
        tvStand.setY(1.0);
        tvStand.setZ(0);
        tvStand.setRotationY(180);
        tvStand.setMaterial(Material.wood());
        preset.addFurniture(tvStand);
        
        // Add a couple of chairs
        Furniture chair1 = new Furniture("chair");
        chair1.setX(1.0);
        chair1.setY(3.5);
        chair1.setZ(0);
        chair1.setRotationY(45);
        chair1.setColor("#556B2F");  // Darker olive
        preset.addFurniture(chair1);
        
        // Add a plant
        Furniture plant = new Furniture("plant");
        plant.setX(0.5);
        plant.setY(0.5);
        plant.setZ(0);
        preset.addFurniture(plant);
        
        // Add a rug
        Furniture rug = new Furniture("rug");
        rug.setX(3.0);
        rug.setY(2.0);
        rug.setZ(0);
        rug.setColor("#F5DEB3");  // Wheat color
        preset.addFurniture(rug);
        
        return preset;
    }
    
    public static DesignPreset createBedroomPreset() {
        DesignPreset preset = new DesignPreset("Cozy Bedroom", "bedroom");
        preset.setDescription("A cozy bedroom with a bed, nightstands, and a dresser");
        preset.setRoomTemplate(Room.createBedroom());
        preset.setPreviewImagePath("/images/presets/bedroom.jpg");
        
        // Add a bed
        Furniture bed = new Furniture("bed");
        bed.setX(2.0);
        bed.setY(3.0);
        bed.setZ(0);
        bed.setRotationY(180);
        bed.setColor("#E6E6FA");  // Lavender
        preset.addFurniture(bed);
        
        // Add nightstands
        Furniture nightstand1 = new Furniture("nightstand");
        nightstand1.setX(1.0);
        nightstand1.setY(3.5);
        nightstand1.setZ(0);
        nightstand1.setMaterial(Material.wood());
        preset.addFurniture(nightstand1);
        
        Furniture nightstand2 = new Furniture("nightstand");
        nightstand2.setX(3.0);
        nightstand2.setY(3.5);
        nightstand2.setZ(0);
        nightstand2.setMaterial(Material.wood());
        preset.addFurniture(nightstand2);
        
        // Add a dresser
        Furniture dresser = new Furniture("dresser");
        dresser.setX(1.0);
        dresser.setY(1.0);
        dresser.setZ(0);
        dresser.setMaterial(Material.wood());
        preset.addFurniture(dresser);
        
        // Add a lamp
        Furniture lamp = new Furniture("lamp");
        lamp.setX(3.0);
        lamp.setY(3.5);
        lamp.setZ(0.6);
        preset.addFurniture(lamp);
        
        return preset;
    }
    
    public static DesignPreset createOfficePreset() {
        DesignPreset preset = new DesignPreset("Home Office", "office");
        preset.setDescription("A productive home office with a desk, chair, and bookshelf");
        preset.setRoomTemplate(Room.createOffice());
        preset.setPreviewImagePath("/images/presets/office.jpg");
        
        // Add a desk
        Furniture desk = new Furniture("desk");
        desk.setX(1.8);
        desk.setY(1.0);
        desk.setZ(0);
        desk.setRotationY(180);
        desk.setMaterial(Material.wood());
        preset.addFurniture(desk);
        
        // Add a chair
        Furniture chair = new Furniture("chair");
        chair.setX(1.8);
        chair.setY(1.6);
        chair.setZ(0);
        chair.setRotationY(180);
        chair.setColor("#363636");  // Dark gray
        preset.addFurniture(chair);
        
        // Add a bookshelf
        Furniture bookshelf = new Furniture("bookshelf");
        bookshelf.setX(0.5);
        bookshelf.setY(3.0);
        bookshelf.setZ(0);
        bookshelf.setMaterial(Material.wood());
        preset.addFurniture(bookshelf);
        
        // Add a plant
        Furniture plant = new Furniture("plant");
        plant.setX(3.0);
        plant.setY(3.5);
        plant.setZ(0);
        preset.addFurniture(plant);
        
        return preset;
    }
    
    public static DesignPreset createDiningRoomPreset() {
        DesignPreset preset = new DesignPreset("Elegant Dining Room", "dining_room");
        preset.setDescription("An elegant dining room with a dining table and chairs");
        preset.setRoomTemplate(Room.createDiningRoom());
        preset.setPreviewImagePath("/images/presets/dining_room.jpg");
        
        // Add a dining table
        Furniture diningTable = new Furniture("dining_table");
        diningTable.setX(2.0);
        diningTable.setY(2.5);
        diningTable.setZ(0);
        diningTable.setMaterial(Material.wood());
        preset.addFurniture(diningTable);
        
        // Add chairs
        double[][] chairPositions = {
            {1.5, 2.0, 0},  // Left side
            {2.5, 2.0, 0},  // Right side
            {2.0, 1.8, 0},  // Bottom
            {2.0, 3.2, 0}   // Top
        };
        
        for (double[] pos : chairPositions) {
            Furniture chair = new Furniture("chair");
            chair.setX(pos[0]);
            chair.setY(pos[1]);
            chair.setZ(pos[2]);
            
            // Rotate chairs based on position
            if (pos[1] > 2.5) { // Top chairs
                chair.setRotationY(180);
            } else if (pos[0] < 2.0) { // Left chairs
                chair.setRotationY(90);
            } else if (pos[0] > 2.0) { // Right chairs
                chair.setRotationY(270);
            }
            
            chair.setColor("#8B4513");  // SaddleBrown
            preset.addFurniture(chair);
        }
        
        // Add a cabinet
        Furniture cabinet = new Furniture("cabinet");
        cabinet.setX(3.5);
        cabinet.setY(1.0);
        cabinet.setZ(0);
        cabinet.setMaterial(Material.wood());
        preset.addFurniture(cabinet);
        
        return preset;
    }
    
    public static DesignPreset createKitchenPreset() {
        DesignPreset preset = new DesignPreset("Modern Kitchen", "kitchen");
        preset.setDescription("A modern kitchen with essential appliances and furniture");
        preset.setRoomTemplate(Room.createKitchen());
        preset.setPreviewImagePath("/images/presets/kitchen.jpg");
        
        // Kitchen cabinets along the wall
        for (int i = 0; i < 3; i++) {
            Furniture cabinet = new Furniture("cabinet");
            cabinet.setX(0.6);
            cabinet.setY(1.0 + i * 1.0);
            cabinet.setZ(0);
            cabinet.setRotationY(0);
            cabinet.setWidth(0.6);
            cabinet.setLength(0.8);
            cabinet.setHeight(0.9);
            cabinet.setColor("#F5F5F5");  // White
            preset.addFurniture(cabinet);
        }
        
        // Add a counter/island in the middle
        Furniture island = new Furniture("table");
        island.setX(2.0);
        island.setY(3.0);
        island.setZ(0);
        island.setWidth(1.6);
        island.setLength(0.8);
        island.setHeight(0.9);
        island.setColor("#F5F5F5");  // White
        preset.addFurniture(island);
        
        // Add a couple of chairs/stools at the island
        for (int i = 0; i < 2; i++) {
            Furniture stool = new Furniture("chair");
            stool.setX(2.0 - 0.4 + i * 0.8);
            stool.setY(3.5);
            stool.setZ(0);
            stool.setHeight(0.75);
            stool.setWidth(0.4);
            stool.setLength(0.4);
            stool.setColor("#696969");  // DimGray
            preset.addFurniture(stool);
        }
        
        return preset;
    }
    
    // Get all available presets
    public static List<DesignPreset> getAllPresets() {
        List<DesignPreset> presets = new ArrayList<>();
        presets.add(createLivingRoomPreset());
        presets.add(createBedroomPreset());
        presets.add(createOfficePreset());
        presets.add(createDiningRoomPreset());
        presets.add(createKitchenPreset());
        return presets;
    }
} 
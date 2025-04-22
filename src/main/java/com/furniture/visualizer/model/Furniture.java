package com.furniture.visualizer.model;

import javafx.scene.paint.Color;

public class Furniture {
    private int id;
    private int designId;
    private FurnitureType type;
    private MaterialType material;
    private double width;
    private double height;
    private double depth;
    private double positionX;
    private double positionY;
    private double positionZ;
    private double rotation;      // in degrees
    private String color;
    
    public Furniture(int id, int designId, FurnitureType type, MaterialType material, double width, 
                    double height, double depth, double positionX, double positionY, 
                    double positionZ, double rotation, String color) {
        this.id = id;
        this.designId = designId;
        this.type = type;
        this.material = material;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.rotation = rotation;
        this.color = color;
    }
    
    // Constructor that takes String for type and material (for backward compatibility)
    public Furniture(int id, int designId, String typeStr, String materialStr, double width, 
                    double height, double depth, double positionX, double positionY, 
                    double positionZ, double rotation, String color) {
        this(id, designId, 
             FurnitureType.fromString(typeStr), 
             MaterialType.fromString(materialStr), 
             width, height, depth, positionX, positionY, positionZ, rotation, color);
    }
    
    // Default constructor
    public Furniture() {
        this(0, 0, FurnitureType.CHAIR, MaterialType.WOOD, 0, 0, 0, 0, 0, 0, 0, Color.WHITE.toString());
    }
    
    // Factory method to create furniture with default dimensions based on type
    public static Furniture createDefault(FurnitureType type, int designId) {
        Furniture furniture = new Furniture();
        furniture.setDesignId(designId);
        furniture.setType(type);
        furniture.setMaterial(MaterialType.WOOD);
        furniture.setWidth(type.getDefaultWidth());
        furniture.setHeight(type.getDefaultHeight());
        furniture.setDepth(type.getDefaultDepth());
        furniture.setColor(Color.BURLYWOOD.toString());
        return furniture;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getDesignId() {
        return designId;
    }
    
    public void setDesignId(int designId) {
        this.designId = designId;
    }
    
    public FurnitureType getType() {
        return type;
    }
    
    // For backward compatibility
    public String getTypeString() {
        return type.getDisplayName();
    }
    
    public void setType(FurnitureType type) {
        this.type = type;
    }
    
    // For backward compatibility
    public void setType(String typeStr) {
        this.type = FurnitureType.fromString(typeStr);
    }
    
    public MaterialType getMaterial() {
        return material;
    }
    
    // For backward compatibility
    public String getMaterialString() {
        return material.getDisplayName();
    }
    
    public void setMaterial(MaterialType material) {
        this.material = material;
    }
    
    // For backward compatibility
    public void setMaterial(String materialStr) {
        this.material = MaterialType.fromString(materialStr);
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getDepth() {
        return depth;
    }
    
    public void setDepth(double depth) {
        this.depth = depth;
    }
    
    public double getPositionX() {
        return positionX;
    }
    
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }
    
    public double getPositionY() {
        return positionY;
    }
    
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }
    
    public double getPositionZ() {
        return positionZ;
    }
    
    public void setPositionZ(double positionZ) {
        this.positionZ = positionZ;
    }
    
    public double getRotation() {
        return rotation;
    }
    
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return type.getDisplayName() + " (" + material.getDisplayName() + ")";
    }
}

package com.furniture.visualizer.model;

public class FurnitureDesign {
    private int id;
    private String name;
    private String description;
    private String furnitureType;
    private double width;
    private double height;
    private double depth;
    private String color;
    private String material;
    
    public FurnitureDesign() {
        this(0, "", "", "", 0, 0, 0, "", "");
    }
    
    public FurnitureDesign(int id, String name, String description, String furnitureType,
                          double width, double height, double depth, String color, String material) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.furnitureType = furnitureType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.color = color;
        this.material = material;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
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
    
    public String getFurnitureType() {
        return furnitureType;
    }
    
    public void setFurnitureType(String furnitureType) {
        this.furnitureType = furnitureType;
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
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    @Override
    public String toString() {
        return name + " (" + furnitureType + ")";
    }
} 
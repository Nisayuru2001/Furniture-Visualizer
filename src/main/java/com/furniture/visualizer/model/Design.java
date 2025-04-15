package com.furniture.visualizer.model;

public class Design {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String furnitureType;
    private double width;
    private double height;
    private double depth;
    private String color;
    private String material;

    public Design(String id, String userId, String name, String description, String furnitureType,
                 double width, double height, double depth, String color, String material) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.furnitureType = furnitureType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.color = color;
        this.material = material;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFurnitureType() { return furnitureType; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getDepth() { return depth; }
    public String getColor() { return color; }
    public String getMaterial() { return material; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setFurnitureType(String furnitureType) { this.furnitureType = furnitureType; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }
    public void setDepth(double depth) { this.depth = depth; }
    public void setColor(String color) { this.color = color; }
    public void setMaterial(String material) { this.material = material; }
} 
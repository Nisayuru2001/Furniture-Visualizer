package com.furnituredesign.models;

/**
 * Enhanced Furniture model with advanced features including materials,
 * 3D model references, and transformation properties.
 */
public class Furniture {
    private String type;
    private double x;
    private double y;
    private double z;
    private double width;
    private double length;
    private double height;
    private String color;
    private Material material;
    private String modelPath;
    private double rotationX;
    private double rotationY;
    private double rotationZ;
    private double scale;
    private double rotation = 0; // Default rotation angle in degrees
    
    public Furniture(String type) {
        this.type = type;
        this.x = 0;
        this.y = 0;
        this.z = 0;


        // Set default material based on type
        switch (type.toLowerCase()) {
            case "chair":
                this.material = Material.fabric();
                break;
            case "table":
                this.material = Material.wood();
                break;
            case "sofa":
                this.material = Material.fabric();
                break;
            case "bed":
                this.material = Material.fabric();
                break;
            case "cabinet":
                this.material = Material.wood();
                break;
            case "bookshelf":
                this.material = Material.wood();
                break;
            case "desk":
                this.material = Material.wood();
                break;
            case "coffee_table":
                this.material = Material.wood();
                break;
            case "dining_table":
                this.material = Material.wood();
                break;
            case "tv_stand":
                this.material = Material.wood();
                break;
            case "lamp":
                this.material = Material.metal();
                break;
            case "wardrobe":
                this.material = Material.wood();
                break;
            case "dresser":
                this.material = Material.wood();
                break;
            case "nightstand":
                this.material = Material.wood();
                break;
            case "rug":
                this.material = Material.fabric();
                break;
            case "plant":
                this.material = new Material("Plant", "#228B22");
                break;
            default:
                this.material = new Material(type, color);
        }

        // Set default dimension based on furniture type
        switch (type.toLowerCase()) {
            case "chair":
                this.width = 0.5;
                this.length = 0.5;
                this.height = 1.0;
                this.modelPath = "models/chair.obj";
                break;
            case "table":
                this.width = 1.2;
                this.length = 0.8;
                this.height = 0.75;
                this.modelPath = "models/table.obj";
                break;
            case "sofa":
                this.width = 2.0;
                this.length = 0.8;
                this.height = 0.9;
                this.modelPath = "models/sofa.obj";
                break;
            case "bed":
                this.width = 2.0;
                this.length = 1.6;
                this.height = 0.5;
                this.modelPath = "models/bed.obj";
                break;
            case "cabinet":
                this.width = 1.0;
                this.length = 0.6;
                this.height = 1.8;
                this.modelPath = "models/cabinet.obj";
                break;
            case "bookshelf":
                this.width = 1.0;
                this.length = 0.4;
                this.height = 2.0;
                this.modelPath = "models/bookshelf.obj";
                break;
            case "desk":
                this.width = 1.4;
                this.length = 0.7;
                this.height = 0.75;
                this.modelPath = "models/desk.obj";
                break;
            case "coffee_table":
                this.width = 0.9;
                this.length = 0.6;
                this.height = 0.45;
                this.modelPath = "models/coffee_table.obj";
                break;
            case "dining_table":
                this.width = 1.8;
                this.length = 1.0;
                this.height = 0.75;
                this.modelPath = "models/dining_table.obj";
                break;
            case "tv_stand":
                this.width = 1.6;
                this.length = 0.5;
                this.height = 0.6;
                this.modelPath = "models/tv_stand.obj";
                break;
            case "lamp":
                this.width = 0.3;
                this.length = 0.3;
                this.height = 1.5;
                this.modelPath = "models/lamp.obj";
                break;
            case "wardrobe":
                this.width = 1.2;
                this.length = 0.6;
                this.height = 2.0;
                this.modelPath = "models/wardrobe.obj";
                break;
            case "dresser":
                this.width = 1.2;
                this.length = 0.5;
                this.height = 0.8;
                this.modelPath = "models/dresser.obj";
                break;
            case "nightstand":
                this.width = 0.5;
                this.length = 0.4;
                this.height = 0.6;
                this.modelPath = "models/nightstand.obj";
                break;
            case "rug":
                this.width = 2.0;
                this.length = 3.0;
                this.height = 0.02;
                this.modelPath = "models/rug.obj";
                break;
            case "plant":
                this.width = 0.5;
                this.length = 0.5;
                this.height = 1.2;
                this.modelPath = "models/plant.obj";
                break;
            default:
                this.width = 1.0;
                this.length = 1.0;
                this.height = 1.0;
                this.modelPath = "models/cube.obj";
        }
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        if (material != null) {
            material.setBaseColor(color);
        }
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
        if (material != null) {
            this.color = material.getBaseColor();
        }
    }
    
    public String getModelPath() {
        return modelPath;
    }
    
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }
    
    public double getRotationX() {
        return rotationX;
    }
    
    public void setRotationX(double rotationX) {
        this.rotationX = rotationX;
    }
    
    public double getRotationY() {
        return rotationY;
    }
    
    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
    }
    
    public double getRotationZ() {
        return rotationZ;
    }
    
    public void setRotationZ(double rotationZ) {
        this.rotationZ = rotationZ;
    }
    
    public double getScale() {
        return scale;
    }
    
    public void setScale(double scale) {
        this.scale = scale;
    }

    public Furniture duplicate() {
        Furniture copy = new Furniture(this.type);
        copy.x = this.x + 0.3;  // Offset slightly to make it visible
        copy.y = this.y + 0.3;
        copy.z = this.z;
        copy.width = this.width;
        copy.length = this.length;
        copy.height = this.height;
        copy.color = this.color;
        copy.material = new Material(this.material);
        copy.modelPath = this.modelPath;
        copy.rotationX = this.rotationX;
        copy.rotationY = this.rotationY;
        copy.rotationZ = this.rotationZ;
        copy.scale = this.scale;
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2fm x %.2fm x %.2fm)", type, width, length, height);
    }

    // Getters and setters for rotation
    public double getRotation() {
        return rotation;
    }
    
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
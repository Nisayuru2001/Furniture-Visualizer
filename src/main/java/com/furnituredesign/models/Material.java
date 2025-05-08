package com.furnituredesign.models;

/**
 * Represents material properties for furniture and room surfaces
 * with advanced rendering capabilities.
 */
public class Material {
    private String name;
    private String baseColor;
    private double roughness;
    private double metallic;
    private String bumpMapPath;
    private String textureMapPath;
    private double reflectivity;
    private double opacity;
    
    public Material(String name, String baseColor) {
        this.name = name;
        this.baseColor = baseColor;
        this.roughness = 0.5;
        this.metallic = 0.0;
        this.reflectivity = 0.0;
        this.opacity = 1.0;
    }
    
    // Copy constructor
    public Material(Material other) {
        this.name = other.name;
        this.baseColor = other.baseColor;
        this.roughness = other.roughness;
        this.metallic = other.metallic;
        this.bumpMapPath = other.bumpMapPath;
        this.textureMapPath = other.textureMapPath;
        this.reflectivity = other.reflectivity;
        this.opacity = other.opacity;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBaseColor() {
        return baseColor;
    }
    
    public void setBaseColor(String baseColor) {
        this.baseColor = baseColor;
    }
    
    public double getRoughness() {
        return roughness;
    }
    
    public void setRoughness(double roughness) {
        this.roughness = Math.max(0.0, Math.min(1.0, roughness));
    }
    
    public double getMetallic() {
        return metallic;
    }
    
    public void setMetallic(double metallic) {
        this.metallic = Math.max(0.0, Math.min(1.0, metallic));
    }
    
    public String getBumpMapPath() {
        return bumpMapPath;
    }
    
    public void setBumpMapPath(String bumpMapPath) {
        this.bumpMapPath = bumpMapPath;
    }
    
    public String getTextureMapPath() {
        return textureMapPath;
    }
    
    public void setTextureMapPath(String textureMapPath) {
        this.textureMapPath = textureMapPath;
    }
    
    public double getReflectivity() {
        return reflectivity;
    }
    
    public void setReflectivity(double reflectivity) {
        this.reflectivity = Math.max(0.0, Math.min(1.0, reflectivity));
    }
    
    public double getOpacity() {
        return opacity;
    }
    
    public void setOpacity(double opacity) {
        this.opacity = Math.max(0.0, Math.min(1.0, opacity));
    }
    
    // Predefined materials
    public static Material wood() {
        Material wood = new Material("Wood", "#8B4513");
        wood.setRoughness(0.7);
        wood.setMetallic(0.0);
        wood.setReflectivity(0.1);
        return wood;
    }
    
    public static Material metal() {
        Material metal = new Material("Metal", "#C0C0C0");
        metal.setRoughness(0.2);
        metal.setMetallic(0.9);
        metal.setReflectivity(0.8);
        return metal;
    }
    
    public static Material fabric() {
        Material fabric = new Material("Fabric", "#6495ED");
        fabric.setRoughness(0.9);
        fabric.setMetallic(0.0);
        fabric.setReflectivity(0.05);
        return fabric;
    }
    
    public static Material glass() {
        Material glass = new Material("Glass", "#ADD8E6");
        glass.setRoughness(0.1);
        glass.setMetallic(0.0);
        glass.setReflectivity(0.9);
        glass.setOpacity(0.7);
        return glass;
    }
    
    public static Material leather() {
        Material leather = new Material("Leather", "#8B4513");
        leather.setRoughness(0.6);
        leather.setMetallic(0.0);
        leather.setReflectivity(0.2);
        return leather;
    }
    
    public static Material marble() {
        Material marble = new Material("Marble", "#F5F5F5");
        marble.setRoughness(0.3);
        marble.setMetallic(0.0);
        marble.setReflectivity(0.5);
        return marble;
    }
} 
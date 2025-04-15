package com.furniture.visualizer.model;

public enum MaterialType {
    WOOD("Wood", "wood.jpg", false, 0.3),
    METAL("Metal", "metal.jpg", true, 0.8),
    PLASTIC("Plastic", "plastic.jpg", true, 0.5),
    GLASS("Glass", "glass.jpg", true, 0.95),
    FABRIC("Fabric", "fabric.jpg", false, 0.1);
    
    private final String displayName;
    private final String textureFile;
    private final boolean reflective;
    private final double reflectivity;
    
    MaterialType(String displayName, String textureFile, boolean reflective, double reflectivity) {
        this.displayName = displayName;
        this.textureFile = textureFile;
        this.reflective = reflective;
        this.reflectivity = reflectivity;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getTextureFile() {
        return textureFile;
    }
    
    public boolean isReflective() {
        return reflective;
    }
    
    public double getReflectivity() {
        return reflectivity;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    public static MaterialType fromString(String text) {
        for (MaterialType type : MaterialType.values()) {
            if (type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return WOOD; // Default
    }
}

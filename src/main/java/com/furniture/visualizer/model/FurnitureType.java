package com.furniture.visualizer.model;

public enum FurnitureType {
    CHAIR("Chair", 0.6, 0.9, 0.6),
    TABLE("Table", 1.2, 0.75, 0.8),
    SOFA("Sofa", 2.0, 0.85, 0.9),
    BED("Bed", 2.0, 0.5, 1.8),
    CABINET("Cabinet", 0.8, 1.8, 0.5),
    DESK("Desk", 1.4, 0.75, 0.7);
    
    private final String displayName;
    private final double defaultWidth;
    private final double defaultHeight;
    private final double defaultDepth;
    
    FurnitureType(String displayName, double defaultWidth, double defaultHeight, double defaultDepth) {
        this.displayName = displayName;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.defaultDepth = defaultDepth;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getDefaultWidth() {
        return defaultWidth;
    }
    
    public double getDefaultHeight() {
        return defaultHeight;
    }
    
    public double getDefaultDepth() {
        return defaultDepth;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    public static FurnitureType fromString(String text) {
        for (FurnitureType type : FurnitureType.values()) {
            if (type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return CHAIR; // Default
    }
}

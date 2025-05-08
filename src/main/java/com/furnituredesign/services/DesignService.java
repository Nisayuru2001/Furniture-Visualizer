package com.furnituredesign.services;

import com.furnituredesign.models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced service for managing room designs with advanced features
 * including presets, export/import, and 3D model handling.
 */
public class DesignService {
    private static final String DESIGNS_DIR = "saved_designs";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public DesignService() {
        // Create the designs directory if it doesn't exist
        File dir = new File(DESIGNS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    /**
     * Represents a complete design with room and furniture
     */
    public static class Design {
        private String name;
        private Room room;
        private List<Furniture> furniture;
        private String createdAt;
        private String lastModified;
        private String author;
        private String description;
        private String thumbnail;
        
        public Design() {
            this.name = "New Design";
            this.room = new Room();
            this.furniture = new ArrayList<>();
            this.createdAt = java.time.LocalDateTime.now().toString();
            this.lastModified = this.createdAt;
        }
        
        public Design(String name, Room room, List<Furniture> furniture) {
            this();
            this.name = name;
            this.room = room;
            this.furniture = furniture;
        }
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Room getRoom() {
            return room;
        }
        
        public void setRoom(Room room) {
            this.room = room;
        }
        
        public List<Furniture> getFurniture() {
            return furniture;
        }
        
        public void setFurniture(List<Furniture> furniture) {
            this.furniture = furniture;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
        
        public String getLastModified() {
            return lastModified;
        }
        
        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getThumbnail() {
            return thumbnail;
        }
        
        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
        
        public void updateLastModified() {
            this.lastModified = java.time.LocalDateTime.now().toString();
        }
    }
    
    /**
     * Save a design to a file
     */
    public void saveDesign(Design design, String filename) throws IOException {
        design.updateLastModified();
        
        String designJson = gson.toJson(design);
        try (FileWriter writer = new FileWriter(DESIGNS_DIR + File.separator + filename + ".json")) {
            writer.write(designJson);
        }
    }
    
    /**
     * Load a design from a file
     */
    public Design loadDesign(String filename) throws IOException {
        try (FileReader reader = new FileReader(DESIGNS_DIR + File.separator + filename + ".json")) {
            return gson.fromJson(reader, Design.class);
        }
    }
    
    /**
     * Get a list of saved designs
     */
    public List<String> getSavedDesigns() {
        File dir = new File(DESIGNS_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return new ArrayList<>();
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) {
            return new ArrayList<>();
        }
        
        List<String> designNames = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName();
            designNames.add(filename.substring(0, filename.lastIndexOf(".")));
        }
        
        return designNames;
    }
    
    /**
     * Get list of all design metadata (for gallery view)
     */
    public List<DesignMetadata> getDesignGallery() {
        List<DesignMetadata> metadataList = new ArrayList<>();
        List<String> designNames = getSavedDesigns();
        
        for (String name : designNames) {
            try {
                Design design = loadDesign(name);
                DesignMetadata metadata = new DesignMetadata();
                metadata.setName(design.getName());
                metadata.setFilename(name);
                metadata.setLastModified(design.getLastModified());
                metadata.setCreatedAt(design.getCreatedAt());
                metadata.setAuthor(design.getAuthor());
                metadata.setDescription(design.getDescription());
                metadata.setThumbnail(design.getThumbnail());
                metadata.setRoomType(design.getRoom().getRoomType());
                metadataList.add(metadata);
            } catch (IOException e) {
                System.err.println("Error loading design: " + name);
            }
        }
        
        return metadataList;
    }
    
    /**
     * Design metadata for gallery listing
     */
    public static class DesignMetadata {
        private String name;
        private String filename;
        private String lastModified;
        private String createdAt;
        private String author;
        private String description;
        private String thumbnail;
        private String roomType;
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public void setFilename(String filename) {
            this.filename = filename;
        }
        
        public String getLastModified() {
            return lastModified;
        }
        
        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getThumbnail() {
            return thumbnail;
        }
        
        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
        
        public String getRoomType() {
            return roomType;
        }
        
        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }
    }
    
    /**
     * Export design to OBJ format for external applications
     */
    public void exportToOBJ(Design design, String filename) throws IOException {
        File outputFile = new File(filename + ".obj");
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("# Furniture Designer export\n");
            writer.write("# Design: " + design.getName() + "\n");
            writer.write("# Date: " + java.time.LocalDateTime.now() + "\n\n");
            
            // Write room geometry
            Room room = design.getRoom();
            writer.write("# Room dimensions: " + room.getWidth() + "x" + room.getLength() + "x" + room.getHeight() + "\n");
            
            // Add walls, floor, ceiling vertices
            
            // Add furniture
            for (Furniture furniture : design.getFurniture()) {
                writer.write("# Furniture: " + furniture.getType() + "\n");
                writer.write("# Position: " + furniture.getX() + ", " + furniture.getY() + ", " + furniture.getZ() + "\n");
                
                // Add include statement for furniture model if available
                String modelPath = furniture.getModelPath();
                if (modelPath != null && !modelPath.isEmpty()) {
                    writer.write("mtllib models/" + furniture.getType() + ".mtl\n");
                    writer.write("o " + furniture.getType() + "\n");
                    
                    // Simple placeholder geometry rather than actual model data
                    writer.write("# Placeholder for actual model data\n");
                    writer.write("# This would reference: " + modelPath + "\n");
                }
            }
        }
    }
    
    /**
     * Create a design from a preset
     */
    public Design createFromPreset(DesignPreset preset) {
        Design design = new Design();
        design.setName(preset.getName());
        design.setDescription(preset.getDescription());
        design.setRoom(preset.getRoomTemplate());
        
        // Deep copy furniture list from preset
        List<Furniture> furnitureCopy = preset.getFurnitureList().stream()
                .map(Furniture::duplicate)
                .collect(Collectors.toList());
                
        design.setFurniture(furnitureCopy);
        
        return design;
    }
    
    /**
     * Calculate estimated cost of the design based on furniture
     */
    public double calculateEstimatedCost(Design design) {
        double totalCost = 0.0;
        
        // Base cost per room type
        switch (design.getRoom().getRoomType()) {
            case "living_room":
                totalCost += 2000.0;
                break;
            case "bedroom":
                totalCost += 1500.0;
                break;
            case "kitchen":
                totalCost += 5000.0;
                break;
            case "office":
                totalCost += 1200.0;
                break;
            case "dining_room":
                totalCost += 1800.0;
                break;
            default:
                totalCost += 1000.0;
        }
        
        // Add cost for each furniture piece
        for (Furniture furniture : design.getFurniture()) {
            switch (furniture.getType().toLowerCase()) {
                case "sofa":
                    totalCost += 800.0;
                    break;
                case "chair":
                    totalCost += 150.0;
                    break;
                case "table":
                    totalCost += 300.0;
                    break;
                case "bed":
                    totalCost += 1200.0;
                    break;
                case "cabinet":
                    totalCost += 500.0;
                    break;
                case "bookshelf":
                    totalCost += 400.0;
                    break;
                case "desk":
                    totalCost += 350.0;
                    break;
                case "coffee_table":
                    totalCost += 250.0;
                    break;
                case "dining_table":
                    totalCost += 600.0;
                    break;
                case "tv_stand":
                    totalCost += 300.0;
                    break;
                case "lamp":
                    totalCost += 120.0;
                    break;
                case "wardrobe":
                    totalCost += 700.0;
                    break;
                case "dresser":
                    totalCost += 450.0;
                    break;
                case "nightstand":
                    totalCost += 180.0;
                    break;
                case "rug":
                    totalCost += 200.0;
                    break;
                case "plant":
                    totalCost += 75.0;
                    break;
                default:
                    totalCost += 100.0;
            }
            
            // Add cost for premium materials
            Material material = furniture.getMaterial();
            if (material != null) {
                if (material.getName().equals("Wood") && material.getReflectivity() > 0.3) {
                    totalCost += 100.0; // Premium wood
                } else if (material.getName().equals("Metal") && material.getMetallic() > 0.7) {
                    totalCost += 150.0; // Premium metal
                } else if (material.getName().equals("Fabric") && material.getRoughness() < 0.3) {
                    totalCost += 120.0; // Premium fabric
                } else if (material.getName().equals("Marble")) {
                    totalCost += 200.0; // Marble is expensive
                } else if (material.getName().equals("Glass") && material.getOpacity() < 0.5) {
                    totalCost += 180.0; // Premium glass
                }
            }
        }
        
        return totalCost;
    }
}

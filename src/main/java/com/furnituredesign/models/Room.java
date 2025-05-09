package com.furnituredesign.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Room model with advanced features including
 * lighting, textures, and room templates.
 */
public class Room {
    private double width;
    private double length;
    private double height;
    private String floorColor;
    private String wallColor;
    private String ceilingColor;
    private Material floorMaterial;
    private Material wallMaterial;
    private Material ceilingMaterial;
    private String floorTexture;
    private String wallTexture;
    private String ceilingTexture;
    private List<Light> lights;
    private String name;
    private String roomType;
    private Map<String, Double> ambientSettings;
    
    public static class Light {
        private double x;
        private double y;
        private double z;
        private String color;
        private double intensity;
        private String type; // "point", "spot", "ambient"
        private double radius;
        
        public Light(double x, double y, double z, String color, double intensity, String type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
            this.intensity = intensity;
            this.type = type;
            this.radius = 0.2;
        }
        
        // Getters and setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        
        public double getZ() { return z; }
        public void setZ(double z) { this.z = z; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public double getIntensity() { return intensity; }
        public void setIntensity(double intensity) { this.intensity = intensity; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public double getRadius() { return radius; }
        public void setRadius(double radius) { this.radius = radius; }
    }

    public Room() {
        this.width = 5.0;
        this.length = 6.0;
        this.height = 2.8;
        this.floorColor = "#CCCCCC";
        this.wallColor = "#FFFFFF";
        this.ceilingColor = "#F8F8F8";
        this.floorMaterial = Material.wood();
        this.wallMaterial = new Material("Wall", wallColor);
        this.ceilingMaterial = new Material("Ceiling", ceilingColor);
        this.name = "New Room";
        this.roomType = "living_room";
        this.lights = new ArrayList<>();
        this.ambientSettings = new HashMap<>();
        
        // Default ambient settings
        ambientSettings.put("globalLightIntensity", 0.7);
        ambientSettings.put("ambientOcclusion", 0.2);
        ambientSettings.put("shadowIntensity", 0.5);
        
        // Add default ceiling light
        Light ceilingLight = new Light(width/2, length/2, height-0.2, "#FFFFFF", 1.0, "point");
        lights.add(ceilingLight);
    }

    public Room(double width, double length, double height) {
        this();
        this.width = width;
        this.length = length;
        this.height = height;
        
        // Reposition ceiling light when room dimensions change
        if (!lights.isEmpty()) {
            Light mainLight = lights.get(0);
            mainLight.setX(width/2);
            mainLight.setY(length/2);
            mainLight.setZ(height-0.2);
        }
    }

    // Getters and setters
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

    public String getFloorColor() {
        return floorColor;
    }

    public void setFloorColor(String floorColor) {
        this.floorColor = floorColor;
        if (floorMaterial != null) {
            floorMaterial.setBaseColor(floorColor);
        }
    }

    public String getWallColor() {
        return wallColor;
    }

    public void setWallColor(String wallColor) {
        this.wallColor = wallColor;
        if (wallMaterial != null) {
            wallMaterial.setBaseColor(wallColor);
        }
    }

    public String getCeilingColor() {
        return ceilingColor;
    }

    public void setCeilingColor(String ceilingColor) {
        this.ceilingColor = ceilingColor;
        if (ceilingMaterial != null) {
            ceilingMaterial.setBaseColor(ceilingColor);
        }
    }
    
    public Material getFloorMaterial() {
        return floorMaterial;
    }
    
    public void setFloorMaterial(Material floorMaterial) {
        this.floorMaterial = floorMaterial;
        if (floorMaterial != null) {
            this.floorColor = floorMaterial.getBaseColor();
        }
    }
    
    public Material getWallMaterial() {
        return wallMaterial;
    }
    
    public void setWallMaterial(Material wallMaterial) {
        this.wallMaterial = wallMaterial;
        if (wallMaterial != null) {
            this.wallColor = wallMaterial.getBaseColor();
        }
    }
    
    public Material getCeilingMaterial() {
        return ceilingMaterial;
    }
    
    public void setCeilingMaterial(Material ceilingMaterial) {
        this.ceilingMaterial = ceilingMaterial;
        if (ceilingMaterial != null) {
            this.ceilingColor = ceilingMaterial.getBaseColor();
        }
    }
    
    public String getFloorTexture() {
        return floorTexture;
    }
    
    public void setFloorTexture(String floorTexture) {
        this.floorTexture = floorTexture;
    }
    
    public String getWallTexture() {
        return wallTexture;
    }
    
    public void setWallTexture(String wallTexture) {
        this.wallTexture = wallTexture;
    }
    
    public String getCeilingTexture() {
        return ceilingTexture;
    }
    
    public void setCeilingTexture(String ceilingTexture) {
        this.ceilingTexture = ceilingTexture;
    }
    
    public List<Light> getLights() {
        return lights;
    }
    
    public void setLights(List<Light> lights) {
        this.lights = lights;
    }
    
    public void addLight(Light light) {
        this.lights.add(light);
    }
    
    public void removeLight(Light light) {
        this.lights.remove(light);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public Map<String, Double> getAmbientSettings() {
        return ambientSettings;
    }
    
    public void setAmbientSettings(Map<String, Double> ambientSettings) {
        this.ambientSettings = ambientSettings;
    }
    
    public double getAmbientSetting(String key) {
        return ambientSettings.getOrDefault(key, 0.5);
    }
    
    public void setAmbientSetting(String key, double value) {
        ambientSettings.put(key, value);
    }
    
    // Create preset room templates
    public static Room createLivingRoom() {
        Room room = new Room(6.0, 8.0, 2.8);
        room.setName("Living Room");
        room.setRoomType("living_room");
        room.setFloorMaterial(Material.wood());
        room.setWallColor("#F5F5F5");
        
        // Add ambient light
        Light ambientLight = new Light(3.0, 4.0, 2.5, "#FFF5E0", 0.7, "ambient");
        room.addLight(ambientLight);
        
        // Add corner lamp
        Light cornerLight = new Light(1.0, 1.0, 1.6, "#FFE0C0", 0.8, "point");
        room.addLight(cornerLight);
        
        return room;
    }
    
    public static Room createBedroom() {
        Room room = new Room(4.0, 5.0, 2.8);
        room.setName("Bedroom");
        room.setRoomType("bedroom");
        room.setWallColor("#E6E6FA");
        room.setFloorMaterial(Material.wood());
        
        // Add soft ambient light
        Light ambientLight = new Light(2.0, 2.5, 2.5, "#F0F8FF", 0.6, "ambient");
        room.addLight(ambientLight);
        
        // Add bedside lamp
        Light bedsideLight = new Light(3.5, 1.0, 0.8, "#FFF0F0", 0.5, "point");
        bedsideLight.setRadius(0.15);
        room.addLight(bedsideLight);
        
        return room;
    }
    
    public static Room createKitchen() {
        Room room = new Room(4.0, 6.0, 2.8);
        room.setName("Kitchen");
        room.setRoomType("kitchen");
        room.setFloorMaterial(Material.marble());
        room.setWallColor("#F0FFFF");
        
        // Add bright ceiling lights
        Light mainLight = new Light(2.0, 3.0, 2.6, "#FFFFFF", 1.0, "point");
        room.addLight(mainLight);
        
        Light counterLight = new Light(3.5, 1.5, 2.6, "#FFFFFF", 0.9, "point");
        room.addLight(counterLight);
        
        return room;
    }
    
    public static Room createOffice() {
        Room room = new Room(3.5, 4.0, 2.8);
        room.setName("Home Office");
        room.setRoomType("office");
        room.setFloorMaterial(Material.wood());
        room.setWallColor("#EDF5E1");
        
        // Add desk lamp
        Light deskLight = new Light(1.8, 1.0, 1.0, "#F5F5DC", 0.8, "spot");
        room.addLight(deskLight);
        
        // Add ceiling light
        Light ceilingLight = new Light(1.75, 2.0, 2.6, "#F8F8FF", 0.9, "point");
        room.addLight(ceilingLight);
        
        return room;
    }
    

}
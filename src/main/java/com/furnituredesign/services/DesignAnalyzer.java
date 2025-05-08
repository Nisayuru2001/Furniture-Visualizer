package com.furnituredesign.services;

import com.furnituredesign.models.Furniture;
import com.furnituredesign.models.Room;
import com.furnituredesign.services.DesignService.Design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for analyzing furniture designs and providing recommendations
 */
public class DesignAnalyzer {

    /**
     * Analyzes a design and returns a list of recommendations
     * @param design The design to analyze
     * @return List of recommendations
     */
    public List<Recommendation> analyzeDesign(Design design) {
        List<Recommendation> recommendations = new ArrayList<>();
        
        // Check room dimensions
        checkRoomDimensions(design, recommendations);
        
        // Check furniture placement
        checkFurniturePlacement(design, recommendations);
        
        // Check furniture density
        checkFurnitureDensity(design, recommendations);
        
        // Check color harmony
        checkColorHarmony(design, recommendations);
        
        // Check traffic flow
        checkTrafficFlow(design, recommendations);
        
        // Check lighting
        checkLighting(design, recommendations);
        
        return recommendations;
    }
    
    /**
     * Checks if the room dimensions are appropriate
     */
    private void checkRoomDimensions(Design design, List<Recommendation> recommendations) {
        Room room = design.getRoom();
        double area = room.getWidth() * room.getLength();
        
        // Get count of major furniture pieces
        long majorPieceCount = design.getFurniture().stream()
                .filter(f -> isMajorFurniture(f.getType()))
                .count();
        
        // Check if room is too small for furniture
        if (area < 10 && majorPieceCount > 2) {
            recommendations.add(new Recommendation(
                RecommendationType.ROOM_SIZE,
                "The room may be too small for the amount of furniture. Consider increasing room size or reducing furniture."
            ));
        }
        
        // Check ceiling height
        if (room.getHeight() < 2.4) {
            recommendations.add(new Recommendation(
                RecommendationType.ROOM_SIZE,
                "The ceiling height is below standard (2.4m). Consider increasing the height for better proportions."
            ));
        }
        
        // Check room proportions
        double ratio = Math.max(room.getWidth(), room.getLength()) / Math.min(room.getWidth(), room.getLength());
        if (ratio > 3.0) {
            recommendations.add(new Recommendation(
                RecommendationType.ROOM_PROPORTION,
                "The room has an unusual proportion (ratio > 3:1). Consider a more balanced width to length ratio."
            ));
        }
    }
    
    /**
     * Checks if furniture is placed properly within the room
     */
    private void checkFurniturePlacement(Design design, List<Recommendation> recommendations) {
        Room room = design.getRoom();
        double roomWidth = room.getWidth();
        double roomLength = room.getLength();
        
        for (Furniture furniture : design.getFurniture()) {
            // Check if furniture is within room bounds
            if (isOutOfBounds(furniture, roomWidth, roomLength)) {
                recommendations.add(new Recommendation(
                    RecommendationType.FURNITURE_PLACEMENT,
                    String.format("The %s is placed outside the room boundaries. Adjust its position.",
                            furniture.getType())
                ));
            }
            
            // Check if furniture is too close to walls
            if (isTooCloseToWall(furniture, roomWidth, roomLength)) {
                recommendations.add(new Recommendation(
                    RecommendationType.FURNITURE_PLACEMENT,
                    String.format("The %s is placed too close to the wall. Consider moving it at least 0.3m from walls.",
                            furniture.getType())
                ));
            }
            
            // Check for potential furniture overlaps
            for (Furniture other : design.getFurniture()) {
                if (furniture != other && furnitureOverlap(furniture, other)) {
                    recommendations.add(new Recommendation(
                        RecommendationType.FURNITURE_COLLISION,
                        String.format("The %s overlaps with a %s. Adjust their positions.",
                                furniture.getType(), other.getType())
                    ));
                    break;  // Only report once per furniture
                }
            }
        }
    }
    
    /**
     * Checks if the furniture density is appropriate for the room
     */
    private void checkFurnitureDensity(Design design, List<Recommendation> recommendations) {
        Room room = design.getRoom();
        double roomArea = room.getWidth() * room.getLength();
        
        // Calculate total furniture footprint
        double totalFurnitureArea = design.getFurniture().stream()
                .mapToDouble(f -> f.getWidth() * f.getLength())
                .sum();
        
        // Calculate furniture density
        double density = totalFurnitureArea / roomArea;
        
        if (density > 0.5) {
            recommendations.add(new Recommendation(
                RecommendationType.FURNITURE_DENSITY,
                "The room has too much furniture (over 50% of floor space). Consider removing some pieces for better flow."
            ));
        } else if (density < 0.2 && design.getFurniture().size() > 3) {
            recommendations.add(new Recommendation(
                RecommendationType.FURNITURE_DENSITY,
                "The furniture seems sparse relative to room size. Consider adding more pieces or rearranging existing ones."
            ));
        }
    }
    
    /**
     * Analyzes color harmony in the design
     */
    private void checkColorHarmony(Design design, List<Recommendation> recommendations) {
        Map<String, Integer> colorCount = new HashMap<>();
        
        // Count colors used
        for (Furniture furniture : design.getFurniture()) {
            String color = furniture.getColor();
            colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
        }
        
        // Add room colors
        Room room = design.getRoom();
        colorCount.put(room.getWallColor(), colorCount.getOrDefault(room.getWallColor(), 0) + 1);
        colorCount.put(room.getFloorColor(), colorCount.getOrDefault(room.getFloorColor(), 0) + 1);
        
        // Check if too many colors
        if (colorCount.size() > 5) {
            recommendations.add(new Recommendation(
                RecommendationType.COLOR_HARMONY,
                "The design uses more than 5 distinct colors. Consider using a more cohesive color palette."
            ));
        }
        
        // More advanced color analysis would go here
        // (complementary colors, analogous colors, etc.)
    }
    
    /**
     * Analyzes traffic flow in the room
     */
    private void checkTrafficFlow(Design design, List<Recommendation> recommendations) {
        // This is a simplified traffic flow analysis
        // In a real implementation, we would use path finding algorithms
        
        Room room = design.getRoom();
        double roomWidth = room.getWidth();
        double roomLength = room.getLength();
        
        // Check if major pathways are blocked
        
        // Simplified approach: check if furniture blocks center area
        boolean centerBlocked = false;
        double centerX = roomWidth / 2;
        double centerY = roomLength / 2;
        
        for (Furniture furniture : design.getFurniture()) {
            if (Math.abs(furniture.getX() - centerX) < 0.5 && 
                Math.abs(furniture.getY() - centerY) < 0.5) {
                centerBlocked = true;
                break;
            }
        }
        
        if (centerBlocked) {
            recommendations.add(new Recommendation(
                RecommendationType.TRAFFIC_FLOW,
                "Furniture may block the main traffic path through the room. Consider rearranging for better flow."
            ));
        }
    }
    
    /**
     * Analyzes lighting in the room
     */
    private void checkLighting(Design design, List<Recommendation> recommendations) {
        Room room = design.getRoom();
        
        // Check if room has enough lights
        if (room.getLights().isEmpty()) {
            recommendations.add(new Recommendation(
                RecommendationType.LIGHTING,
                "No lighting sources defined. Consider adding ceiling or ambient lights."
            ));
            return;
        }
        
        // Check for adequate coverage
        double roomArea = room.getWidth() * room.getLength();
        double totalLightCoverage = 0;
        
        for (Room.Light light : room.getLights()) {
            // Simple calculation based on light type and intensity
            if ("ambient".equals(light.getType())) {
                totalLightCoverage += roomArea * light.getIntensity();
            } else {
                // Point and spot lights cover circular areas
                double radius = light.getType().equals("spot") ? 2.0 : 3.0;
                double coverage = Math.PI * radius * radius * light.getIntensity();
                totalLightCoverage += Math.min(coverage, roomArea);
            }
        }
        
        if (totalLightCoverage < roomArea * 0.8) {
            recommendations.add(new Recommendation(
                RecommendationType.LIGHTING,
                "Room may have inadequate lighting coverage. Consider adding more light sources."
            ));
        }
        
        // Check if there are lamp furniture pieces but no corresponding lights
        boolean hasLamps = design.getFurniture().stream()
            .anyMatch(f -> f.getType().equalsIgnoreCase("lamp"));
        
        if (hasLamps && room.getLights().size() <= 1) {
            recommendations.add(new Recommendation(
                RecommendationType.LIGHTING,
                "Lamps are present but not configured as light sources. Consider adding light sources for each lamp."
            ));
        }
    }
    
    /**
     * Returns whether the given furniture type is considered "major"
     */
    private boolean isMajorFurniture(String type) {
        switch (type.toLowerCase()) {
            case "sofa":
            case "bed":
            case "table":
            case "dining_table":
            case "desk":
            case "cabinet":
            case "bookshelf":
            case "wardrobe":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Returns whether the furniture is out of room bounds
     */
    private boolean isOutOfBounds(Furniture furniture, double roomWidth, double roomLength) {
        double fwidth = furniture.getWidth();
        double flength = furniture.getLength();
        double fx = furniture.getX();
        double fy = furniture.getY();
        
        return fx < 0 || fy < 0 || 
               (fx + fwidth > roomWidth) || 
               (fy + flength > roomLength);
    }
    
    /**
     * Returns whether the furniture is too close to a wall
     */
    private boolean isTooCloseToWall(Furniture furniture, double roomWidth, double roomLength) {
        double fx = furniture.getX();
        double fy = furniture.getY();
        double fw = furniture.getWidth();
        double fl = furniture.getLength();
        
        return fx < 0.3 || fy < 0.3 || 
               (roomWidth - (fx + fw) < 0.3) || 
               (roomLength - (fy + fl) < 0.3);
    }
    
    /**
     * Returns whether two pieces of furniture overlap
     */
    private boolean furnitureOverlap(Furniture a, Furniture b) {
        double ax = a.getX();
        double ay = a.getY();
        double aw = a.getWidth();
        double al = a.getLength();
        
        double bx = b.getX();
        double by = b.getY();
        double bw = b.getWidth();
        double bl = b.getLength();
        
        return !(ax + aw <= bx || 
                bx + bw <= ax || 
                ay + al <= by || 
                by + bl <= ay);
    }
    
    /**
     * Generates a complete design evaluation with scores
     */
    public DesignEvaluation evaluateDesign(Design design) {
        DesignEvaluation evaluation = new DesignEvaluation();
        
        // Get recommendations
        List<Recommendation> recommendations = analyzeDesign(design);
        evaluation.setRecommendations(recommendations);
        
        // Calculate scores
        int functionalityScore = calculateFunctionalityScore(design, recommendations);
        int aestheticsScore = calculateAestheticsScore(design, recommendations);
        int ergonomicsScore = calculateErgonomicsScore(design, recommendations);
        int lightingScore = calculateLightingScore(design, recommendations);
        int flowScore = calculateFlowScore(design, recommendations);
        
        evaluation.setFunctionalityScore(functionalityScore);
        evaluation.setAestheticsScore(aestheticsScore);
        evaluation.setErgonomicsScore(ergonomicsScore);
        evaluation.setLightingScore(lightingScore);
        evaluation.setFlowScore(flowScore);
        
        // Calculate overall score (weighted average)
        int overallScore = (int)(0.3 * functionalityScore + 
                                 0.2 * aestheticsScore + 
                                 0.2 * ergonomicsScore + 
                                 0.15 * lightingScore + 
                                 0.15 * flowScore);
        
        evaluation.setOverallScore(overallScore);
        
        return evaluation;
    }
    
    private int calculateFunctionalityScore(Design design, List<Recommendation> recommendations) {
        // Base score
        int score = 80;
        
        // Check if essential furniture for the room type is present
        String roomType = design.getRoom().getRoomType();
        List<String> missingEssentials = getMissingEssentialFurniture(design, roomType);
        
        // Deduct for missing essentials
        score -= missingEssentials.size() * 10;
        
        // Deduct for relevant recommendation types
        for (Recommendation rec : recommendations) {
            if (rec.getType() == RecommendationType.FURNITURE_DENSITY || 
                rec.getType() == RecommendationType.FURNITURE_PLACEMENT ||
                rec.getType() == RecommendationType.FURNITURE_COLLISION) {
                score -= 5;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private int calculateAestheticsScore(Design design, List<Recommendation> recommendations) {
        // Base score
        int score = 80;
        
        // Adjust for color diversity
        Map<String, Integer> colorCount = new HashMap<>();
        for (Furniture furniture : design.getFurniture()) {
            colorCount.put(furniture.getColor(), colorCount.getOrDefault(furniture.getColor(), 0) + 1);
        }
        
        // Too few or too many colors affect score
        if (colorCount.size() < 2) score -= 10;
        if (colorCount.size() > 5) score -= 15;
        
        // Deduct for relevant recommendation types
        for (Recommendation rec : recommendations) {
            if (rec.getType() == RecommendationType.COLOR_HARMONY || 
                rec.getType() == RecommendationType.ROOM_PROPORTION) {
                score -= 5;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private int calculateErgonomicsScore(Design design, List<Recommendation> recommendations) {
        // Base score
        int score = 75;
        
        // Check for appropriate furniture spacing
        boolean properSpacing = true;
        
        for (Furniture a : design.getFurniture()) {
            for (Furniture b : design.getFurniture()) {
                if (a != b) {
                    double distance = calculateDistance(a, b);
                    if (distance < 0.5 && !areCompanionPieces(a.getType(), b.getType())) {
                        properSpacing = false;
                        break;
                    }
                }
            }
            
            if (!properSpacing) break;
        }
        
        if (properSpacing) score += 15;
        else score -= 10;
        
        // Deduct for relevant recommendation types
        for (Recommendation rec : recommendations) {
            if (rec.getType() == RecommendationType.FURNITURE_PLACEMENT || 
                rec.getType() == RecommendationType.FURNITURE_COLLISION ||
                rec.getType() == RecommendationType.TRAFFIC_FLOW) {
                score -= 5;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private int calculateLightingScore(Design design, List<Recommendation> recommendations) {
        // Base score
        int score = 70;
        
        Room room = design.getRoom();
        
        // Add points for having lights
        score += room.getLights().size() * 5;
        
        // Add for light diversity
        boolean hasAmbient = false;
        boolean hasPoint = false;
        boolean hasSpot = false;
        
        for (Room.Light light : room.getLights()) {
            if ("ambient".equals(light.getType())) hasAmbient = true;
            if ("point".equals(light.getType())) hasPoint = true;
            if ("spot".equals(light.getType())) hasSpot = true;
        }
        
        if (hasAmbient) score += 5;
        if (hasPoint) score += 5;
        if (hasSpot) score += 5;
        
        // Deduct for lighting recommendations
        for (Recommendation rec : recommendations) {
            if (rec.getType() == RecommendationType.LIGHTING) {
                score -= 10;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private int calculateFlowScore(Design design, List<Recommendation> recommendations) {
        // Base score
        int score = 75;
        
        // Check density
        Room room = design.getRoom();
        double roomArea = room.getWidth() * room.getLength();
        
        // Calculate total furniture footprint
        double totalFurnitureArea = design.getFurniture().stream()
                .mapToDouble(f -> f.getWidth() * f.getLength())
                .sum();
        
        // Calculate furniture density
        double density = totalFurnitureArea / roomArea;
        
        // Adjust score based on density
        if (density > 0.6) score -= 20;
        else if (density > 0.5) score -= 10;
        else if (density > 0.4) score -= 5;
        else if (density < 0.1) score -= 5;
        
        // Deduct for traffic flow recommendations
        for (Recommendation rec : recommendations) {
            if (rec.getType() == RecommendationType.TRAFFIC_FLOW) {
                score -= 15;
            }
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private double calculateDistance(Furniture a, Furniture b) {
        double ax = a.getX() + a.getWidth() / 2;
        double ay = a.getY() + a.getLength() / 2;
        
        double bx = b.getX() + b.getWidth() / 2;
        double by = b.getY() + b.getLength() / 2;
        
        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }
    
    private boolean areCompanionPieces(String typeA, String typeB) {
        if ((typeA.equalsIgnoreCase("coffee_table") && typeB.equalsIgnoreCase("sofa")) ||
            (typeA.equalsIgnoreCase("sofa") && typeB.equalsIgnoreCase("coffee_table"))) {
            return true;
        }
        
        if ((typeA.equalsIgnoreCase("nightstand") && typeB.equalsIgnoreCase("bed")) ||
            (typeA.equalsIgnoreCase("bed") && typeB.equalsIgnoreCase("nightstand"))) {
            return true;
        }
        
        if ((typeA.equalsIgnoreCase("chair") && typeB.equalsIgnoreCase("desk")) ||
            (typeA.equalsIgnoreCase("desk") && typeB.equalsIgnoreCase("chair"))) {
            return true;
        }
        
        if ((typeA.equalsIgnoreCase("chair") && typeB.equalsIgnoreCase("dining_table")) ||
            (typeA.equalsIgnoreCase("dining_table") && typeB.equalsIgnoreCase("chair"))) {
            return true;
        }
        
        return false;
    }
    
    private List<String> getMissingEssentialFurniture(Design design, String roomType) {
        List<String> essentials = getEssentialFurniture(roomType);
        List<String> missing = new ArrayList<>(essentials);
        
        // Check which essentials are present
        for (Furniture furniture : design.getFurniture()) {
            String type = furniture.getType().toLowerCase();
            missing.removeIf(essential -> essential.equalsIgnoreCase(type));
        }
        
        return missing;
    }
    
    private List<String> getEssentialFurniture(String roomType) {
        List<String> essentials = new ArrayList<>();
        
        switch (roomType.toLowerCase()) {
            case "living_room":
                essentials.add("sofa");
                essentials.add("coffee_table");
                break;
            case "bedroom":
                essentials.add("bed");
                essentials.add("nightstand");
                essentials.add("wardrobe");
                break;
            case "office":
                essentials.add("desk");
                essentials.add("chair");
                essentials.add("bookshelf");
                break;
            case "dining_room":
                essentials.add("dining_table");
                essentials.add("chair");
                break;
            case "kitchen":
                essentials.add("cabinet");
                essentials.add("table");
                break;
        }
        
        return essentials;
    }
    
    /**
     * Types of design recommendations
     */
    public enum RecommendationType {
        ROOM_SIZE,
        ROOM_PROPORTION,
        FURNITURE_PLACEMENT,
        FURNITURE_COLLISION,
        FURNITURE_DENSITY,
        COLOR_HARMONY,
        TRAFFIC_FLOW,
        LIGHTING
    }
    
    /**
     * Represents a design recommendation
     */
    public static class Recommendation {
        private RecommendationType type;
        private String description;
        
        public Recommendation(RecommendationType type, String description) {
            this.type = type;
            this.description = description;
        }
        
        public RecommendationType getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Represents a complete design evaluation
     */
    public static class DesignEvaluation {
        private int overallScore;
        private int functionalityScore;
        private int aestheticsScore;
        private int ergonomicsScore;
        private int lightingScore;
        private int flowScore;
        private List<Recommendation> recommendations;
        
        public DesignEvaluation() {
            this.recommendations = new ArrayList<>();
        }
        
        public int getOverallScore() {
            return overallScore;
        }
        
        public void setOverallScore(int overallScore) {
            this.overallScore = overallScore;
        }
        
        public int getFunctionalityScore() {
            return functionalityScore;
        }
        
        public void setFunctionalityScore(int functionalityScore) {
            this.functionalityScore = functionalityScore;
        }
        
        public int getAestheticsScore() {
            return aestheticsScore;
        }
        
        public void setAestheticsScore(int aestheticsScore) {
            this.aestheticsScore = aestheticsScore;
        }
        
        public int getErgonomicsScore() {
            return ergonomicsScore;
        }
        
        public void setErgonomicsScore(int ergonomicsScore) {
            this.ergonomicsScore = ergonomicsScore;
        }
        
        public int getLightingScore() {
            return lightingScore;
        }
        
        public void setLightingScore(int lightingScore) {
            this.lightingScore = lightingScore;
        }
        
        public int getFlowScore() {
            return flowScore;
        }
        
        public void setFlowScore(int flowScore) {
            this.flowScore = flowScore;
        }
        
        public List<Recommendation> getRecommendations() {
            return recommendations;
        }
        
        public void setRecommendations(List<Recommendation> recommendations) {
            this.recommendations = recommendations;
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("Design Evaluation Summary\n");
            summary.append("------------------------\n");
            summary.append(String.format("Overall Score: %d/100\n", overallScore));
            summary.append(String.format("Functionality: %d/100\n", functionalityScore));
            summary.append(String.format("Aesthetics: %d/100\n", aestheticsScore));
            summary.append(String.format("Ergonomics: %d/100\n", ergonomicsScore));
            summary.append(String.format("Lighting: %d/100\n", lightingScore));
            summary.append(String.format("Flow: %d/100\n", flowScore));
            
            if (!recommendations.isEmpty()) {
                summary.append("\nRecommendations:\n");
                for (Recommendation rec : recommendations) {
                    summary.append("- ").append(rec.getDescription()).append("\n");
                }
            }
            
            return summary.toString();
        }
    }
} 
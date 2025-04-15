package com.furniture.visualizer.view;

import com.furniture.visualizer.model.Furniture;
import com.furniture.visualizer.model.FurnitureType;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2D visualization component for room and furniture layout
 */
public class Visualization2D extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private double roomWidth = 3.0;  // Default value
    private double roomLength = 4.0;  // Default value
    private Color roomColor = Color.WHITE;
    private List<Furniture> furnitureItems = new ArrayList<>();
    private double scale = 1.0;
    private double roomX = 0;
    private double roomY = 0;
    private Furniture selectedFurniture = null;
    private Map<FurnitureType, Color> furnitureColorMap = new HashMap<>();
    private boolean showDimensions = true;
    private boolean showGrid = true;
    private boolean showLabels = true;
    
    /**
     * Constructor: Creates a new 2D visualization component
     */
    public Visualization2D() {
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        
        // Make canvas resize with parent
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        
        // Redraw when size changes
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
        
        // Initialize furniture colors
        initFurnitureColors();
        
        getChildren().add(canvas);
        
        // Add mouse event handlers for interaction
        setupMouseHandlers();
    }
    
    /**
     * Initializes the color map for furniture types
     */
    private void initFurnitureColors() {
        furnitureColorMap.put(FurnitureType.CHAIR, Color.CORNFLOWERBLUE);
        furnitureColorMap.put(FurnitureType.TABLE, Color.BURLYWOOD);
        furnitureColorMap.put(FurnitureType.SOFA, Color.INDIANRED);
        furnitureColorMap.put(FurnitureType.BED, Color.LIGHTSTEELBLUE);
        furnitureColorMap.put(FurnitureType.CABINET, Color.TAN);
        furnitureColorMap.put(FurnitureType.DESK, Color.DARKKHAKI);
    }
    
    /**
     * Sets up mouse event handlers for interacting with furniture
     */
    private void setupMouseHandlers() {
        canvas.setOnMousePressed(event -> {
            // Convert mouse coordinates to room coordinates
            double[] roomCoords = mouseToRoomCoordinates(event.getX(), event.getY());
            
            // Check if a furniture item was clicked
            Furniture clickedFurniture = getFurnitureAt(event.getX(), event.getY());
            if (clickedFurniture != null) {
                selectedFurniture = clickedFurniture;
                draw(); // Redraw to show selection
            } else {
                // Deselect if clicking empty space
                if (selectedFurniture != null) {
                    selectedFurniture = null;
                    draw();
                }
            }
        });
        
        canvas.setOnMouseDragged(event -> {
            if (selectedFurniture != null) {
                // Convert mouse coordinates to room coordinates
                double[] roomCoords = mouseToRoomCoordinates(event.getX(), event.getY());
                
                // Constrain furniture within room boundaries
                double halfWidth = selectedFurniture.getWidth() / 2;
                double halfDepth = selectedFurniture.getDepth() / 2;
                
                double x = Math.min(roomWidth - halfWidth, Math.max(halfWidth, roomCoords[0]));
                double y = Math.min(roomLength - halfDepth, Math.max(halfDepth, roomCoords[1]));
                
                // Update furniture position
                selectedFurniture.setPositionX(x);
                selectedFurniture.setPositionY(y);
                
                // Redraw
                draw();
            }
        });
        
        canvas.setOnScroll(event -> {
            if (selectedFurniture != null) {
                // Rotate selected furniture with scroll wheel
                double delta = event.getDeltaY() > 0 ? 15 : -15;
                double newRotation = (selectedFurniture.getRotation() + delta) % 360;
                selectedFurniture.setRotation(newRotation);
                draw();
            }
        });
    }
    
    /**
     * Updates room dimensions and redraws the visualization
     * 
     * @param width Room width in meters
     * @param length Room length in meters
     */
    public void updateRoomDimensions(double width, double length) {
        // Validate inputs
        if (width <= 0 || length <= 0) {
            System.err.println("Invalid room dimensions: " + width + "x" + length);
            return;
        }
        
        this.roomWidth = width;
        this.roomLength = length;
        
        // Ensure furniture stays within room boundaries after resize
        validateFurniturePositions();
        
        draw();
    }
    
    /**
     * Validates and adjusts furniture positions to keep them within room boundaries
     */
    private void validateFurniturePositions() {
        if (furnitureItems == null || furnitureItems.isEmpty()) return;
        
        for (Furniture furniture : furnitureItems) {
            double halfWidth = furniture.getWidth() / 2;
            double halfDepth = furniture.getDepth() / 2;
            
            // Constrain X position
            double x = furniture.getPositionX();
            if (x - halfWidth < 0) {
                x = halfWidth;
            } else if (x + halfWidth > roomWidth) {
                x = roomWidth - halfWidth;
            }
            
            // Constrain Y position
            double y = furniture.getPositionY();
            if (y - halfDepth < 0) {
                y = halfDepth;
            } else if (y + halfDepth > roomLength) {
                y = roomLength - halfDepth;
            }
            
            // Update furniture if position changed
            if (x != furniture.getPositionX() || y != furniture.getPositionY()) {
                furniture.setPositionX(x);
                furniture.setPositionY(y);
            }
        }
    }
    
    /**
     * Updates the room color and redraws the visualization
     * 
     * @param color Room wall color
     */
    public void updateRoomColor(Color color) {
        if (color == null) {
            System.err.println("Null color provided, using default");
            this.roomColor = Color.WHITE;
        } else {
            this.roomColor = color;
        }
        draw();
    }
    
    /**
     * Sets the furniture items to display
     * 
     * @param furniture List of furniture items
     */
    public void setFurniture(List<Furniture> furniture) {
        if (furniture == null) {
            this.furnitureItems = new ArrayList<>();
        } else {
            this.furnitureItems = new ArrayList<>(furniture);
        }
        validateFurniturePositions();
        draw();
    }
    
    /**
     * Adds a furniture item to the visualization
     * 
     * @param furniture Furniture item to add
     */
    public void addFurniture(Furniture furniture) {
        if (furniture == null) return;
        
        this.furnitureItems.add(furniture);
        validateFurniturePositions();
        draw();
    }
    
    /**
     * Removes a furniture item from the visualization
     * 
     * @param furniture Furniture item to remove
     */
    public void removeFurniture(Furniture furniture) {
        if (furniture == null) return;
        
        this.furnitureItems.remove(furniture);
        if (selectedFurniture == furniture) {
            selectedFurniture = null;
        }
        draw();
    }
    
    /**
     * Clears all furniture from the visualization
     */
    public void clearFurniture() {
        this.furnitureItems.clear();
        selectedFurniture = null;
        draw();
    }
    
    /**
     * Sets whether to show dimension indicators
     * 
     * @param show True to show dimensions, false to hide
     */
    public void setShowDimensions(boolean show) {
        this.showDimensions = show;
        draw();
    }
    
    /**
     * Sets whether to show the grid
     * 
     * @param show True to show grid, false to hide
     */
    public void setShowGrid(boolean show) {
        this.showGrid = show;
        draw();
    }
    
    /**
     * Sets whether to show furniture labels
     * 
     * @param show True to show labels, false to hide
     */
    public void setShowLabels(boolean show) {
        this.showLabels = show;
        draw();
    }
    
    /**
     * Resets the visualization to default state
     */
    public void reset() {
        roomWidth = 3.0;
        roomLength = 4.0;
        roomColor = Color.WHITE;
        furnitureItems.clear();
        selectedFurniture = null;
        draw();
    }
    
    /**
     * Draws the room, grid, dimensions, and furniture
     */
    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        if (roomWidth <= 0 || roomLength <= 0) {
            drawPlaceholder();
            return;
        }
        
        // Calculate scale to fit room in canvas with margin
        scale = Math.min(
            (canvas.getWidth() - 60) / roomWidth,
            (canvas.getHeight() - 60) / roomLength
        );
        
        // Calculate room dimensions in pixels
        double width = roomWidth * scale;
        double length = roomLength * scale;
        
        // Center the room in the canvas
        roomX = (canvas.getWidth() - width) / 2;
        roomY = (canvas.getHeight() - length) / 2;
        
        // Draw room
        gc.setFill(roomColor);
        gc.fillRect(roomX, roomY, width, length);
        
        // Draw grid if enabled
        if (showGrid) {
            drawGrid(width, length);
        }
        
        // Draw dimensions if enabled
        if (showDimensions) {
            drawDimensions(width, length);
        }
        
        // Draw furniture
        drawFurniture();
    }
    
    /**
     * Draws a placeholder when room dimensions are invalid
     */
    private void drawPlaceholder() {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(20, 20, canvas.getWidth() - 40, canvas.getHeight() - 40);
        
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font(16));
        gc.fillText("Invalid room dimensions. Please configure room size.", 
                    canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
    
    /**
     * Draws the grid within the room
     * 
     * @param width Room width in pixels
     * @param length Room length in pixels
     */
    private void drawGrid(double width, double length) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        
        // Draw vertical grid lines every meter
        for (int i = 0; i <= roomWidth; i++) {
            double gridX = roomX + (i * scale);
            gc.strokeLine(gridX, roomY, gridX, roomY + length);
        }
        
        // Draw horizontal grid lines every meter
        for (int i = 0; i <= roomLength; i++) {
            double gridY = roomY + (i * scale);
            gc.strokeLine(roomX, gridY, roomX + width, gridY);
        }
    }
    
    /**
     * Draws the dimension indicators for the room
     * 
     * @param width Room width in pixels
     * @param length Room length in pixels
     */
    private void drawDimensions(double width, double length) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font(14));
        
        // Draw width dimension
        gc.fillText(String.format("%.1fm", roomWidth), roomX + width/2, roomY - 15);
        
        // Draw length dimension
        gc.save();
        gc.translate(roomX - 15, roomY + length/2);
        gc.rotate(-90);
        gc.fillText(String.format("%.1fm", roomLength), 0, 0);
        gc.restore();
        
        // Draw dimension lines
        // Width
        double arrowSize = 5;
        double lineOffset = 10;
        
        gc.strokeLine(roomX, roomY - lineOffset, roomX + width, roomY - lineOffset);
        
        // Arrow at start
        gc.strokeLine(roomX, roomY - lineOffset, roomX + arrowSize, roomY - lineOffset - arrowSize);
        gc.strokeLine(roomX, roomY - lineOffset, roomX + arrowSize, roomY - lineOffset + arrowSize);
        
        // Arrow at end
        gc.strokeLine(roomX + width, roomY - lineOffset, roomX + width - arrowSize, roomY - lineOffset - arrowSize);
        gc.strokeLine(roomX + width, roomY - lineOffset, roomX + width - arrowSize, roomY - lineOffset + arrowSize);
        
        // Length
        gc.strokeLine(roomX - lineOffset, roomY, roomX - lineOffset, roomY + length);
        
        // Arrow at start
        gc.strokeLine(roomX - lineOffset, roomY, roomX - lineOffset - arrowSize, roomY + arrowSize);
        gc.strokeLine(roomX - lineOffset, roomY, roomX - lineOffset + arrowSize, roomY + arrowSize);
        
        // Arrow at end
        gc.strokeLine(roomX - lineOffset, roomY + length, roomX - lineOffset - arrowSize, roomY + length - arrowSize);
        gc.strokeLine(roomX - lineOffset, roomY + length, roomX - lineOffset + arrowSize, roomY + length - arrowSize);
    }
    
    /**
     * Draws all furniture items in the room
     */
    private void drawFurniture() {
        if (furnitureItems == null || furnitureItems.isEmpty()) return;
        
        for (Furniture furniture : furnitureItems) {
            // Calculate position based on room coordinates
            double x = roomX + (furniture.getPositionX() * scale);
            double y = roomY + (furniture.getPositionY() * scale);
            double width = furniture.getWidth() * scale;
            double depth = furniture.getDepth() * scale;
            
            // Save current transform
            gc.save();
            
            // Move to the center of the furniture item for rotation
            gc.translate(x, y);
            gc.rotate(furniture.getRotation());
            
            // Draw furniture with its color
            Color color;
            try {
                color = Color.valueOf(furniture.getColor());
            } catch (Exception e) {
                // Fallback to type-specific color if parsing fails
                color = furnitureColorMap.getOrDefault(furniture.getType(), Color.GRAY);
            }
            
            gc.setFill(color);
            gc.fillRect(-width/2, -depth/2, width, depth);
            
            // Draw selection indicator if this furniture is selected
            if (furniture == selectedFurniture) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(2.0);
                double selectionPadding = 2;
                gc.strokeRect(
                    -width/2 - selectionPadding, 
                    -depth/2 - selectionPadding, 
                    width + (2 * selectionPadding), 
                    depth + (2 * selectionPadding)
                );
            } else {
                // Draw regular border
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.0);
                gc.strokeRect(-width/2, -depth/2, width, depth);
            }
            
            // Draw furniture type text if labels are enabled
            if (showLabels) {
                gc.setFill(Color.BLACK);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.setFont(Font.font(10));
                gc.fillText(furniture.getType().getDisplayName(), 0, 0);
            }
            
            // Restore original transform
            gc.restore();
        }
    }
    
    /**
     * Finds the furniture item at a specific position
     * 
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @return The furniture item at the position, or null if none
     */
    public Furniture getFurnitureAt(double mouseX, double mouseY) {
        if (furnitureItems == null || furnitureItems.isEmpty()) return null;
        
        // Check in reverse order (top furniture first)
        for (int i = furnitureItems.size() - 1; i >= 0; i--) {
            Furniture furniture = furnitureItems.get(i);
            
            // Convert to room coordinates
            double furnitureX = roomX + (furniture.getPositionX() * scale);
            double furnitureY = roomY + (furniture.getPositionY() * scale);
            double width = furniture.getWidth() * scale;
            double depth = furniture.getDepth() * scale;
            
            // Check if point is within rotated rectangle
            // Convert to furniture local coordinates
            double angle = -Math.toRadians(furniture.getRotation());
            double dx = mouseX - furnitureX;
            double dy = mouseY - furnitureY;
            
            // Rotate point
            double rotatedX = dx * Math.cos(angle) - dy * Math.sin(angle);
            double rotatedY = dx * Math.sin(angle) + dy * Math.cos(angle);
            
            // Check if point is inside the furniture bounds
            if (rotatedX >= -width/2 && rotatedX <= width/2 &&
                rotatedY >= -depth/2 && rotatedY <= depth/2) {
                return furniture;
            }
        }
        
        return null;
    }
    
    /**
     * Converts mouse coordinates to room coordinates
     * 
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @return Array with [x, y] room coordinates
     */
    public double[] mouseToRoomCoordinates(double mouseX, double mouseY) {
        double roomCoordX = (mouseX - roomX) / scale;
        double roomCoordY = (mouseY - roomY) / scale;
        return new double[] { roomCoordX, roomCoordY };
    }
    
    /**
     * Gets the currently selected furniture item
     * 
     * @return The selected furniture item, or null if none
     */
    public Furniture getSelectedFurniture() {
        return selectedFurniture;
    }
    
    /**
     * Sets the selected furniture item
     * 
     * @param furniture The furniture item to select
     */
    public void setSelectedFurniture(Furniture furniture) {
        this.selectedFurniture = furniture;
        draw();
    }
}
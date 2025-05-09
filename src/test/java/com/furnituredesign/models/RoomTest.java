package com.furnituredesign.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Test class for the Room model
 */
public class RoomTest {

    @Test
    @DisplayName("Test room creation with default values")
    public void testRoomCreation() {
        Room room = new Room();
        
        // Verify default values
        assertNotNull(room);
        assertEquals(0, room.getX());
        assertEquals(0, room.getY());
        assertEquals(0, room.getZ());
        
        // Check default room type
        assertNotNull(room.getRoomType());
    }
    
    @Test
    @DisplayName("Test room dimensions setting")
    public void testRoomDimensions() {
        Room room = new Room();
        
        // Set dimensions
        room.setWidth(5.5);
        room.setLength(7.2);
        room.setHeight(3.0);
        
        // Verify dimensions
        assertEquals(5.5, room.getWidth());
        assertEquals(7.2, room.getLength());
        assertEquals(3.0, room.getHeight());
    }
    
    @Test
    @DisplayName("Test room type setting")
    public void testRoomType() {
        Room room = new Room();
        
        // Set room type
        room.setRoomType("bedroom");
        
        // Verify room type
        assertEquals("bedroom", room.getRoomType());
    }
    
    @Test
    @DisplayName("Test room floor material")
    public void testRoomFloorMaterial() {
        Room room = new Room();
        
        // Create and set floor material
        Material woodFloor = Material.wood();
        woodFloor.setColor("#8B4513");  // Brown color for wood floor
        room.setFloorMaterial(woodFloor);
        
        // Verify floor material
        assertNotNull(room.getFloorMaterial());
        assertEquals("Wood", room.getFloorMaterial().getType());
        assertEquals("#8B4513", room.getFloorMaterial().getColor());
    }
    
    @Test
    @DisplayName("Test room wall material")
    public void testRoomWallMaterial() {
        Room room = new Room();
        
        // Create and set wall material
        Material wallPaint = new Material("Paint", "#F5F5F5");  // Off-white color
        room.setWallMaterial(wallPaint);
        
        // Verify wall material
        assertNotNull(room.getWallMaterial());
        assertEquals("Paint", room.getWallMaterial().getType());
        assertEquals("#F5F5F5", room.getWallMaterial().getColor());
    }
} 
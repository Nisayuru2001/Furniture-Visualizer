package com.furnituredesign.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Test class for the Furniture model
 */
public class FurnitureTest {

    @Test
    @DisplayName("Test furniture creation with proper default values")
    public void testFurnitureCreation() {
        // Create a chair
        Furniture chair = new Furniture("chair");
        
        // Check default values
        assertEquals("chair", chair.getType());
        assertEquals(0, chair.getX());
        assertEquals(0, chair.getY());
        assertEquals(0, chair.getZ());
        assertEquals("#ffffff", chair.getColor());
        assertEquals(0.5, chair.getWidth());
        assertEquals(0.5, chair.getLength());
        assertEquals(1.0, chair.getHeight());
        assertEquals("models/chair.obj", chair.getModelPath());
        assertEquals(0, chair.getRotation());
        
        // Check material is set to fabric for chair
        assertNotNull(chair.getMaterial());
        assertEquals("Fabric", chair.getMaterial().getType());
    }
    
    @Test
    @DisplayName("Test position setting for furniture")
    public void testFurniturePosition() {
        Furniture table = new Furniture("table");
        
        // Set position
        table.setX(10.5);
        table.setY(5.2);
        table.setZ(2.0);
        
        // Check position
        assertEquals(10.5, table.getX());
        assertEquals(5.2, table.getY());
        assertEquals(2.0, table.getZ());
    }
    
    @Test
    @DisplayName("Test furniture duplication")
    public void testFurnitureDuplication() {
        // Create original furniture
        Furniture original = new Furniture("sofa");
        original.setX(5.0);
        original.setY(3.0);
        original.setZ(1.0);
        original.setColor("#FF5733");
        original.setRotation(45.0);
        
        // Duplicate the furniture
        Furniture duplicate = original.duplicate();
        
        // Check that duplicate has same values
        assertEquals(original.getType(), duplicate.getType());
        assertEquals(original.getX(), duplicate.getX());
        assertEquals(original.getY(), duplicate.getY());
        assertEquals(original.getZ(), duplicate.getZ());
        assertEquals(original.getColor(), duplicate.getColor());
        assertEquals(original.getRotation(), duplicate.getRotation());
        assertEquals(original.getWidth(), duplicate.getWidth());
        assertEquals(original.getLength(), duplicate.getLength());
        assertEquals(original.getHeight(), duplicate.getHeight());
        assertEquals(original.getModelPath(), duplicate.getModelPath());
        
        // Change the original to verify the duplicate is independent
        original.setColor("#000000");
        assertNotEquals(original.getColor(), duplicate.getColor());
    }
} 
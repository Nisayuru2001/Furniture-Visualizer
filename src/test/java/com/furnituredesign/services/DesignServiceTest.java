package com.furnituredesign.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import com.furnituredesign.models.*;
import com.furnituredesign.services.DesignService.Design;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the DesignService
 */
public class DesignServiceTest {
    
    private DesignService designService;
    private static final String TEST_DESIGN_NAME = "test_design";
    
    @BeforeEach
    public void setup() {
        designService = new DesignService();
    }
    
    @AfterEach
    public void cleanup() {
        // Delete any test design files after test completion
        File testFile = new File("saved_designs/" + TEST_DESIGN_NAME + ".json");
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Test
    @DisplayName("Test saving and loading a design")
    public void testSaveAndLoadDesign() throws IOException {
        // Create a test design
        Room room = new Room();
        room.setWidth(5.0);
        room.setLength(4.0);
        room.setHeight(3.0);
        room.setRoomType("living room");
        
        List<Furniture> furnitureList = new ArrayList<>();
        Furniture sofa = new Furniture("sofa");
        sofa.setX(1.0);
        sofa.setY(0);
        sofa.setZ(1.0);
        furnitureList.add(sofa);
        
        Furniture table = new Furniture("table");
        table.setX(2.5);
        table.setY(0);
        table.setZ(2.0);
        furnitureList.add(table);
        
        Design design = new Design(TEST_DESIGN_NAME, room, furnitureList);
        design.setAuthor("Test Author");
        design.setDescription("Test Description");
        
        // Save the design
        designService.saveDesign(design, TEST_DESIGN_NAME);
        
        // Check if file exists
        File savedFile = new File("saved_designs/" + TEST_DESIGN_NAME + ".json");
        assertTrue(savedFile.exists());
        
        // Load the design
        Design loadedDesign = designService.loadDesign(TEST_DESIGN_NAME);
        
        // Verify design properties
        assertEquals(TEST_DESIGN_NAME, loadedDesign.getName());
        assertEquals("Test Author", loadedDesign.getAuthor());
        assertEquals("Test Description", loadedDesign.getDescription());
        assertEquals("living room", loadedDesign.getRoom().getRoomType());
        assertEquals(5.0, loadedDesign.getRoom().getWidth());
        assertEquals(4.0, loadedDesign.getRoom().getLength());
        assertEquals(3.0, loadedDesign.getRoom().getHeight());
        
        // Verify furniture
        assertEquals(2, loadedDesign.getFurniture().size());
        assertEquals("sofa", loadedDesign.getFurniture().get(0).getType());
        assertEquals("table", loadedDesign.getFurniture().get(1).getType());
        assertEquals(1.0, loadedDesign.getFurniture().get(0).getX());
        assertEquals(2.5, loadedDesign.getFurniture().get(1).getX());
    }
    
    @Test
    @DisplayName("Test getting saved designs list")
    public void testGetSavedDesigns() throws IOException {
        // Create and save multiple test designs
        Design design1 = new Design(TEST_DESIGN_NAME + "_1", new Room(), new ArrayList<>());
        Design design2 = new Design(TEST_DESIGN_NAME + "_2", new Room(), new ArrayList<>());
        
        designService.saveDesign(design1, TEST_DESIGN_NAME + "_1");
        designService.saveDesign(design2, TEST_DESIGN_NAME + "_2");
        
        // Get list of saved designs
        List<String> savedDesigns = designService.getSavedDesigns();
        
        // Check if our test designs are in the list
        assertTrue(savedDesigns.contains(TEST_DESIGN_NAME + "_1"));
        assertTrue(savedDesigns.contains(TEST_DESIGN_NAME + "_2"));
        
        // Cleanup extra test files
        new File("saved_designs/" + TEST_DESIGN_NAME + "_1.json").delete();
        new File("saved_designs/" + TEST_DESIGN_NAME + "_2.json").delete();
    }
    
    @Test
    @DisplayName("Test design gallery metadata")
    public void testDesignGallery() throws IOException {
        // Create a test design with specific metadata
        Room room = new Room();
        room.setRoomType("bedroom");
        
        Design design = new Design(TEST_DESIGN_NAME, room, new ArrayList<>());
        design.setAuthor("Gallery Test");
        design.setDescription("Gallery Test Description");
        
        // Save the design
        designService.saveDesign(design, TEST_DESIGN_NAME);
        
        // Get the gallery metadata
        List<DesignService.DesignMetadata> gallery = designService.getDesignGallery();
        
        // Find our test design metadata
        DesignService.DesignMetadata testMetadata = null;
        for (DesignService.DesignMetadata metadata : gallery) {
            if (metadata.getFilename().equals(TEST_DESIGN_NAME)) {
                testMetadata = metadata;
                break;
            }
        }
        
        // Verify the metadata
        assertNotNull(testMetadata);
        assertEquals(TEST_DESIGN_NAME, testMetadata.getName());
        assertEquals("Gallery Test", testMetadata.getAuthor());
        assertEquals("Gallery Test Description", testMetadata.getDescription());
        assertEquals("bedroom", testMetadata.getRoomType());
    }
} 
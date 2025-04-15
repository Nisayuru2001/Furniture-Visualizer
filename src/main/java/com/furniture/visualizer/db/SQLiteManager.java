package com.furniture.visualizer.db;

import com.furniture.visualizer.model.User;
import com.furniture.visualizer.model.Design;
import com.furniture.visualizer.model.FurnitureDesign;
import com.furniture.visualizer.model.Furniture;
import com.furniture.visualizer.model.FurnitureType;
import com.furniture.visualizer.model.MaterialType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteManager {
    private static final String DB_URL = "jdbc:sqlite:furniture_visualizer.db";
    private Connection connection;

    public SQLiteManager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            connection = DriverManager.getConnection("jdbc:sqlite:furniture_visualizer.db");
            System.out.println("Successfully connected to SQLite database");
            
            // Create tables if they don't exist
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            throw new RuntimeException("Failed to initialize SQLite connection", e);
        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite database: " + e.getMessage());
            throw new RuntimeException("Failed to initialize SQLite connection", e);
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create users table
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL" +
                    ")");

            // Create designs table
            statement.execute("CREATE TABLE IF NOT EXISTS designs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "furniture_type TEXT NOT NULL," +
                    "width REAL NOT NULL," +
                    "height REAL NOT NULL," +
                    "depth REAL NOT NULL," +
                    "color TEXT NOT NULL," +
                    "material TEXT NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")");
            
            // Drop and recreate furniture table to ensure it has all required columns
            try {
                statement.execute("DROP TABLE IF EXISTS furniture");
            } catch (SQLException e) {
                System.err.println("Error dropping furniture table: " + e.getMessage());
            }
                    
            // Create furniture table with all required columns
            statement.execute("CREATE TABLE IF NOT EXISTS furniture (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "design_id INTEGER NOT NULL," +
                    "type TEXT NOT NULL," +
                    "material TEXT NOT NULL," +
                    "width REAL NOT NULL," +
                    "height REAL NOT NULL," +
                    "depth REAL NOT NULL," +
                    "position_x REAL NOT NULL," +
                    "position_y REAL NOT NULL," +
                    "position_z REAL NOT NULL," +
                    "rotation REAL NOT NULL," +
                    "color TEXT NOT NULL," +
                    "FOREIGN KEY (design_id) REFERENCES designs(id) ON DELETE CASCADE" +
                    ")");
                    
            // Add a default user if none exists
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                statement.execute("INSERT INTO users (username, password) VALUES ('admin', 'admin')");
                System.out.println("Added default user: admin/admin");
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public List<Design> getUserDesigns(String userId) {
        List<Design> designs = new ArrayList<>();
        String sql = "SELECT * FROM designs WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                designs.add(new Design(
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("furniture_type"),
                    rs.getDouble("width"),
                    rs.getDouble("height"),
                    rs.getDouble("depth"),
                    rs.getString("color"),
                    rs.getString("material")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user designs: " + e.getMessage());
        }
        return designs;
    }

    public boolean saveDesign(Design design) {
        String sql = "INSERT INTO designs (user_id, name, description, furniture_type, width, height, depth, color, material) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(design.getUserId()));
            pstmt.setString(2, design.getName());
            pstmt.setString(3, design.getDescription());
            pstmt.setString(4, design.getFurnitureType());
            pstmt.setDouble(5, design.getWidth());
            pstmt.setDouble(6, design.getHeight());
            pstmt.setDouble(7, design.getDepth());
            pstmt.setString(8, design.getColor());
            pstmt.setString(9, design.getMaterial());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving design: " + e.getMessage());
            return false;
        }
    }

    public void createDesign(FurnitureDesign design) {
        String sql = "INSERT INTO designs (user_id, name, description, furniture_type, " +
                    "width, height, depth, color, material) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, 1); // Using user_id 1 for now
            statement.setString(2, design.getName());
            statement.setString(3, design.getDescription());
            statement.setString(4, design.getFurnitureType());
            statement.setDouble(5, design.getWidth());
            statement.setDouble(6, design.getHeight());
            statement.setDouble(7, design.getDepth());
            statement.setString(8, design.getColor());
            statement.setString(9, design.getMaterial());
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                design.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDesign(FurnitureDesign design) {
        String sql = "UPDATE designs SET name = ?, description = ?, furniture_type = ?, " +
                    "width = ?, height = ?, depth = ?, color = ?, material = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, design.getName());
            statement.setString(2, design.getDescription());
            statement.setString(3, design.getFurnitureType());
            statement.setDouble(4, design.getWidth());
            statement.setDouble(5, design.getHeight());
            statement.setDouble(6, design.getDepth());
            statement.setString(7, design.getColor());
            statement.setString(8, design.getMaterial());
            statement.setInt(9, design.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDesign(int designId) {
        String sql = "DELETE FROM designs WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, designId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FurnitureDesign> getAllDesigns() {
        List<FurnitureDesign> designs = new ArrayList<>();
        String sql = "SELECT * FROM designs";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                FurnitureDesign design = new FurnitureDesign(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("furniture_type"),
                    resultSet.getDouble("width"),
                    resultSet.getDouble("height"),
                    resultSet.getDouble("depth"),
                    resultSet.getString("color"),
                    resultSet.getString("material")
                );
                designs.add(design);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return designs;
    }
    
    // Furniture Management Methods
    public void addFurniture(Furniture furniture) {
        String sql = "INSERT INTO furniture (design_id, type, material, width, height, depth, " +
                "position_x, position_y, position_z, rotation, color) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, furniture.getDesignId());
            statement.setString(2, furniture.getTypeString());
            statement.setString(3, furniture.getMaterialString());
            statement.setDouble(4, furniture.getWidth());
            statement.setDouble(5, furniture.getHeight());
            statement.setDouble(6, furniture.getDepth());
            statement.setDouble(7, furniture.getPositionX());
            statement.setDouble(8, furniture.getPositionY());
            statement.setDouble(9, furniture.getPositionZ());
            statement.setDouble(10, furniture.getRotation());
            statement.setString(11, furniture.getColor());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    furniture.setId(generatedKeys.getInt(1));
                    System.out.println("Furniture added with ID: " + furniture.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding furniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateFurniture(Furniture furniture) {
        String sql = "UPDATE furniture SET type = ?, material = ?, width = ?, height = ?, depth = ?, " +
                "position_x = ?, position_y = ?, position_z = ?, rotation = ?, color = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, furniture.getTypeString());
            statement.setString(2, furniture.getMaterialString());
            statement.setDouble(3, furniture.getWidth());
            statement.setDouble(4, furniture.getHeight());
            statement.setDouble(5, furniture.getDepth());
            statement.setDouble(6, furniture.getPositionX());
            statement.setDouble(7, furniture.getPositionY());
            statement.setDouble(8, furniture.getPositionZ());
            statement.setDouble(9, furniture.getRotation());
            statement.setString(10, furniture.getColor());
            statement.setInt(11, furniture.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFurniture(int furnitureId) {
        String sql = "DELETE FROM furniture WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, furnitureId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Furniture> getFurnitureForDesign(int designId) {
        List<Furniture> furnitureList = new ArrayList<>();
        String sql = "SELECT * FROM furniture WHERE design_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, designId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Furniture furniture = new Furniture(
                    resultSet.getInt("id"),
                    resultSet.getInt("design_id"),
                    resultSet.getString("type"),
                    resultSet.getString("material"),
                    resultSet.getDouble("width"),
                    resultSet.getDouble("height"),
                    resultSet.getDouble("depth"),
                    resultSet.getDouble("position_x"),
                    resultSet.getDouble("position_y"),
                    resultSet.getDouble("position_z"),
                    resultSet.getDouble("rotation"),
                    resultSet.getString("color")
                );
                furnitureList.add(furniture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return furnitureList;
    }

    public Furniture getFurnitureById(int furnitureId) {
        String sql = "SELECT * FROM furniture WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, furnitureId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return new Furniture(
                    resultSet.getInt("id"),
                    resultSet.getInt("design_id"),
                    resultSet.getString("type"),
                    resultSet.getString("material"),
                    resultSet.getDouble("width"),
                    resultSet.getDouble("height"),
                    resultSet.getDouble("depth"),
                    resultSet.getDouble("position_x"),
                    resultSet.getDouble("position_y"),
                    resultSet.getDouble("position_z"),
                    resultSet.getDouble("rotation"),
                    resultSet.getString("color")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}

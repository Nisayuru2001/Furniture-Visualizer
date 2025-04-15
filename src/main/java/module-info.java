module com.furniture.visualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires org.slf4j;
    
    opens com.furniture.visualizer to javafx.fxml;
    opens com.furniture.visualizer.controller to javafx.fxml;
    opens com.furniture.visualizer.model to javafx.base;
    opens com.furniture.visualizer.view to javafx.fxml;
    opens com.furniture.visualizer.db to javafx.fxml;
    
    exports com.furniture.visualizer;
    exports com.furniture.visualizer.controller;
    exports com.furniture.visualizer.model;
    exports com.furniture.visualizer.view;
    exports com.furniture.visualizer.db;
}


#!/bin/bash

# Set JavaFX SDK path (update this path to where you extracted JavaFX SDK 21)
JAVAFX_PATH="/Users/nisayurusandaneth/Downloads/javafx-sdk-21/lib"

# Check if JavaFX SDK exists
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "Error: JavaFX SDK not found at $JAVAFX_PATH"
    echo "Please download JavaFX SDK 21 from https://gluonhq.com/products/javafx/"
    echo "Extract it and update the JAVAFX_PATH in this script"
    exit 1
fi

# Compile the application and copy dependencies
mvn clean compile dependency:copy-dependencies

# Get the SQLite JDBC jar path
SQLITE_JAR=$(find target/dependency -name "sqlite-jdbc*.jar")

if [ -z "$SQLITE_JAR" ]; then
    echo "Error: SQLite JDBC driver not found in target/dependency"
    exit 1
fi

# Run the application with JavaFX modules and 3D support
java --module-path "$JAVAFX_PATH" \
     --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.web \
     -Dprism.forceGPU=true \
     -Dprism.verbose=true \
     -Djavafx.animation.fullspeed=true \
     -Dprism.order=es2,sw \
     -Dprism.targetvram=2G \
     -Xmx4G \
     -cp "target/classes:$SQLITE_JAR:target/dependency/*" \
     -Djava.library.path=target/dependency \
     com.furniture.visualizer.Main 
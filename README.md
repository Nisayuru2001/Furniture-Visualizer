# Furniture Visualization Tool

A Java desktop application for furniture designers to create and visualize furniture designs in 2D and 3D.

## Features

- User authentication system
- Room dimension specification
- 2D and 3D visualization
- Color scheme customization
- Furniture design tools
- Save and load designs

## Technical Requirements

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17
- SQLite database...

## Project Structure

```
src/main/java/com/furniture/visualizer/
├── controller/     # MVC Controllers
├── model/         # Data models
├── view/          # UI components
├── db/            # Database management
└── util/          # Utility classes
```

## Building and Running

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn javafx:run
   ```

## Usage

1. Log in with your credentials
2. Specify room dimensions
3. Choose a color scheme
4. Use the 2D/3D view toggle to switch between visualization modes
5. Add, edit, and delete furniture designs
6. Save your designs for later use

## Development

The project follows MVC architecture and uses:
- JavaFX for UI components
- SQLite for data persistence
- Java 3D for 3D visualization

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
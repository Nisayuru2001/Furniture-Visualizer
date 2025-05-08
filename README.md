# Advanced Furniture Designer Application

A professional JavaFX desktop application for furniture design visualization, allowing designers to create and visualize room layouts in both 2D and 3D with advanced materials, lighting, and rendering features.

## Features

- User authentication for designers
- Room dimension input and customization
- 2D and 3D visualization of room layouts with realistic rendering
- Advanced material properties including textures, roughness, metallics, and reflectivity
- Dynamic lighting system with multiple light types (point, spot, ambient)
- Predefined room templates and furniture presets
- Design analysis and recommendations engine
- Furniture placement, customization, and rotation
- Advanced color scheme selection and harmony analysis
- Save, load, and export designs
- Cost estimation for design implementation
- Modern and intuitive user interface

## New Advanced Features

- **Advanced Material System**: Define materials with properties like roughness, metallics, reflectivity, and transparency
- **Realistic Lighting**: Add multiple light sources with different types, colors, and intensities
- **Predefined Room Templates**: Quickly start with room templates for living rooms, bedrooms, kitchens, offices, etc.
- **Furniture Presets**: Easily add complete furniture arrangements with a single click
- **Design Analysis**: Evaluate your designs with scores for functionality, aesthetics, ergonomics, lighting, and flow
- **Design Recommendations**: Get smart recommendations to improve your designs
- **3D Model Support**: Import and use custom 3D models for furniture
- **Export Options**: Export your designs to OBJ format for use in other applications
- **Cost Estimation**: Get an estimated cost for implementing your design in real life

## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Dependencies

- JavaFX 21.0.1
- SQLite JDBC 3.42.0.0
- Gson 2.10.1
- Java OBJ Loader 0.4.0
- Apache Commons IO 2.13.0
- Apache Commons Lang 3.13.0
- iText PDF 5.5.13.3

## Setup

1. Clone the repository:

```bash
git clone <repository-url>
cd furniture-designer
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn javafx:run
```

4. Alternatively, you can run the compiled JAR directly:

```bash
java -jar target/furniture-designer-1.0-SNAPSHOT.jar
```

## Default Login

- Username: admin
- Password: admin

## Usage

1. Log in using the default credentials
2. Create a new design by entering room dimensions or selecting a room template
3. Add furniture using the furniture panel or select a furniture preset
4. Customize furniture materials, colors, and positions
5. Add and adjust lighting to enhance the scene
6. Switch between 2D and 3D views
7. Use the Analyze Design feature to get improvement recommendations
8. Save your design for later use or export it in various formats

## Room Templates

- **Living Room**: A spacious layout optimized for entertainment and relaxation
- **Bedroom**: A cozy bedroom layout with proper furniture placement
- **Kitchen**: A functional kitchen layout with essential appliances
- **Office**: A productive home office setup
- **Dining Room**: An elegant dining area arrangement

## Material Types

- **Wood**: Customizable wood material with adjustable grain and finish
- **Metal**: Metallic material with controllable reflectivity and roughness
- **Fabric**: Soft materials with texture options
- **Glass**: Transparent material with adjustable opacity
- **Marble**: High-end material with natural patterns
- **Leather**: Realistic leather material with texture options

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── furnituredesign/
│   │           ├── controllers/      # UI controllers
│   │           ├── models/           # Data models
│   │           ├── services/         # Business logic services
│   │           ├── utils/            # Utility classes
│   │           └── Main.java         # Application entry point
│   └── resources/
│       ├── fxml/                     # UI layout files
│       ├── styles/                   # CSS styles
│       ├── models/                   # 3D model files
│       └── images/                   # Image resources
└── test/                             # Unit tests
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

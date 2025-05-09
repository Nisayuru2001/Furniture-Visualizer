# Test Suite for Furniture Designer Application

This directory contains unit tests for the Furniture Designer application. The tests are implemented using JUnit 5 and are organized by package structure mirroring the main application.

## Test Structure

- `com.furnituredesign.models.*Test` - Tests for domain model classes
- `com.furnituredesign.services.*Test` - Tests for service layer classes

## Running the Tests

To run the tests, you can use Maven from the command line:

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=FurnitureTest

# Run a specific test method
mvn test -Dtest=FurnitureTest#testFurnitureCreation
```

You can also run the tests from your IDE by right-clicking on the test class or method and selecting "Run Test".

## Test Coverage

The tests cover:

1. **Furniture Model**
   - Object creation with default values
   - Position setting
   - Object duplication

2. **Room Model**
   - Room creation with default values
   - Room dimension setting
   - Room type setting
   - Floor and wall material handling

3. **Design Service**
   - Saving and loading designs
   - Retrieving saved designs list
   - Design gallery metadata

## Troubleshooting

If you encounter linter errors in your IDE:
1. Make sure you've run `mvn clean install` to download all dependencies
2. Refresh your IDE's Maven project view
3. Check that your IDE is configured to use JUnit 5 
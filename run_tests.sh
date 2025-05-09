#!/bin/bash
# Simple script to run the tests for the Furniture Designer application

echo "Running tests for Furniture Designer application..."

# First make sure dependencies are downloaded
echo "Downloading dependencies..."
mvn dependency:resolve

# Run the tests
echo "Running tests..."
mvn test

# Check the test result
if [ $? -eq 0 ]; then
    echo "All tests passed!"
else
    echo "Some tests failed. See the test report above for details."
fi 
#!/bin/bash

# App Locker Build Script

echo "🚀 Building App Locker..."

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build the project
echo "🔨 Building project..."
./gradlew build

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Install on connected device
    echo "📱 Installing on device..."
    ./gradlew installDebug
    
    if [ $? -eq 0 ]; then
        echo "✅ App installed successfully!"
        echo "🎉 App Locker is ready to use!"
    else
        echo "❌ Failed to install app. Make sure a device is connected."
    fi
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi 
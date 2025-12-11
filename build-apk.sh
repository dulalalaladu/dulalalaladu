#!/bin/bash

# Vehicle Search APK Build Script
# This script builds the Android application

set -e

echo "========================================="
echo "Vehicle Search APK Builder"
echo "========================================="
echo ""

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found!"
    echo "Please run this script from the project root directory."
    echo "Or run: gradle wrapper"
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

echo "Cleaning previous builds..."
./gradlew clean

echo ""
echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Debug APK built successfully!"
    echo "Location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""

    # Ask if user wants to build release APK
    read -p "Do you want to build release APK? (y/n): " -n 1 -r
    echo ""

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Building release APK..."
        ./gradlew assembleRelease

        if [ $? -eq 0 ]; then
            echo ""
            echo "✓ Release APK built successfully!"
            echo "Location: app/build/outputs/apk/release/app-release-unsigned.apk"
            echo ""
            echo "Note: Release APK needs to be signed for production use."
            echo "See README.md for signing instructions."
        else
            echo "✗ Release build failed!"
            exit 1
        fi
    fi
else
    echo "✗ Debug build failed!"
    exit 1
fi

echo ""
echo "========================================="
echo "Build process complete!"
echo "========================================="

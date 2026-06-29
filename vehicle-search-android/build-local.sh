#!/bin/bash

###############################################################################
# Local APK Builder for Vehicle Search App
# This script builds the APK on your local machine
###############################################################################

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║         Vehicle Search App - Local APK Builder                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Check if Android SDK is installed
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "❌ ERROR: Android SDK not found!"
    echo ""
    echo "Please install Android SDK first:"
    echo "1. Download Android Studio from: https://developer.android.com/studio"
    echo "2. Open Android Studio → More Actions → SDK Manager"
    echo "3. Install Android SDK API 34"
    echo "4. Set ANDROID_HOME environment variable:"
    echo "   export ANDROID_HOME=\$HOME/Library/Android/sdk  # macOS"
    echo "   export ANDROID_HOME=\$HOME/Android/Sdk          # Linux"
    echo "   export ANDROID_HOME=%LOCALAPPDATA%\\Android\\Sdk  # Windows"
    echo ""
    exit 1
fi

echo "✓ Android SDK found at: ${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
echo "✓ Java version: $JAVA_VERSION"
echo ""

# Make gradlew executable
chmod +x ./gradlew

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean
echo ""

# Build Debug APK
echo "🔨 Building Debug APK..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "✅ SUCCESS! Debug APK built successfully!"
    echo ""
    echo "📱 APK Location:"
    echo "   $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📦 File size:"
    ls -lh app/build/outputs/apk/debug/app-debug.apk | awk '{print "   " $5}'
    echo ""
    echo "📲 To install on your phone:"
    echo "   1. Connect your Pixel 8 Pro via USB"
    echo "   2. Enable USB debugging on your phone"
    echo "   3. Run: adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "   OR"
    echo ""
    echo "   1. Copy app-debug.apk to your phone"
    echo "   2. Open the file and tap 'Install'"
    echo ""
else
    echo ""
    echo "❌ Build failed! Check the error messages above."
    echo ""
    exit 1
fi

# Ask about release build
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
read -p "Do you want to build the Release APK too? (y/n): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🔨 Building Release APK..."
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    ./gradlew assembleRelease

    if [ $? -eq 0 ]; then
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "✅ SUCCESS! Release APK built successfully!"
        echo ""
        echo "📱 APK Location:"
        echo "   $(pwd)/app/build/outputs/apk/release/app-release-unsigned.apk"
        echo ""
        echo "⚠️  NOTE: This APK is unsigned. To sign it for production:"
        echo ""
        echo "   1. Create a keystore (first time only):"
        echo "      keytool -genkey -v -keystore vehicle-search.keystore \\"
        echo "        -alias vehicle-search -keyalg RSA -keysize 2048 -validity 10000"
        echo ""
        echo "   2. Sign the APK:"
        echo "      jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \\"
        echo "        -keystore vehicle-search.keystore \\"
        echo "        app/build/outputs/apk/release/app-release-unsigned.apk vehicle-search"
        echo ""
        echo "   3. Align the APK:"
        echo "      zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk \\"
        echo "        VehicleSearch-v1.0.apk"
        echo ""
    else
        echo ""
        echo "❌ Release build failed! The debug APK is still available."
    fi
fi

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                    Build Complete! 🎉                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"

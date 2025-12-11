#!/bin/bash

###############################################################################
# Vehicle Search App - Ubuntu Environment Setup Script
# This script sets up everything needed to build the Android APK on Ubuntu
###############################################################################

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║    Android Build Environment Setup for Ubuntu Linux           ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Update package list
echo "📦 Updating package list..."
sudo apt-get update

# Install required packages
echo "📦 Installing required packages (wget, unzip, git)..."
sudo apt-get install -y wget unzip git

# Install Java 17 (OpenJDK)
echo "☕ Installing Java 17 (OpenJDK)..."
sudo apt-get install -y openjdk-17-jdk

# Verify Java installation
echo ""
echo "✓ Java installed:"
java -version
echo ""

# Set up Android SDK directory
ANDROID_SDK_DIR="$HOME/android-sdk"
echo "📱 Setting up Android SDK in: $ANDROID_SDK_DIR"

# Download Android SDK Command Line Tools
echo "⬇️  Downloading Android SDK Command Line Tools..."
mkdir -p "$ANDROID_SDK_DIR/cmdline-tools"
cd /tmp
wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip

echo "📦 Extracting Command Line Tools..."
unzip -q cmdline-tools.zip
mv cmdline-tools "$ANDROID_SDK_DIR/cmdline-tools/latest"
rm cmdline-tools.zip

# Set environment variables
echo "🔧 Setting up environment variables..."

# Add to current session
export ANDROID_HOME="$ANDROID_SDK_DIR"
export ANDROID_SDK_ROOT="$ANDROID_SDK_DIR"
export PATH="$PATH:$ANDROID_SDK_DIR/cmdline-tools/latest/bin:$ANDROID_SDK_DIR/platform-tools"

# Add to .bashrc for persistence
cat >> ~/.bashrc << 'EOF'

# Android SDK Environment Variables
export ANDROID_HOME="$HOME/android-sdk"
export ANDROID_SDK_ROOT="$HOME/android-sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"
EOF

echo "✓ Environment variables added to ~/.bashrc"
echo ""

# Accept Android SDK licenses
echo "📜 Accepting Android SDK licenses..."
yes | $ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager --licenses

# Install required Android SDK components
echo "📦 Installing Android SDK components..."
echo "   - platform-tools"
echo "   - platforms;android-34"
echo "   - build-tools;34.0.0"
$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

echo ""
echo "✓ Android SDK components installed"
echo ""

# Verify installation
echo "🔍 Verifying installation..."
echo ""
echo "ANDROID_HOME: $ANDROID_HOME"
echo ""
echo "Installed SDK components:"
$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager --list_installed
echo ""

# Clone the repository (if not already in it)
if [ ! -f "build.gradle.kts" ]; then
    echo "📥 Cloning the repository..."
    cd ~
    git clone https://github.com/dulalalaladu/dulalalaladu.git
    cd dulalalaladu
else
    echo "✓ Already in project directory"
fi

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║              Setup Complete! ✅                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "🎯 Next Steps:"
echo ""
echo "1. Reload your shell environment:"
echo "   source ~/.bashrc"
echo ""
echo "2. Navigate to the project directory:"
echo "   cd ~/dulalalaladu"
echo ""
echo "3. Build the APK:"
echo "   ./gradlew assembleDebug"
echo ""
echo "   OR use the interactive script:"
echo "   ./build-local.sh"
echo ""
echo "4. Find your APK at:"
echo "   app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💡 TIP: After running 'source ~/.bashrc', you can build with:"
echo "   cd ~/dulalalaladu && ./gradlew assembleDebug"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Ubuntu Setup Guide for Building Android APK

## Quick Setup (Automated Script)

Run this single command to set up everything automatically:

```bash
# Download and run the setup script
wget https://raw.githubusercontent.com/dulalalaladu/dulalalaladu/claude/vehicle-search-android-app-01AChWFEecRhN4TcaTJFcfmX/setup-ubuntu.sh
chmod +x setup-ubuntu.sh
./setup-ubuntu.sh
```

After the script completes:
```bash
source ~/.bashrc
cd ~/dulalalaladu
./gradlew assembleDebug
```

---

## Manual Setup (Step-by-Step Commands)

If you prefer to run commands manually:

### 1. Install Required Packages

```bash
# Update package list
sudo apt-get update

# Install Java 17, wget, unzip, git
sudo apt-get install -y openjdk-17-jdk wget unzip git

# Verify Java installation
java -version
```

### 2. Download and Install Android SDK

```bash
# Create SDK directory
mkdir -p $HOME/android-sdk/cmdline-tools

# Download Android Command Line Tools
cd /tmp
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# Extract to SDK directory
unzip commandlinetools-linux-9477386_latest.zip
mv cmdline-tools $HOME/android-sdk/cmdline-tools/latest

# Clean up
rm commandlinetools-linux-9477386_latest.zip
```

### 3. Set Environment Variables

```bash
# Add to ~/.bashrc
cat >> ~/.bashrc << 'EOF'

# Android SDK Environment Variables
export ANDROID_HOME="$HOME/android-sdk"
export ANDROID_SDK_ROOT="$HOME/android-sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"
EOF

# Reload environment
source ~/.bashrc
```

### 4. Accept SDK Licenses and Install Components

```bash
# Accept all licenses
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# Install required SDK components
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

# Verify installation
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --list_installed
```

### 5. Clone Repository and Build

```bash
# Clone the repository
cd ~
git clone https://github.com/dulalalaladu/dulalalaladu.git
cd dulalalaladu

# Make gradlew executable
chmod +x gradlew

# Build the APK
./gradlew assembleDebug
```

### 6. Locate Your APK

```bash
# The APK will be at:
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

---

## Quick Build Commands (After Setup)

Once environment is set up, use these commands to rebuild:

```bash
# Clean and rebuild
cd ~/dulalalaladu
./gradlew clean assembleDebug

# Or use the interactive script
./build-local.sh
```

---

## Installing APK on Your Phone

### Option A: USB Installation (Recommended)

```bash
# 1. Enable USB Debugging on your Pixel 8 Pro:
#    Settings → About phone → Tap "Build number" 7 times
#    Settings → System → Developer options → Enable "USB debugging"

# 2. Connect phone via USB

# 3. Install APK
sudo apt-get install -y adb  # Install ADB if needed
adb devices                   # Verify phone is connected
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option B: Manual Transfer

```bash
# 1. Copy APK to a USB drive or cloud storage
cp app/build/outputs/apk/debug/app-debug.apk ~/Downloads/

# 2. Transfer to phone

# 3. On phone: Open file, tap "Install"
#    (Allow installation from unknown sources if prompted)
```

---

## Troubleshooting

### "Command not found: java"
```bash
sudo apt-get install openjdk-17-jdk
java -version
```

### "ANDROID_HOME not set"
```bash
source ~/.bashrc
echo $ANDROID_HOME  # Should show /home/your-username/android-sdk
```

### "SDK licenses not accepted"
```bash
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
```

### "Build failed: SDK not found"
```bash
# Reinstall SDK components
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"
```

### "gradlew: Permission denied"
```bash
chmod +x gradlew
```

---

## System Requirements

- **OS**: Ubuntu 20.04 or later (also works on Debian, Linux Mint)
- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: ~5GB for Android SDK
- **Java**: OpenJDK 17
- **Internet**: Required for downloading SDK and dependencies

---

## Build Times

- **First build**: 5-10 minutes (downloads dependencies)
- **Subsequent builds**: 1-3 minutes

---

## What Gets Installed

| Component | Size | Purpose |
|-----------|------|---------|
| OpenJDK 17 | ~200MB | Java runtime for Gradle |
| Android SDK Command Line Tools | ~100MB | SDK manager |
| Platform Tools (ADB) | ~10MB | Install APK to phone |
| Android Platform 34 | ~50MB | API level for app |
| Build Tools 34.0.0 | ~60MB | Build the APK |
| Gradle Dependencies | ~500MB | App libraries |
| **Total** | **~1GB** | |

---

## After Building

Your APK will be located at:
```
~/dulalalaladu/app/build/outputs/apk/debug/app-debug.apk
```

File size: ~10-20MB

Ready to install on your Pixel 8 Pro! 🚀

# How to Build and Download the APK

There are three ways to get the APK file:

## Option 1: Download from GitHub Actions (Recommended - Easiest)

GitHub Actions will automatically build the APK when you push code to the repository.

### Steps:
1. **Push the code** (already done!)
2. **Go to GitHub Actions**:
   - Visit: `https://github.com/dulalalaladu/dulalalaladu/actions`
3. **Click on the latest workflow run**
4. **Download the APK**:
   - Scroll down to "Artifacts"
   - Download `app-debug.apk` for testing
   - Download `app-release.apk` for production (unsigned)

### Installing on Your Phone:
```bash
# Transfer the downloaded APK to your phone, then:
# - Open the APK file on your phone
# - Allow installation from unknown sources if prompted
# - Tap "Install"
```

---

## Option 2: Build Locally Using Android Studio (Most Reliable)

### Prerequisites:
- Install [Android Studio](https://developer.android.com/studio)
- Install Android SDK (API 34)
- Install JDK 17

### Steps:

1. **Open Project**:
   ```bash
   # Clone the repository if you haven't
   git clone https://github.com/dulalalaladu/dulalalaladu.git
   cd dulalalaladu
   ```

2. **Open in Android Studio**:
   - File → Open → Select the project folder
   - Wait for Gradle sync to complete (may take 5-10 minutes first time)

3. **Build APK**:
   - **For Debug (Testing)**:
     - Build → Build Bundle(s) / APK(s) → Build APK(s)
     - Or: `./gradlew assembleDebug`
     - Output: `app/build/outputs/apk/debug/app-debug.apk`

   - **For Release (Production)**:
     - Build → Generate Signed Bundle / APK
     - Follow wizard to create/use keystore
     - Output: `app/build/outputs/apk/release/app-release.apk`

4. **Install on Phone**:
   ```bash
   # Connect phone via USB with Developer Mode enabled
   adb install app/build/outputs/apk/debug/app-debug.apk

   # Or copy APK to phone and install manually
   ```

---

## Option 3: Build Using Command Line

### Prerequisites:
- Install JDK 17
- Install Android SDK
- Set environment variables:
  ```bash
  export ANDROID_HOME=/path/to/android/sdk
  export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
  ```

### Steps:

1. **Navigate to project**:
   ```bash
   cd dulalalaladu
   ```

2. **Make gradlew executable**:
   ```bash
   chmod +x gradlew
   ```

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

4. **Build Release APK** (unsigned):
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

5. **Sign Release APK** (for production):
   ```bash
   # Create keystore (first time only)
   keytool -genkey -v -keystore my-release-key.keystore \
     -alias vehicle-search -keyalg RSA -keysize 2048 -validity 10000

   # Sign the APK
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore my-release-key.keystore \
     app/build/outputs/apk/release/app-release-unsigned.apk vehicle-search

   # Align the APK
   zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk \
     VehicleSearch-v1.0.apk
   ```

---

## Quick Start (Recommended)

**The fastest way:**

1. Go to: `https://github.com/dulalalaladu/dulalalaladu/actions`
2. Click the latest successful build
3. Download `app-debug` artifact
4. Extract the ZIP file
5. Transfer `app-debug.apk` to your Pixel 8 Pro
6. Install and enjoy!

---

## Troubleshooting

### "Android SDK not found"
- Install Android Studio
- Open Android Studio → More Actions → SDK Manager
- Install Android SDK API 34

### "Build failed: Unable to find JDK"
- Download and install [JDK 17](https://adoptium.net/)
- Set `JAVA_HOME` environment variable

### "gradlew: Permission denied"
```bash
chmod +x gradlew
```

### "Installation blocked"
- On your phone: Settings → Security → Allow installation from unknown sources
- Or: Settings → Apps → Special app access → Install unknown apps → Enable for your file manager

---

## File Locations After Build

```
dulalalaladu/
└── app/
    └── build/
        └── outputs/
            └── apk/
                ├── debug/
                │   └── app-debug.apk          ← Install this for testing
                └── release/
                    └── app-release-unsigned.apk  ← Sign this for production
```

---

## Next Steps After Installation

1. **Grant Permissions**:
   - Notification permission (for vehicle alerts)
   - Location permission (optional, for geocoding)

2. **First Search**:
   - Enter make/model: "Toyota Sienna"
   - Enter address: "3101 Bridlewood Dr, Rochester, MI 48306"
   - Set your filters
   - Enable notifications
   - Tap "Search"

3. **Background Monitoring**:
   - The app will check for new vehicles every 6 hours
   - You'll receive push notifications automatically

Enjoy your vehicle search app! 🚗

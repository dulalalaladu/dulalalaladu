# Vehicle Search App Icon

## Design Overview

The Vehicle Search app icon features:

### Visual Elements
- **Blue Gradient Background**: Deep blue (#1565C0) with lighter blue (#1976D2) wave overlay
- **White Car Silhouette**: Centered, simple car design with visible wheels and windows
- **Orange Magnifying Glass**: Search icon overlay (top-right) representing the app's search functionality
- **Dark Gray Wheels**: Realistic wheel design with white rims

### Color Palette
- **Primary Blue**: #1565C0 (background)
- **Accent Blue**: #1976D2 (overlay)
- **Search Orange**: #FF9800 (magnifying glass)
- **Wheel Gray**: #455A64
- **White**: #FFFFFF (car body and accents)

## Technical Details

### Vector Drawables
The icon uses Android vector drawables for perfect scalability:

- **ic_launcher_background.xml**: Blue gradient background with wave pattern
- **ic_launcher_foreground.xml**: Car with search magnifying glass

### Adaptive Icon
The icon is adaptive and works across all Android versions:
- API 26+: Full adaptive icon with background and foreground layers
- API 25 and below: Fallback to standard icon

## Files Structure

```
app/src/main/res/
├── drawable/
│   ├── ic_launcher_background.xml (Background layer)
│   └── ic_launcher_foreground.xml (Foreground layer with car + search)
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml (Adaptive icon config)
│   └── ic_launcher_round.xml (Round variant)
└── mipmap-*/
    └── (Legacy PNG icons generated from vector)
```

## Icon Meaning

The icon visually represents the app's core functionality:
- **Car**: Vehicle marketplace and automotive focus
- **Magnifying Glass**: Search and discovery features
- **Blue Theme**: Trust, reliability, professionalism

## Generating PNG Icons (Optional)

If you need PNG versions for legacy support or other platforms:

### Using Android Studio:
1. Right-click on `res` folder
2. New → Image Asset
3. Choose "Foreground Layer" and "Background Layer"
4. Select the vector drawable files
5. Generate all densities

### Using Command Line:
```bash
# Requires Android SDK tools
$ANDROID_HOME/tools/bin/avdmanager create avd ...
# Then export as PNG using Studio
```

## Testing the Icon

Build and install the app to see the icon:
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

The icon will appear on your home screen and app drawer.

## Customization

To modify the icon colors, edit:
- `ic_launcher_background.xml` - Change background gradient
- `ic_launcher_foreground.xml` - Change car/search colors

### Quick Color Changes:
- Background: Line 10 & 16 in background.xml
- Car color: Line 15 in foreground.xml
- Search icon: Line 48 in foreground.xml
- Wheels: Line 25 & 28 in foreground.xml

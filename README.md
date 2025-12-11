# Vehicle Search Android Application

A web crawler-based Android mobile application that helps you search for vehicles from dealers within a specified radius of your location. The app automatically checks for newly posted vehicles and sends notifications when matching vehicles become available.

## Features

- **Advanced Search**: Search for vehicles by make/model (e.g., "Toyota Sienna")
- **Location-Based**: Find dealers within 50 miles of any address (e.g., "3101 Bridlewood Dr, Rochester, MI 48306")
- **Flexible Filters**:
  - Maximum mileage
  - Year range
  - Fuel type (Hybrid, Gas, Electric, Diesel)
  - Condition (New, Used, Certified Pre-Owned)
- **Web Crawler**: Automatically scrapes multiple vehicle listing sites (Cars.com, AutoTrader)
- **Background Monitoring**: Periodically checks for newly posted vehicles every 6 hours
- **Push Notifications**: Get instant alerts when matching vehicles are posted
- **Dealer Contact**: Direct access to dealer phone numbers and location information

## Technical Architecture

### Technologies Used

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **Web Scraping**: Jsoup
- **Networking**: Retrofit + OkHttp
- **UI**: Material Design 3, View Binding
- **Async Operations**: Kotlin Coroutines

### Key Components

1. **Data Layer** (`app/src/main/java/com/vehiclesearch/data/`)
   - `Vehicle.kt`: Vehicle entity with all attributes
   - `SearchCriteria.kt`: Saved search parameters
   - `VehicleDatabase.kt`: Room database configuration
   - `VehicleRepository.kt`: Data access abstraction

2. **Network Layer** (`app/src/main/java/com/vehiclesearch/network/`)
   - `VehicleScraper.kt`: Interface for vehicle scrapers
   - `CarsComScraper.kt`: Cars.com web scraper implementation
   - `AutoTraderScraper.kt`: AutoTrader web scraper implementation
   - `ScraperManager.kt`: Coordinates multiple scrapers

3. **UI Layer** (`app/src/main/java/com/vehiclesearch/ui/`)
   - `MainActivity.kt`: Search form with filters
   - `SearchResultsActivity.kt`: Display search results
   - `VehicleDetailActivity.kt`: Detailed vehicle information
   - ViewModels for each activity

4. **Background Worker** (`app/src/main/java/com/vehiclesearch/worker/`)
   - `VehicleCheckWorker.kt`: Periodic background checks for new vehicles

5. **Utilities** (`app/src/main/java/com/vehiclesearch/utils/`)
   - `DistanceCalculator.kt`: Calculate distances using Haversine formula
   - `GeocodingUtil.kt`: Convert addresses to coordinates

## Building the Application

### Prerequisites

- Android Studio (Arctic Fox or later)
- JDK 17
- Android SDK 34
- Minimum Android version: API 26 (Android 8.0)

### Build Instructions

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd dulalalaladu
   ```

2. **Open in Android Studio**:
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

4. **Build Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Signing the Release APK (for Production)

1. **Create a keystore** (first time only):
   ```bash
   keytool -genkey -v -keystore my-release-key.keystore \
     -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Sign the APK**:
   ```bash
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore my-release-key.keystore \
     app/build/outputs/apk/release/app-release-unsigned.apk my-key-alias
   ```

3. **Align the APK**:
   ```bash
   zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk \
     VehicleSearch.apk
   ```

## Testing in Emulator

1. **Create an AVD** (Android Virtual Device):
   - Tools → AVD Manager → Create Virtual Device
   - Select Pixel 8 Pro or similar
   - System Image: API 34 (Android 14)

2. **Run the app**:
   ```bash
   ./gradlew installDebug
   ```
   Or click the "Run" button in Android Studio

## Installing on Pixel 8 Pro

### Method 1: USB Installation

1. **Enable Developer Options** on your Pixel 8 Pro:
   - Settings → About phone → Tap "Build number" 7 times
   - Settings → System → Developer options → Enable "USB debugging"

2. **Connect phone via USB**

3. **Install the APK**:
   ```bash
   adb install VehicleSearch.apk
   ```

### Method 2: Direct APK Installation

1. Transfer the APK file to your phone (email, cloud storage, etc.)
2. Open the APK file on your phone
3. Allow installation from unknown sources if prompted
4. Tap "Install"

## Usage

1. **First Launch**:
   - Grant location permissions (optional, for geocoding)
   - Grant notification permissions (for vehicle alerts)

2. **Search for Vehicles**:
   - Enter make/model (e.g., "Toyota Sienna")
   - Enter your address (e.g., "3101 Bridlewood Dr, Rochester, MI 48306")
   - Set filters: max mileage, year, fuel type, condition
   - Enable notifications to get alerts for new vehicles
   - Tap "Search"

3. **View Results**:
   - Browse vehicles sorted by distance
   - See price, mileage, dealer info
   - Tap "Call Dealer" to phone directly
   - Tap "View Details" for more information

4. **Background Monitoring**:
   - If notifications are enabled, the app checks every 6 hours
   - You'll receive push notifications for new matching vehicles
   - Tap notification to open the app

## Permissions

- **INTERNET**: Required for web scraping
- **ACCESS_NETWORK_STATE**: Check network connectivity
- **POST_NOTIFICATIONS**: Send alerts for new vehicles
- **ACCESS_FINE_LOCATION**: Geocode addresses (optional)
- **ACCESS_COARSE_LOCATION**: Geocode addresses (optional)

## Notes

- Web scraping functionality depends on the structure of external websites (Cars.com, AutoTrader)
- If these sites change their HTML structure, scrapers may need updates
- Background checks run every 6 hours when device is connected to internet
- Vehicle data is stored locally in SQLite database
- The app uses mock dealer coordinates; production version would need geocoding API

## Future Enhancements

- Additional vehicle listing sources (Craigslist, Facebook Marketplace, etc.)
- Save multiple search criteria
- Vehicle comparison feature
- Price history tracking
- Email notifications
- Map view of dealers
- Image gallery for vehicles
- VIN decoder integration

## License

This project is provided as-is for personal use.

## Support

For issues or questions, please contact the development team.

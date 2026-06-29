# Vehicle Search App - Feature Enhancement Guide

## ✅ Completed (Phase 1 & 2)

### Database & Data Model
- ✅ Added `UserPreferences` entity for phone number storage
- ✅ Added `UserPreferencesDao` for user settings
- ✅ Updated `Vehicle` entity with:
  - `imageUrls` field for multiple images
  - `isNew` flag for tracking new vehicles
  - `getImageList()` helper method
- ✅ Created comprehensive `CarMakesModels` utility (37 makes, 300+ models)
- ✅ Updated database version to 2
- ✅ Fixed all ViewModels and Worker to use updated repository

### Infrastructure
- ✅ Updated `VehicleRepository` with phone number methods
- ✅ Added LiveData for user preferences
- ✅ Updated `MainViewModel` to support separate make/model parameters

---

## 🔨 Remaining Implementation

###  1. Update MainActivity UI (Make/Model Dropdowns + Phone Number)

**File to modify:** `app/src/main/res/layout/activity_main.xml`

Add these fields (replace the existing makeModelEditText):

```xml
<!-- Make Dropdown -->
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Make"
    android:layout_marginBottom="12dp"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox.ExposedDropdownMenu">

    <AutoCompleteTextView
        android:id="@+id/makeSpinner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="none" />
</com.google.android.material.textfield.TextInputLayout>

<!-- Model Dropdown (dependent on Make) -->
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Model"
    android:layout_marginBottom="12dp"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox.ExposedDropdownMenu">

    <AutoCompleteTextView
        android:id="@+id/modelSpinner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="none"
        android:enabled="false" />
</com.google.android.material.textfield.TextInputLayout>

<!-- Phone Number for SMS Notifications -->
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Phone Number (for SMS alerts)"
    android:layout_marginBottom="12dp"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/phoneNumberEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone" />
</com.google.android.material.textfield.TextInputLayout>
```

**File to modify:** `app/src/main/java/com/vehiclesearch/ui/MainActivity.kt`

In `setupSpinners()` method, add:

```kotlin
// Make dropdown
val makes = CarMakesModels.getAllMakes()
(binding.makeSpinner as? MaterialAutoCompleteTextView)?.apply {
    setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, makes))

    // When make selected, populate models
    setOnItemClickListener { _, _, position, _ ->
        val selectedMake = makes[position]
        val models = CarMakesModels.getModelsForMake(selectedMake)

        (binding.modelSpinner as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, models))
            isEnabled = true
            setText("", false)
        }
    }
}
```

In `performSearch()` method, change to:

```kotlin
val make = binding.makeSpinner.text.toString().trim()
val model = binding.modelSpinner.text.toString().trim()
val phoneNumber = binding.phoneNumberEditText.text.toString().trim()

if (make.isEmpty()) {
    binding.makeSpinner.error = "Please select a make"
    return
}

if (model.isEmpty()) {
    binding.modelSpinner.error = "Please select a model"
    return
}

viewModel.searchVehicles(
    make = make,
    model = model,
    address = address,
    radiusMiles = 50,
    maxMileage = maxMileage,
    minYear = minYear,
    maxYear = null,
    fuelType = fuelType,
    condition = condition,
    enableNotifications = enableNotifications,
    phoneNumber = phoneNumber.ifEmpty { null }
)
```

---

### 2. Add SMS Notification Service (Twilio Integration)

**Add dependency** to `app/build.gradle.kts`:

```kotlin
// Twilio SMS
implementation("com.twilio.sdk:twilio:9.14.1")
```

**Create new file:** `app/src/main/java/com/vehiclesearch/utils/SmsNotificationService.kt`

```kotlin
package com.vehiclesearch.utils

import android.util.Log
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SmsNotificationService {
    private const val TAG = "SmsNotificationService"

    // TODO: Replace with your Twilio credentials
    private const val ACCOUNT_SID = "YOUR_TWILIO_ACCOUNT_SID"
    private const val AUTH_TOKEN = "YOUR_TWILIO_AUTH_TOKEN"
    private const val FROM_PHONE = "+1234567890" // Your Twilio phone number

    init {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
    }

    suspend fun sendVehicleAlert(
        toPhoneNumber: String,
        vehicleTitle: String,
        price: String,
        dealerName: String,
        listingUrl: String
    ) = withContext(Dispatchers.IO) {
        try {
            val messageBody = """
                New Vehicle Match!
                $vehicleTitle
                Price: $price
                Dealer: $dealerName
                View: $listingUrl
            """.trimIndent()

            Message.creator(
                PhoneNumber(toPhoneNumber),
                PhoneNumber(FROM_PHONE),
                messageBody
            ).create()

            Log.d(TAG, "SMS sent to $toPhoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS: ${e.message}")
        }
    }
}
```

**Update** `VehicleCheckWorker.kt` to send SMS when new vehicles found:

```kotlin
private suspend fun sendNotification(vehicle: Vehicle, search: SearchCriteria) {
    // Get user phone number
    val userPrefs = repository.getUserPreferencesSync()
    val phoneNumber = userPrefs?.phoneNumber

    // Send SMS if phone number exists
    if (!phoneNumber.isNullOrEmpty() && userPrefs.smsNotificationsEnabled) {
        SmsNotificationService.sendVehicleAlert(
            toPhoneNumber = phoneNumber,
            vehicleTitle = vehicle.title,
            price = vehicle.price,
            dealerName = vehicle.dealerName,
            listingUrl = vehicle.listingUrl
        )
    }

    // Send push notification as before...
    withContext(Dispatchers.Main) {
        // existing notification code...
    }
}
```

---

### 3. Add Vehicle Feed/History Activity

**Create new file:** `app/src/main/res/layout/activity_vehicle_feed.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@color/primary"
        app:title="New Vehicles Feed"
        app:titleTextColor="@color/white" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/feedRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"
        android:padding="8dp" />

</LinearLayout>
```

**Create new file:** `app/src/main/java/com/vehiclesearch/ui/VehicleFeedActivity.kt`

```kotlin
package com.vehiclesearch.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vehiclesearch.databinding.ActivityVehicleFeedBinding

class VehicleFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleFeedBinding
    private val viewModel: SearchResultsViewModel by viewModels()
    private lateinit var adapter: VehicleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeNewVehicles()
    }

    private fun setupRecyclerView() {
        adapter = VehicleAdapter { vehicle ->
            // Open detail page
        }
        binding.feedRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@VehicleFeedActivity)
            adapter = this@VehicleFeedActivity.adapter
        }
    }

    private fun observeNewVehicles() {
        viewModel.vehicles.observe(this) { vehicles ->
            // Filter only new vehicles
            val newVehicles = vehicles.filter { it.isNew }
            adapter.submitList(newVehicles)
        }
    }
}
```

---

### 4. Add More Scrapers (CarGurus, eBay Motors)

**Create:** `app/src/main/java/com/vehiclesearch/network/CarGurusScraper.kt`

```kotlin
package com.vehiclesearch.network

import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.utils.DistanceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class CarGurusScraper : VehicleScraper {
    private val BASE_URL = "https://www.cargurus.com"

    override suspend fun searchVehicles(
        makeModel: String,
        latitude: Double,
        longitude: Double,
        radiusMiles: Int,
        maxMileage: Int?,
        minYear: Int?,
        maxYear: Int?,
        fuelType: String?,
        condition: String?
    ): List<Vehicle> = withContext(Dispatchers.IO) {
        val vehicles = mutableListOf<Vehicle>()

        try {
            val parts = makeModel.split(" ")
            val make = parts.getOrNull(0) ?: return@withContext emptyList()
            val model = parts.drop(1).joinToString("-")

            val url = "$BASE_URL/Cars/$make-$model"

            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(15000)
                .get()

            // Parse CarGurus specific HTML structure
            doc.select("div.cg-dealFinder-result").forEach { listing ->
                // Extract vehicle data from listing
                // This is a template - actual selectors need to match CarGurus' HTML
                vehicles.add(parseCarGurusListing(listing, searchLat, searchLon))
            }
        } catch (e: Exception) {
            // Handle errors
        }

        vehicles
    }

    override fun getSourceName() = "CarGurus"

    private fun parseCarGurusListing(element: org.jsoup.nodes.Element, lat: Double, lon: Double): Vehicle {
        // Implementation details...
    }
}
```

**Update:** `app/src/main/java/com/vehiclesearch/network/ScraperManager.kt`

```kotlin
private val scrapers = listOf(
    CarsComScraper(),
    AutoTraderScraper(),
    CarGurusScraper(),
    // Add more scrapers here
)
```

---

### 5. Enhanced Vehicle Detail with Image Gallery

**Add dependency:**

```kotlin
// Image loading library
implementation("io.coil-kt:coil:2.5.0")
```

**Update:** `app/src/main/res/layout/activity_vehicle_detail.xml`

Add ViewPager for image gallery at the top:

```xml
<androidx.viewpager2.widget.ViewPager2
    android:id="@+id/imageViewPager"
    android:layout_width="match_parent"
    android:layout_height="250dp"
    android:background="@color/black" />

<com.google.android.material.tabs.TabLayout
    android:id="@+id/imageIndicator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:tabBackground="@drawable/tab_selector"
    app:tabGravity="center"
    app:tabIndicatorHeight="0dp" />
```

**Create image adapter:** `app/src/main/java/com/vehiclesearch/ui/ImagePagerAdapter.kt`

```kotlin
package com.vehiclesearch.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vehiclesearch.databinding.ItemImageBinding

class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount() = imageUrls.size

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            binding.imageView.load(imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}
```

**Update VehicleDetailActivity:**

```kotlin
private fun setupImageGallery(images: List<String>) {
    val adapter = ImagePagerAdapter(images)
    binding.imageViewPager.adapter = adapter

    TabLayoutMediator(binding.imageIndicator, binding.imageViewPager) { _, _ ->
    }.attach()
}

// In setupObservers():
vehicle?.let {
    val images = it.getImageList()
    if (images.isNotEmpty()) {
        setupImageGallery(images)
    }
    // ...rest of binding
}
```

---

## 📋 Summary

### What's Working Now:
- ✅ Database schema updated
- ✅ Phone number storage ready
- ✅ Make/Model data complete
- ✅ All ViewModels fixed

### What Needs Implementation:
1. **UI Updates** - Add Make/Model dropdowns and phone number field to MainActivity
2. **SMS Integration** - Set up Twilio account and integrate SMS sending
3. **Vehicle Feed** - Create new activity to show newly found vehicles
4. **More Scrapers** - Implement CarGurus, eBay Motors scrapers
5. **Image Gallery** - Add ViewPager2 for multiple vehicle images

### Testing After Implementation:
```bash
./gradlew clean assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔐 Security Notes

- **SMS Credentials**: Never commit Twilio credentials to Git
- Use environment variables or Android BuildConfig for API keys
- Consider using Firebase Cloud Messaging as free alternative to Twilio

---

## 📱 Next Steps

1. Implement UI changes (easiest, start here)
2. Add more scrapers (moderate difficulty)
3. Add image gallery (moderate)
4. Create vehicle feed (moderate)
5. Integrate SMS (requires Twilio account)

Each feature can be tested independently!

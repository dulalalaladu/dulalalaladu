package com.vehiclesearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val make: String,
    val model: String,
    val year: Int,
    val price: String,
    val mileage: Int,
    val fuelType: String, // Hybrid, Gas, Electric, Diesel
    val condition: String, // New, Used, Certified
    val dealerName: String,
    val dealerPhone: String,
    val dealerAddress: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double, // Distance from search address in miles
    val imageUrl: String?,
    val imageUrls: String? = null, // Comma-separated list of image URLs
    val listingUrl: String,
    val description: String?,
    val vin: String?,
    val source: String, // e.g., "Cars.com", "AutoTrader", etc.
    val datePosted: Long, // Timestamp
    val lastChecked: Long = System.currentTimeMillis(),
    val isNotified: Boolean = false,
    val isNew: Boolean = true // Mark as new when first added
) {
    fun getImageList(): List<String> {
        val images = mutableListOf<String>()
        imageUrls?.split(",")?.forEach { url ->
            url.trim().takeIf { it.isNotEmpty() }?.let { images.add(it) }
        }
        // Fallback to single imageUrl if no imageUrls
        if (images.isEmpty() && !imageUrl.isNullOrEmpty()) {
            images.add(imageUrl)
        }
        return images
    }
}

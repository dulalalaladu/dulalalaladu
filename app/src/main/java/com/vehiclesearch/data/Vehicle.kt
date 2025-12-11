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
    val listingUrl: String,
    val description: String?,
    val vin: String?,
    val source: String, // e.g., "Cars.com", "AutoTrader", etc.
    val datePosted: Long, // Timestamp
    val lastChecked: Long = System.currentTimeMillis(),
    val isNotified: Boolean = false
)

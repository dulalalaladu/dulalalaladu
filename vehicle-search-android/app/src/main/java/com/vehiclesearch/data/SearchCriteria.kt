package com.vehiclesearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_criteria")
data class SearchCriteria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val makeModel: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMiles: Int = 50,
    val maxMileage: Int?,
    val minYear: Int?,
    val maxYear: Int?,
    val fuelType: String?, // null means "Any"
    val condition: String?, // null means "Any"
    val notificationsEnabled: Boolean = true,
    val lastSearched: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

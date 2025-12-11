package com.vehiclesearch.network

import com.vehiclesearch.data.Vehicle

interface VehicleScraper {
    suspend fun searchVehicles(
        makeModel: String,
        latitude: Double,
        longitude: Double,
        radiusMiles: Int,
        maxMileage: Int?,
        minYear: Int?,
        maxYear: Int?,
        fuelType: String?,
        condition: String?
    ): List<Vehicle>

    fun getSourceName(): String
}

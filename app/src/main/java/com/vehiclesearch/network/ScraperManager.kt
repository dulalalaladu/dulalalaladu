package com.vehiclesearch.network

import android.util.Log
import com.vehiclesearch.data.Vehicle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ScraperManager {
    private val TAG = "ScraperManager"
    private val scrapers = listOf(
        CarsComScraper(),
        AutoTraderScraper()
    )

    suspend fun searchAllSources(
        makeModel: String,
        latitude: Double,
        longitude: Double,
        radiusMiles: Int,
        maxMileage: Int?,
        minYear: Int?,
        maxYear: Int?,
        fuelType: String?,
        condition: String?
    ): List<Vehicle> = coroutineScope {
        Log.d(TAG, "Starting search across ${scrapers.size} sources")

        val results = scrapers.map { scraper ->
            async {
                try {
                    Log.d(TAG, "Searching ${scraper.getSourceName()}...")
                    scraper.searchVehicles(
                        makeModel, latitude, longitude, radiusMiles,
                        maxMileage, minYear, maxYear, fuelType, condition
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error with ${scraper.getSourceName()}: ${e.message}")
                    emptyList()
                }
            }
        }.awaitAll()

        val allVehicles = results.flatten()
        Log.d(TAG, "Found ${allVehicles.size} total vehicles")

        // Remove duplicates based on similar titles and prices
        val uniqueVehicles = allVehicles
            .distinctBy { "${it.title}_${it.price}_${it.dealerName}" }
            .sortedBy { it.distance }

        Log.d(TAG, "After deduplication: ${uniqueVehicles.size} vehicles")

        uniqueVehicles
    }
}

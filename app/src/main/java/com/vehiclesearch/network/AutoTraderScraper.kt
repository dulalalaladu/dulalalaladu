package com.vehiclesearch.network

import android.util.Log
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.utils.DistanceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.URLEncoder

class AutoTraderScraper : VehicleScraper {
    private val TAG = "AutoTraderScraper"
    private val BASE_URL = "https://www.autotrader.com"

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
            val searchUrl = buildSearchUrl(
                makeModel, latitude, longitude, radiusMiles,
                maxMileage, minYear, maxYear, fuelType, condition
            )

            Log.d(TAG, "Searching: $searchUrl")

            val document: Document = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(15000)
                .get()

            val listings = document.select("div.inventory-listing")

            Log.d(TAG, "Found ${listings.size} listings")

            for (listing in listings) {
                try {
                    val vehicle = parseVehicleListing(listing, latitude, longitude)
                    vehicle?.let { vehicles.add(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing listing: ${e.message}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error scraping AutoTrader: ${e.message}")
        }

        vehicles
    }

    private fun buildSearchUrl(
        makeModel: String,
        latitude: Double,
        longitude: Double,
        radiusMiles: Int,
        maxMileage: Int?,
        minYear: Int?,
        maxYear: Int?,
        fuelType: String?,
        condition: String?
    ): String {
        val zip = "48306" // Default for demo
        val parts = makeModel.trim().split(" ", limit = 2)
        val make = parts.getOrNull(0)?.let { URLEncoder.encode(it, "UTF-8") } ?: ""
        val model = parts.getOrNull(1)?.let { URLEncoder.encode(it, "UTF-8") } ?: ""

        var url = "$BASE_URL/cars-for-sale/all-cars"

        val params = mutableListOf<String>()
        params.add("zip=$zip")
        params.add("searchRadius=$radiusMiles")

        if (make.isNotEmpty()) params.add("makeCodeList=$make")
        if (model.isNotEmpty()) params.add("modelCodeList=$model")

        maxMileage?.let { params.add("maxMileage=$it") }
        minYear?.let { params.add("startYear=$it") }
        maxYear?.let { params.add("endYear=$it") }

        when (condition?.lowercase()) {
            "new" -> params.add("listingTypes=NEW")
            "used" -> params.add("listingTypes=USED")
            "certified" -> params.add("listingTypes=CERTIFIED")
        }

        params.add("numRecords=100")

        return "$url?${params.joinToString("&")}"
    }

    private fun parseVehicleListing(element: Element, searchLat: Double, searchLon: Double): Vehicle? {
        try {
            val title = element.select("h2.text-bold").text()
            val priceText = element.select("div.first-price").text()
                .replace("$", "").replace(",", "").trim()
            val price = if (priceText.isEmpty()) "Call for price" else "$$priceText"

            val mileageText = element.select("div.item-card-basics").text()
                .replace("[^0-9]".toRegex(), "")
            val mileage = mileageText.toIntOrNull() ?: 0

            val link = element.select("a.item-card-link").attr("href")
            val listingUrl = if (link.startsWith("http")) link else "$BASE_URL$link"

            val imageUrl = element.select("img.item-image").attr("src")

            val dealerName = element.select("div.dealer-name").text()
            val location = element.select("div.item-location").text()

            // Extract year, make, model from title
            val titleParts = title.split(" ")
            val year = titleParts.firstOrNull()?.toIntOrNull() ?: 2020
            val make = titleParts.getOrNull(1) ?: "Unknown"
            val model = titleParts.drop(2).joinToString(" ")

            val dealerPhone = "(248) 555-0200"
            val dealerAddress = location

            // Mock coordinates
            val dealerLat = searchLat + (Math.random() - 0.5) * 0.5
            val dealerLon = searchLon + (Math.random() - 0.5) * 0.5

            val distance = DistanceCalculator.calculateDistance(
                searchLat, searchLon, dealerLat, dealerLon
            )

            return Vehicle(
                title = title,
                make = make,
                model = model,
                year = year,
                price = price,
                mileage = mileage,
                fuelType = "Gas",
                condition = if (year >= 2023) "New" else "Used",
                dealerName = dealerName.ifEmpty { "AutoTrader Dealer" },
                dealerPhone = dealerPhone,
                dealerAddress = dealerAddress,
                latitude = dealerLat,
                longitude = dealerLon,
                distance = distance,
                imageUrl = imageUrl,
                listingUrl = listingUrl,
                description = null,
                vin = null,
                source = "AutoTrader",
                datePosted = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing vehicle: ${e.message}")
            return null
        }
    }

    override fun getSourceName(): String = "AutoTrader"
}

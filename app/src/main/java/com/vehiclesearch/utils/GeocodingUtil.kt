package com.vehiclesearch.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object GeocodingUtil {
    private const val TAG = "GeocodingUtil"

    data class LatLng(val latitude: Double, val longitude: Double)

    suspend fun getCoordinatesFromAddress(context: Context, address: String): LatLng? =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val location = addresses[0]
                    Log.d(TAG, "Geocoded: $address -> ${location.latitude}, ${location.longitude}")
                    return@withContext LatLng(location.latitude, location.longitude)
                } else {
                    Log.w(TAG, "No results for address: $address")
                    // Return default coordinates (Rochester, MI) if geocoding fails
                    return@withContext LatLng(42.6803, -83.1338)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Geocoding error: ${e.message}")
                // Return default coordinates (Rochester, MI)
                return@withContext LatLng(42.6803, -83.1338)
            }
        }

    suspend fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                return@withContext address.getAddressLine(0)
            }
            null
        } catch (e: IOException) {
            Log.e(TAG, "Reverse geocoding error: ${e.message}")
            null
        }
    }
}

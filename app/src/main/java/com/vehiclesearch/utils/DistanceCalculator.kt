package com.vehiclesearch.utils

import kotlin.math.*

object DistanceCalculator {
    private const val EARTH_RADIUS_MILES = 3958.8

    /**
     * Calculate distance between two points using Haversine formula
     * Returns distance in miles
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_MILES * c
    }

    /**
     * Check if a point is within a radius of another point
     */
    fun isWithinRadius(
        centerLat: Double,
        centerLon: Double,
        pointLat: Double,
        pointLon: Double,
        radiusMiles: Double
    ): Boolean {
        val distance = calculateDistance(centerLat, centerLon, pointLat, pointLon)
        return distance <= radiusMiles
    }

    /**
     * Format distance for display
     */
    fun formatDistance(miles: Double): String {
        return String.format("%.1f mi", miles)
    }
}

package com.vehiclesearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vehiclesearch.data.*
import com.vehiclesearch.network.ScraperManager
import com.vehiclesearch.utils.GeocodingUtil
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VehicleDatabase.getDatabase(application)
    private val repository = VehicleRepository(
        database.vehicleDao(),
        database.searchCriteriaDao()
    )
    private val scraperManager = ScraperManager()

    private val _searchResults = MutableLiveData<List<Vehicle>>()
    val searchResults: LiveData<List<Vehicle>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun searchVehicles(
        makeModel: String,
        address: String,
        radiusMiles: Int,
        maxMileage: Int?,
        minYear: Int?,
        maxYear: Int?,
        fuelType: String?,
        condition: String?,
        enableNotifications: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Geocode address
                val coordinates = GeocodingUtil.getCoordinatesFromAddress(
                    getApplication(),
                    address
                )

                if (coordinates == null) {
                    _error.value = "Unable to find location. Please check the address."
                    _isLoading.value = false
                    return@launch
                }

                // Save search criteria
                val criteria = SearchCriteria(
                    makeModel = makeModel,
                    address = address,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                    radiusMiles = radiusMiles,
                    maxMileage = maxMileage,
                    minYear = minYear,
                    maxYear = maxYear,
                    fuelType = fuelType,
                    condition = condition,
                    notificationsEnabled = enableNotifications
                )
                repository.insertSearchCriteria(criteria)

                // Search vehicles
                val vehicles = scraperManager.searchAllSources(
                    makeModel = makeModel,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                    radiusMiles = radiusMiles,
                    maxMileage = maxMileage,
                    minYear = minYear,
                    maxYear = maxYear,
                    fuelType = fuelType,
                    condition = condition
                )

                // Filter by distance
                val filteredVehicles = vehicles.filter { it.distance <= radiusMiles }

                // Save to database
                repository.deleteAllVehicles() // Clear old results
                repository.insertVehicles(filteredVehicles)

                _searchResults.value = filteredVehicles
                _isLoading.value = false

            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

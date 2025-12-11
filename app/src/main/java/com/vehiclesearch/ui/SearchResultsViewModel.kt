package com.vehiclesearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.data.VehicleDatabase
import com.vehiclesearch.data.VehicleRepository

class SearchResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VehicleDatabase.getDatabase(application)
    private val repository = VehicleRepository(
        database.vehicleDao(),
        database.searchCriteriaDao()
    )

    val vehicles: LiveData<List<Vehicle>> = repository.allVehicles.map { list ->
        list.sortedBy { it.distance }
    }
}

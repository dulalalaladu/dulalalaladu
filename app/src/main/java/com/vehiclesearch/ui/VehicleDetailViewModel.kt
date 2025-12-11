package com.vehiclesearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.data.VehicleDatabase
import com.vehiclesearch.data.VehicleRepository
import kotlinx.coroutines.launch

class VehicleDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VehicleDatabase.getDatabase(application)
    private val repository = VehicleRepository(
        database.vehicleDao(),
        database.searchCriteriaDao()
    )

    private val _vehicle = MutableLiveData<Vehicle?>()
    val vehicle: LiveData<Vehicle?> = _vehicle

    fun loadVehicle(vehicleId: Long) {
        viewModelScope.launch {
            val v = repository.getVehicleById(vehicleId)
            _vehicle.value = v
        }
    }
}

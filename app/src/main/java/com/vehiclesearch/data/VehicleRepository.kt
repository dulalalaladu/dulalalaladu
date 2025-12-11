package com.vehiclesearch.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleRepository(
    private val vehicleDao: VehicleDao,
    private val searchCriteriaDao: SearchCriteriaDao
) {
    val allVehicles: LiveData<List<Vehicle>> = vehicleDao.getAllVehicles()
    val allSearchCriteria: LiveData<List<SearchCriteria>> = searchCriteriaDao.getAllSearchCriteria()

    suspend fun insertVehicle(vehicle: Vehicle): Long = withContext(Dispatchers.IO) {
        vehicleDao.insertVehicle(vehicle)
    }

    suspend fun insertVehicles(vehicles: List<Vehicle>) = withContext(Dispatchers.IO) {
        vehicleDao.insertVehicles(vehicles)
    }

    suspend fun getVehicleById(id: Long): Vehicle? = withContext(Dispatchers.IO) {
        vehicleDao.getVehicleById(id)
    }

    suspend fun getVehicleByUrl(url: String): Vehicle? = withContext(Dispatchers.IO) {
        vehicleDao.getVehicleByUrl(url)
    }

    suspend fun getUnnotifiedVehicles(timestamp: Long): List<Vehicle> = withContext(Dispatchers.IO) {
        vehicleDao.getUnnotifiedVehicles(timestamp)
    }

    suspend fun markAsNotified(id: Long) = withContext(Dispatchers.IO) {
        vehicleDao.markAsNotified(id)
    }

    suspend fun deleteAllVehicles() = withContext(Dispatchers.IO) {
        vehicleDao.deleteAll()
    }

    suspend fun insertSearchCriteria(criteria: SearchCriteria): Long = withContext(Dispatchers.IO) {
        searchCriteriaDao.insertSearchCriteria(criteria)
    }

    suspend fun getActiveSearchCriteria(): List<SearchCriteria> = withContext(Dispatchers.IO) {
        searchCriteriaDao.getActiveSearchCriteria()
    }

    suspend fun getSearchCriteriaById(id: Long): SearchCriteria? = withContext(Dispatchers.IO) {
        searchCriteriaDao.getSearchCriteriaById(id)
    }

    suspend fun updateSearchCriteria(criteria: SearchCriteria) = withContext(Dispatchers.IO) {
        searchCriteriaDao.updateSearchCriteria(criteria)
    }

    suspend fun updateLastSearched(id: Long, timestamp: Long) = withContext(Dispatchers.IO) {
        searchCriteriaDao.updateLastSearched(id, timestamp)
    }

    suspend fun getVehicleCount(): Int = withContext(Dispatchers.IO) {
        vehicleDao.getVehicleCount()
    }
}

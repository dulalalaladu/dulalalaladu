package com.vehiclesearch.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY datePosted DESC")
    fun getAllVehicles(): LiveData<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: Long): Vehicle?

    @Query("SELECT * FROM vehicles WHERE listingUrl = :url")
    suspend fun getVehicleByUrl(url: String): Vehicle?

    @Query("SELECT * FROM vehicles WHERE datePosted > :timestamp AND isNotified = 0")
    suspend fun getUnnotifiedVehicles(timestamp: Long): List<Vehicle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<Vehicle>)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Query("UPDATE vehicles SET isNotified = 1 WHERE id = :id")
    suspend fun markAsNotified(id: Long)

    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getVehicleCount(): Int
}

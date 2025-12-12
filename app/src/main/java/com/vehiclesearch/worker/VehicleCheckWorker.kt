package com.vehiclesearch.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vehiclesearch.R
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.data.VehicleDatabase
import com.vehiclesearch.data.VehicleRepository
import com.vehiclesearch.network.ScraperManager
import com.vehiclesearch.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "VehicleCheckWorker"
    private val database = VehicleDatabase.getDatabase(context)
    private val repository = VehicleRepository(
        database.vehicleDao(),
        database.searchCriteriaDao(),
        database.userPreferencesDao()
    )
    private val scraperManager = ScraperManager()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting periodic vehicle check...")

            val activeSearches = repository.getActiveSearchCriteria()
            Log.d(TAG, "Found ${activeSearches.size} active searches")

            for (search in activeSearches) {
                try {
                    checkForNewVehicles(search)
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking search ${search.id}: ${e.message}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Worker failed: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun checkForNewVehicles(search: com.vehiclesearch.data.SearchCriteria) {
        Log.d(TAG, "Checking for new vehicles matching: ${search.makeModel}")

        val lastCheckTime = search.lastSearched

        val vehicles = scraperManager.searchAllSources(
            makeModel = search.makeModel,
            latitude = search.latitude,
            longitude = search.longitude,
            radiusMiles = search.radiusMiles,
            maxMileage = search.maxMileage,
            minYear = search.minYear,
            maxYear = search.maxYear,
            fuelType = search.fuelType,
            condition = search.condition
        )

        val newVehicles = vehicles.filter { vehicle ->
            vehicle.datePosted > lastCheckTime &&
            repository.getVehicleByUrl(vehicle.listingUrl) == null
        }

        Log.d(TAG, "Found ${newVehicles.size} new vehicles")

        if (newVehicles.isNotEmpty()) {
            repository.insertVehicles(newVehicles)

            for (vehicle in newVehicles) {
                sendNotification(vehicle, search)
            }

            repository.updateLastSearched(search.id, System.currentTimeMillis())
        }
    }

    private suspend fun sendNotification(vehicle: Vehicle, search: com.vehiclesearch.data.SearchCriteria) {
        withContext(Dispatchers.Main) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            createNotificationChannel(notificationManager)

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                vehicle.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(applicationContext.getString(R.string.new_vehicle_notification))
                .setContentText("${vehicle.title} - ${vehicle.price}")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("${vehicle.title}\n${vehicle.price}\n${vehicle.dealerName}")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(vehicle.id.toInt(), notification)

            repository.markAsNotified(vehicle.id)
            Log.d(TAG, "Notification sent for vehicle: ${vehicle.title}")
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = applicationContext.getString(R.string.notification_channel_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "vehicle_notifications"
        const val WORK_NAME = "vehicle_check_work"
    }
}

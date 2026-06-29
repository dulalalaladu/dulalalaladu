package com.vehiclesearch

import android.app.Application
import androidx.work.*
import com.vehiclesearch.worker.VehicleCheckWorker
import java.util.concurrent.TimeUnit

class VehicleSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupPeriodicVehicleCheck()
    }

    private fun setupPeriodicVehicleCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<VehicleCheckWorker>(
            6, TimeUnit.HOURS  // Check every 6 hours
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            VehicleCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

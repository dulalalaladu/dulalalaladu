package com.vehiclesearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: Int = 1, // Single row for user preferences
    val phoneNumber: String? = null,
    val smsNotificationsEnabled: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

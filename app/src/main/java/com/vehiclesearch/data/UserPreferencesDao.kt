package com.vehiclesearch.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): LiveData<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getUserPreferencesSync(): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreferences(preferences: UserPreferences)

    @Query("UPDATE user_preferences SET phoneNumber = :phoneNumber, smsNotificationsEnabled = :enabled WHERE id = 1")
    suspend fun updatePhoneNumber(phoneNumber: String?, enabled: Boolean)

    @Query("UPDATE user_preferences SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSync(timestamp: Long)
}

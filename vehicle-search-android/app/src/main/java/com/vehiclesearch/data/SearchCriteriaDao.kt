package com.vehiclesearch.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SearchCriteriaDao {
    @Query("SELECT * FROM search_criteria ORDER BY lastSearched DESC")
    fun getAllSearchCriteria(): LiveData<List<SearchCriteria>>

    @Query("SELECT * FROM search_criteria WHERE notificationsEnabled = 1")
    suspend fun getActiveSearchCriteria(): List<SearchCriteria>

    @Query("SELECT * FROM search_criteria WHERE id = :id")
    suspend fun getSearchCriteriaById(id: Long): SearchCriteria?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchCriteria(criteria: SearchCriteria): Long

    @Update
    suspend fun updateSearchCriteria(criteria: SearchCriteria)

    @Query("UPDATE search_criteria SET lastSearched = :timestamp WHERE id = :id")
    suspend fun updateLastSearched(id: Long, timestamp: Long)

    @Query("DELETE FROM search_criteria WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM search_criteria")
    suspend fun deleteAll()
}

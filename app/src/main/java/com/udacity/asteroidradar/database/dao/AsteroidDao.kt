package com.udacity.asteroidradar.database.dao

import androidx.room.*
import com.udacity.asteroidradar.database.entity.DatabaseAsteroid
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {
    // The question is, does the information change on the service side?
    // I don't know... Let's assume yes.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(vararg asteroids: DatabaseAsteroid)
    @Query("DELETE FROM asteroids WHERE close_approach_date<:date")
    fun deleteOlderThan(date: String)
    @Query("SELECT * FROM asteroids WHERE close_approach_date>=:date ORDER BY close_approach_date ASC, id ASC")
    fun getFromDate(date: String): Flow<List<DatabaseAsteroid>>
    @Query("SELECT * FROM asteroids WHERE close_approach_date=:date ORDER BY id ASC")
    fun getByDate(date: String): Flow<List<DatabaseAsteroid>>
    @Query("SELECT * FROM asteroids WHERE close_approach_date>=:startDate AND close_approach_date<:endDate ORDER BY close_approach_date ASC, id ASC")
    fun getByDateRange(startDate: String, endDate: String): Flow<List<DatabaseAsteroid>>
}
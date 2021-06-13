package com.udacity.asteroidradar.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.database.entity.DatabasePictureOfDay
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureOfDayDao {
    // no need to update
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(picture: DatabasePictureOfDay)
    @Query("SELECT * FROM picture_of_day ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<DatabasePictureOfDay?>
    @Query("SELECT * FROM picture_of_day WHERE date=:date")
    fun getByDate(date: String): Flow<DatabasePictureOfDay?>
}
package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.database.dao.AsteroidDao
import com.udacity.asteroidradar.database.dao.PictureOfDayDao
import com.udacity.asteroidradar.database.entity.DatabaseAsteroid
import com.udacity.asteroidradar.database.entity.DatabasePictureOfDay

@Database(entities = [DatabasePictureOfDay::class, DatabaseAsteroid::class], version = 2, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract fun pictureOfDayDao(): PictureOfDayDao
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AsteroidDatabase

        fun getInstance(context: Context): AsteroidDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context, AsteroidDatabase::class.java, "asteroid_db")
                        .fallbackToDestructiveMigration() // no need to do proper migration in our case
                        .build()
                }
            }

            return INSTANCE
        }
    }
}
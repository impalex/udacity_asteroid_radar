package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.getDateString
import com.udacity.asteroidradar.api.model.toDatabase
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val latestPicture = database.pictureOfDayDao().getLatest()

    fun getPictureByDate(date: Date) = database.pictureOfDayDao().getByDate(getDateString(date))

    fun getAsteroidsFromDate(date: Date) = database.asteroidDao().getFromDate(getDateString(date))

    fun getAsteroidsByDate(date: Date) = database.asteroidDao().getByDate(getDateString(date))

    fun getAsteroidsByDateRange(startDate: Date, days: Int) =
        database.asteroidDao().getByDateRange(getDateString(startDate), getDateString(startDate.addDays(days)))

    fun deleteOlderThan(date: Date) = database.asteroidDao().deleteOlderThan(getDateString(date))

    suspend fun updatePictureOfDay(date: Date) {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Updating the picture of a day")
                val pictureOfDay = try {
                    // using date explicitly coz our device can travel in time
                    NasaApi.service.getTodayPicture(getDateString(date))
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        // we can receive 404 coz of timezones (or date in future), let's fallback to default
                        NasaApi.service.getTodayPicture(null)
                    } else throw e
                }
                if (pictureOfDay.isImage) {
                    // Don't save anything except images
                    database.pictureOfDayDao().add(pictureOfDay.toDatabase())
                    Timber.d("Picture of a day is updated")
                }
            } catch (e: Exception) {
                Timber.e(e, "Unable to get the picture of a day")
            }
        }
    }

    suspend fun updateAsteroids(date: Date) {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Updating asteroids")
                val asteroidsRaw = NasaApi.service.getAsteroidsRaw(getDateString(date), getDateString(date.addDays(7)))
                val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsRaw))
                database.asteroidDao().addAll(*asteroids.map { it.toDatabase() }.toTypedArray())
                Timber.d("Asteroid list updated")
            } catch (e: Exception) {
                Timber.e(e, "Unable to get asteroids")
            }
        }
    }

    private fun Date.addDays(days: Int) = Calendar.getInstance().apply {
        time = this@addDays
        add(Calendar.DAY_OF_YEAR, days)
    }.time

    companion object {
        @Volatile
        private lateinit var INSTANCE: AsteroidRepository

        fun getInstance(database: AsteroidDatabase): AsteroidRepository {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = AsteroidRepository(database)
                }
            }
            return INSTANCE
        }

    }
}
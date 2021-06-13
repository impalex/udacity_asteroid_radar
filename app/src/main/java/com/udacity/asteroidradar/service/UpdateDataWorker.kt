package com.udacity.asteroidradar.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.util.*

class UpdateDataWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository.getInstance(database)
        val date = Calendar.getInstance().time

        try {
            repository.deleteOlderThan(date)
            repository.updateAsteroids(date)
            repository.updatePictureOfDay(date)
        } catch (_: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "UpdateAsteroidsDataWork"
    }
}
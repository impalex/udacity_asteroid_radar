package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.service.UpdateDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AsteroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        CoroutineScope(Dispatchers.Default).launch { setupWorker() }
    }

    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }
            .build()
        val request = PeriodicWorkRequestBuilder<UpdateDataWorker>(1, TimeUnit.DAYS).setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(UpdateDataWorker.WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

}
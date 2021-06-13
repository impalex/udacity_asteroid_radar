package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class AsteroidListMode { TODAY, WEEK, ALL }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val repository = AsteroidRepository.getInstance(database)

    private val _currentDate = MutableStateFlow(getToday())
    private val _currentAsteroidListMode = MutableStateFlow(AsteroidListMode.WEEK)

    val currentAsteroidListMode: StateFlow<AsteroidListMode>
        get() = _currentAsteroidListMode

    // get picture from _currentDate or latest cached if there is no picture from _currentDate
    @ExperimentalCoroutinesApi
    val pictureOfDay = _currentDate
        .flatMapLatest {
            repository.getPictureByDate(it)
        }
        .combine(repository.latestPicture) { _pictureOfSpecificDate, _latestPicture ->
            _pictureOfSpecificDate ?: _latestPicture
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @ExperimentalCoroutinesApi
    val asteroids = _currentDate
        .combine(_currentAsteroidListMode) { _date, _mode ->
            Pair(_date, _mode)
        }
        .flatMapLatest {
            when (it.second) {
                AsteroidListMode.ALL -> repository.getAsteroidsFromDate(it.first)
                AsteroidListMode.TODAY -> repository.getAsteroidsByDate(it.first)
                AsteroidListMode.WEEK -> repository.getAsteroidsByDateRange(it.first, 7)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            _currentDate.collect {
                repository.updatePictureOfDay(it)
                repository.updateAsteroids(it)
            }
        }
    }

    private fun getToday(): Date = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    fun updateToday() {
        // Just in case... Now we can travel in time.
        // "Roads?! Where we're going we don't need roads!!!"
        _currentDate.value = getToday()
    }

    fun setListMode(mode: AsteroidListMode) {
        _currentAsteroidListMode.value = mode
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Invalid ViewModel class")
        }

    }
}
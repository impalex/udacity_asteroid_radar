package com.udacity.asteroidradar.api.model

import com.udacity.asteroidradar.database.entity.DatabaseAsteroid

data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun Asteroid.toDatabase(): DatabaseAsteroid =
    DatabaseAsteroid(id, codename, closeApproachDate, absoluteMagnitude, estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)

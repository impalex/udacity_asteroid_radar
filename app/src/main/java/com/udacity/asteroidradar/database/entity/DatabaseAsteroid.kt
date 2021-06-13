package com.udacity.asteroidradar.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "asteroids")
data class DatabaseAsteroid(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val codename: String,
    @ColumnInfo(name = "close_approach_date")
    val closeApproachDate: String, // In our case we don't really need to parse date from string
    @ColumnInfo(name = "absolute_magnitude")
    val absoluteMagnitude: Double,
    @ColumnInfo(name = "estimated_diameter")
    val estimatedDiameter: Double,
    @ColumnInfo(name = "relative_velocity")
    val relativeVelocity: Double,
    @ColumnInfo(name = "distance_from_earth")
    val distanceFromEarth: Double,
    @ColumnInfo(name = "is_hazardous")
    val isPotentiallyHazardous: Boolean
) : Parcelable

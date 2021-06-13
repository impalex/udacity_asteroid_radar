package com.udacity.asteroidradar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_of_day")
data class DatabasePictureOfDay(
    @PrimaryKey(autoGenerate = false)
    val date: String,
    @ColumnInfo(name = "media_type")
    val mediaType: String,
    val title: String,
    val url: String
)
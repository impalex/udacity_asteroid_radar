package com.udacity.asteroidradar.api.model

import com.squareup.moshi.Json
import com.udacity.asteroidradar.database.entity.DatabasePictureOfDay

data class PictureOfDay(
    val date: String,
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String
) {
    val isImage
        get() = mediaType == "image"
}

fun PictureOfDay.toDatabase(): DatabasePictureOfDay =
    DatabasePictureOfDay(this.date, this.mediaType, this.title, this.url)
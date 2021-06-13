package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.model.PictureOfDay
import com.udacity.asteroidradar.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getTodayPicture(
        @Query("date") date: String?,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): PictureOfDay

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsRaw(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): String
}

object NasaApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}
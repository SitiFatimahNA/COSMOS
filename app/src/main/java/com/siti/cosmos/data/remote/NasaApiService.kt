package com.siti.cosmos.data.remote

import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.model.MarsPhotosResponse
import com.siti.cosmos.data.model.NasaResponse
import com.siti.cosmos.data.model.NeoFeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {

    // ── APOD ─────────────────────────────────────────────────────────────────
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date")    date: String? = null    // null = today
    ): ApodResponse

    // ── Mars Rover Photos ─────────────────────────────────────────────────────
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    suspend fun getMarsPhotos(
        @Query("api_key")    apiKey: String,
        @Query("earth_date") earthDate: String,
        @Query("page")       page: Int    = 1
    ): MarsPhotosResponse

    @GET("mars-photos/api/v1/rovers/opportunity/photos")
    suspend fun getMarsPhotosOpportunity(
        @Query("api_key")    apiKey: String,
        @Query("earth_date") earthDate: String,
        @Query("page")       page: Int = 1
    ): MarsPhotosResponse

    @GET("mars-photos/api/v1/rovers/spirit/photos")
    suspend fun getMarsPhotosSpirit(
        @Query("api_key")    apiKey: String,
        @Query("earth_date") earthDate: String,
        @Query("page")       page: Int = 1
    ): MarsPhotosResponse

    // ── NeoWs ─────────────────────────────────────────────────────────────────
    @GET("neo/rest/v1/feed")
    suspend fun getNeoFeed(
        @Query("api_key")    apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date")   endDate: String
    ): NeoFeedResponse
}

// NASA Images API (separate base URL — no API key needed)
interface NasaImagesApiService {
    @GET("search")
    suspend fun searchImages(
        @Query("q")          query: String,
        @Query("media_type") mediaType: String = "image",
        @Query("page_size")  pageSize: Int = 20
    ): NasaResponse
}

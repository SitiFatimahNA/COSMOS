package com.siti.cosmos.data.remote

import com.siti.cosmos.data.model.NasaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApi {

    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image"
    ): NasaResponse
}

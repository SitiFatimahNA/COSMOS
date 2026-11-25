package com.siti.cosmos.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val NASA_BASE_URL    = "https://api.nasa.gov/"
    private const val IMAGES_BASE_URL  = "https://images-api.nasa.gov/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val nasaApi: NasaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NASA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    val imagesApi: NasaImagesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(IMAGES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaImagesApiService::class.java)
    }

    // Keep old `api` reference for backward compatibility with NasaGallery
    val api: NasaApi by lazy {
        Retrofit.Builder()
            .baseUrl(IMAGES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApi::class.java)
    }
}

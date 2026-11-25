package com.siti.cosmos.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TTL constants — how long each cache is considered "fresh".
 * After TTL expires, the app will attempt a network refresh.
 * If the network fails, stale cache is still shown with an indicator.
 */
object CacheTtl {
    const val APOD_MS  = 12 * 60 * 60 * 1000L   // 12 hours  (APOD updates daily)
    const val MARS_MS  =  6 * 60 * 60 * 1000L   //  6 hours
    const val NEO_MS   =  6 * 60 * 60 * 1000L   //  6 hours
}

/** APOD cache row — keyed by date string (e.g. "2024-11-25") */
@Entity(tableName = "apod_cache")
data class ApodCacheEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: String,
    val copyright: String?,
    val serviceVersion: String,
    val cachedAt: Long = System.currentTimeMillis()
)

/** Mars photo cache row — keyed by composite (rover + date + page + photoId) */
@Entity(tableName = "mars_cache", primaryKeys = ["id"])
data class MarsCacheEntity(
    val id: Int,
    val sol: Int,
    val imgSrc: String,
    val earthDate: String,
    val cameraName: String,
    val cameraFullName: String,
    val roverName: String,
    val roverStatus: String,
    val roverLaunchDate: String,
    val roverLandingDate: String,
    // Lookup keys
    val rover: String,          // "curiosity" | "opportunity" | "spirit"
    val page: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

/** NEO cache row — keyed by neo id + date range */
@Entity(tableName = "neo_cache")
data class NeoCacheEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nasaJplUrl: String,
    val absoluteMagnitude: Double,
    val isPotentiallyHazardous: Boolean,
    val diameterMinKm: Double,
    val diameterMaxKm: Double,
    // First close approach data (flattened for simplicity)
    val closeApproachDate: String,
    val relativeVelocityKmh: String,
    val missDistanceKm: String,
    val missDistanceLunar: String,
    val orbitingBody: String,
    // Range keys
    val startDate: String,
    val endDate: String,
    val cachedAt: Long = System.currentTimeMillis()
)

/** Rate-limit tracker — prevents hammering NASA when 429 is received */
@Entity(tableName = "rate_limit")
data class RateLimitEntity(
    @PrimaryKey val endpoint: String,   // "apod" | "mars" | "neo"
    val blockedUntilMs: Long            // epoch ms when we can retry
)

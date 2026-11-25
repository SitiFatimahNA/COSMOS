package com.siti.cosmos.data.repository

import android.content.Context
import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.local.db.CacheTtl
import com.siti.cosmos.data.local.db.NasaDatabase
import com.siti.cosmos.data.local.db.toEntity
import com.siti.cosmos.data.local.db.toModel
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.data.remote.Endpoints
import com.siti.cosmos.data.remote.RateLimitedException
import com.siti.cosmos.data.remote.RetrofitClient
import com.siti.cosmos.data.remote.withRetry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MarsRepository(context: Context) {
    private val api    = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY
    private val db     = NasaDatabase.getInstance(context)
    private val dao    = db.marsDao()

    suspend fun getPhotos(
        rover: String,
        earthDate: String,
        page: Int = 1
    ): ApiResult<List<MarsPhoto>> {
        // ── 1. Cache check ────────────────────────────────────────────────────
        val cached = dao.getByKey(rover, earthDate, page)
        val isFresh = cached.isNotEmpty() &&
                (System.currentTimeMillis() - (cached.firstOrNull()?.cachedAt ?: 0L)) < CacheTtl.MARS_MS

        if (isFresh) {
            return ApiResult.Success(
                data      = cached.map { it.toModel() },
                fromCache = true,
                cachedAt  = cached.first().cachedAt
            )
        }

        // ── 2. Network fetch ──────────────────────────────────────────────────
        return try {
            val photos = withRetry(db, Endpoints.MARS) {
                when (rover.lowercase()) {
                    "curiosity"   -> api.getMarsPhotos(apiKey, earthDate, page).photos
                    "opportunity" -> api.getMarsPhotosOpportunity(apiKey, earthDate, page).photos
                    "spirit"      -> api.getMarsPhotosSpirit(apiKey, earthDate, page).photos
                    else          -> api.getMarsPhotos(apiKey, earthDate, page).photos
                }
            }
            dao.insertAll(photos.map { it.toEntity(rover, page) })
            // Keep only last 3 days per rover
            dao.evictOldForRover(rover, System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L)
            ApiResult.Success(data = photos, fromCache = false)

        } catch (e: RateLimitedException) {
            val stale = cached.map { it.toModel() }
            ApiResult.Error(
                message    = "Rate limit reached. Showing saved data. Retry in ${e.retryAfterMs / 1000}s.",
                code       = 429,
                cachedData = stale.ifEmpty { null }
            )
        } catch (e: retrofit2.HttpException) {
            val stale = cached.map { it.toModel() }
            ApiResult.Error(
                message = when (e.code()) {
                    403  -> "Invalid API Key."
                    429  -> "Rate limit exceeded."
                    else -> "Network error (${e.code()})"
                },
                code = e.code(),
                cachedData = stale.ifEmpty { null }
            )
        } catch (e: java.net.UnknownHostException) {
            val stale = cached.map { it.toModel() }
            if (stale.isNotEmpty()) {
                ApiResult.Success(data = stale, fromCache = true,
                    cachedAt = cached.first().cachedAt)
            } else {
                ApiResult.Error("No internet connection. No saved data available.")
            }
        } catch (e: java.net.SocketTimeoutException) {
            val stale = cached.map { it.toModel() }
            if (stale.isNotEmpty()) {
                ApiResult.Success(data = stale, fromCache = true,
                    cachedAt = cached.first().cachedAt)
            } else {
                ApiResult.Error("Connection timed out.")
            }
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    /** Try progressively earlier dates until photos are found (max 30 days back) */
    suspend fun getPhotosWithFallback(
        rover: String = "curiosity",
        daysBack: Int = 0
    ): ApiResult<List<MarsPhoto>> {
        val fmt  = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.now().minusDays(daysBack.toLong()).format(fmt)
        val result = getPhotos(rover, date)
        return if (result is ApiResult.Success && result.data.isEmpty() && daysBack < 30) {
            getPhotosWithFallback(rover, daysBack + 1)
        } else result
    }
}

package com.siti.cosmos.data.repository

import android.content.Context
import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.local.db.CacheTtl
import com.siti.cosmos.data.local.db.NasaDatabase
import com.siti.cosmos.data.local.db.toEntity
import com.siti.cosmos.data.local.db.toModel
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.data.remote.Endpoints
import com.siti.cosmos.data.remote.RateLimitedException
import com.siti.cosmos.data.remote.RetrofitClient
import com.siti.cosmos.data.remote.withRetry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NeoRepository(context: Context) {
    private val api    = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY
    private val db     = NasaDatabase.getInstance(context)
    private val dao    = db.neoDao()

    suspend fun getNeoFeed(
        startDate: String? = null,
        endDate: String?   = null
    ): ApiResult<List<NeoObject>> {
        val fmt   = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = startDate ?: LocalDate.now().format(fmt)
        val end   = endDate   ?: LocalDate.now().plusDays(6).format(fmt)

        // ── 1. Cache check ────────────────────────────────────────────────────
        val cached = dao.getByRange(start, end)
        val isFresh = cached.isNotEmpty() &&
                (System.currentTimeMillis() - (cached.firstOrNull()?.cachedAt ?: 0L)) < CacheTtl.NEO_MS

        if (isFresh) {
            return ApiResult.Success(
                data      = cached.map { it.toModel() },
                fromCache = true,
                cachedAt  = cached.first().cachedAt
            )
        }

        // ── 2. Network fetch ──────────────────────────────────────────────────
        return try {
            val response = withRetry(db, Endpoints.NEO) {
                api.getNeoFeed(apiKey, start, end)
            }
            val objects = response.nearEarthObjects.values
                .flatten()
                .sortedWith(
                    compareByDescending<NeoObject> { it.isPotentiallyHazardous }
                        .thenBy { it.closeApproachData.firstOrNull()?.closeApproachDate ?: "" }
                )
            dao.insertAll(objects.map { it.toEntity(start, end) })
            dao.evictOld(System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000L) // keep 14 days
            ApiResult.Success(data = objects, fromCache = false)

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
                    400  -> "Invalid date range. Max 7 days between start and end."
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
}

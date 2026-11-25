package com.siti.cosmos.data.repository

import android.content.Context
import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.local.db.CacheTtl
import com.siti.cosmos.data.local.db.NasaDatabase
import com.siti.cosmos.data.local.db.toEntity
import com.siti.cosmos.data.local.db.toModel
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.remote.Endpoints
import com.siti.cosmos.data.remote.RateLimitedException
import com.siti.cosmos.data.remote.RetrofitClient
import com.siti.cosmos.data.remote.withRetry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ApodRepository(context: Context) {
    private val api    = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY
    private val db     = NasaDatabase.getInstance(context)
    private val dao    = db.apodDao()

    /**
     * Cache-first strategy:
     *  1. Check DB for a fresh entry (within TTL).
     *  2. If fresh → return cache immediately, no network call.
     *  3. If stale or absent → fetch from network, persist, return fresh data.
     *  4. If network fails → return stale cache (with fromCache=true) + error message.
     */
    suspend fun getApod(date: String? = null): ApiResult<ApodResponse> {
        val targetDate = date ?: LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // ── 1. Check cache ────────────────────────────────────────────────────
        val cached = dao.getByDate(targetDate)
        val isFresh = cached != null &&
                (System.currentTimeMillis() - cached.cachedAt) < CacheTtl.APOD_MS

        if (isFresh && cached != null) {
            return ApiResult.Success(
                data      = cached.toModel(),
                fromCache = true,
                cachedAt  = cached.cachedAt
            )
        }

        // ── 2. Network fetch ──────────────────────────────────────────────────
        return try {
            val response = withRetry(db, Endpoints.APOD) {
                api.getApod(apiKey = apiKey, date = date)
            }
            dao.insert(response.toEntity())
            dao.evictOld(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L) // keep 7 days
            ApiResult.Success(data = response, fromCache = false)

        } catch (e: RateLimitedException) {
            val fallback = cached?.toModel()
            ApiResult.Error(
                message    = "Rate limit reached. Showing saved data. Retry in ${e.retryAfterMs / 1000}s.",
                code       = 429,
                cachedData = fallback
            )
        } catch (e: retrofit2.HttpException) {
            val fallback = cached?.toModel()
            ApiResult.Error(
                message = when (e.code()) {
                    400  -> "Invalid date format. Use YYYY-MM-DD."
                    403  -> "Invalid API Key. Check your local.properties."
                    404  -> "No APOD found for this date."
                    500  -> "NASA server error. Please try again later."
                    else -> "Network error (${e.code()})"
                },
                code = e.code(),
                cachedData = fallback
            )
        } catch (e: java.net.UnknownHostException) {
            val fallback = cached?.toModel()
            if (fallback != null) {
                ApiResult.Success(data = fallback, fromCache = true,
                    cachedAt = cached!!.cachedAt)
            } else {
                ApiResult.Error("No internet connection. No saved data available.")
            }
        } catch (e: java.net.SocketTimeoutException) {
            val fallback = cached?.toModel()
            if (fallback != null) {
                ApiResult.Success(data = fallback, fromCache = true,
                    cachedAt = cached!!.cachedAt)
            } else {
                ApiResult.Error("Connection timed out. Check your internet.")
            }
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}

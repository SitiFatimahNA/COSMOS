package com.siti.cosmos.data.repository

import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.data.remote.RetrofitClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NeoRepository {
    private val api    = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY

    /** Returns objects for the next 7 days (max window allowed by API) */
    suspend fun getNeoFeed(
        startDate: String? = null,
        endDate: String?   = null
    ): ApiResult<List<NeoObject>> {
        return try {
            val fmt   = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val start = startDate ?: LocalDate.now().format(fmt)
            val end   = endDate   ?: LocalDate.now().plusDays(6).format(fmt)

            val response = api.getNeoFeed(apiKey, start, end)
            // Flatten the map (keyed by date) into a single list, sorted by date
            val objects = response.nearEarthObjects.values
                .flatten()
                .sortedWith(
                    compareByDescending<NeoObject> { it.isPotentiallyHazardous }
                        .thenBy { it.closeApproachData.firstOrNull()?.closeApproachDate ?: "" }
                )
            ApiResult.Success(objects)
        } catch (e: retrofit2.HttpException) {
            ApiResult.Error(
                when (e.code()) {
                    400  -> "Invalid date range. Max 7 days."
                    403  -> "Invalid API Key."
                    429  -> "API rate limit exceeded."
                    else -> "Network error (${e.code()})"
                }, e.code()
            )
        } catch (e: java.net.UnknownHostException) {
            ApiResult.Error("No internet connection.")
        } catch (e: java.net.SocketTimeoutException) {
            ApiResult.Error("Connection timed out.")
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}

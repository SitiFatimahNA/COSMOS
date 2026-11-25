package com.siti.cosmos.data.repository

import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.data.remote.RetrofitClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MarsRepository {
    private val api    = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY

    suspend fun getCuriosityPhotos(earthDate: String, page: Int = 1): ApiResult<List<MarsPhoto>> {
        return safeApiCall { api.getMarsPhotos(apiKey, earthDate, page).photos }
    }

    suspend fun getOpportunityPhotos(earthDate: String, page: Int = 1): ApiResult<List<MarsPhoto>> {
        return safeApiCall { api.getMarsPhotosOpportunity(apiKey, earthDate, page).photos }
    }

    suspend fun getSpiritPhotos(earthDate: String, page: Int = 1): ApiResult<List<MarsPhoto>> {
        return safeApiCall { api.getMarsPhotosSpirit(apiKey, earthDate, page).photos }
    }

    /** Try progressively earlier dates if no photos returned for today */
    suspend fun getCuriosityPhotosWithFallback(daysBack: Int = 0): ApiResult<List<MarsPhoto>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.now().minusDays(daysBack.toLong()).format(formatter)
        val result = getCuriosityPhotos(date)
        return if (result is ApiResult.Success && result.data.isEmpty() && daysBack < 30) {
            getCuriosityPhotosWithFallback(daysBack + 1)
        } else result
    }

    private suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> {
        return try {
            ApiResult.Success(block())
        } catch (e: retrofit2.HttpException) {
            ApiResult.Error(
                when (e.code()) {
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

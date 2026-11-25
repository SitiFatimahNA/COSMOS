package com.siti.cosmos.data.repository

import com.siti.cosmos.BuildConfig
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.remote.RetrofitClient

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

class ApodRepository {
    private val api = RetrofitClient.nasaApi
    private val apiKey = BuildConfig.NASA_API_KEY

    suspend fun getApod(date: String? = null): ApiResult<ApodResponse> {
        return try {
            val result = api.getApod(apiKey = apiKey, date = date)
            ApiResult.Success(result)
        } catch (e: retrofit2.HttpException) {
            ApiResult.Error(
                message = when (e.code()) {
                    400 -> "Invalid date format. Use YYYY-MM-DD."
                    403 -> "Invalid API Key."
                    429 -> "API rate limit exceeded. Try again later."
                    500 -> "NASA server error. Try again later."
                    else -> "Network error (${e.code()})"
                },
                code = e.code()
            )
        } catch (e: java.net.UnknownHostException) {
            ApiResult.Error("No internet connection.")
        } catch (e: java.net.SocketTimeoutException) {
            ApiResult.Error("Connection timed out. Check your internet.")
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}

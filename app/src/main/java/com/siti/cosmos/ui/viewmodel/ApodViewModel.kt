package com.siti.cosmos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.ApodRepository
import com.siti.cosmos.data.repository.toCacheLabel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ApodUiState(
    val isLoading: Boolean    = false,
    val apod: ApodResponse?   = null,
    val error: String?        = null,
    val selectedDate: String? = null,
    // Cache metadata
    val fromCache: Boolean    = false,
    val cacheLabel: String    = ""
)

class ApodViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = ApodRepository(app)

    private val _state = MutableStateFlow(ApodUiState())
    val state: StateFlow<ApodUiState> = _state

    // Prevent duplicate in-flight requests
    private var currentJob: Job? = null
    private var lastLoadedDate: String? = "NEVER"   // sentinel so first load always fires

    init { loadApod() }

    fun loadApod(date: String? = null) {
        val dateKey = date ?: "today"

        // Jangan mengirim request baru jika tanggal yang sama
        // masih sedang dimuat.
        if (dateKey == lastLoadedDate && _state.value.isLoading) {
            return
        }

        // Jangan memuat ulang data yang sudah berhasil dimuat.
        if (dateKey == lastLoadedDate &&
            _state.value.apod != null &&
            _state.value.error == null
        ) {
            return
        }

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedDate = date)
            lastLoadedDate = dateKey

            when (val result = repository.getApod(date)) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading  = false,
                    apod       = result.data,
                    fromCache  = result.fromCache,
                    cacheLabel = if (result.fromCache) result.cachedAt.toCacheLabel() else ""
                )
                is ApiResult.Error -> {
                    // Show stale cache alongside error if available
                    val stale = result.cachedData as? ApodResponse
                    _state.value = _state.value.copy(
                        isLoading  = false,
                        error      = result.message,
                        apod       = stale ?: _state.value.apod,
                        fromCache  = stale != null,
                        cacheLabel = if (stale != null) "Saved data" else ""
                    )
                    lastLoadedDate = "NEVER" // allow retry
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun retry() {
        lastLoadedDate = "NEVER"
        loadApod(_state.value.selectedDate)
    }
}

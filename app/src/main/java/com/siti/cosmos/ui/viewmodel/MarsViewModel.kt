package com.siti.cosmos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.MarsRepository
import com.siti.cosmos.data.repository.toCacheLabel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MarsUiState(
    val isLoading: Boolean       = false,
    val photos: List<MarsPhoto>  = emptyList(),
    val error: String?           = null,
    val selectedRover: String    = "curiosity",
    val selectedDate: String     = "",
    val page: Int                = 1,
    // Cache metadata
    val fromCache: Boolean       = false,
    val cacheLabel: String       = ""
)

class MarsViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = MarsRepository(app)

    private val _state = MutableStateFlow(MarsUiState(isLoading = true))
    val state: StateFlow<MarsUiState> = _state

    private var currentJob: Job? = null
    // Tracks last (rover, date, page) combo so we don't re-fetch on recomposition
    private var lastKey: Triple<String, String, Int>? = null

    init { loadPhotos() }

    fun loadPhotos(rover: String? = null, date: String? = null, page: Int = 1) {
        val targetRover = rover ?: _state.value.selectedRover
        val targetDate  = date  ?: _state.value.selectedDate

        // ── Deduplication ─────────────────────────────────────────────────────
        val newKey = Triple(targetRover, targetDate, page)
        if (newKey == lastKey &&
            !_state.value.isLoading &&
            _state.value.photos.isNotEmpty() &&
            _state.value.error == null
        ) return

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading     = true,
                error         = null,
                selectedRover = targetRover,
                selectedDate  = targetDate,
                page          = page
            )
            lastKey = newKey

            val result = if (targetDate.isBlank()) {
                repository.getPhotosWithFallback(targetRover)
            } else {
                repository.getPhotos(targetRover, targetDate, page)
            }

            @Suppress("UNCHECKED_CAST")
            when (result) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading  = false,
                    photos     = if (page == 1) result.data
                                 else _state.value.photos + result.data,
                    fromCache  = result.fromCache,
                    cacheLabel = if (result.fromCache) result.cachedAt.toCacheLabel() else ""
                )
                is ApiResult.Error -> {
                    val stalePhotos = (result.cachedData as? List<MarsPhoto>)
                    _state.value = _state.value.copy(
                        isLoading  = false,
                        error      = result.message,
                        photos     = stalePhotos ?: _state.value.photos,
                        fromCache  = stalePhotos != null,
                        cacheLabel = if (stalePhotos != null) "Saved data" else ""
                    )
                    lastKey = null // allow retry
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun selectRover(rover: String) {
        lastKey = null
        loadPhotos(rover = rover, page = 1)
    }

    fun retry() {
        lastKey = null
        loadPhotos(
            rover = _state.value.selectedRover,
            date  = _state.value.selectedDate.ifBlank { null },
            page  = 1
        )
    }
}

package com.siti.cosmos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.NeoRepository
import com.siti.cosmos.data.repository.toCacheLabel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NeoUiState(
    val isLoading: Boolean       = false,
    val objects: List<NeoObject> = emptyList(),
    val error: String?           = null,
    val startDate: String        = "",
    val endDate: String          = "",
    // Cache metadata
    val fromCache: Boolean       = false,
    val cacheLabel: String       = ""
)

class NeoViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = NeoRepository(app)

    private val _state = MutableStateFlow(NeoUiState(isLoading = true))
    val state: StateFlow<NeoUiState> = _state

    private var currentJob: Job? = null
    private var lastKey: Pair<String, String>? = null

    init { loadNeo() }

    fun loadNeo(startDate: String? = null, endDate: String? = null) {
        val start = startDate ?: ""
        val end   = endDate   ?: ""

        // ── Deduplication ─────────────────────────────────────────────────────
        val newKey = start to end
        if (newKey == lastKey &&
            !_state.value.isLoading &&
            _state.value.objects.isNotEmpty() &&
            _state.value.error == null
        ) return

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true, error = null,
                startDate = start, endDate = end
            )
            lastKey = newKey

            @Suppress("UNCHECKED_CAST")
            when (val result = repository.getNeoFeed(
                startDate = startDate,
                endDate   = endDate
            )) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading  = false,
                    objects    = result.data,
                    fromCache  = result.fromCache,
                    cacheLabel = if (result.fromCache) result.cachedAt.toCacheLabel() else ""
                )
                is ApiResult.Error -> {
                    val stale = (result.cachedData as? List<NeoObject>)
                    _state.value = _state.value.copy(
                        isLoading  = false,
                        error      = result.message,
                        objects    = stale ?: _state.value.objects,
                        fromCache  = stale != null,
                        cacheLabel = if (stale != null) "Saved data" else ""
                    )
                    lastKey = null
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun retry() {
        lastKey = null
        loadNeo(
            startDate = _state.value.startDate.ifBlank { null },
            endDate   = _state.value.endDate.ifBlank { null }
        )
    }
}

package com.siti.cosmos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.NeoObject
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.NeoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NeoUiState(
    val isLoading: Boolean       = false,
    val objects: List<NeoObject> = emptyList(),
    val error: String?           = null,
    val startDate: String        = "",
    val endDate: String          = ""
)

class NeoViewModel : ViewModel() {
    private val repository = NeoRepository()

    private val _state = MutableStateFlow(NeoUiState(isLoading = true))
    val state: StateFlow<NeoUiState> = _state

    init { loadNeo() }

    fun loadNeo(startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true, error = null,
                startDate = startDate ?: "",
                endDate   = endDate   ?: ""
            )
            when (val result = repository.getNeoFeed(startDate, endDate)) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading = false, objects = result.data
                )
                is ApiResult.Error   -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun retry() = loadNeo(
        startDate = _state.value.startDate.ifBlank { null },
        endDate   = _state.value.endDate.ifBlank { null }
    )
}

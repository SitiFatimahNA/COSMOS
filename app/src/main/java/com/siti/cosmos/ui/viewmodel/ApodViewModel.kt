package com.siti.cosmos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.ApodResponse
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ApodUiState(
    val isLoading: Boolean    = false,
    val apod: ApodResponse?   = null,
    val error: String?        = null,
    val selectedDate: String? = null
)

class ApodViewModel : ViewModel() {
    private val repository = ApodRepository()

    private val _state = MutableStateFlow(ApodUiState(isLoading = true))
    val state: StateFlow<ApodUiState> = _state

    init { loadApod() }

    fun loadApod(date: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedDate = date)
            when (val result = repository.getApod(date)) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading = false, apod = result.data
                )
                is ApiResult.Error   -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun retry() = loadApod(_state.value.selectedDate)
}

package com.siti.cosmos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siti.cosmos.data.model.MarsPhoto
import com.siti.cosmos.data.repository.ApiResult
import com.siti.cosmos.data.repository.MarsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MarsUiState(
    val isLoading: Boolean       = false,
    val photos: List<MarsPhoto>  = emptyList(),
    val error: String?           = null,
    val selectedRover: String    = "curiosity",
    val selectedDate: String     = "",
    val page: Int                = 1
)

class MarsViewModel : ViewModel() {
    private val repository = MarsRepository()

    private val _state = MutableStateFlow(MarsUiState(isLoading = true))
    val state: StateFlow<MarsUiState> = _state

    init { loadPhotos() }

    fun loadPhotos(rover: String? = null, date: String? = null, page: Int = 1) {
        val targetRover = rover ?: _state.value.selectedRover
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true, error = null,
                selectedRover = targetRover, page = page
            )
            val result = when (targetRover.lowercase()) {
                "curiosity"   -> if (date != null)
                    repository.getCuriosityPhotos(date, page)
                else
                    repository.getCuriosityPhotosWithFallback()
                "opportunity" -> repository.getOpportunityPhotos(
                    date ?: "2018-06-10", page
                )
                "spirit"      -> repository.getSpiritPhotos(
                    date ?: "2010-03-21", page
                )
                else          -> repository.getCuriosityPhotosWithFallback()
            }
            when (result) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    isLoading = false,
                    photos = if (page == 1) result.data else _state.value.photos + result.data,
                    selectedDate = date ?: _state.value.selectedDate
                )
                is ApiResult.Error   -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun selectRover(rover: String) = loadPhotos(rover = rover, page = 1)
    fun retry() = loadPhotos(
        rover = _state.value.selectedRover,
        date  = _state.value.selectedDate.ifBlank { null }
    )
}

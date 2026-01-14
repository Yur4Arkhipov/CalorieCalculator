package com.jacqulin.calcalc.feature.home.ui.aitext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiMealDescriptionUiState(
    val description: String = "",
    val isProcessing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiMealDescriptionViewModel @Inject constructor(
    // TODO: Inject AI service/repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMealDescriptionUiState())
    val uiState: StateFlow<AiMealDescriptionUiState> = _uiState.asStateFlow()

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onAnalyze() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)

            // TODO: Вызов AI сервиса для анализа описания
            delay(2000)

            _uiState.value = _uiState.value.copy(isProcessing = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}


package com.jacqulin.calcalc.feature.home.ui.aitext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiMealDescriptionUiState(
    val description: String = "",
    val isProcessing: Boolean = false,
    val error: String? = null,
    val result: Nutrition? = null
)

@HiltViewModel
class AiMealDescriptionViewModel @Inject constructor(
    private val analyzeMealUseCase: AnalyzeMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMealDescriptionUiState())
    val uiState: StateFlow<AiMealDescriptionUiState> = _uiState.asStateFlow()

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description, result = null)
    }

    fun onAnalyze() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null, result = null)
            try {
                val nutrition = analyzeMealUseCase(_uiState.value.description)
                _uiState.value = _uiState.value.copy(isProcessing = false, result = nutrition)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Ошибка анализа: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
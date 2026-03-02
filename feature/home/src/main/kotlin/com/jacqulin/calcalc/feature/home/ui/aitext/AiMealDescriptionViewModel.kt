package com.jacqulin.calcalc.feature.home.ui.aitext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AiMealDescriptionUiState(
    val mealName: String = "",
    val description: String = "",
    val selectedMealType: MealType = MealType.BREAKFAST,
    val isProcessing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val result: Nutrition? = null
)

sealed interface AiMealEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : AiMealEffect
    data object CloseScreen : AiMealEffect
}

@HiltViewModel
class AiMealDescriptionViewModel @Inject constructor(
    private val analyzeMealUseCase: AnalyzeMealUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    observeSelectedDate: ObserveSelectedDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMealDescriptionUiState())
    val uiState: StateFlow<AiMealDescriptionUiState> = _uiState.asStateFlow()

    private val selectedDate = observeSelectedDate()

    private val _effect = Channel<AiMealEffect>()
    val effect = _effect.receiveAsFlow()

    fun onMealNameChange(name: String) {
        _uiState.value = _uiState.value.copy(mealName = name)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description, result = null)
    }

    fun onMealTypeSelected(type: MealType) {
        _uiState.value = _uiState.value.copy(selectedMealType = type)
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

    fun onSave() {
        val state = _uiState.value
        val nutrition = state.result ?: return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            try {
                val meal = Meal(
                    name = nutrition.name.ifBlank { state.mealName.ifBlank { "Блюдо (ИИ)" } },
                    calories = nutrition.calories.toInt(),
                    proteins = nutrition.protein.toInt(),
                    fats = nutrition.fat.toInt(),
                    carbs = nutrition.carbs.toInt(),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = state.selectedMealType
                )
                saveManualAddMealDBUseCase(selectedDate.value, meal)
                _effect.send(AiMealEffect.ShowSnackbar("Блюдо сохранено!"))
                delay(1000)
                _effect.send(AiMealEffect.CloseScreen)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _effect.send(AiMealEffect.ShowSnackbar("Ошибка сохранения", isError = true))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
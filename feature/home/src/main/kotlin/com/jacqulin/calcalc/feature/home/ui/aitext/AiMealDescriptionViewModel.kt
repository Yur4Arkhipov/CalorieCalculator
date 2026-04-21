package com.jacqulin.calcalc.feature.home.ui.aitext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    val description: String = "",
    val selectedMealType: MealType = MealType.BREAKFAST,
    val isProcessing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val result: Nutrition? = null
)

@HiltViewModel
class AiMealDescriptionViewModel @Inject constructor(
    private val analyzeMealUseCase: AnalyzeMealUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    observeSelectedDate: ObserveSelectedDateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMealDescriptionUiState())
    val uiState: StateFlow<AiMealDescriptionUiState> = _uiState.asStateFlow()

    private val selectedDate = observeSelectedDate()

    private val _effect = Channel<UiEffect>()
    val effect = _effect.receiveAsFlow()


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
                    error = e.localizedMessage
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
                val calories = state.result.calories.toInt()
                val proteins = state.result.protein.toInt()
                val fats = state.result.fat.toInt()
                val carbs = state.result.carbs.toInt()

                val meal = Meal(
                    name = nutrition.name,
                    calories = calories,
                    proteins = proteins,
                    fats = fats,
                    carbs = carbs,
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = state.selectedMealType
                )

                saveManualAddMealDBUseCase(selectedDate.value, meal)

                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVED,
                        isError = false
                    )
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVE_ERROR,
                        isError = true
                    )
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
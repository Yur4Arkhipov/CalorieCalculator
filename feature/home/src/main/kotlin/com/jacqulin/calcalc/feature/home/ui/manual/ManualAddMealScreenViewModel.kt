package com.jacqulin.calcalc.feature.home.ui.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ManualAddMealUiState(
    val selectedMealType: MealType = MealType.BREAKFAST,
    val mealName: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbs: String = ""
)

sealed class ManualAddMealEvent {
    data class MealNameChanged(val name: String) : ManualAddMealEvent()
    data class MealTypeSelected(val type: MealType) : ManualAddMealEvent()
    data class CaloriesChanged(val calories: String) : ManualAddMealEvent()
    data class ProteinsChanged(val proteins: String) : ManualAddMealEvent()
    data class FatsChanged(val fats: String) : ManualAddMealEvent()
    data class CarbsChanged(val carbs: String) : ManualAddMealEvent()
    object OnSaveClick : ManualAddMealEvent()
}

sealed interface ManualAddMealEffect {
    data object CloseScreen : ManualAddMealEffect
    data class ShowSnackbar(val message: String, val type: SnackbarType) : ManualAddMealEffect
}

enum class SnackbarType { SUCCESS, ERROR }

data class SnackbarData(
    val message: String,
    val type: SnackbarType
)

@HiltViewModel
class ManualAddMealScreenViewModel @Inject constructor(
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualAddMealUiState())
    val uiState: StateFlow<ManualAddMealUiState> = _uiState.asStateFlow()

    private val _effect = Channel<ManualAddMealEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ManualAddMealEvent) {
        when (event) {
            is ManualAddMealEvent.MealNameChanged -> {
                _uiState.update {
                    it.copy(mealName = event.name)
                }
            }
            is ManualAddMealEvent.MealTypeSelected -> {
                _uiState.update {
                    it.copy(selectedMealType = event.type)
                }
            }
            is ManualAddMealEvent.CaloriesChanged -> {
                if (event.calories.isEmpty() || event.calories.toIntOrNull() != null) {
                    _uiState.update {
                        it.copy(calories = event.calories)
                    }
                }
            }
            is ManualAddMealEvent.ProteinsChanged -> {
                _uiState.update {
                    it.copy(proteins = event.proteins)
                }
            }
            is ManualAddMealEvent.FatsChanged -> {
                _uiState.update {
                    it.copy(fats = event.fats)
                }
            }
            is ManualAddMealEvent.CarbsChanged -> {
                _uiState.update {
                    it.copy(carbs = event.carbs)
                }
            }
            ManualAddMealEvent.OnSaveClick -> {
                saveMeal()
            }
        }
    }

    private fun saveMeal() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                val meal = Meal(
                    name = state.mealName,
                    calories = state.calories.toIntOrNull() ?: 0,
                    proteins = state.proteins.toIntOrNull() ?: 0,
                    fats = state.fats.toIntOrNull() ?: 0,
                    carbs = state.carbs.toIntOrNull() ?: 0,
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = state.selectedMealType
                )
                saveManualAddMealDBUseCase(Date(), meal)
                _effect.send(ManualAddMealEffect.ShowSnackbar("Блюдо успешно сохранено!", SnackbarType.SUCCESS))
                delay(1500)
                _effect.send(ManualAddMealEffect.CloseScreen)
            } catch (_: Exception) {
                _effect.send(ManualAddMealEffect.ShowSnackbar("Ошибка сохранения", SnackbarType.ERROR))
            }
        }
    }
}
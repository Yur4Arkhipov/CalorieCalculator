package com.jacqulin.calcalc.feature.home.ui.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
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

@HiltViewModel
class ManualAddMealScreenViewModel @Inject constructor(
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    observeSelectedDate: ObserveSelectedDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualAddMealUiState())
    val uiState: StateFlow<ManualAddMealUiState> = _uiState.asStateFlow()

    private val selectedDate = observeSelectedDate()

    private val _effect = Channel<UiEffect>()
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
                saveManualAddMealDBUseCase(selectedDate.value, meal)
                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVED,
                        isError = false
                    )
                )
                delay(2000)
                _effect.send(element = UiEffect.CloseScreen)
            } catch (_: Exception) {
                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVE_ERROR,
                        isError = true
                    )
                )
            }
        }
    }
}
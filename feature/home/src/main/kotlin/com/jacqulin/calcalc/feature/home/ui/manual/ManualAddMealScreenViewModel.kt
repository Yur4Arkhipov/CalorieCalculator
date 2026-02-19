package com.jacqulin.calcalc.feature.home.ui.manual

import androidx.lifecycle.ViewModel
import com.jacqulin.calcalc.core.domain.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    object SaveClicked : ManualAddMealEvent()
}

@HiltViewModel
class ManualAddMealScreenViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualAddMealUiState())
    val uiState: StateFlow<ManualAddMealUiState> = _uiState.asStateFlow()

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
//            ManualAddMealEvent.SaveClicked -> {
//                saveMeal()
//            }
            else -> {}
        }
    }

}

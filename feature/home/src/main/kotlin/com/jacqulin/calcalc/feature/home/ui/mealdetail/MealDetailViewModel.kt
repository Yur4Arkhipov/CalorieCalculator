package com.jacqulin.calcalc.feature.home.ui.mealdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.usecase.GetMealDetailUseCase
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput
import com.jacqulin.calcalc.feature.home.navigation.MealDetailRoute
import com.jacqulin.calcalc.feature.home.ui.review.IngredientUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMealDetail: GetMealDetailUseCase
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MealDetailRoute>()

    private val _uiState = MutableStateFlow(MealDetailUiState(isLoading = true))
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    private val _effect = Channel<UiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val meal = getMealDetail(route.mealId)

            _uiState.update { it ->
                it.copy(
                    isLoading = false,
                    meal = meal,
                    name = meal.name,
                    calories = meal.calories.toString(),
                    carbs = meal.carbs.toString(),
                    fats = meal.fats.toString(),
                    proteins = meal.proteins.toString(),
                    weight = meal.weight.toString(),
                    ingredients = meal.ingredient.map {
                        IngredientUi(
                            name = it.name,
                            weight = it.weight.toString(),
                            calories = it.calories.toString(),
                            protein = it.protein.toString(),
                            fat = it.fat.toString(),
                            carb = it.carb.toString()
                        )
                    }
                )
            }
        }
    }

    fun onNameChange(newValue: String) {
        if (newValue.length <= 50) {
            _uiState.update {
                it.copy(
                    name = newValue,
                    isSaveIconEnable = true
                )
            }
        }
    }

    fun onWeightChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 4,
            maxValue = 2000
        )
        _uiState.update {
            it.copy(
                weight = filtered,
                isSaveIconEnable = true
            )
        }
    }

    fun onCaloriesChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 4,
            maxValue = 2500
        )
        _uiState.update {
            it.copy(
                calories = filtered,
                isSaveIconEnable = true
            )
        }
    }

    fun onCarbsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 300
        )
        _uiState.update {
            it.copy(
                carbs = filtered,
                isSaveIconEnable = true
            )
        }
    }

    fun onProteinsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 150
        )
        _uiState.update {
            it.copy(
                proteins = filtered,
                isSaveIconEnable = true
            )
        }
    }

    fun onFatsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 150
        )
        _uiState.update {
            it.copy(
                fats = filtered,
                isSaveIconEnable = true
            )
        }
    }

    fun onMealTypeSelected(type: MealType) {
        _uiState.value = _uiState.value.copy(
            selectedMealType = type,
            isSaveIconEnable = true
        )
    }

     fun onSaveUpdatedMeal() {
         viewModelScope.launch {
             try {
                 val currentState = uiState.value

                 val updatedMeal = currentState.meal!!.copy(
                     name = currentState.name,
                     weight = currentState.weight.toIntOrNull() ?: 0,
                     calories = currentState.calories.toIntOrNull() ?: 0,
                     fats = currentState.fats.toIntOrNull() ?: 0,
                     carbs = currentState.carbs.toIntOrNull() ?: 0,
                     proteins = currentState.proteins.toIntOrNull() ?: 0,
                     type = currentState.selectedMealType
                 )

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
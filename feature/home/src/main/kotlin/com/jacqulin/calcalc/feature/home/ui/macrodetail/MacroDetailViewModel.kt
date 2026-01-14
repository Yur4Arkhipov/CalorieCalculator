package com.jacqulin.calcalc.feature.home.ui.macrodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MacroDetailViewModel @Inject constructor(
    private val getDayDataUseCase: GetDayDataUseCase,
    observeSelectedDate: ObserveSelectedDateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MacroDetailUiState(isLoading = true))
    val uiState: StateFlow<MacroDetailUiState> = _uiState.asStateFlow()

    init {
        loadData(observeSelectedDate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadData(observeSelectedDate: ObserveSelectedDateUseCase) {
        observeSelectedDate()
            .flatMapLatest { date ->
                flow {
                    val data = getDayDataUseCase(date)
                    emit(
                        MacroDetailUiState(
                            consumedCalories = data.meals.sumOf { it.calories },
                            mealsToday = data.meals,
                            todayMacros = data.macros,
                            isLoading = false
                        )
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                MacroDetailUiState(isLoading = true)
            )
            .also { flow ->
                viewModelScope.launch {
                    flow.collect { newState ->
                        _uiState.update { it.copy(
                            consumedCalories = newState.consumedCalories,
                            mealsToday = newState.mealsToday,
                            todayMacros = newState.todayMacros,
                            isLoading = newState.isLoading
                        )}
                    }
                }
            }
    }

    fun onEditMeal(meal: Meal) {
        _uiState.update { it.copy(editingMeal = meal, isEditingSheetOpen = true) }
    }

    fun onDismissEditMeal() {
        _uiState.update { it.copy(editingMeal = null, isEditingSheetOpen = false) }
    }

    fun onUpdateMeal(updatedMeal: Meal) {
        // TODO: Реализовать логику обновления блюда через UseCase
        // Пока просто обновляем локально в списке
        _uiState.update { currentState ->
            val updatedMeals = currentState.mealsToday.map { meal ->
                if (meal == currentState.editingMeal) updatedMeal else meal
            }

            val updatedCalories = updatedMeals.sumOf { it.calories }
            val updatedProteins = updatedMeals.sumOf { it.proteins }
            val updatedCarbs = updatedMeals.sumOf { it.carbs }
            val updatedFats = updatedMeals.sumOf { it.fats }

            currentState.copy(
                mealsToday = updatedMeals,
                consumedCalories = updatedCalories,
                todayMacros = currentState.todayMacros.copy(
                    protein = updatedProteins,
                    carb = updatedCarbs,
                    fat = updatedFats
                ),
                editingMeal = null,
                isEditingSheetOpen = false
            )
        }
    }
}
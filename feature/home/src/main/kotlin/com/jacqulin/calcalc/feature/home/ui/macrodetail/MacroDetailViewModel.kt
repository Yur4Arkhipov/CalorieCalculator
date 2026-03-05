package com.jacqulin.calcalc.feature.home.ui.macrodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.UpdateMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MacroDetailViewModel @Inject constructor(
    private val getDayDataUseCase: GetDayDataUseCase,
    observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val updateMealUseCase: UpdateMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase
) : ViewModel() {

    private val uiLocalState = MutableStateFlow(
        MacroDetailUiState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MacroDetailUiState> =
        observeSelectedDateUseCase()
            .flatMapLatest { date ->
                combine(
                    getDayDataUseCase(date),
                    observeUserProfileUseCase(),
                    uiLocalState
                ) { data, profile, local ->

                    val consumedCalories = data.meals.sumOf { it.calories }
                    val macrosWithGoals = data.macros.copy(
                        caloriesGoal = profile.caloriesGoal,
                        proteinsGoal = profile.proteinGoal,
                        carbsGoal = profile.carbsGoal,
                        fatsGoal = profile.fatGoal
                    )
                    MacroDetailUiState(
                        consumedCalories = consumedCalories,
                        dailyCaloriesGoal = profile.caloriesGoal,
                        remainingCalories = (profile.caloriesGoal - consumedCalories)
                                .coerceAtLeast(0),
                        mealsToday = data.meals,
                        todayMacros = macrosWithGoals,
                        editingMeal = local.editingMeal,
                        isEditingSheetOpen = local.isEditingSheetOpen,
                        isLoading = false
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MacroDetailUiState(isLoading = true)
            )


    fun onEditMeal(meal: Meal) {
        uiLocalState.update {
            it.copy(
                editingMeal = meal,
                isEditingSheetOpen = true
            )
        }
    }

    fun onDismissEditMeal() {
        uiLocalState.update {
            it.copy(
                editingMeal = null,
                isEditingSheetOpen = false
            )
        }
    }

    fun onUpdateMeal(updatedMeal: Meal) {
        viewModelScope.launch {
            updateMealUseCase(updatedMeal)
        }
        uiLocalState.update {
            it.copy(editingMeal = null, isEditingSheetOpen = false)
        }
    }

    fun onDeleteMeal(meal: Meal) {
        viewModelScope.launch {
            deleteMealUseCase(meal)
        }
        uiLocalState.update {
            it.copy(editingMeal = null, isEditingSheetOpen = false)
        }
    }
}
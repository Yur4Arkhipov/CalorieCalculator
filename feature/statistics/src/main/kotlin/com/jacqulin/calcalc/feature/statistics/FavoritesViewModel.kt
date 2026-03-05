package com.jacqulin.calcalc.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.UpdateMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val meals: List<Meal> = emptyList(),
    val isLoading: Boolean = true,
    val editingMeal: Meal? = null,
    val isEditingSheetOpen: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    mealRepository: MealRepository,
    private val updateMealUseCase: UpdateMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase
) : ViewModel() {

    private val editingFlow = MutableStateFlow<Pair<Meal?, Boolean>>(Pair(null, false))

    val uiState: StateFlow<FavoritesUiState> = combine(
        mealRepository.observeFavoriteMeals(),
        editingFlow
    ) { meals, editingPair ->
        FavoritesUiState(
            meals = meals,
            isLoading = false,
            editingMeal = editingPair.first,
            isEditingSheetOpen = editingPair.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState(isLoading = true)
    )

    fun onEditMeal(meal: Meal) {
        editingFlow.value = Pair(meal, true)
    }

    fun onDismissEditMeal() {
        editingFlow.value = Pair(null, false)
    }

    fun onUpdateMeal(meal: Meal) {
        viewModelScope.launch { updateMealUseCase(meal) }
        editingFlow.value = Pair(null, false)
    }

    fun onDeleteMeal(meal: Meal) {
        viewModelScope.launch { deleteMealUseCase(meal) }
        editingFlow.value = Pair(null, false)
    }
}


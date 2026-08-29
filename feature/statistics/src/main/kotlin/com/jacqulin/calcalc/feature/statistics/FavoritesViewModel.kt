package com.jacqulin.calcalc.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
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
    val selectedMealIds: Set<Int> = emptySet()
) {
    val isSelectionMode: Boolean
        get() = selectedMealIds.isNotEmpty()

    val selectedCount: Int
        get() = selectedMealIds.size
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val mealRepository: MealRepository,
) : ViewModel() {

    private val _selectedMealIds = MutableStateFlow<Set<Int>>(emptySet())

    val uiState: StateFlow<FavoritesUiState> = combine(
        mealRepository.observeFavoriteMeals(),
        _selectedMealIds
    ) { meals, selectedIds ->
        FavoritesUiState(
            meals = meals,
            isLoading = false,
            selectedMealIds = selectedIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState(isLoading = true)
    )

    fun onMealLongClick(meal: Meal) {
        _selectedMealIds.value = setOf(meal.id)
    }

    fun onMealClick(meal: Meal) {
        val selected = _selectedMealIds.value
        if (selected.isEmpty()) {
            return
        }
        _selectedMealIds.value =
            if (meal.id in selected) {
                selected - meal.id
            } else {
                selected + meal.id
            }
    }

    fun clearSelection() {
        _selectedMealIds.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val ids = _selectedMealIds.value.toList()
            if (ids.isEmpty()) return@launch
            mealRepository.removeFromFavorites(ids)
            _selectedMealIds.value = emptySet()
        }
    }
}


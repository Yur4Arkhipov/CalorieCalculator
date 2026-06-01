package com.jacqulin.calcalc.feature.home.ui.mealdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jacqulin.calcalc.core.domain.usecase.GetMealDetailUseCase
import com.jacqulin.calcalc.feature.home.navigation.MealDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        viewModelScope.launch {
            val meal = getMealDetail(route.mealId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    meal = meal
                )
            }
        }
    }
}
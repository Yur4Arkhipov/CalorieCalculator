package com.jacqulin.calcalc.feature.home.ui.macrodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MacroDetailViewModel @Inject constructor(
    private val getDayDataUseCase: GetDayDataUseCase,
    observeSelectedDate: ObserveSelectedDateUseCase,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MacroDetailUiState> =
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
}
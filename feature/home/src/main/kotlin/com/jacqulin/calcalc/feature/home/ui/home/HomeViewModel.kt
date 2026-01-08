package com.jacqulin.calcalc.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.CalendarDay
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDayDataUseCase: GetDayDataUseCase,
    private val generateWeekDaysUseCase: GenerateWeekDaysUseCase,
    private val setSelectedDateUseCase: SetSelectedDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        onDateSelected(Date())
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val weekDays = generateWeekDaysUseCase(
                    weekIndex = _uiState.value.currentWeekIndex,
                    selectedDate = _uiState.value.selectedDate
                )
                val selectedDateData = getDayDataUseCase(_uiState.value.selectedDate)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weekDays = weekDays,
                    consumedCalories = selectedDateData.meals.sumOf { it.calories },
                    remainingCalories = (_uiState.value.dailyCaloriesGoal - selectedDateData.meals.sumOf { it.calories }).coerceAtLeast(0),
                    mealsToday = selectedDateData.meals,
                    todayMacros = selectedDateData.macros
                )
            } catch (e: Exception) {
                // Handle error
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun generateWeekDays(): List<CalendarDay> {
        return generateWeekDaysUseCase(
            weekIndex = _uiState.value.currentWeekIndex,
            selectedDate = _uiState.value.selectedDate
        )
    }

    fun onDateSelected(date: Date) {
        viewModelScope.launch {
            try {
                setSelectedDateUseCase(date)
                val selectedDateData = getDayDataUseCase(date)
                val dateFormat = SimpleDateFormat("d MMMM", Locale.forLanguageTag("ru"))
                val weekDays = generateWeekDays()

                _uiState.value = _uiState.value.copy(
                    selectedDate = date,
                    currentDate = dateFormat.format(date),
                    consumedCalories = selectedDateData.meals.sumOf { it.calories },
                    remainingCalories = (_uiState.value.dailyCaloriesGoal - selectedDateData.meals.sumOf { it.calories }).coerceAtLeast(0),
                    mealsToday = selectedDateData.meals,
                    todayMacros = selectedDateData.macros,
                    weekDays = weekDays
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onWeekChanged(weekIndex: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentWeekIndex = weekIndex,
                weekDays = generateWeekDays()
            )
        }
    }
}
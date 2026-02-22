package com.jacqulin.calcalc.feature.home.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.feature.home.model.CalendarDay
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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
        onWeekChanged(0)
        onDateSelected(Date())
    }

    fun onDateSelected(date: Date) {
        viewModelScope.launch {
            setSelectedDateUseCase(date)
            Log.d("CURDATE", "$date")
            refreshState(date)
        }
    }

    fun onWeekChanged(weekIndex: Int) {
        Log.d("onWeekChanged", "week idx: $weekIndex")
        viewModelScope.launch {
            val dates = generateWeekDaysUseCase(weekIndex)
            Log.d("onWeekChanged", "dates: $dates")
            val weekDays = mapToCalendarDays(dates)
            Log.d("onWeekChanged", "weekDays: $weekDays")
            _uiState.value = _uiState.value.copy(
                currentWeekIndex = weekIndex,
                weekDays = weekDays
            )
        }
    }

    private fun mapToCalendarDays(dates: List<Date>): List<CalendarDay> {
        val selectedDate = _uiState.value.selectedDate
        Log.d("mapToCalendarDays", "selectedDate: $selectedDate")
        val today = Date()
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dates.map { date ->
            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                isToday = isSameDay(date, today),
                isSelected = isSameDay(date, selectedDate)
            )
        }
    }

    private suspend fun refreshState(date: Date) {
        val selectedDateData = getDayDataUseCase(date)
        val consumed = selectedDateData.meals.sumOf { it.calories }

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            consumedCalories = consumed,
            remainingCalories = (_uiState.value.dailyCaloriesGoal - consumed)
                .coerceAtLeast(0),
            mealsToday = selectedDateData.meals,
            todayMacros = selectedDateData.macros,
            isLoading = false
        )
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
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

const val MAX_FUTURE_WEEKS = 1
const val MAX_PAST_WEEKS = 20

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDayDataUseCase: GetDayDataUseCase,
    private val generateWeekDaysUseCase: GenerateWeekDaysUseCase,
    private val setSelectedDateUseCase: SetSelectedDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        preloadAllWeeks()
        onDateSelected(Date())
    }

    fun onDateSelected(date: Date) {
        viewModelScope.launch {
            setSelectedDateUseCase(date)
            refreshState(date)
        }
    }

    fun onWeekChanged(weekIndex: Int) {
        val weekDays = _uiState.value.weeks[weekIndex] ?: return
        _uiState.value = _uiState.value.copy(
            currentWeekIndex = weekIndex,
            weekDays = weekDays
        )

            Log.d("onWeekChanged", "weekIdx: $weekIndex")
    }

    private fun preloadAllWeeks() {
        viewModelScope.launch {
            val today = Date()
            val allWeeks = mutableMapOf<Int, List<CalendarDay>>()
            for (weekIndex in -MAX_PAST_WEEKS..MAX_FUTURE_WEEKS) {
                val dates = generateWeekDaysUseCase(weekIndex)
                allWeeks[weekIndex] = mapToCalendarDays(dates, today)
            }
            _uiState.value = _uiState.value.copy(
                weeks = allWeeks,
                currentWeekIndex = 0,
                weekDays = allWeeks[0] ?: emptyList()
            )
        }
    }

    private fun mapToCalendarDays(dates: List<Date>, selectedDate: Date): List<CalendarDay> {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val todayDate = today.time
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dates.map { date ->
            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                isToday = isSameDay(date, todayDate),
                isSelected = isSameDay(date, selectedDate),
                isFuture = date.after(today.time)
            )
        }
    }

    private suspend fun refreshState(date: Date) {
        val selectedDateData = getDayDataUseCase(date)
        val consumed = selectedDateData.meals.sumOf { it.calories }

        val updatedWeeks = _uiState.value.weeks.mapValues { (_, days) ->
            days.map { day -> day.copy(isSelected = isSameDay(day.date, date)) }
        }

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            weeks = updatedWeeks,
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
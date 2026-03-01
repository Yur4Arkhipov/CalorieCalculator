package com.jacqulin.calcalc.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import com.jacqulin.calcalc.feature.home.model.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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
    observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
    private val generateWeekDaysUseCase: GenerateWeekDaysUseCase,
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val setSelectedDateUseCase: SetSelectedDateUseCase
) : ViewModel() {

    private val currentWeekIndexFlow = MutableStateFlow(0)

    private val weeksFlow = flow {
        val today = Date()
        val allWeeks = mutableMapOf<Int, List<CalendarDay>>()

        for (weekIndex in -MAX_PAST_WEEKS..MAX_FUTURE_WEEKS) {
            val dates = generateWeekDaysUseCase(weekIndex)
            allWeeks[weekIndex] = mapToCalendarDays(dates, today)
        }

        emit(allWeeks)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> =
        combine(
            observeSelectedDateUseCase(),
            currentWeekIndexFlow,
            observeUserProfileUseCase()
        ) { selectedDate, weekIndex, profile ->
            Triple(selectedDate, weekIndex, profile)
        }
            .flatMapLatest { (selectedDate, weekIndex, profile) ->
                combine(
                    getDayDataUseCase(selectedDate),
                    weeksFlow
                ) { dayData, weeks ->

                    val consumedCalories = dayData.meals.sumOf { it.calories }

                    val updatedWeeks = weeks.mapValues { (_, days) ->
                        days.map {
                            it.copy(isSelected = isSameDay(it.date, selectedDate))
                        }
                    }

                    val macrosWithGoals = dayData.macros.copy(
                        caloriesGoal = profile.caloriesGoal,
                        proteinsGoal = profile.proteinGoal,
                        carbsGoal = profile.carbsGoal,
                        fatsGoal = profile.fatGoal
                    )

                    HomeUiState(
                        selectedDate = selectedDate,
                        weeks = updatedWeeks,
                        currentWeekIndex = weekIndex,
                        weekDays = updatedWeeks[weekIndex] ?: emptyList(),
                        mealsToday = dayData.meals,
                        todayMacros = macrosWithGoals,
                        consumedCalories = consumedCalories,
                        dailyCaloriesGoal = profile.caloriesGoal,
                        remainingCalories = (profile.caloriesGoal - consumedCalories)
                            .coerceAtLeast(0),
                        isLoading = false
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HomeUiState(isLoading = true)
            )

    fun onDateSelected(date: Date) {
        viewModelScope.launch {
            setSelectedDateUseCase(date)
        }
    }

    fun onWeekChanged(weekIndex: Int) {
        currentWeekIndexFlow.value = weekIndex
    }

    private fun mapToCalendarDays(
        dates: List<Date>,
        selectedDate: Date
    ): List<CalendarDay> {
        val today = Date()
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        return dates.map { date ->
            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                isToday = isSameDay(date, today),
                isSelected = isSameDay(date, selectedDate),
                isFuture = date.after(today)
            )
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
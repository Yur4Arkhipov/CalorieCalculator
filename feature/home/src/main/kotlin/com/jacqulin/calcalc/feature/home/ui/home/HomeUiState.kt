package com.jacqulin.calcalc.feature.home.ui.home

import com.jacqulin.calcalc.core.domain.model.CalendarDay
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal
import java.util.Date

data class HomeUiState(
    val selectedDate: Date = Date(),
    val currentDate: String = "",
    val weekDays: List<CalendarDay> = emptyList(),
    val currentWeekIndex: Int = 0,
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 2000,
    val dailyCaloriesGoal: Int = 2000,
    val mealsToday: List<Meal> = emptyList(),
    val todayMacros: MacroNutrients = MacroNutrients(0f, 0f, 0f, 150f, 240f, 67f),
    val isLoading: Boolean = true
)
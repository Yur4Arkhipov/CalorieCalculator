package com.jacqulin.calcalc.feature.home.ui.home

import com.jacqulin.calcalc.feature.home.model.CalendarDay
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal
import java.util.Date

data class HomeUiState(
    val selectedDate: Date = Date(),
    val currentDate: String = "",
    val weekDays: List<CalendarDay> = emptyList(),
    val weeks: Map<Int, List<CalendarDay>> = emptyMap(),
    val currentWeekIndex: Int = 0,
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 0,
    val dailyCaloriesGoal: Int = 0,
    val mealsToday: List<Meal> = emptyList(),
    val todayMacros: MacroNutrients = MacroNutrients(
        calories = 0,
        protein = 0,
        carb = 0,
        fat = 0,
        caloriesGoal = 0,
        proteinsGoal = 0,
        carbsGoal = 0,
        fatsGoal = 0
    ),
    val isLoading: Boolean = true
)
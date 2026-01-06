package com.jacqulin.calcalc.feature.home.ui.model

import java.util.Date

data class CalendarDay(
    val date: Date,
    val displayDay: String,
    val displayDate: String,
    val calories: Int = 0,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)

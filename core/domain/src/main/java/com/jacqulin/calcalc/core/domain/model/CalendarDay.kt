package com.jacqulin.calcalc.core.domain.model

import java.util.Date

data class CalendarDay(
    val date: Date,
    val displayDay: String,
    val displayDate: String,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)

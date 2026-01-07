package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.CalendarDay
import java.util.Date

interface GenerateWeekDaysUseCase  {
    suspend operator fun invoke(weekIndex: Int, selectedDate: Date) : List<CalendarDay>
}
package com.jacqulin.calcalc.core.domain.usecase

import java.util.Date

interface GenerateWeekDaysUseCase  {
    suspend operator fun invoke(weekIndex: Int): List<Date>
}
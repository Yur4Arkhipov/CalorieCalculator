package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.DayData
import java.util.Date

interface GetDayDataUseCase  {
    suspend operator fun invoke(date: Date) : DayData
}
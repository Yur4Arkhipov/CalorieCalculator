package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.DayData
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface GetDayDataUseCase {
    operator fun invoke(date: Date): Flow<DayData>
}
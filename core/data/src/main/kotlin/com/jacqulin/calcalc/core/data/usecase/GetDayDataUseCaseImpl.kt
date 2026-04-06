package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetDayDataUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GetDayDataUseCase {

    override fun invoke(date: Date): Flow<DayData> {
        return mealRepository.observeDayData(date)
    }
}

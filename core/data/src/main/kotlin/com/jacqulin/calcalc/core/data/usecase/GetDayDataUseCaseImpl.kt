package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import java.util.Date
import javax.inject.Inject

class GetDayDataUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GetDayDataUseCase {
    override suspend fun invoke(date: Date): DayData {
        val repositoryData = mealRepository.getDayData(date)
        return DayData(
            meals = repositoryData.meals,
            macros = repositoryData.macros
        )
    }
}

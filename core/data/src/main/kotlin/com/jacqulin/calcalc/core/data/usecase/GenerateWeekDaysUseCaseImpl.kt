package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GenerateWeekDaysUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GenerateWeekDaysUseCase {
    override suspend fun invoke(weekIndex: Int): List<Date> {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY

        calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        calendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

        return (0..6).map { offset ->
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.DAY_OF_YEAR, offset)
            cal.time
        }
    }
}
package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.CalendarDay
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GenerateWeekDaysUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GenerateWeekDaysUseCase {
    override suspend fun invoke(weekIndex: Int, selectedDate: Date): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        val todayCalendar = Calendar.getInstance()
        todayCalendar.time = today

        val dayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        todayCalendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        todayCalendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

        return (0..6).map { dayOffset ->
            calendar.time = todayCalendar.time
            calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
            val date = calendar.time

            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                isToday = isSameDay(date, today),
                isSelected = isSameDay(date, selectedDate)
            )
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
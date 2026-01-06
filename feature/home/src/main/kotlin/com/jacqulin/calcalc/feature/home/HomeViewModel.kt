package com.jacqulin.calcalc.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.feature.home.ui.model.CalendarDay
import com.jacqulin.calcalc.feature.home.ui.model.MacroNutrients
import com.jacqulin.calcalc.feature.home.ui.model.Meal
import com.jacqulin.calcalc.feature.home.ui.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HomeUiState(
    val selectedDate: Date = Date(),
    val currentDate: String = "",
    val weekDays: List<CalendarDay> = emptyList(),
    val currentWeekIndex: Int = 0,
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 2000,
    val dailyCaloriesGoal: Int = 2000,
    val mealsToday: List<Meal> = emptyList(),
    val waterGlasses: Int = 0,
    val waterGoal: Int = 8,
    val todayMacros: MacroNutrients = MacroNutrients(0f, 0f, 0f, 150f, 240f, 67f),
    val showDatePicker: Boolean = false,
    val userName: String = "Пользователь",
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dailyData = mutableMapOf<String, DayData>()

    init {
        generateMockData()
        loadData()
        onDateSelected(Date())
    }


    private fun generateMockData() {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        // Генерируем данные на неделю назад
        for (i in -7..0) {
            calendar.time = today
            calendar.add(Calendar.DAY_OF_YEAR, i)
            val date = calendar.time
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

            val meals = when (i) {
                0 -> listOf( // Сегодня
                    Meal("Овсянка с ягодами", 320, "08:30", MealType.BREAKFAST),
                    Meal("Греческий салат", 280, "13:15", MealType.LUNCH),
                    Meal("Яблоко", 95, "16:00", MealType.SNACK)
                )
                -1 -> listOf( // Вчера
                    Meal("Тост с авокадо", 280, "09:00", MealType.BREAKFAST),
                    Meal("Куриная грудка с рисом", 420, "13:30", MealType.LUNCH),
                    Meal("Йогурт с орехами", 180, "16:30", MealType.SNACK),
                    Meal("Лосось с овощами", 380, "19:00", MealType.DINNER)
                )
                else -> listOf( // Другие дни
                    Meal("Завтрак", (200..400).random(), "08:${(0..59).random().toString().padStart(2, '0')}", MealType.BREAKFAST),
                    Meal("Обед", (300..600).random(), "13:${(0..59).random().toString().padStart(2, '0')}", MealType.LUNCH),
                    Meal("Ужин", (250..500).random(), "19:${(0..59).random().toString().padStart(2, '0')}", MealType.DINNER)
                )
            }

            dailyData[dateKey] = DayData(
                meals = meals,
                water = (4..10).random(),
                macros = MacroNutrients(
                    proteins = if (i == 0) 85f else (50..120).random().toFloat(), // Сегодня больше белков
//                    carbs = if (i == 0) 180f else (120..250).random().toFloat(),  // Сегодня больше углеводов
                    carbs = if (i == 0) 180f else (320..400).random().toFloat(),  // Сегодня больше углеводов
                    fats = if (i == 0) 45f else (30..80).random().toFloat()       // Сегодня больше жиров
                )
            )
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val weekDays = generateWeekDays()
            val selectedDateData = getDataForDate(_uiState.value.selectedDate)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                weekDays = weekDays,
                consumedCalories = selectedDateData.meals.sumOf { it.calories },
                remainingCalories = (_uiState.value.dailyCaloriesGoal - selectedDateData.meals.sumOf { it.calories }).coerceAtLeast(0),
                mealsToday = selectedDateData.meals,
                waterGlasses = selectedDateData.water,
                todayMacros = selectedDateData.macros
            )
        }
    }

    private fun generateWeekDays(): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val selectedDate = _uiState.value.selectedDate
        val weekIndex = _uiState.value.currentWeekIndex
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Находим понедельник текущей недели
        val todayCalendar = Calendar.getInstance()
        todayCalendar.time = today

        // Находим понедельник текущей недели
        val dayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        todayCalendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        // Добавляем смещение для нужной недели
        todayCalendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

        return (0..6).map { dayOffset -> // 7 дней недели (пн-вс)
            calendar.time = todayCalendar.time
            calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
            val date = calendar.time
            val dateKey = keyFormat.format(date)
            val dayData = dailyData[dateKey]

            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                calories = dayData?.meals?.sumOf { it.calories } ?: 0,
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

    private fun getDataForDate(date: Date): DayData {
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        return dailyData[dateKey] ?: DayData()
    }

    fun onDateSelected(date: Date) {
        val selectedDateData = getDataForDate(date)
        val dateFormat = SimpleDateFormat("d MMMM", Locale.forLanguageTag("ru"))

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            currentDate = dateFormat.format(date),
            consumedCalories = selectedDateData.meals.sumOf { it.calories },
            remainingCalories = (_uiState.value.dailyCaloriesGoal - selectedDateData.meals.sumOf { it.calories }).coerceAtLeast(0),
            mealsToday = selectedDateData.meals,
            waterGlasses = selectedDateData.water,
            todayMacros = selectedDateData.macros,
            weekDays = generateWeekDays()
        )
    }

    fun addWaterGlass() {
        val currentState = _uiState.value
        if (currentState.waterGlasses < currentState.waterGoal) {
            // Обновляем данные в моковом хранилище
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentState.selectedDate)
            dailyData[dateKey] = dailyData[dateKey]?.copy(water = currentState.waterGlasses + 1)
                ?: DayData(water = currentState.waterGlasses + 1)

            _uiState.value = currentState.copy(
                waterGlasses = currentState.waterGlasses + 1
            )
        }
    }

    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    fun navigateToNextWeek() {
        _uiState.value = _uiState.value.copy(
            currentWeekIndex = _uiState.value.currentWeekIndex + 1,
            weekDays = generateWeekDays()
        )
    }

    fun navigateToPreviousWeek() {
        _uiState.value = _uiState.value.copy(
            currentWeekIndex = _uiState.value.currentWeekIndex - 1,
            weekDays = generateWeekDays()
        )
    }

    fun navigateToCurrentWeek() {
        _uiState.value = _uiState.value.copy(
            currentWeekIndex = 0,
            selectedDate = Date(),
            weekDays = generateWeekDays()
        )
        onDateSelected(Date()) // Автоматически выбираем сегодняшний день
    }

    fun onWeekChanged(weekIndex: Int) {
        _uiState.value = _uiState.value.copy(
            currentWeekIndex = weekIndex,
            weekDays = generateWeekDays()
        )
    }
}

data class DayData(
    val meals: List<Meal> = emptyList(),
    val water: Int = 0,
    val macros: MacroNutrients = MacroNutrients()
)

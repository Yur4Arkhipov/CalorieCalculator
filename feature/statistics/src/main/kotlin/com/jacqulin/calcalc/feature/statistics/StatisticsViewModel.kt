package com.jacqulin.calcalc.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: TimePeriod = TimePeriod.WEEK,
    val dailyStats: List<DailyStats> = emptyList(),
    val weeklyAverage: WeeklyAverage = WeeklyAverage(),
    val selectedDayStats: DailyStats? = null,
    val showEditDialog: Boolean = false
)

enum class TimePeriod(val displayName: String) {
    WEEK("Неделя"),
    MONTH("Месяц"),
    YEAR("Год")
}

data class DailyStats(
    val date: String,
    val calories: Int,
    val caloriesGoal: Int,
    val water: Int,
    val waterGoal: Int,
    val proteins: Float,
    val carbs: Float,
    val fats: Float,
    val proteinsGoal: Float,
    val carbsGoal: Float,
    val fatsGoal: Float,
    val mealsCount: Int,
    val weight: Float? = null,
    val steps: Int = 0
) {
    val caloriesProgress: Float get() = (calories.toFloat() / caloriesGoal).coerceIn(0f, 1.2f)
    val waterProgress: Float get() = (water.toFloat() / waterGoal).coerceIn(0f, 1.2f)
}

data class WeeklyAverage(
    val avgCalories: Int = 0,
    val avgWater: Int = 0,
    val avgWeight: Float = 0f,
    val avgSteps: Int = 0,
    val perfectDays: Int = 0,
    val totalDays: Int = 7
)

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            // Генерируем моковые данные для демонстрации
            val mockData = generateMockWeeklyData()
            val weeklyAvg = calculateWeeklyAverage(mockData)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dailyStats = mockData,
                weeklyAverage = weeklyAvg
            )
        }
    }

    fun onPeriodChanged(period: TimePeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadStatisticsForPeriod(period)
    }

    fun onDaySelected(dayStats: DailyStats) {
        _uiState.value = _uiState.value.copy(selectedDayStats = dayStats)
    }

    fun showEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = true)
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false)
    }

    fun updateDayStats(updatedStats: DailyStats) {
        val currentStats = _uiState.value.dailyStats.toMutableList()
        val index = currentStats.indexOfFirst { it.date == updatedStats.date }
        if (index != -1) {
            currentStats[index] = updatedStats
            val weeklyAvg = calculateWeeklyAverage(currentStats)
            _uiState.value = _uiState.value.copy(
                dailyStats = currentStats,
                weeklyAverage = weeklyAvg,
                selectedDayStats = updatedStats,
                showEditDialog = false
            )
        }
    }

    private fun loadStatisticsForPeriod(period: TimePeriod) {
        viewModelScope.launch {
            val mockData = when (period) {
                TimePeriod.WEEK -> generateMockWeeklyData()
                TimePeriod.MONTH -> generateMockMonthlyData()
                TimePeriod.YEAR -> generateMockYearlyData()
            }
            val weeklyAvg = calculateWeeklyAverage(mockData)

            _uiState.value = _uiState.value.copy(
                dailyStats = mockData,
                weeklyAverage = weeklyAvg
            )
        }
    }

    private fun calculateWeeklyAverage(stats: List<DailyStats>): WeeklyAverage {
        if (stats.isEmpty()) return WeeklyAverage()

        return WeeklyAverage(
            avgCalories = stats.map { it.calories }.average().toInt(),
            avgWater = stats.map { it.water }.average().toInt(),
            avgWeight = stats.mapNotNull { it.weight }.average().toFloat(),
            avgSteps = stats.map { it.steps }.average().toInt(),
            perfectDays = stats.count { it.caloriesProgress >= 0.9f && it.caloriesProgress <= 1.1f },
            totalDays = stats.size
        )
    }

    private fun generateMockWeeklyData(): List<DailyStats> {
        val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6) // Начинаем с недели назад

        return (0..6).map { dayIndex ->
            val currentDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            DailyStats(
                date = dateFormat.format(currentDate),
                calories = Random.nextInt(1500, 2500),
                caloriesGoal = 2000,
                water = Random.nextInt(5, 12),
                waterGoal = 8,
                proteins = Random.nextFloat() * 50 + 100,
                carbs = Random.nextFloat() * 100 + 200,
                fats = Random.nextFloat() * 30 + 50,
                proteinsGoal = 150f,
                carbsGoal = 250f,
                fatsGoal = 67f,
                mealsCount = Random.nextInt(3, 6),
                weight = if (Random.nextBoolean()) 70f + Random.nextFloat() * 10 else null,
                steps = Random.nextInt(5000, 15000)
            )
        }
    }

    private fun generateMockMonthlyData(): List<DailyStats> {
        val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -29)

        return (0..29).map { dayIndex ->
            val currentDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            DailyStats(
                date = dateFormat.format(currentDate),
                calories = Random.nextInt(1500, 2500),
                caloriesGoal = 2000,
                water = Random.nextInt(5, 12),
                waterGoal = 8,
                proteins = Random.nextFloat() * 50 + 100,
                carbs = Random.nextFloat() * 100 + 200,
                fats = Random.nextFloat() * 30 + 50,
                proteinsGoal = 150f,
                carbsGoal = 250f,
                fatsGoal = 67f,
                mealsCount = Random.nextInt(3, 6),
                weight = if (Random.nextBoolean()) 70f + Random.nextFloat() * 10 else null,
                steps = Random.nextInt(5000, 15000)
            )
        }
    }

    private fun generateMockYearlyData(): List<DailyStats> {
        // Для года показываем данные по неделям
        val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -11)

        return (0..11).map { monthIndex ->
            val currentDate = calendar.time
            calendar.add(Calendar.MONTH, 1)

            DailyStats(
                date = dateFormat.format(currentDate),
                calories = Random.nextInt(1800, 2200),
                caloriesGoal = 2000,
                water = Random.nextInt(6, 10),
                waterGoal = 8,
                proteins = Random.nextFloat() * 30 + 130,
                carbs = Random.nextFloat() * 50 + 225,
                fats = Random.nextFloat() * 20 + 57,
                proteinsGoal = 150f,
                carbsGoal = 250f,
                fatsGoal = 67f,
                mealsCount = Random.nextInt(3, 5),
                weight = if (Random.nextBoolean()) 70f + Random.nextFloat() * 5 else null,
                steps = Random.nextInt(7000, 12000)
            )
        }
    }
}
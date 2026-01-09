package com.jacqulin.calcalc.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile = UserProfile(),
    val showEditDialog: Boolean = false,
    val showStatsDialog: Boolean = false,
    val achievements: List<Achievement> = emptyList(),
    val weeklyStats: WeeklyStats = WeeklyStats()
)

data class UserProfile(
    val name: String = "Пользователь",
    val age: Int = 25,
    val height: Float = 170f,
    val currentWeight: Float = 70f,
    val targetWeight: Float = 65f,
    val activityLevel: String = "Умеренная активность",
    val goal: String = "Похудение",
    val dailyCaloriesGoal: Int = 2000,
    val joinDate: String = "Январь 2024",
    val streak: Int = 7, // дней подряд выполнения целей
    val avatarEmoji: String = "👤"
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f, // от 0 до 1
    val maxProgress: Int = 100
)

data class WeeklyStats(
    val totalDays: Int = 7,
    val completedDays: Int = 5,
    val avgCalories: Int = 1850,
    val avgWater: Int = 7,
    val weightLoss: Float = 0.5f, // за неделю
    val totalWeightLoss: Float = 2.3f // с начала
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // Имитация загрузки данных
            val mockProfile = UserProfile(
                name = "Анна",
                age = 28,
                height = 165f,
                currentWeight = 68.5f,
                targetWeight = 62f,
                activityLevel = "Активный образ жизни",
                goal = "Похудение и поддержание формы",
                dailyCaloriesGoal = 1800,
                joinDate = "Декабрь 2023",
                streak = 12,
                avatarEmoji = "👩‍💼"
            )

            val mockAchievements = listOf(
                Achievement("first_week", "Первая неделя", "Ведите дневник питания 7 дней подряд", "🏆", true, 1f, 7),
                Achievement("water_master", "Мастер воды", "Выпейте цель по воде 30 дней", "💧", false, 0.6f, 30),
                Achievement("calorie_tracker", "Точный трекер", "Отслеживайте калории 50 дней", "🎯", false, 0.24f, 50),
                Achievement("weight_loss", "Цель достигнута", "Сбросьте 5 кг", "⚖️", false, 0.46f, 5),
                Achievement("healthy_meals", "Здоровое питание", "Ешьте 5 порций овощей/фруктов в день", "🥗", true, 1f, 100),
                Achievement("streak_master", "Чемпион постоянства", "Выполняйте цели 30 дней подряд", "🔥", false, 0.4f, 30)
            )

            val mockWeeklyStats = WeeklyStats(
                completedDays = 5,
                avgCalories = 1750,
                avgWater = 8,
                weightLoss = 0.3f,
                totalWeightLoss = 1.5f
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                userProfile = mockProfile,
                achievements = mockAchievements,
                weeklyStats = mockWeeklyStats
            )
        }
    }

    fun showEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = true)
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false)
    }

    fun showStatsDialog() {
        _uiState.value = _uiState.value.copy(showStatsDialog = true)
    }

    fun hideStatsDialog() {
        _uiState.value = _uiState.value.copy(showStatsDialog = false)
    }

    fun updateProfile(updatedProfile: UserProfile) {
        _uiState.value = _uiState.value.copy(
            userProfile = updatedProfile,
            showEditDialog = false
        )
    }

    fun logout() {
        // TODO: Implement logout logic
        viewModelScope.launch {
            // Clear user data and navigate to onboarding
        }
    }
}

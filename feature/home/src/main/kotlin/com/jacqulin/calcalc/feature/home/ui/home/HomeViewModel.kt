package com.jacqulin.calcalc.feature.home.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.TempImage
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.AiAccessRepository
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import com.jacqulin.calcalc.feature.home.model.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

const val MAX_FUTURE_WEEKS = 1
const val MAX_PAST_WEEKS = 20

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
    private val generateWeekDaysUseCase: GenerateWeekDaysUseCase,
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val setSelectedDateUseCase: SetSelectedDateUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
    private val imageRepository: ImageRepository,
    aiAccessRepository: AiAccessRepository
) : ViewModel() {

    private val currentWeekIndexFlow = MutableStateFlow(0)
    private val _selectedMealIds = MutableStateFlow<Set<Int>>(emptySet())

    private val _uiEvents = Channel<HomeUiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val weeksFlow = flow {
        val today = Date()
        val allWeeks = mutableMapOf<Int, List<CalendarDay>>()

        for (weekIndex in -MAX_PAST_WEEKS..MAX_FUTURE_WEEKS) {
            val dates = generateWeekDaysUseCase(weekIndex)
            allWeeks[weekIndex] = mapToCalendarDays(dates, today)
        }

        emit(allWeeks)
    }

    private data class HomeInputs(
        val selectedDate: Date,
        val weekIndex: Int,
        val profile: UserProfile,
        val isAiAccessAllowed: Boolean
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> =
        combine(
            observeSelectedDateUseCase(),
            currentWeekIndexFlow,
            observeUserProfileUseCase(),
            aiAccessRepository.observeAccessAllowed()
        ) { selectedDate, weekIndex, profile, isAiAccessAllowed  ->
            HomeInputs(
                selectedDate = selectedDate,
                weekIndex = weekIndex,
                profile = profile,
                isAiAccessAllowed = isAiAccessAllowed
            )
        }
            .flatMapLatest { inputs ->
                combine(
                    getDayDataUseCase(inputs.selectedDate),
                    weeksFlow,
                    _selectedMealIds
                ) { dayData, weeks, selectedIds ->

                    val consumedCalories = dayData.meals.sumOf { it.calories }

                    val updatedWeeks = weeks.mapValues { (_, days) ->
                        days.map {
                            it.copy(isSelected = isSameDay(it.date, inputs.selectedDate))
                        }
                    }

                    val macrosWithGoals = dayData.macros.copy(
                        caloriesGoal = inputs.profile.caloriesGoal,
                        proteinsGoal = inputs.profile.proteinGoal,
                        carbsGoal = inputs.profile.carbsGoal,
                        fatsGoal = inputs.profile.fatGoal
                    )

                    HomeUiState(
                        selectedDate = inputs.selectedDate,
                        weeks = updatedWeeks,
                        currentWeekIndex = inputs.weekIndex,
                        weekDays = updatedWeeks[inputs.weekIndex] ?: emptyList(),
                        mealsToday = dayData.meals,
                        todayMacros = macrosWithGoals,
                        consumedCalories = consumedCalories,
                        dailyCaloriesGoal = inputs.profile.caloriesGoal,
                        remainingCalories = (inputs.profile.caloriesGoal - consumedCalories)
                            .coerceAtLeast(0),
                        isLoading = false,
                        selectedMealIds = selectedIds,
                        isAiAccessAllowed = inputs.isAiAccessAllowed,
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HomeUiState(isLoading = true)
            )

    fun onDateSelected(date: Date) {
        viewModelScope.launch {
            setSelectedDateUseCase(date)
        }
    }

    fun onWeekChanged(weekIndex: Int) {
        currentWeekIndexFlow.value = weekIndex
    }

    private suspend fun deleteMeal(meal: Meal) {
        deleteMealUseCase(meal)

        meal.imageUri?.let {
            imageRepository.deleteImage(it)
        }
    }

    fun onAddPhotoFromGallery() {
        viewModelScope.launch {
            _uiEvents.send(HomeUiEvent.LaunchGallery)
        }
    }

    private fun onAddPhotoFromCamera() {
        viewModelScope.launch {
            val temp = imageRepository.createTempImage()
            _uiEvents.send(HomeUiEvent.LaunchCamera(temp))
        }
    }

    fun onRequestCameraPermission() {
        viewModelScope.launch {
            _uiEvents.send(HomeUiEvent.RequestCameraPermission)
        }
    }

    fun onCameraPermissionResult(granted: Boolean) {
        if (granted) onAddPhotoFromCamera()
    }

    fun onCameraResult(success: Boolean, temp: TempImage) {
        viewModelScope.launch {
            if (success) {
                _uiEvents.send(HomeUiEvent.NavigateToMealReview(temp))
            } else {
                imageRepository.deleteTempImage(temp.file.absolutePath)
            }
        }
    }

    fun onGalleryResult(uri: Uri) {
        viewModelScope.launch {
            val temp = imageRepository.copyUriToTemp(uri)
            _uiEvents.send(HomeUiEvent.NavigateToMealReview(temp))
        }
    }

    fun clearSelection() {
        _selectedMealIds.value = emptySet()
    }

    fun onMealLongClick(meal: Meal) {
        if (_selectedMealIds.value.isEmpty()) {
            _selectedMealIds.value = setOf(meal.id)
        }
    }

    fun onMealClick(meal: Meal) {
        val selected = _selectedMealIds.value

        if (selected.isNotEmpty()) {
            _selectedMealIds.value =
                if (meal.id in selected) {
                    selected - meal.id
                } else {
                    selected + meal.id
                }
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            try {
                val selectedIds = _selectedMealIds.value
                uiState.value.mealsToday
                    .filter { it.id in selectedIds }
                    .forEach { meal ->
                        deleteMeal(meal)
                    }
                _selectedMealIds.value = emptySet()
            } catch (e: Exception) {
                Log.e("DeleteMeal", "Error delete: $e")
            }
        }
    }

    private fun mapToCalendarDays(
        dates: List<Date>,
        selectedDate: Date
    ): List<CalendarDay> {
        val today = Date()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        return dates.map { date ->
            CalendarDay(
                date = date,
                displayDay = dayFormat.format(date),
                displayDate = dateFormat.format(date),
                isToday = isSameDay(date, today),
                isSelected = isSameDay(date, selectedDate),
                isFuture = date.after(today)
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
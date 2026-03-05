package com.jacqulin.calcalc.feature.home.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.PendingMeal
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.UpdateMealUseCase
import com.jacqulin.calcalc.core.util.NotFoodException
import com.jacqulin.calcalc.feature.home.model.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
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
    private val analyzeMealFromImageUseCase: AnalyzeMealFromImageUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    private val updateMealUseCase: UpdateMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val currentWeekIndexFlow = MutableStateFlow(0)
    private val pendingMealsFlow = MutableStateFlow<List<PendingMeal>>(emptyList())
    private val editingMealFlow = MutableStateFlow<Pair<Meal?, Boolean>>(Pair(null, false))
    private val selectedDate = observeSelectedDateUseCase()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> =
        combine(
            observeSelectedDateUseCase(),
            currentWeekIndexFlow,
            observeUserProfileUseCase()
        ) { selectedDate, weekIndex, profile ->
            Triple(selectedDate, weekIndex, profile)
        }
            .flatMapLatest { (selectedDate, weekIndex, profile) ->
                combine(
                    getDayDataUseCase(selectedDate),
                    weeksFlow,
                    pendingMealsFlow,
                    editingMealFlow
                ) { dayData, weeks, pendingMeals, editingPair ->

                    val consumedCalories = dayData.meals.sumOf { it.calories }

                    val updatedWeeks = weeks.mapValues { (_, days) ->
                        days.map {
                            it.copy(isSelected = isSameDay(it.date, selectedDate))
                        }
                    }

                    val macrosWithGoals = dayData.macros.copy(
                        caloriesGoal = profile.caloriesGoal,
                        proteinsGoal = profile.proteinGoal,
                        carbsGoal = profile.carbsGoal,
                        fatsGoal = profile.fatGoal
                    )

                    HomeUiState(
                        selectedDate = selectedDate,
                        weeks = updatedWeeks,
                        currentWeekIndex = weekIndex,
                        weekDays = updatedWeeks[weekIndex] ?: emptyList(),
                        mealsToday = dayData.meals,
                        pendingMeals = pendingMeals,
                        todayMacros = macrosWithGoals,
                        consumedCalories = consumedCalories,
                        dailyCaloriesGoal = profile.caloriesGoal,
                        remainingCalories = (profile.caloriesGoal - consumedCalories)
                            .coerceAtLeast(0),
                        editingMeal = editingPair.first,
                        isEditingSheetOpen = editingPair.second,
                        isLoading = false
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

    fun onImageCaptured(imageBytes: ByteArray, mealType: MealType) {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val imageFile = saveImageToFile(bitmap)
        val imageUri = imageFile?.absolutePath

        val pending = PendingMeal(type = mealType, isLoading = true, imageUri = imageUri)
        pendingMealsFlow.update { it + pending }

        viewModelScope.launch {
            try {
                val base64 = encodeImageForAi(bitmap)
                val nutrition = analyzeMealFromImageUseCase(base64)

                val mealName = nutrition.name.ifBlank { "Блюдо" }
                val meal = Meal(
                    name = mealName,
                    calories = nutrition.calories.toInt(),
                    proteins = nutrition.protein.toInt(),
                    fats = nutrition.fat.toInt(),
                    carbs = nutrition.carbs.toInt(),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = mealType,
                    imageUri = imageUri
                )
                saveManualAddMealDBUseCase(selectedDate.value, meal)
                pendingMealsFlow.update { list -> list.filter { it.id != pending.id } }
            } catch (_: NotFoodException) {
                if (imageUri != null) File(imageUri).delete()
                pendingMealsFlow.update { list -> list.filter { it.id != pending.id } }
                _uiEvents.send(HomeUiEvent.ShowNotFoodError)
            } catch (_: Exception) {
                if (imageUri != null) File(imageUri).delete()
                pendingMealsFlow.update { list ->
                    list.map {
                        if (it.id == pending.id) it.copy(isLoading = false, error = "Ошибка анализа")
                        else it
                    }
                }
            }
        }
    }

    fun dismissPendingError(id: String) {
        pendingMealsFlow.update { list -> list.filter { it.id != id } }
    }

    fun onEditMeal(meal: Meal) {
        editingMealFlow.value = Pair(meal, true)
    }

    fun onDismissEditMeal() {
        editingMealFlow.value = Pair(null, false)
    }

    fun onUpdateMeal(updatedMeal: Meal) {
        viewModelScope.launch {
            updateMealUseCase(updatedMeal)
        }
        editingMealFlow.value = Pair(null, false)
    }

    fun onDeleteMeal(meal: Meal) {
        viewModelScope.launch {
            deleteMealUseCase(meal)
            if (meal.imageUri != null) {
                try { File(meal.imageUri!!).delete() } catch (_: Exception) {}
            }
        }
        editingMealFlow.value = Pair(null, false)
    }

    fun onAddPhotoFromCamera(mealType: MealType) {
        viewModelScope.launch {
            val uri = createImageFileUri()
            _uiEvents.send(HomeUiEvent.LaunchCamera(uri, mealType))
        }
    }

    fun onAddPhotoFromGallery(mealType: MealType) {
        viewModelScope.launch {
            _uiEvents.send(HomeUiEvent.LaunchGallery(mealType))
        }
    }

    fun onRequestCameraPermission(mealType: MealType) {
        viewModelScope.launch {
            _uiEvents.send(HomeUiEvent.RequestCameraPermission(mealType))
        }
    }

    fun onCameraPermissionResult(granted: Boolean, mealType: MealType) {
        if (granted) {
            onAddPhotoFromCamera(mealType)
        }
    }

    fun onCameraResult(success: Boolean, uri: Uri, mealType: MealType) {
        if (success) {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                onImageCaptured(bytes, mealType)
            }
        }
        try {
            context.contentResolver.delete(uri, null, null)
            val path = uri.path
            if (path != null) File(path).delete()
        } catch (_: Exception) {}
    }

    fun onGalleryResult(uri: Uri, mealType: MealType) {
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
        if (bytes != null) {
            onImageCaptured(bytes, mealType)
        }
    }

    fun createImageFileUri(): Uri {
        val file = File(context.cacheDir, "meal_photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    private fun saveImageToFile(bitmap: Bitmap): File? {
        return try {
            val imagesDir = File(context.filesDir, "meal_images").also { it.mkdirs() }
            val file = File(imagesDir, "meal_${System.currentTimeMillis()}.jpg")
            val scaled = scaleBitmap(bitmap, maxDim = 800)
            file.outputStream().use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 75, out)
            }
            if (scaled !== bitmap) scaled.recycle()
            file
        } catch (_: Exception) {
            null
        }
    }

    private fun encodeImageForAi(bitmap: Bitmap): String {
        val scaled = scaleBitmap(bitmap, maxDim = 1024)
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, output)
        if (scaled !== bitmap) scaled.recycle()
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDim && h <= maxDim) return bitmap
        val scale = maxDim.toFloat() / maxOf(w, h)
        return bitmap.scale((w * scale).toInt(), (h * scale).toInt())
    }


    private fun mapToCalendarDays(
        dates: List<Date>,
        selectedDate: Date
    ): List<CalendarDay> {
        val today = Date()
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
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
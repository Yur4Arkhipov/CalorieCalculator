package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor() : MealRepository {

    private val dailyData = mutableMapOf<String, MutableStateFlow<DayData>>()

    init {
        generateMockData()
    }

    private fun generateMockData() {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        for (i in -14..14) {
            calendar.time = today
            calendar.add(Calendar.DAY_OF_YEAR, i)
            val date = calendar.time
            val dateKey = getDateKey(date)

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
                -2 -> listOf( // Позавчера
                    Meal("Творог с медом", 250, "08:15", MealType.BREAKFAST),
                    Meal("Паста с курицей", 520, "14:00", MealType.LUNCH),
                    Meal("Орехи", 150, "17:00", MealType.SNACK),
                    Meal("Рыба на пару", 300, "19:30", MealType.DINNER)
                )
                else -> {
                    // Случайные данные для других дней
                    val mealCount = (2..4).random()
                    (0 until mealCount).map { mealIndex ->
                        val mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK)
                        val type = mealTypes[mealIndex % mealTypes.size]
                        Meal(
                            name = when (type) {
                                MealType.BREAKFAST -> listOf("Овсянка", "Творог", "Яичница", "Мюсли").random()
                                MealType.LUNCH -> listOf("Салат", "Суп", "Курица с рисом", "Паста").random()
                                MealType.DINNER -> listOf("Рыба", "Мясо", "Овощи на гриле", "Запеканка").random()
                                MealType.SNACK -> listOf("Яблоко", "Орехи", "Йогурт", "Банан").random()
                            },
                            calories = when (type) {
                                MealType.BREAKFAST -> (200..400).random()
                                MealType.LUNCH -> (300..600).random()
                                MealType.DINNER -> (250..500).random()
                                MealType.SNACK -> (50..200).random()
                            },
                            time = when (type) {
                                MealType.BREAKFAST -> "0${(7..9).random()}:${(0..59).random().toString().padStart(2, '0')}"
                                MealType.LUNCH -> "1${(2..4).random()}:${(0..59).random().toString().padStart(2, '0')}"
                                MealType.DINNER -> "1${(8..9).random()}:${(0..59).random().toString().padStart(2, '0')}"
                                MealType.SNACK -> "1${(5..7).random()}:${(0..59).random().toString().padStart(2, '0')}"
                            },
                            type = type
                        )
                    }
                }
            }

            val dayData = DayData(
                meals = meals,
                macros = MacroNutrients(
                    proteins = if (i == 0) 85f else (50..120).random().toFloat(),
                    carbs = if (i == 0) 180f else (120..400).random().toFloat(),
                    fats = if (i == 0) 45f else (30..80).random().toFloat()
                )
            )

            dailyData[dateKey] = MutableStateFlow(dayData)
        }
    }

    private fun getDateKey(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    override suspend fun getDayData(date: Date): DayData {
        val dateKey = getDateKey(date)
        return dailyData[dateKey]?.value ?: DayData()
    }

//    override suspend fun updateWater(date: Date, waterGlasses: Int) {
//        val dateKey = getDateKey(date)
//        dailyData[dateKey]?.let { flow ->
//            flow.value = flow.value.copy(water = waterGlasses)
//        }
//    }
//
//    override suspend fun addMeal(date: Date, meal: Meal) {
//        val dateKey = getDateKey(date)
//        dailyData[dateKey]?.let { flow ->
//            val currentMeals = flow.value.meals.toMutableList()
//            currentMeals.add(meal)
//            flow.value = flow.value.copy(meals = currentMeals)
//        }
//    }
//
//    override fun observeDayData(date: Date): Flow<DayData> {
//        val dateKey = getDateKey(date)
//        return dailyData[dateKey]?.asStateFlow() ?: MutableStateFlow(DayData()).asStateFlow()
//    }
}

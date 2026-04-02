package com.jacqulin.calcalc.core.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.jacqulin.calcalc.core.domain.model.Meal

data class MealWithComponents(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val components: List<MealComponentEntity> = emptyList()
)

fun MealWithComponents.toDomain(): Meal {
    val totalCalories = components.sumOf { it.calories }
    val totalProtein = components.sumOf { it.protein }
    val totalFat = components.sumOf { it.fat }
    val totalCarbs = components.sumOf { it.carbs }

    return Meal(
        id = meal.id,
        name = meal.name,
        calories = totalCalories.coerceAtLeast(meal.calories),
        proteins = totalProtein,
        carbs = totalCarbs,
        fats = totalFat,
        time = meal.time,
        type = meal.type,
        imageUri = meal.imageUri,
        isFavorite = meal.isFavorite,
        components = components.map { it.toDomain() }
    )
}

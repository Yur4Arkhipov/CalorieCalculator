package com.jacqulin.calcalc.core.data.local.models

import androidx.room.Embedded
import androidx.room.Relation
import com.jacqulin.calcalc.core.data.local.entities.IngredientEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import com.jacqulin.calcalc.core.data.local.entities.toDomain
import com.jacqulin.calcalc.core.domain.model.Meal

data class MealWithIngredients(
    @Embedded
    val meal: MealEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val ingredients: List<IngredientEntity>
)

fun MealWithIngredients.toDomain(): Meal {
    return Meal(
        id = meal.id,
        name = meal.name,
        calories = meal.calories,
        proteins = meal.protein,
        fats = meal.fat,
        carbs = meal.carbs,
        weight = meal.weight,
        time = meal.time,
        type = meal.type,
        imageUri = meal.imageUri,
        isFavorite = meal.isFavorite,
        ingredient = ingredients.map { it.toDomain() }
    )
}
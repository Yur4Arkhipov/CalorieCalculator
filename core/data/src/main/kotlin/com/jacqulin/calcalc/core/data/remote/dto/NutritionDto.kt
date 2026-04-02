package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.FoodItem
import com.jacqulin.calcalc.core.domain.model.Macros
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.model.NutritionNew
import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val name: String? = null,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

fun NutritionDto.toDomain() = Nutrition(
    name = name ?: "",
    calories,
    protein,
    fat,
    carbs
)

@Serializable
data class NutritionDtoNew(
    val name: String? = null,

    val total_calories: Double? = null,
    val total_protein: Double? = null,
    val total_fat: Double? = null,
    val total_carbs: Double? = null,

    val calories: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null,

    val items: List<ItemDto> = emptyList()
)

@Serializable
data class ItemDto(
    val name: String,
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

fun NutritionDtoNew.toDomain(): NutritionNew {

    val itemsMapped = items.map {
        FoodItem(
            name = it.name,
            weight = it.weight,
            macros = Macros(
                calories = it.calories,
                protein = it.protein,
                fat = it.fat,
                carbs = it.carbs
            )
        )
    }

    val totalMacros = if (total_calories != null) {
        Macros(
            calories = total_calories,
            protein = total_protein ?: 0.0,
            fat = total_fat ?: 0.0,
            carbs = total_carbs ?: 0.0
        )
    } else {
        Macros(
            calories = calories ?: 0.0,
            protein = protein ?: 0.0,
            fat = fat ?: 0.0,
            carbs = carbs ?: 0.0
        )
    }

    return NutritionNew(
        name = name ?: "",
        total = totalMacros,
        items = itemsMapped
    )
}
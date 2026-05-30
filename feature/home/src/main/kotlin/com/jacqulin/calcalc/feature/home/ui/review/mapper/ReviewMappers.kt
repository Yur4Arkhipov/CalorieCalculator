package com.jacqulin.calcalc.feature.home.ui.review.mapper

import com.jacqulin.calcalc.core.domain.model.Ingredient
import com.jacqulin.calcalc.feature.home.ui.review.IngredientUi

fun List<IngredientUi>.toDomainIngredients(): List<Ingredient> =
    mapNotNull { ui ->
        val weight = ui.weight.toIntOrNull() ?: return@mapNotNull null
        Ingredient(
            name = ui.name.trim(),
            weight = weight,
            calories = ui.calories.toIntOrNull() ?: 0,
            protein = ui.protein.toIntOrNull() ?: 0,
            fat = ui.fat.toIntOrNull() ?: 0,
            carb = ui.carb.toIntOrNull() ?: 0
        )
    }
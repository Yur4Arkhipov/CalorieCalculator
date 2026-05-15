package com.jacqulin.calcalc.feature.home.ui.review.mapper

import com.jacqulin.calcalc.core.domain.model.Ingredient
import com.jacqulin.calcalc.feature.home.ui.review.IngredientUi

fun List<IngredientUi>.toDomainIngredients(): List<Ingredient> =
    mapNotNull { ui ->
        val weight = ui.weight.toDoubleOrNull() ?: return@mapNotNull null
        Ingredient(
            name = ui.name.trim(),
            weight = weight,
            calories = ui.calories.toDoubleOrNull() ?: 0.0,
            protein = ui.protein.toDoubleOrNull() ?: 0.0,
            fat = ui.fat.toDoubleOrNull() ?: 0.0,
            carb = ui.carb.toDoubleOrNull() ?: 0.0
        )
    }
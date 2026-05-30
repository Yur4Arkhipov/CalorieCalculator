package com.jacqulin.calcalc.core.designsystem.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.MealType

@Composable
fun MealType.displayName(): String {
    return when (this) {
        MealType.BREAKFAST -> stringResource(R.string.meal_type_breakfast)
        MealType.LUNCH     -> stringResource(R.string.meal_type_lunch)
        MealType.DINNER    -> stringResource(R.string.meal_type_dinner)
        MealType.SNACK     -> stringResource(R.string.meal_type_snack)
    }
}
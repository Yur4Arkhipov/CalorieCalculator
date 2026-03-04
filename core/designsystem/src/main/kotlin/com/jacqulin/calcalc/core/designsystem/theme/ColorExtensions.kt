package com.jacqulin.calcalc.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object AppColors {
    val proteinMain: Color
        @Composable
        @ReadOnlyComposable
        get() = ProteinMain

    val proteinLight: Color
        @Composable
        @ReadOnlyComposable
        get() = ProteinLight

    val carbsMain: Color
        @Composable
        @ReadOnlyComposable
        get() = CarbsMain

    val carbsLight: Color
        @Composable
        @ReadOnlyComposable
        get() = CarbsLight

    val fatMain: Color
        @Composable
        @ReadOnlyComposable
        get() = FatMain

    val fatLight: Color
        @Composable
        @ReadOnlyComposable
        get() = FatLight

    val caloriesDark: Color
        @Composable
        @ReadOnlyComposable
        get() = CaloriesDark

    val caloriesLight: Color
        @Composable
        @ReadOnlyComposable
        get() = CaloriesLight

    val dateSelected: Color
        @Composable
        @ReadOnlyComposable
        get() = DateSelected

    val dateInactive: Color
        @Composable
        @ReadOnlyComposable
        get() = DateInactive

    val dateToday: Color
        @Composable
        @ReadOnlyComposable
        get() = DateToday

    val textTertiary: Color
        @Composable
        @ReadOnlyComposable
        get() = TextTertiary
}


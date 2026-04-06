package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import java.util.Date

interface SaveManualAddMealDBUseCase {
    suspend operator fun invoke(date: Date, meal: Meal)
}
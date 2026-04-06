package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition

interface AnalyzeMealUseCase {
    suspend operator fun invoke(description: String): Nutrition
}
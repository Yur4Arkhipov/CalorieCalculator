package com.jacqulin.calcalc.core.domain.usecase

import java.util.Date

interface SetSelectedDateUseCase  {
    suspend operator fun invoke(date: Date)
}
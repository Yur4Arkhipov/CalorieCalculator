package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.SelectedDateRepository
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import jakarta.inject.Inject
import java.util.Date

class SetSelectedDateUseCaseImpl @Inject constructor(
    private val repository: SelectedDateRepository
) : SetSelectedDateUseCase {
    override suspend fun invoke(date: Date) {
        repository.setDate(date)
    }
}
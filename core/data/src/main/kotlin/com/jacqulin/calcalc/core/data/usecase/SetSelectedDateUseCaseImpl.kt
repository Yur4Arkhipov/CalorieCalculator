package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import jakarta.inject.Inject
import java.util.Date

class SetSelectedDateUseCaseImpl @Inject constructor(
    private val repository: SelectedDateHolder
) : SetSelectedDateUseCase {
    override suspend fun invoke(date: Date) {
        repository.setDate(date)
    }
}
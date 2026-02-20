package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class ObserveSelectedDateUseCaseImpl @Inject constructor(
    private val repository: SelectedDateHolder
) : ObserveSelectedDateUseCase {
    override operator fun invoke(): StateFlow<Date> = repository.selectedDate
}
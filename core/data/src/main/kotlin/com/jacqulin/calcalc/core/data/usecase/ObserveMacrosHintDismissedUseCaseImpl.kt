package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.UiPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.ObserveMacrosHintDismissedUseCase
import kotlinx.coroutines.flow.Flow

class ObserveMacrosHintDismissedUseCaseImpl(
    private val repository: UiPreferencesRepository
) : ObserveMacrosHintDismissedUseCase {
    override fun invoke(): Flow<Boolean> = repository.observeMacrosHintDismissed()
}
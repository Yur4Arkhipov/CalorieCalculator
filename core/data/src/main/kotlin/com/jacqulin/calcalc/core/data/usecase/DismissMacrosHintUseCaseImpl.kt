package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.repository.UiPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.DismissMacrosHintUseCase

class DismissMacrosHintUseCaseImpl(
    private val repository: UiPreferencesRepository
) : DismissMacrosHintUseCase {
    override suspend fun invoke() = repository.dismissMacrosHint()
}
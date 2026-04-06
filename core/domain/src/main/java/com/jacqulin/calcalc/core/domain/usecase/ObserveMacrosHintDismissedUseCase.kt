package com.jacqulin.calcalc.core.domain.usecase

import kotlinx.coroutines.flow.Flow

interface ObserveMacrosHintDismissedUseCase {
    operator fun invoke(): Flow<Boolean>
}
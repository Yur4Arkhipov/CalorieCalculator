package com.jacqulin.calcalc.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UiPreferencesRepository {
    fun observeMacrosHintDismissed(): Flow<Boolean>
    suspend fun dismissMacrosHint()
}
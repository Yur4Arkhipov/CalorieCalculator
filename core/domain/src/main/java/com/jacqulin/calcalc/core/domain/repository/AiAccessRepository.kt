package com.jacqulin.calcalc.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AiAccessRepository {
    fun observeAccessAllowed(): Flow<Boolean>
    suspend fun isAccessAllowed(): Boolean
    suspend fun markLimitReached()
}
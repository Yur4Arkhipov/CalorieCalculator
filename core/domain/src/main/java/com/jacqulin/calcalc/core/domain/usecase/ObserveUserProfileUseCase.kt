package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ObserveUserProfileUseCase {
    operator fun invoke(): Flow<UserProfile>
}
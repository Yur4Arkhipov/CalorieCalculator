package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveUserProfileUseCaseImpl @Inject constructor(
    private val repository: UserPreferencesRepository
) : ObserveUserProfileUseCase {
    override operator fun invoke(): Flow<UserProfile> =
        repository.observeUserProfile()
}
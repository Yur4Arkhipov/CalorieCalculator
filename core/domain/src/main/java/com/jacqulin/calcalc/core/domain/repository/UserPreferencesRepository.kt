package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userData: Flow<UserProfile>
    suspend fun saveUserData(profile: UserProfile)
}
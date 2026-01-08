package com.jacqulin.calcalc.core.data.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        private val USER_AGE = intPreferencesKey("user_age")
        private val USER_HEIGHT = floatPreferencesKey("user_height")
        private val USER_WEIGHT = floatPreferencesKey("user_weight")
        private val USER_ACTIVITY = stringPreferencesKey("user_activity")
    }

    override val userData: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            age = prefs[USER_AGE],
            height = prefs[USER_HEIGHT],
            weight = prefs[USER_WEIGHT],
            activityLevel = prefs[USER_ACTIVITY]?.let { ActivityLevel.valueOf(it) }
        )
    }

    override suspend fun saveUserData(profile: UserProfile) {
        dataStore.edit { prefs ->
            profile.age?.let { prefs[USER_AGE] = it }
            profile.height?.let { prefs[USER_HEIGHT] = it }
            profile.weight?.let { prefs[USER_WEIGHT] = it }
            profile.activityLevel?.let { prefs[USER_ACTIVITY] = it.name }
        }
    }
}
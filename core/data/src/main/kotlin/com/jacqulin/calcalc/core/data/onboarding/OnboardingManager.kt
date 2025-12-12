package com.jacqulin.calcalc.core.data.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val ONBOARDING_SKIPPED = booleanPreferencesKey("onboarding_skipped")
        private val USER_AGE = intPreferencesKey("user_age")
        private val USER_HEIGHT = floatPreferencesKey("user_height")
        private val USER_WEIGHT = floatPreferencesKey("user_weight")
        private val USER_ACTIVITY_LEVEL = stringPreferencesKey("user_activity_level")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(skipped: Boolean = false) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
            prefs[ONBOARDING_SKIPPED] = skipped
        }
    }

    suspend fun saveUserData(
        age: Int?,
        height: Float?,
        weight: Float?,
        activityLevel: Any?
    ) {
        dataStore.edit { prefs ->
            age?.let { prefs[USER_AGE] = it }
            height?.let { prefs[USER_HEIGHT] = it }
            weight?.let { prefs[USER_WEIGHT] = it }
            activityLevel?.let { prefs[USER_ACTIVITY_LEVEL] = it.toString() }
        }
    }
}
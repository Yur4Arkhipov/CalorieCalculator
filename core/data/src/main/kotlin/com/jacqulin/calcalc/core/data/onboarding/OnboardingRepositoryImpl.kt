package com.jacqulin.calcalc.core.data.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.jacqulin.calcalc.core.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val ONBOARDING_SKIPPED = booleanPreferencesKey("onboarding_skipped")
    }

    override val isOnboardingCompleted: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED] ?: false
        }

    override suspend fun setOnboardingCompleted(skipped: Boolean) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
            prefs[ONBOARDING_SKIPPED] = skipped
        }
    }
}
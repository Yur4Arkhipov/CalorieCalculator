package com.jacqulin.calcalc.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.jacqulin.calcalc.core.domain.repository.UiPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UiPreferencesRepository {

    override fun observeMacrosHintDismissed(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[MACROS_HINT_DISMISSED_KEY] ?: false }

    override suspend fun dismissMacrosHint() {
        dataStore.edit { prefs -> prefs[MACROS_HINT_DISMISSED_KEY] = true }
    }

    companion object {
        private val MACROS_HINT_DISMISSED_KEY = booleanPreferencesKey("macros_hint_dismissed")
    }
}
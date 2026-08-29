package com.jacqulin.calcalc.core.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jacqulin.calcalc.core.data.local.models.AiUsageState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class AiUsageDataStore @Inject constructor (
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val LIMIT_REACHED = booleanPreferencesKey("ai_limit_reached")
        val BLOCKED_DATE = stringPreferencesKey("ai_blocked_date")
    }

    val data: Flow<AiUsageState> =
        dataStore.data.map { preferences ->

            AiUsageState(
                limitReached = preferences[LIMIT_REACHED] ?: false,
                blockedDate = preferences[BLOCKED_DATE]
                    ?.let(LocalDate::parse)
            )
        }

    suspend fun setLimitReached(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[LIMIT_REACHED] = true
            preferences[BLOCKED_DATE] = date.toString()
        }
    }

    suspend fun clearLimit() {
        dataStore.edit { preferences ->
            preferences[LIMIT_REACHED] = false
            preferences.remove(BLOCKED_DATE)
        }
    }
}
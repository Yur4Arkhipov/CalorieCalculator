package com.jacqulin.calcalc.core.data.repository

import android.util.Log
import com.jacqulin.calcalc.core.data.local.datastore.AiUsageDataStore
import com.jacqulin.calcalc.core.domain.repository.AiAccessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class AiAccessRepositoryImpl @Inject constructor (
    private val dataStore: AiUsageDataStore
) : AiAccessRepository {
    override fun observeAccessAllowed(): Flow<Boolean> {
        return dataStore.data
            .map { blockedDate ->
                Log.d("AI", "BlockedDate: $blockedDate")
                blockedDate.blockedDate != LocalDate.now()
            }
    }

    override suspend fun isAccessAllowed(): Boolean {

        val state = dataStore.data.first()
        val today = LocalDate.now()

        if (state.blockedDate != today) {
            dataStore.clearLimit()
            return true
        }

        return !state.limitReached
    }

    override suspend fun markLimitReached() {
        dataStore.setLimitReached(
            date = LocalDate.now()
        )
    }
}
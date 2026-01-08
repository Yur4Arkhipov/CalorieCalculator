package com.jacqulin.calcalc.core.domain.repository

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface SelectedDateRepository {
    val selectedDate: StateFlow<Date>
    suspend fun setDate(date: Date)
}
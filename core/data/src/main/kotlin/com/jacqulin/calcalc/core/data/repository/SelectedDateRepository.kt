package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

@Singleton
class SelectedDateHolderImpl @Inject constructor() : SelectedDateHolder {

    private val _selectedDate = MutableStateFlow(Date())
    override val selectedDate: StateFlow<Date> = _selectedDate

    override suspend fun setDate(date: Date) {
        _selectedDate.value = date
    }
}
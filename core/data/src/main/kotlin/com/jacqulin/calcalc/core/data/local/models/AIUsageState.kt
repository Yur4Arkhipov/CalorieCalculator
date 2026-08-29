package com.jacqulin.calcalc.core.data.local.models

import java.time.LocalDate

data class AiUsageState(
    val limitReached: Boolean,
    val blockedDate: LocalDate?
)
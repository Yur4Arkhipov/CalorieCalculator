package com.jacqulin.calcalc.core.domain.model

enum class ActivityLevel {
    SEDENTARY,
    LIGHT,
    MODERATE,
    ACTIVE,
    VERY_ACTIVE
}

data class UserProfile(
    val age: Int?,
    val height: Float?,
    val weight: Float?,
    val activityLevel: ActivityLevel?
)
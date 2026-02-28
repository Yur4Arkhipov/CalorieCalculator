package com.jacqulin.calcalc.core.domain.model

enum class ActivityLevel {
    SEDENTARY,
    LIGHT,
    MODERATE,
    ACTIVE,
    VERY_ACTIVE
}

enum class Gender {
    MALE,
    FEMALE
}

enum class Goal {
    LOSE_WEIGHT,
    MAINTAIN,
    GAIN_WEIGHT
}

data class UserProfile(
    val age: Int?,
    val height: Int?,
    val weight: Int?,
    val gender: Gender?,
    val goal: Goal?,
    val activityLevel: ActivityLevel?
)
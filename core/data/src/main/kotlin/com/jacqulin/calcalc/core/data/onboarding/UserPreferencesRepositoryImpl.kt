package com.jacqulin.calcalc.core.data.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        private val USER_AGE = intPreferencesKey("user_age")
        private val USER_HEIGHT = intPreferencesKey("user_height")
        private val USER_WEIGHT = intPreferencesKey("user_weight")
        private val USER_GENDER = stringPreferencesKey("user_gender")
        private val USER_GOAL = stringPreferencesKey("user_goal")
        private val USER_ACTIVITY = stringPreferencesKey("user_activity")
        private val CALORIES_GOAL = intPreferencesKey("calories_goal")
        private val CARBS_GOAL = intPreferencesKey("carbs_goal")
        private val FAT_GOAL = intPreferencesKey("fat_goal")
        private val PROTEIN_GOAL = intPreferencesKey("protein_goal")
    }

    override val userData: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            age = prefs[USER_AGE] ?: 20,
            height = prefs[USER_HEIGHT] ?: 165,
            weight = prefs[USER_WEIGHT] ?: 65,
            gender = prefs[USER_GENDER]?.let { Gender.valueOf(it) } ?: Gender.MALE,
            goal = prefs[USER_GOAL]?.let { Goal.valueOf(it) } ?: Goal.MAINTAIN,
            activityLevel = prefs[USER_ACTIVITY]?.let { ActivityLevel.valueOf(it) } ?: ActivityLevel.MODERATE,
            caloriesGoal = prefs[CALORIES_GOAL] ?: 2000,
            carbsGoal = prefs[CARBS_GOAL] ?: 250,
            fatGoal = prefs[FAT_GOAL] ?: 70,
            proteinGoal = prefs[PROTEIN_GOAL] ?: 150
        )
    }

    override suspend fun saveUserData(profile: UserProfile) {
        dataStore.edit { prefs ->
            prefs[USER_AGE] = profile.age
            prefs[USER_HEIGHT] = profile.height
            prefs[USER_WEIGHT] = profile.weight
            prefs[USER_GENDER] = profile.gender.name
            prefs[USER_ACTIVITY] = profile.activityLevel.name
            prefs[USER_GOAL] = profile.goal.name
            prefs[CALORIES_GOAL] = profile.caloriesGoal
            prefs[CARBS_GOAL] = profile.carbsGoal
            prefs[FAT_GOAL] = profile.fatGoal
            prefs[PROTEIN_GOAL] = profile.proteinGoal
        }
    }

    override fun observeUserProfile(): Flow<UserProfile> {
        return userData
    }
}
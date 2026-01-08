package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.onboarding.OnboardingRepositoryImpl
import com.jacqulin.calcalc.core.data.onboarding.UserPreferencesRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.MealRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.SelectedDateRepositoryImpl
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.OnboardingRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateRepository
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindMealRepository(
        impl: MealRepositoryImpl
    ): MealRepository

    @Binds
    fun bindSelectedDateRepository(
        impl: SelectedDateRepositoryImpl
    ): SelectedDateRepository

    @Binds
    fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl
    ): OnboardingRepository
}
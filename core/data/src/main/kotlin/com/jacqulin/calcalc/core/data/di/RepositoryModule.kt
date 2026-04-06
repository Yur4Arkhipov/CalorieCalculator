package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.onboarding.OnboardingRepositoryImpl
import com.jacqulin.calcalc.core.data.onboarding.UserPreferencesRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.AiRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.ImageRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.MealRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.SelectedDateHolderImpl
import com.jacqulin.calcalc.core.data.repository.UiPreferencesRepositoryImpl
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.OnboardingRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import com.jacqulin.calcalc.core.domain.repository.UiPreferencesRepository
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
        impl: SelectedDateHolderImpl
    ): SelectedDateHolder

    @Binds
    fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl
    ): OnboardingRepository

    @Binds
    fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository

    @Binds
    fun bindImageRepository(
        impl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    fun bindUiPreferencesRepository(
        impl: UiPreferencesRepositoryImpl
    ): UiPreferencesRepository
}
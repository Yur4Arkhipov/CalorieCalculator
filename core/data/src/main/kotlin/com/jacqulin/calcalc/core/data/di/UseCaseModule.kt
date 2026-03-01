package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.usecase.GenerateWeekDaysUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.GetDayDataUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveUserProfileUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.SaveManualAddMealDBUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.SetSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetDayDataUseCase(mealRepository: MealRepository) : GetDayDataUseCase {
        return GetDayDataUseCaseImpl(mealRepository)
    }

    @Provides
    fun provideGenerateWeekDaysUseCase(mealRepository: MealRepository): GenerateWeekDaysUseCase {
        return GenerateWeekDaysUseCaseImpl(mealRepository)
    }

    @Provides
    fun provideObserveDayDataUseCase(selectedDateRepository: SelectedDateHolder) : ObserveSelectedDateUseCase {
        return ObserveSelectedDateUseCaseImpl(selectedDateRepository)
    }

    @Provides
    fun setSelectedDateUseCase(selectedDateRepository: SelectedDateHolder) : SetSelectedDateUseCase {
        return SetSelectedDateUseCaseImpl(selectedDateRepository)
    }

    @Provides
    fun provideSaveManualAddMealDBUseCase(mealRepository: MealRepository): SaveManualAddMealDBUseCase {
        return SaveManualAddMealDBUseCaseImpl(mealRepository)
    }

    @Provides
    fun observeUserProfileUseCase(userPreferencesRepository: UserPreferencesRepository): ObserveUserProfileUseCase {
        return ObserveUserProfileUseCaseImpl(userPreferencesRepository)
    }
}
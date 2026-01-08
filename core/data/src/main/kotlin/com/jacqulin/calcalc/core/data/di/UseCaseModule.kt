package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.usecase.GenerateWeekDaysUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.GetDayDataUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.SetSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateRepository
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
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
    fun provideObserveDayDataUseCase(selectedDateRepository: SelectedDateRepository) : ObserveSelectedDateUseCase {
        return ObserveSelectedDateUseCaseImpl(selectedDateRepository)
    }

    @Provides
    fun setSelectedDateUseCase(selectedDateRepository: SelectedDateRepository) : SetSelectedDateUseCase {
        return SetSelectedDateUseCaseImpl(selectedDateRepository)
    }
}
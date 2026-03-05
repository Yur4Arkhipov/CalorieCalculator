package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.usecase.AnalyzeMealFromImageUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.AnalyzeMealUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.DismissMacrosHintUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.GenerateWeekDaysUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.GetDayDataUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveMacrosHintDismissedUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.ObserveUserProfileUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.SaveManualAddMealDBUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.SetSelectedDateUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.DeleteMealUseCaseImpl
import com.jacqulin.calcalc.core.data.usecase.UpdateMealUseCaseImpl
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateHolder
import com.jacqulin.calcalc.core.domain.repository.UiPreferencesRepository
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.DismissMacrosHintUseCase
import com.jacqulin.calcalc.core.domain.usecase.GenerateWeekDaysUseCase
import com.jacqulin.calcalc.core.domain.usecase.GetDayDataUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveMacrosHintDismissedUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveUserProfileUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.domain.usecase.SetSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.UpdateMealUseCase
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

    @Provides
    fun provideAnalyzeMealUseCase(aiRepository: AiRepository): AnalyzeMealUseCase {
        return AnalyzeMealUseCaseImpl(aiRepository)
    }

    @Provides
    fun provideAnalyzeMealFromImageUseCase(aiRepository: AiRepository): AnalyzeMealFromImageUseCase {
        return AnalyzeMealFromImageUseCaseImpl(aiRepository)
    }

    @Provides
    fun provideUpdateMealUseCase(mealRepository: MealRepository): UpdateMealUseCase {
        return UpdateMealUseCaseImpl(mealRepository)
    }

    @Provides
    fun provideDeleteMealUseCase(mealRepository: MealRepository): DeleteMealUseCase {
        return DeleteMealUseCaseImpl(mealRepository)
    }

    @Provides
    fun provideObserveMacrosHintDismissedUseCase(
        uiPreferencesRepository: UiPreferencesRepository
    ): ObserveMacrosHintDismissedUseCase {
        return ObserveMacrosHintDismissedUseCaseImpl(uiPreferencesRepository)
    }

    @Provides
    fun provideDismissMacrosHintUseCase(
        uiPreferencesRepository: UiPreferencesRepository
    ): DismissMacrosHintUseCase {
        return DismissMacrosHintUseCaseImpl(uiPreferencesRepository)
    }
}
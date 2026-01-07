package com.jacqulin.calcalc.core.data.di

import com.jacqulin.calcalc.core.data.repository.MealRepositoryImpl
import com.jacqulin.calcalc.core.data.repository.SelectedDateRepositoryImpl
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.SelectedDateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMealRepository(): MealRepository {
        return MealRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSelectedDateRepository(): SelectedDateRepository {
        return SelectedDateRepositoryImpl()
    }
}
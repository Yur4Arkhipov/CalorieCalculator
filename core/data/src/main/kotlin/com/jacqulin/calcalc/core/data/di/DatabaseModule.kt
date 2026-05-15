package com.jacqulin.calcalc.core.data.di

import android.content.Context
import androidx.room.Room
import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.database.MealDatabase
import com.jacqulin.calcalc.core.data.local.database.MealDatabase.Companion.provideMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MealDatabase =
        Room.databaseBuilder(
            context,
            MealDatabase::class.java,
            "meal_db"
        )
            .addMigrations(*provideMigrations())
            .build()

    @Provides
    fun provideMealDao(db: MealDatabase): MealDao = db.mealDao()
}
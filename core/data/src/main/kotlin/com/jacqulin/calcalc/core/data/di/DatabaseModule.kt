package com.jacqulin.calcalc.core.data.di

import android.content.Context
import androidx.room.Room
//import com.jacqulin.calcalc.core.data.BuildConfig
import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.database.MealDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): MealDatabase {
        val builder = Room.databaseBuilder(
            context,
            MealDatabase::class.java,
            "meal_db"
        )
            .addMigrations(
                MealDatabase.MIGRATION_1_2,
                MealDatabase.MIGRATION_2_3,
                MealDatabase.MIGRATION_3_4
            )

        // Use fallback to destructive migration in debug builds
//        if (BuildConfig.DEBUG) {
//            builder.fallbackToDestructiveMigration()
//        }

        return builder.build()
    }

    @Provides
    fun provideMealDao(db: MealDatabase): MealDao = db.mealDao()
}
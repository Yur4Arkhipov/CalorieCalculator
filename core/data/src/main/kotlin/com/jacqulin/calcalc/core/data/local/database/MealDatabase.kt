package com.jacqulin.calcalc.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.entities.MealEntity

@Database(
    entities = [MealEntity::class],
    version = 1
)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}
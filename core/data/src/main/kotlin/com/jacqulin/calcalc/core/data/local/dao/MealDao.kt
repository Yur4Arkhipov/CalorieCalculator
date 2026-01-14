package com.jacqulin.calcalc.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jacqulin.calcalc.core.data.local.entities.MealEntity

@Dao
interface MealDao {

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time ASC")
    suspend fun getMealsForDate(date: String): List<MealEntity>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMeal(meal: MealEntity)
//
//    @Delete
//    suspend fun deleteMeal(meal: MealEntity)
}
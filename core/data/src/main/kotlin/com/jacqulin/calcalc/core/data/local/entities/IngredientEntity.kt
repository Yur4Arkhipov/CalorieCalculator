package com.jacqulin.calcalc.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealId")]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mealId: Int,
    val name: String,
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val carb: Double,
    val fat: Double
)
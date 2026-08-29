package com.jacqulin.calcalc.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jacqulin.calcalc.core.domain.model.Ingredient

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
    val weight: Int,
    val calories: Int,
    val protein: Int,
    val carb: Int,
    val fat: Int
)

fun IngredientEntity.toDomain(): Ingredient =
    Ingredient(
        name = name,
        weight = weight,
        calories = calories,
        protein = protein,
        carb = carb,
        fat = fat,
    )
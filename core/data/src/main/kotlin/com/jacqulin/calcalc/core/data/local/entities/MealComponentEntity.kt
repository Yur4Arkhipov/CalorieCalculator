package com.jacqulin.calcalc.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jacqulin.calcalc.core.domain.model.MealComponent

@Entity(
    tableName = "meal_component",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"])
    ]
)
data class MealComponentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: Int,
    val name: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
)

fun MealComponentEntity.toDomain(): MealComponent =
    MealComponent(
        id = id,
        name = name,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs
    )
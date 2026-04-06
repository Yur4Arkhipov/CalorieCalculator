package com.jacqulin.calcalc.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType

@Entity(tableName = "meal")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val time: String,
    val type: MealType,
    val date: String,
    val imageUri: String? = null,
    val isFavorite: Boolean = false
)

fun MealEntity.toDomain(): Meal =
    Meal(
        id = id,
        name = name,
        calories = calories,
        proteins = protein,
        carbs = carbs,
        fats = fat,
        time = time,
        type = type,
        imageUri = imageUri,
        isFavorite = isFavorite
    )
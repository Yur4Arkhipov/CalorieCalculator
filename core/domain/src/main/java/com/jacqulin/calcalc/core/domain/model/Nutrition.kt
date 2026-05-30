package com.jacqulin.calcalc.core.domain.model

data class Nutrition(
    val name: String = "",
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val ingredient: List<Ingredient> = emptyList()
)

fun Nutrition.toJsonString(): String {
    return buildString {

        append(
            """
            {
              "name": "$name",
              "weight": $weight,
              "calories": $calories,
              "protein": $protein,
              "fat": $fat,
              "carb": $carb,
              "ingredient": [
            """.trimIndent()
        )

        ingredient.forEachIndexed { index, ingredient ->

            append(
                """
                {
                  "name": "${ingredient.name}",
                  "weight": ${ingredient.weight},
                  "calories": ${ingredient.calories},
                  "protein": ${ingredient.protein},
                  "fat": ${ingredient.fat},
                  "carb": ${ingredient.carb}
                }
                """.trimIndent()
            )

            if (index.toDouble() != ingredient.component6()) {
                append(",")
            }
        }

        append("]}")
    }
}
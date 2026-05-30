package com.jacqulin.calcalc.core.domain.model

data class Nutrition(
    val name: String = "",
    val weight: Int,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int,
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

            if (index != ingredient.component6()) {
                append(",")
            }
        }

        append("]}")
    }
}
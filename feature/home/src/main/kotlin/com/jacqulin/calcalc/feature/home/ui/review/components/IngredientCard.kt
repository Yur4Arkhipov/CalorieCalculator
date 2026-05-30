package com.jacqulin.calcalc.feature.home.ui.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.MacroBadge
import com.jacqulin.calcalc.core.designsystem.theme.AppColors
import com.jacqulin.calcalc.feature.home.ui.review.IngredientUi

@Composable
fun IngredientCard(
    ingredient: IngredientUi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${ingredient.weight} г",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MacroBadge(
                        label = stringResource(R.string.macro_protein_short),
//                        value = ingredient.protein.toInt(),
                        value = ingredient.protein
                            .toDoubleOrNull()
                            ?.toInt()
                            ?: 0,
                        color = AppColors.proteinMain
                    )
                    MacroBadge(
                        label = stringResource(R.string.macro_fat_short),
//                        value = ingredient.fat.toInt(),
                        value = ingredient.fat
                            .toDoubleOrNull()
                            ?.toInt()
                            ?: 0,
                        color = AppColors.fatMain
                    )
                    MacroBadge(
                        label = stringResource(R.string.macro_carbs_short),
//                        value = ingredient.carb.toInt(),
                        value = ingredient.carb
                            .toDoubleOrNull()
                            ?.toInt()
                            ?: 0,
                        color = AppColors.carbsMain
                    )
                }
                Row {
                    Text(
                        text = "${ingredient.calories} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
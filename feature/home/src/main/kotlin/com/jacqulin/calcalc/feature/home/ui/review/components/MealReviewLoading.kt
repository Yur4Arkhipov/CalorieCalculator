package com.jacqulin.calcalc.feature.home.ui.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MealReviewLoading(
    modifier: Modifier = Modifier,
    color: Color
) {
    Column(modifier = modifier) {
        Text("Анализирую...")
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.3f))
        )
        Spacer(Modifier.height(12.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.2f))
            )
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.25f))
        )
    }
}
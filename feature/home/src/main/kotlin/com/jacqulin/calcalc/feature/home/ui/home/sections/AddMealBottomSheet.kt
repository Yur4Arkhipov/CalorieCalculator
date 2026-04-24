package com.jacqulin.calcalc.feature.home.ui.home.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealBottomSheet(
    onManual: () -> Unit,
    onAiDescription: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(R.string.home_add_meal),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AddMealOptionCard(
                    icon = painterResource(R.drawable.ic_edit),
                    text = stringResource(R.string.home_add_meal_manual_title),
                    subtitle = stringResource(R.string.home_add_meal_manual_subtitle),
                    onClick = onManual
                )

                AddMealOptionCard(
                    icon = painterResource(R.drawable.ic_auto_awesome),
                    text = stringResource(R.string.home_add_meal_ai_title),
                    subtitle = stringResource(R.string.home_add_meal_ai_subtitle),
                    onClick = onAiDescription
                )

                AddMealOptionCard(
                    icon = painterResource(R.drawable.ic_camera_alt),
                    text = stringResource(R.string.home_add_meal_camera_title),
                    subtitle = stringResource(R.string.home_add_meal_camera_subtitle),
                    onClick = onCamera
                )

                AddMealOptionCard(
                    icon = painterResource(R.drawable.ic_photo),
                    text = stringResource(R.string.home_add_meal_gallery_title),
                    subtitle = stringResource(R.string.home_add_meal_gallery_subtitle),
                    onClick = onGallery
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AddMealOptionCard(
    icon: Painter,
    text: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
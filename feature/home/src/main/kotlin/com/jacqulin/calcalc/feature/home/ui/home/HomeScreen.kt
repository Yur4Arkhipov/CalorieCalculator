package com.jacqulin.calcalc.feature.home.ui.home

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.designsystem.component.AddMealFloatingActionButton
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.PendingMeal
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToMacroDetail: () -> Unit = {},
    onNavigateToAiMealDescription: () -> Unit = {},
    onNavigateToManualAddMeal: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddFoodSheet by remember { mutableStateOf(false) }
    var showMealTypePicker by remember { mutableStateOf<AddPhotoSource?>(null) }
    val lazyListState = rememberLazyListState()
    val isFabVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 ||
                !lazyListState.isScrollInProgress && lazyListState.firstVisibleItemScrollOffset < 100
        }
    }

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraMealType by remember { mutableStateOf<MealType?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        val mealType = pendingCameraMealType
        if (success && uri != null && mealType != null) {
            viewModel.onCameraResult(success = true, uri = uri, mealType = mealType)
        }
        pendingCameraUri = null
        pendingCameraMealType = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val mealType = pendingCameraMealType ?: MealType.BREAKFAST
        viewModel.onCameraPermissionResult(granted, mealType)
        if (!granted) {
            pendingCameraMealType = null
        }
    }

    var pendingGalleryMealType by remember { mutableStateOf<MealType?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val mealType = pendingGalleryMealType ?: MealType.BREAKFAST
        if (uri != null) {
            viewModel.onGalleryResult(uri, mealType)
        }
        pendingGalleryMealType = null
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeUiEvent.LaunchCamera -> {
                    pendingCameraUri = event.uri
                    pendingCameraMealType = event.mealType
                    cameraLauncher.launch(event.uri)
                }
                is HomeUiEvent.RequestCameraPermission -> {
                    pendingCameraMealType = event.mealType
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                is HomeUiEvent.LaunchGallery -> {
                    pendingGalleryMealType = event.mealType
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }

    showMealTypePicker?.let { source ->
        MealTypePickerDialog(
            onSelect = { mealType ->
                showMealTypePicker = null
                when (source) {
                    AddPhotoSource.CAMERA -> viewModel.onRequestCameraPermission(mealType)
                    AddPhotoSource.GALLERY -> viewModel.onAddPhotoFromGallery(mealType)
                }
            },
            onDismiss = { showMealTypePicker = null }
        )
    }


    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 60.dp
                )
            ) {
                item {
                    CalendarSection(
                        currentWeekIndex = uiState.currentWeekIndex,
                        weeks = uiState.weeks,
                        onDateSelected = viewModel::onDateSelected,
                        onWeekChanged = viewModel::onWeekChanged
                    )
                }
                item {
                    CaloriesSection(uiState = uiState)
                }
                item {
                    TodayMealsSection(
                        meals = uiState.mealsToday,
                        pendingMeals = uiState.pendingMeals,
                        onDismissError = viewModel::dismissPendingError,
                        onDetailClick = onNavigateToMacroDetail
                    )
                }
            }

            AnimatedVisibility(
                visible = isFabVisible,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp)
                    .padding(bottom = 74.dp)
                    .navigationBarsPadding(),
                enter = slideInVertically(
                    animationSpec = tween(150, easing = LinearEasing),
                    initialOffsetY = { it + 200 }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(150, easing = LinearEasing),
                    targetOffsetY = { it + 200 }
                )
            ) {
                AddMealFloatingActionButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Add meal",
                    onClick = { showAddFoodSheet = true },
                )
            }

            if (showAddFoodSheet) {
                AddFoodBottomSheet(
                    onManual = {
                        showAddFoodSheet = false
                        onNavigateToManualAddMeal()
                    },
                    onAiDescription = {
                        showAddFoodSheet = false
                        onNavigateToAiMealDescription()
                    },
                    onCamera = {
                        showAddFoodSheet = false
                        showMealTypePicker = AddPhotoSource.CAMERA
                    },
                    onGallery = {
                        showAddFoodSheet = false
                        showMealTypePicker = AddPhotoSource.GALLERY
                    },
                    onDismiss = { showAddFoodSheet = false }
                )
            }
        }
    }
}

@Composable
fun MealTypePickerDialog(
    onSelect: (MealType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите приём пищи") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MealType.entries.forEach { type ->
                    TextButton(
                        onClick = { onSelect(type) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(type.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodBottomSheet(
    onManual: () -> Unit,
    onAiDescription: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Добавить еду",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            AddFoodOption(icon = Icons.Default.Edit, text = "Ввести вручную", onClick = onManual)
            AddFoodOption(icon = Icons.Default.Edit, text = "Описать для ИИ", onClick = onAiDescription)
            AddFoodOption(icon = Icons.Default.CameraAlt, text = "Сделать фото", onClick = onCamera)
            AddFoodOption(icon = Icons.Default.Photo, text = "Выбрать из галереи", onClick = onGallery)
        }
    }
}

@Composable
private fun AddFoodOption(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun TodayMealsSection(
    meals: List<Meal>,
    pendingMeals: List<PendingMeal> = emptyList(),
    onDismissError: (String) -> Unit = {},
    onDetailClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Приемы пищи",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(
                onClick = onDetailClick
            ) {
                Text("Подрбнее")
            }
        }

        val isEmpty = meals.isEmpty() && pendingMeals.isEmpty()
        if (isEmpty) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Еще нет записей о еде",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pendingMeals.forEach { pending ->
                    PendingMealCard(
                        pending = pending,
                        onDismissError = { onDismissError(pending.id) }
                    )
                }
                meals.forEach { meal -> MealCard(meal = meal) }
            }
        }
    }
}

@Composable
private fun PendingMealCard(
    pending: PendingMeal,
    onDismissError: () -> Unit
) {
    val isError = pending.error != null
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (pending.imageUri != null) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = File(pending.imageUri!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        alpha = if (pending.isLoading) 0.4f else 1f
                    )
                    if (pending.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (pending.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isError) "Ошибка анализа фото" else "Анализируем фото...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = pending.type.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            if (isError) {
                TextButton(onClick = onDismissError) {
                    Text("Убрать", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun MealCard(meal: Meal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (meal.imageUri != null) {
                AsyncImage(
                    model = File(meal.imageUri!!),
                    contentDescription = meal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    MealTypeChip(meal.type)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${meal.calories} ккал",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = meal.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MealTypeChip(type: MealType) {
    val color = mealTypeColor(type)
    Box(
        modifier = Modifier
            .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun mealTypeColor(mealType: MealType): Color {
    return when (mealType) {
        MealType.BREAKFAST -> MaterialTheme.colorScheme.tertiary
        MealType.LUNCH -> MaterialTheme.colorScheme.primary
        MealType.DINNER -> MaterialTheme.colorScheme.secondary
        MealType.SNACK -> MaterialTheme.colorScheme.error
    }
}

private enum class AddPhotoSource { CAMERA, GALLERY }
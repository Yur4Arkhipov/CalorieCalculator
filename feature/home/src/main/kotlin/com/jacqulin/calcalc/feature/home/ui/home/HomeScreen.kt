package com.jacqulin.calcalc.feature.home.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.FloatingActionButton
import com.jacqulin.calcalc.core.designsystem.component.MealCard
import com.jacqulin.calcalc.core.designsystem.theme.SelectedToRemoveLightRed
import com.jacqulin.calcalc.core.designsystem.theme.White
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.TempImage
import com.jacqulin.calcalc.feature.home.ui.home.sections.AddMealBottomSheet
import com.jacqulin.calcalc.feature.home.ui.home.sections.CalendarSection
import com.jacqulin.calcalc.feature.home.ui.home.sections.CaloriesSection
import com.jacqulin.calcalc.feature.home.ui.home.sections.EditMealBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToMacroDetail: () -> Unit = {},
    onNavigateToAiMealDescription: () -> Unit = {},
    onNavigateToManualAddMeal: () -> Unit = {},
    onNavigateToAddFavoriteMeal: () -> Unit = {},
    onNavigateToMealReview: (TempImage) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddFoodSheet by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var cameraSession by remember { mutableStateOf<TempImage?>(null) }
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermanentDeniedDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelection()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val temp = cameraSession
        if (temp != null) {
            viewModel.onCameraResult(success = success, temp = temp)
        }
        cameraSession = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onCameraPermissionResult(granted = true)
        } else {
            val shouldShow = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false

            if (shouldShow) {
                showRationaleDialog = true
            } else {
                showPermanentDeniedDialog = true
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onGalleryResult(uri)
        }
    }

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index == layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
        }
    }
    var fabHeight by remember { mutableFloatStateOf(0f) }
    var fabOffsetY by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (isAtBottom) {
                    fabOffsetY = (fabOffsetY - delta)
                        .coerceIn(0f, fabHeight)
                }

                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                if (fabOffsetY > 0f) {
                    animate(
                        initialValue = fabOffsetY,
                        targetValue = 0f
                    ) { value, _ ->
                        fabOffsetY = value
                    }
                }

                return Velocity.Zero
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeUiEvent.LaunchCamera -> {
                    cameraSession = event.tempImage
                    cameraLauncher.launch(event.tempImage.uri)
                }
                is HomeUiEvent.RequestCameraPermission -> {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                is HomeUiEvent.LaunchGallery -> {
                    galleryLauncher.launch("image/*")
                }
                is HomeUiEvent.NavigateToMealReview -> {
                    onNavigateToMealReview(event.tempImage)
                }
            }
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(stringResource(R.string.home_alert_dialog_access_required)) },
            text = { Text(stringResource(R.string.home_alert_dialog_description)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text(stringResource(R.string.home_alert_dialog_try_again)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                }) { Text(stringResource(R.string.home_dialog_cancel)) }
            },
            containerColor = White
        )
    }

    if (showPermanentDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermanentDeniedDialog = false },
            title = { Text(stringResource(R.string.home_alert_dialog_access_block)) },
            text = { Text(stringResource(R.string.home_alert_dialog_access_description)) },
            confirmButton = {
                TextButton(onClick = {
                    showPermanentDeniedDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) { Text(stringResource(R.string.home_alert_dialog_open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermanentDeniedDialog = false
                }) { Text(stringResource(R.string.home_alert_dialog_close)) }
            },
            containerColor = White
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(
//                                Color(0xFFFCFCFD),                    // верх
//                                AppPrimaryContainer.copy(alpha = 0.2f) // низ, очень прозрачный
//                            )
//                        )
//                    )
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 12.dp
                    )
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .nestedScroll(nestedScrollConnection),
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
                            selectedMealIds = uiState.selectedMealIds,
                            onDetailClick = onNavigateToMacroDetail,
                            onMealClick = viewModel::onMealClick,
                            onMealLongClick = viewModel::onMealLongClick,
                            onDeleteClick = viewModel::deleteSelected
                        )
                    }
                }

                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 60.dp, end = 12.dp)
                        .navigationBarsPadding()
                        .onGloballyPositioned {
                            fabHeight = it.size.height.toFloat()
                        }
                        .graphicsLayer {
                            translationY = fabOffsetY
                        }
                ) {
                    FloatingActionButton(
                        icon = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.home_add_meal),
                        onClick = { showAddFoodSheet = true },
                    )
                }

                if (showAddFoodSheet) {
                    AddMealBottomSheet(
                        onFavorite = {
                            showAddFoodSheet = false
                            onNavigateToAddFavoriteMeal()
                        },
                        onManual = {
                            showAddFoodSheet = false
                            onNavigateToManualAddMeal()
                        },
                        onAiDescription = {
                            showAddFoodSheet = false
                            onNavigateToAiMealDescription()
                        },
                        onCamera = {
                            viewModel.onRequestCameraPermission()
                            showAddFoodSheet = false
                        },
                        onGallery = {
                            viewModel.onAddPhotoFromGallery()
                            showAddFoodSheet = false
                        },
                        onDismiss = { showAddFoodSheet = false }
                    )
                }

                if (uiState.isEditingSheetOpen && uiState.editingMeal != null) {
                    EditMealBottomSheet(
                        meal = uiState.editingMeal!!,
                        sheetState = editSheetState,
                        onDismiss = viewModel::onDismissEditMeal,
                        onSave = viewModel::onUpdateMeal,
                        onDelete = viewModel::onDeleteMeal
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayMealsSection(
    meals: List<Meal>,
    selectedMealIds: Set<Int>,
    onDetailClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMealClick: (Meal) -> Unit = {},
    onMealLongClick: (Meal) -> Unit = {}
) {
    val isSelectionMode = selectedMealIds.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (isSelectionMode) 16.dp else 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =  if (isSelectionMode) {
                    "Выделено: ${selectedMealIds.size}"
                } else {
                    stringResource(R.string.home_meals)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .widthIn(min = 80.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SelectedToRemoveLightRed)
                        .clickable(onClick = onDeleteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = "Удалить",
                            tint = White,
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }
            } else {
                TextButton(
                    onClick = onDetailClick
                ) {
                    Text(stringResource(R.string.home_more_details))
                }
            }
        }

        if (meals.isEmpty()) {
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
                        text = stringResource(R.string.home_no_meals_add),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meals.forEach { meal ->
                    MealCard(
                        meal = meal,
                        isSelectionEnabled = isSelectionMode,
                        isSelected = meal.id in selectedMealIds,
                        onClick = { onMealClick(meal) },
                        onLongClick = {
                            onMealLongClick(meal)
                            Log.d("Selection", "UI long click ${meal.id}")
                            Log.d("Selection", "UI long click ${meal.id}")
                        }
                    )
                }
            }
        }
    }
}
package com.jacqulin.calcalc.feature.home.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.AddMealFloatingActionButton
import com.jacqulin.calcalc.core.designsystem.component.MealCard
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.PendingMeal
import com.jacqulin.calcalc.feature.home.ui.home.sections.AddMealBottomSheet
import com.jacqulin.calcalc.feature.home.ui.home.sections.CalendarSection
import com.jacqulin.calcalc.feature.home.ui.home.sections.CaloriesSection
import com.jacqulin.calcalc.feature.home.ui.home.sections.EditMealBottomSheet
import com.jacqulin.calcalc.feature.home.ui.home.sections.MealTypePickerDialog

private enum class AddPhotoSource { CAMERA, GALLERY }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraMealType by remember { mutableStateOf<MealType?>(null) }
    var pendingGalleryMealType by remember { mutableStateOf<MealType?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val mealType = pendingGalleryMealType ?: MealType.BREAKFAST
        if (uri != null) {
            viewModel.onGalleryResult(uri, mealType)
        }
        pendingGalleryMealType = null
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
                is HomeUiEvent.ShowNotFoodError -> {
                    snackbarHostState.showSnackbar("На фото не обнаружена еда")
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 44.dp
                    )
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { _ ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                            pendingMeals = uiState.pendingMeals,
                            onDismissError = viewModel::dismissPendingError,
                            onDetailClick = onNavigateToMacroDetail,
                            onMealClick = viewModel::onEditMeal
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
                    AddMealFloatingActionButton(
                        icon = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.home_add_meal),
                        onClick = { showAddFoodSheet = true },
                    )
                }

                if (showAddFoodSheet) {
                    AddMealBottomSheet(
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
    pendingMeals: List<PendingMeal> = emptyList(),
    onDismissError: (String) -> Unit = {},
    onDetailClick: () -> Unit = {},
    onMealClick: (Meal) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_meals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(
                onClick = onDetailClick
            ) {
                Text(stringResource(R.string.home_more_details))
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
                        text = stringResource(R.string.home_no_meals_add),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pendingMeals.forEach { pending ->
                    PendingMealCard(
                        pending = pending,
                        onDismissError = { onDismissError(pending.id) }
                    )
                }
                meals.forEach { meal ->
                    MealCard(
                        meal = meal,
                        onClick = { onMealClick(meal) }
                    )
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
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
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (pending.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isError)
                        stringResource(R.string.home_analyze_error)
                    else
                        stringResource(R.string.home_analyzing),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
                    Text(
                        text = stringResource(R.string.home_remove),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
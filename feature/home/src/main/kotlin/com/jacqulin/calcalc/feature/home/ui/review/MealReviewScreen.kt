package com.jacqulin.calcalc.feature.home.ui.review

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jacqulin.calcalc.core.designsystem.theme.AppOnPrimaryContainer
import com.jacqulin.calcalc.core.designsystem.theme.AppPrimaryContainer
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MealReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: MealReviewScreenViewModel = hiltViewModel(),
    filePath: String,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val file = remember(filePath) { File(filePath) }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )

    LaunchedEffect(uiState.isError) {
        uiState.isError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            SheetContent(uiState, viewModel)
        },
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
        sheetPeekHeight = 120.dp, // 🔥 насколько видно изначально
        containerColor = Color.Transparent,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // 📸 КАРТИНКА
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                AsyncImage(
                    model = file,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

//                TopOverlay(onBackClick)
            }
        }
    }
}

//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(
//                hostState = snackbarHostState,
//                modifier = Modifier.padding(
//                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 44.dp
//                )
//            ) { data ->
//                Snackbar(
//                    snackbarData = data,
//                    containerColor = MaterialTheme.colorScheme.errorContainer,
//                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
//                    shape = RoundedCornerShape(12.dp)
//                )
//            }
//        },
//    ) { _ ->
//        Column(modifier = modifier.fillMaxSize()) {
//            Box(modifier = Modifier.fillMaxWidth()) {
//                AsyncImage(
//                    model = file,
//                    contentDescription = uiState.name,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(240.dp)
//                )
//
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .fillMaxWidth()
//                        .height(20.dp)
//                        .background(
//                            Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.Transparent,                    // сверху — прозрачно (видно фото)
//                                    Color(0xFFFCFCFD).copy(alpha = 0.9f), // снизу — цвет фона
//                                    Color(0xFFFCFCFD).copy(alpha = 0.95f)
//                                ),
//                                startY = 0f,
//                                endY = 60f
//                            )
//                        )
//                )
//            }
//
//            Surface(
//                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//                color = Color.Transparent,
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            Brush.verticalGradient(
//                                colors = listOf(
//                                    Color(0xFFFCFCFD).copy(alpha = 0.95f),
//                                    AppPrimaryContainer.copy(alpha = 0.6f)
//                                )
//                            )
//                        )
//                )
//
//                if (uiState.isLoading) {
//                    Column(
//                        modifier = Modifier
//                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
//                            .fillMaxWidth()
//                    ) {
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth(0.6f)
//                                .height(28.dp)
//                                .clip(RoundedCornerShape(8.dp))
//                                .background(Color.Gray.copy(alpha = 0.3f))
//                        )
//
//                        Spacer(Modifier.height(12.dp))
//
//                        repeat(3) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(16.dp)
//                                    .padding(vertical = 4.dp)
//                                    .clip(RoundedCornerShape(6.dp))
//                                    .background(Color.Gray.copy(alpha = 0.2f))
//                            )
//                        }
//
//                        Spacer(Modifier.height(24.dp))
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.dp)
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(Color.Gray.copy(alpha = 0.25f))
//                        )
//                    }
//                } else {
//                    Column(
//                        modifier = Modifier
//                            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
//                            .fillMaxWidth()
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            BasicTextField(
//                                value = uiState.name,
//                                onValueChange = viewModel::onNameChange,
//                                textStyle = MaterialTheme.typography.titleLarge,
//                                keyboardOptions = KeyboardOptions(
//                                    imeAction = ImeAction.Done,
//                                    capitalization = KeyboardCapitalization.Sentences
//                                ),
//                                keyboardActions = KeyboardActions(
//                                    onDone = {
//                                        focusRequester.requestFocus()
//                                    }
//                                ),
//                                maxLines = 2,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .padding(end = 10.dp)
//                            )
//                        }
////
////                        Text(
////                            text = uiState.name,
////                            style = MaterialTheme.typography.headlineMedium,
////                            color = AppOnPrimaryContainer
////                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "Описание блюда, ингредиенты, вес порции и другая информация.",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = AppOnPrimaryContainer.copy(alpha = 0.75f)
//                        )
//                        Button(
//                            onClick = {
//                                viewModel.saveMeal()
//                                onBackClick()
//                            }
//                        ) {
//                            Text("Save")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun TopOverlay(onBackClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        IconButton(onClick = onBackClick) {
//            Icon(Icons.Default.ArrowBack, contentDescription = null)
//        }
//
//        IconButton(onClick = { /* favorite */ }) {
//            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
//        }
//    }
//}

@Composable
fun SheetContent(
    uiState: MealReviewUiState,
    viewModel: MealReviewScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 250.dp) // 🔥 ключ
    ) {
        // сам sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
        ) {

            // ручка
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray)
            )

            Spacer(Modifier.height(16.dp))

            BasicTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange
            )

            Spacer(Modifier.height(8.dp))

            Text("Описание блюда...")

            Spacer(Modifier.height(16.dp))

            Button(onClick = { viewModel.saveMeal() }) {
                Text("Save")
            }
        }
    }
}
package com.jacqulin.calcalc.feature.home.ui.review

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jacqulin.calcalc.core.designsystem.theme.AppOnPrimaryContainer
import com.jacqulin.calcalc.core.designsystem.theme.AppPrimaryContainer
import java.io.File

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
    val meal = uiState.meal

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isError) {
        uiState.isError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

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
    ) { _ ->
        Column(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = file,
                    contentDescription = meal?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,                    // сверху — прозрачно (видно фото)
                                    Color(0xFFFCFCFD).copy(alpha = 0.9f), // снизу — цвет фона
                                    Color(0xFFFCFCFD).copy(alpha = 0.95f)
                                ),
                                startY = 0f,
                                endY = 60f
                            )
                        )
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 🔽 Нижняя часть: контент с закруглением
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFCFCFD).copy(alpha = 0.95f),
                                        AppPrimaryContainer.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = meal?.name ?: "Блюдо",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AppOnPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Описание блюда, ингредиенты, вес порции и другая информация.",
//                        text = meal?.calories ?: "Блюдо",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppOnPrimaryContainer.copy(alpha = 0.75f)
                        )
                        Button(
                            onClick = {
                                viewModel.saveMeal()
                                onBackClick()
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
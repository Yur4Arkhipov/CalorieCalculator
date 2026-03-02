package com.jacqulin.calcalc.feature.home.ui.home

import android.net.Uri
import com.jacqulin.calcalc.core.domain.model.MealType

sealed interface HomeUiEvent {
    data class RequestCameraPermission(val mealType: MealType) : HomeUiEvent
    data class LaunchCamera(val uri: Uri, val mealType: MealType) : HomeUiEvent
    data class LaunchGallery(val mealType: MealType) : HomeUiEvent
}
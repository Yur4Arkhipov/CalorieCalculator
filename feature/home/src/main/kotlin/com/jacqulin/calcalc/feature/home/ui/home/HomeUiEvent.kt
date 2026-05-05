package com.jacqulin.calcalc.feature.home.ui.home

import com.jacqulin.calcalc.core.domain.model.TempImage

sealed interface HomeUiEvent {
    data object RequestCameraPermission : HomeUiEvent
    data class LaunchCamera(val tempImage: TempImage) : HomeUiEvent
    data object LaunchGallery : HomeUiEvent
    data class NavigateToMealReview(val tempImage: TempImage) : HomeUiEvent
}
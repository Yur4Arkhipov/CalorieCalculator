package com.jacqulin.calcalc.core.util.effects

sealed interface UiEffect {
    data object CloseScreen : UiEffect
    data class ShowSnackbar(
        val messageCode: SnackbarMessageCode,
        val isError: Boolean
    ) : UiEffect
}
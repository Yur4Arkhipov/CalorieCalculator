package com.jacqulin.calcalc.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Основные цвета
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppOnPrimaryContainer,

    // Вторичные цвета
    secondary = AppSecondary,
    onSecondary = SurfaceWhite,
    secondaryContainer = AppSecondaryContainer,
    onSecondaryContainer = TextPrimary,

    // Третичные цвета
    tertiary = FatMain,
    onTertiary = SurfaceWhite,
    tertiaryContainer = FatLight,
    onTertiaryContainer = TextPrimary,

    // Фоны
    background = BackgroundMain,         // Основной фон приложения
    onBackground = TextPrimary,          // Текст на фоне

    surface = SurfaceWhite,              // Фон карточек
    onSurface = TextPrimary,             // Текст на карточках
    surfaceVariant = BackgroundSecondary,// Альтернативный фон
    onSurfaceVariant = TextSecondary,    // Вторичный текст

    // Границы и разделители
    outline = DateInactive,              // Границы элементов
    outlineVariant = CaloriesLight,      // Светлые границы

    // Ошибки
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun CalorieCalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
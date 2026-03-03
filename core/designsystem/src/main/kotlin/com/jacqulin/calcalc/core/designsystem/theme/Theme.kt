package com.jacqulin.calcalc.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Основные цвета
    primary = ProteinMain,              // Основной акцентный цвет - синий (для кнопок, выделений)
    onPrimary = SurfaceWhite,           // Текст на primary
    primaryContainer = ProteinLight,     // Светлый фон для выделенных элементов
    onPrimaryContainer = TextPrimary,    // Текст на primaryContainer

    // Вторичные цвета
    secondary = CarbsMain,               // Вторичный акцент - жёлтый
    onSecondary = TextPrimary,
    secondaryContainer = CarbsLight,
    onSecondaryContainer = TextPrimary,

    // Третичные цвета
    tertiary = FatMain,                  // Третичный цвет - зелёный
    onTertiary = TextPrimary,
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

    // Ошибки (оставляем стандартные)
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
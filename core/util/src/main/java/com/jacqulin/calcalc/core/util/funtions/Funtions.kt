package com.jacqulin.calcalc.core.util.funtions

fun filterNumericInput(
    input: String,
    maxLength: Int,
    maxValue: Int
): String {
    // выбор только цифр
    val digits = input.filter { it.isDigit() }
    // ограничиваем длину
    val trimmed = digits.take(maxLength)
    if (trimmed.isEmpty()) return ""
    // ограничиваем по максимальному значению
    val value = trimmed.toIntOrNull()
    return if (value != null && value > maxValue) {
        // Возвращаем строку с максимальным значением, но тоже обрезаем по длине
        maxValue.toString().take(maxLength)
    } else {
        trimmed
    }
}
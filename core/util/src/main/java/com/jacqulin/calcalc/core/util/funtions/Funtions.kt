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

    // ограничиваем по максимальному значению
    val value = trimmed.toIntOrNull() ?: return ""
    if (value > maxValue) return maxValue.toString()

    return trimmed
}
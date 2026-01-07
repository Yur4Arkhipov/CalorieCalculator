package com.jacqulin.calcalc.feature.home.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.domain.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val MAX_FUTURE_WEEKS = 1
private const val MAX_PAST_WEEKS = 260

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarSection(
    currentWeekIndex: Int,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onWeekChanged: (Int) -> Unit
) {
    val initialPage = MAX_PAST_WEEKS
    val pageCount = MAX_PAST_WEEKS + MAX_FUTURE_WEEKS + 1

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                val weekOffset = page - initialPage
                if (weekOffset != currentWeekIndex) {
                    onWeekChanged(weekOffset)
                }
            }
    }

    LaunchedEffect(currentWeekIndex) {
        val targetPage = initialPage + currentWeekIndex
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column {
        WeekHeader(
            weekOffset = currentWeekIndex,
            onPreviousWeek = {
                onWeekChanged(currentWeekIndex - 1)
            },
            onNextWeek = {
                if (currentWeekIndex < MAX_FUTURE_WEEKS) {
                    onWeekChanged(currentWeekIndex + 1)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekOffset = page - initialPage
            WeekRow(
                weekDays = generateWeekDaysForOffset(
                    weekOffset = weekOffset,
                    selectedDate = selectedDate
                ),
                onDateSelected = { date ->
                    if (!isFutureDate(date)) {
                        onDateSelected(date)
                    }
                }
            )
        }
    }
}

@Composable
private fun WeekHeader(
    weekOffset: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))
    val title = monthFormat
        .format(calendar.time)
        .replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Предыдущая неделя"
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = onNextWeek,
            enabled = weekOffset < MAX_FUTURE_WEEKS
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Следующая неделя",
                tint = if (weekOffset < MAX_FUTURE_WEEKS)
                    MaterialTheme.colorScheme.onBackground
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun WeekRow(
    weekDays: List<CalendarDay>,
    onDateSelected: (Date) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { day ->
            CalendarDayItem(
                day = day,
                onClick = { onDateSelected(day.date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFuture = isFutureDate(day.date)

    val backgroundColor = when {
        isFuture -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        day.isSelected -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    val textColor = when {
        isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        day.isSelected -> MaterialTheme.colorScheme.onPrimary
        day.isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isFuture) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.displayDay,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = day.displayDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

private fun generateWeekDaysForOffset(
    weekOffset: Int,
    selectedDate: Date
): List<CalendarDay> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        add(Calendar.WEEK_OF_YEAR, weekOffset)
    }

    val today = Date()
    val dayFormat = SimpleDateFormat("EEE", Locale("ru"))
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

    return (0..6).map {
        val date = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        CalendarDay(
            date = date,
            displayDay = dayFormat.format(date),
            displayDate = dateFormat.format(date),
            isToday = isSameDay(date, today),
            isSelected = isSameDay(date, selectedDate)
        )
    }
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isFutureDate(date: Date): Boolean {
    val today = Calendar.getInstance()
    val checkDate = Calendar.getInstance().apply { time = date }

    today.set(Calendar.HOUR_OF_DAY, 23)
    today.set(Calendar.MINUTE, 59)
    today.set(Calendar.SECOND, 59)
    today.set(Calendar.MILLISECOND, 999)

    return checkDate.timeInMillis > today.timeInMillis
}
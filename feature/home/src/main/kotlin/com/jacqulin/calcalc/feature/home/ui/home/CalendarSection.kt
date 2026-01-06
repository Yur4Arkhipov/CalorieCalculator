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
import com.jacqulin.calcalc.feature.home.ui.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val PAGE_CENTER = 500
private const val TOTAL_PAGES = 1000

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarSection(
    weekDays: List<CalendarDay>,
    currentWeekIndex: Int = 0,
    onDateSelected: (Date) -> Unit,
    onWeekChanged: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = PAGE_CENTER,
        pageCount = { TOTAL_PAGES }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val weekOffset = page - PAGE_CENTER
            if (weekOffset != currentWeekIndex) {
                onWeekChanged(weekOffset)
            }
        }
    }

    LaunchedEffect(currentWeekIndex) {
        val targetPage = PAGE_CENTER + currentWeekIndex
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column {
        WeekHeader(
            weekDays = weekDays,
            onPreviousWeek = { onWeekChanged(currentWeekIndex - 1) },
            onNextWeek = { onWeekChanged(currentWeekIndex + 1) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekOffset = page - PAGE_CENTER
            WeekRow(
                weekDays = generateWeekDaysForOffset(weekOffset),
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun WeekHeader(
    weekDays: List<CalendarDay>,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    if (weekDays.isNotEmpty()) {
        val monthFormat = SimpleDateFormat("LLLL yyyy", Locale.forLanguageTag("ru"))
        val firstDay = weekDays.first().date
        val monthYear = monthFormat.format(firstDay).replaceFirstChar { it.uppercase() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousWeek) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущая неделя",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = monthYear,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onNextWeek) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Следующая неделя",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
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
    val backgroundColor = when {
        day.isSelected -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    val textColor = when {
        day.isSelected -> MaterialTheme.colorScheme.onPrimary
        day.isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
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

@Composable
private fun generateWeekDaysForOffset(weekOffset: Int): List<CalendarDay> {
    val calendar = Calendar.getInstance()
    val today = calendar.time
    val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("ru"))
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

    val todayCalendar = Calendar.getInstance()
    todayCalendar.time = today

    val dayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
    todayCalendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

    todayCalendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    return (0..6).map { dayOffset ->
        calendar.time = todayCalendar.time
        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        val date = calendar.time

        CalendarDay(
            date = date,
            displayDay = dayFormat.format(date),
            displayDate = dateFormat.format(date),
            calories = 0,
            isToday = isSameDay(date, today),
            isSelected = false
        )
    }
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
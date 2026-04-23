package com.jacqulin.calcalc.feature.home.ui.home.sections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.theme.AppColors
import com.jacqulin.calcalc.feature.home.model.CalendarDay
import com.jacqulin.calcalc.feature.home.ui.home.MAX_FUTURE_WEEKS
import com.jacqulin.calcalc.feature.home.ui.home.MAX_PAST_WEEKS
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarSection(
    currentWeekIndex: Int,
    weeks: Map<Int, List<CalendarDay>>,
    onDateSelected: (Date) -> Unit,
    onWeekChanged: (Int) -> Unit
) {
    val initialPage = MAX_PAST_WEEKS

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { MAX_PAST_WEEKS + MAX_FUTURE_WEEKS + 1 }
    )

    LaunchedEffect(currentWeekIndex) {
        val targetPage = initialPage + currentWeekIndex
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onWeekChanged(page - initialPage)
        }
    }

    val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))
    val weekTitle = weeks[currentWeekIndex]
        ?.firstOrNull()
        ?.date
        ?.let { monthFormat.format(it).replaceFirstChar { c -> c.uppercase() } }
        ?: ""

    Column {
        WeekHeader(
            weekOffset = currentWeekIndex,
            title = weekTitle,
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
            val days = weeks[weekOffset] ?: emptyList()
            WeekRow(
                weekDays = days,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun WeekHeader(
    weekOffset: Int,
    title: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.home_calendar_section_prev_week)
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
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = stringResource(R.string.home_calendar_section_next_week),
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
                onClick = { if (!day.isFuture) onDateSelected(day.date) },
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
    val isFuture = day.isFuture

    val backgroundColor = when {
        isFuture -> MaterialTheme.colorScheme.background
        day.isToday -> AppColors.dateToday
        day.isSelected -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isFuture -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        day.isToday && day.isSelected -> MaterialTheme.colorScheme.onSurface
        day.isToday -> AppColors.dateToday.copy(alpha = 0.6f)
        day.isSelected -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val borderWidth = when {
        day.isSelected -> 2.dp
        else -> 1.dp
    }

    val textColor = when {
        isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        day.isToday -> MaterialTheme.colorScheme.onSurface
        day.isSelected -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                enabled = !isFuture,
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
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
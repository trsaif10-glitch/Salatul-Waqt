package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.DailySchedule
import com.example.domain.model.UiMonthlyDayItem
import com.example.domain.prayer.BanglaDateFormatter
import com.example.ui.viewmodel.MonthlyUiState
import com.example.ui.viewmodel.SalatulWaqtUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyScheduleScreen(
  state: SalatulWaqtUiState,
  monthlyState: MonthlyUiState,
  onPrevMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onResetToToday: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  val strings = LocalAppStrings.current
  val use24h = state.preferences.use24HourFormat
  val tzId = state.preferences.location.timeZoneId
  var selectedDaySchedule by remember { mutableStateOf<DailySchedule?>(null) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = strings.hijriCalendarHeader,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = state.hijriDateText,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("hijri_calendar_back_btn"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = strings.back,
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
        ),
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 720.dp)
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            .border(1.75.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(horizontal = 6.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          IconButton(
            onClick = onPrevMonth,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("prev_month_btn"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
              contentDescription = strings.prevMonthCd,
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = monthlyState.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "${state.preferences.location.name} • ${strings.tapDayDetailsSuffix}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onResetToToday,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("today_month_btn"),
            ) {
              Icon(
                imageVector = Icons.Filled.Today,
                contentDescription = strings.currentMonthCd,
                tint = MaterialTheme.colorScheme.primary,
              )
            }
            IconButton(
              onClick = onNextMonth,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("next_month_btn"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = strings.nextMonthCd,
              )
            }
          }
        }

        val tableShape = MaterialTheme.shapes.medium
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(tableShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.75.dp, MaterialTheme.colorScheme.outline, tableShape),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.secondary)
              .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            HeaderCell(strings.colDateHijri, Modifier.weight(1.35f), TextAlign.Start)
            HeaderCell(strings.colFajr, Modifier.weight(0.9f))
            HeaderCell(strings.colDhuhr, Modifier.weight(0.9f))
            HeaderCell(strings.colAsr, Modifier.weight(0.9f))
            HeaderCell(strings.colMaghrib, Modifier.weight(0.9f))
            HeaderCell(strings.colIsha, Modifier.weight(0.9f))
          }

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
          ) {
            items(
              items = monthlyState.items,
              key = { "${it.schedule.year}_${it.schedule.month}_${it.schedule.day}" },
              contentType = { "month_row" },
            ) { item ->
              PrecomputedMonthRow(
                item = item,
                onClick = { selectedDaySchedule = item.schedule },
              )
            }
          }
        }
      }
    }
  }

  selectedDaySchedule?.let { daySched ->
    AlertDialog(
      onDismissRequest = { selectedDaySchedule = null },
      title = {
        Column {
          Text(
            text = BanglaDateFormatter.formatGregorianDate(
              daySched.year,
              daySched.month,
              daySched.day,
              tzId,
              strings,
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = BanglaDateFormatter.formatHijriDate(
              daySched.dhuhrStartMillis,
              state.preferences.hijriOffsetDays,
              tzId,
              strings,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          daySched.windows.forEach { w ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = w.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(6.dp),
                  )
                  .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp),
              ) {
                Text(
                  text = "${BanglaDateFormatter.formatClockCompact(w.startMillis, use24h, tzId)} – ${BanglaDateFormatter.formatClockCompact(w.endMillis, use24h, tzId)}",
                  style = MaterialTheme.typography.labelSmall,
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
        }
      },
      confirmButton = {
        TextButton(onClick = { selectedDaySchedule = null }) {
          Text(strings.close)
        }
      },
    )
  }
}

@Composable
private fun PrecomputedMonthRow(
  item: UiMonthlyDayItem,
  onClick: () -> Unit,
) {
  val rowBg = when {
    item.isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    item.schedule.day % 2 == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    else -> MaterialTheme.colorScheme.surface
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(rowBg)
        .clickable(onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .testTag("month_row_${item.schedule.day}"),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1.35f)) {
        Text(
          text = if (item.isToday) "${item.dateShortText} ★" else item.dateShortText,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = if (item.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = item.hijriShortText,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      TimeCell(item.fajrText, item.isToday, Modifier.weight(0.9f))
      TimeCell(item.dhuhrText, item.isToday, Modifier.weight(0.9f))
      TimeCell(item.asrText, item.isToday, Modifier.weight(0.9f))
      TimeCell(item.maghribText, item.isToday, Modifier.weight(0.9f))
      TimeCell(item.ishaText, item.isToday, Modifier.weight(0.9f))
    }
    HorizontalDivider(
      thickness = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant,
    )
  }
}

@Composable
private fun HeaderCell(
  text: String,
  modifier: Modifier,
  align: TextAlign = TextAlign.Center,
) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onSecondary,
    textAlign = align,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier,
  )
}

@Composable
private fun TimeCell(
  text: String,
  highlight: Boolean,
  modifier: Modifier,
) {
  Text(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
    color = MaterialTheme.colorScheme.onSurface,
    textAlign = TextAlign.Center,
    maxLines = 1,
    modifier = modifier,
  )
}

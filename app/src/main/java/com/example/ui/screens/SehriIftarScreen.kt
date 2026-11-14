package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.UiMonthlyDayItem
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.viewmodel.MonthlyUiState
import com.example.ui.viewmodel.SalatulWaqtUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SehriIftarScreen(
  state: SalatulWaqtUiState,
  monthlyState: MonthlyUiState,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  val strings = LocalAppStrings.current

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = strings.sehriIftarTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "${state.preferences.location.name} • ${state.hijriDateText}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("sehri_iftar_back_btn"),
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
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 640.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        item(key = "today_sehri_iftar") {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            val cardShape = RoundedCornerShape(20.dp)
            Column(
              modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer, cardShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, cardShape)
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Brightness2,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = strings.sehriEndsToday,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
              Text(
                text = state.todaySehriText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
            }

            Column(
              modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface, cardShape)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, cardShape)
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.WbTwilight,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = strings.todaysIftar,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Text(
                text = state.todayIftarText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }

        item(key = "roza_duas") {
          val boxShape = RoundedCornerShape(18.dp)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, boxShape)
              .border(1.5.dp, MaterialTheme.colorScheme.outline, boxShape)
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = strings.fastingDuasTitle,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = strings.suhoorIntentionLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
              )
              Text(
                text = "نَوَيْتُ أَنْ أَصُومَ غَدًا مِنْ شَهْرِ رَمَضَانَ",
                fontFamily = AmiriFontFamily,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = strings.suhoorIntentionDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }

            HorizontalDivider(
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = strings.iftarDuaLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
              )
              Text(
                text = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
                fontFamily = AmiriFontFamily,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = strings.iftarDuaDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        item(key = "monthly_header") {
          Text(
            text = "${monthlyState.title} — ${strings.sehriIftarTimetableSuffix}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp),
          )
        }

        item(key = "table_header") {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = strings.colDate,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.weight(0.9f),
            )
            Text(
              text = strings.colSehriEnds,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.weight(1f),
            )
            Text(
              text = strings.colIftar,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.weight(1f),
            )
          }
        }

        items(
          items = monthlyState.items,
          key = { "si_${it.schedule.day}" },
          contentType = { "si_row" },
        ) { item ->
          SehriIftarRow(item = item)
        }
      }
    }
  }
}

@Composable
private fun SehriIftarRow(item: UiMonthlyDayItem) {
  val bg = if (item.isToday) {
    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
  } else {
    MaterialTheme.colorScheme.surface
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(bg)
        .padding(horizontal = 14.dp, vertical = 11.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(0.9f)) {
        Text(
          text = if (item.isToday) "${item.dateShortText} ★" else item.dateShortText,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.SemiBold,
          color = if (item.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = item.hijriShortText,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Text(
        text = item.sehriText,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = item.iftarText,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.weight(1f),
      )
    }
    HorizontalDivider(
      thickness = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant,
    )
  }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.AppLanguage
import com.example.domain.model.PrayerCategory
import com.example.domain.model.PrayerType
import com.example.domain.model.PrayerWindow
import com.example.domain.model.UiPrayerItem
import com.example.domain.prayer.PrayerKnowledgeBase
import com.example.ui.viewmodel.SalatulWaqtUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPrayersScreen(
  state: SalatulWaqtUiState,
  onBack: () -> Unit,
  onOpenPrayerDetail: (PrayerWindow) -> Unit,
  onToggleReminder: (PrayerType) -> Unit,
) {
  BackHandler(onBack = onBack)

  val strings = LocalAppStrings.current
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(strings.tabAllPrayers, strings.tabSalahGuide)

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = strings.prayerScheduleTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "${state.preferences.location.name} • ${strings.asrTitle(state.preferences.asrJuristic)}",
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
              .testTag("all_prayers_back_btn"),
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
          .widthIn(max = 640.dp)
          .fillMaxSize(),
      ) {
        SingleChoiceSegmentedButtonRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        ) {
          tabs.forEachIndexed { idx, label ->
            SegmentedButton(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              shape = SegmentedButtonDefaults.itemShape(index = idx, count = tabs.size),
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Text(label)
            }
          }
        }

        if (selectedTab == 0) {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp),
          ) {
            items(
              items = state.todayPrayerItems,
              key = { it.window.type.id },
              contentType = { it.window.category },
            ) { item ->
              val isReminderOn = state.preferences.enabledReminders.contains(item.window.type)
              FlatPrayerRow(
                item = item,
                isReminderEnabled = isReminderOn,
                onClick = { onOpenPrayerDetail(item.window) },
                onToggleReminder = { onToggleReminder(item.window.type) },
              )
              HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
              )
            }
          }
        } else {
          FastGuideList(
            language = state.preferences.language,
            modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }
  }
}

@Composable
private fun FlatPrayerRow(
  item: UiPrayerItem,
  isReminderEnabled: Boolean,
  onClick: () -> Unit,
  onToggleReminder: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val window = item.window
  val isFard = window.category == PrayerCategory.FARD
  val isForbidden = window.category == PrayerCategory.FORBIDDEN

  val accentColor = when (window.category) {
    PrayerCategory.FARD -> MaterialTheme.colorScheme.primary
    PrayerCategory.NAFL -> MaterialTheme.colorScheme.tertiary
    PrayerCategory.WAJIB -> MaterialTheme.colorScheme.secondary
    PrayerCategory.FORBIDDEN -> MaterialTheme.colorScheme.error
  }

  val rowBg = when {
    isForbidden -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
    isFard -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
    else -> MaterialTheme.colorScheme.background
  }

  val icon = when (window.category) {
    PrayerCategory.FORBIDDEN -> Icons.Filled.Block
    PrayerCategory.WAJIB -> Icons.Filled.Brightness2
    PrayerCategory.NAFL -> Icons.Filled.NightsStay
    PrayerCategory.FARD -> Icons.Filled.WbSunny
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(rowBg)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("prayer_card_${window.type.id}"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f),
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.14f))
          .border(1.25.dp, accentColor.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp),
        )
      }
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = window.title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = window.rakaatSummary,
          style = MaterialTheme.typography.labelSmall,
          color = if (isForbidden) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = item.startPeriodText,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = "${strings.endsPrefix} ${item.endPeriodText}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      if (window.isReminderSupported) {
        IconButton(
          onClick = onToggleReminder,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("bell_${window.type.id}"),
        ) {
          Icon(
            imageVector = if (isReminderEnabled) {
              Icons.Filled.NotificationsActive
            } else {
              Icons.Outlined.NotificationsNone
            },
            contentDescription = if (isReminderEnabled) strings.reminderEnabledCd else strings.reminderDisabledCd,
            tint = if (isReminderEnabled) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun FastGuideList(
  language: AppLanguage,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val topics = remember(language) { PrayerKnowledgeBase.getGuideTopics(language) }
  var query by remember { mutableStateOf("") }
  var expandedId by remember(language) { mutableStateOf<String?>(topics.firstOrNull()?.id) }

  val filteredTopics = remember(query, topics) {
    val q = query.trim()
    if (q.isEmpty()) {
      topics
    } else {
      topics.filter { topic ->
        topic.title.contains(q, ignoreCase = true) ||
          topic.subtitle.contains(q, ignoreCase = true) ||
          topic.sections.any { sec ->
            sec.heading.contains(q, ignoreCase = true) ||
              sec.points.any { it.contains(q, ignoreCase = true) }
          }
      }
    }
  }

  Column(
    modifier = modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      placeholder = { Text(strings.guideSearchPlaceholder) },
      leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth(),
    )

    LazyColumn(
      modifier = Modifier.weight(1f),
      contentPadding = PaddingValues(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(filteredTopics, key = { it.id }) { topic ->
        val isExpanded = expandedId == topic.id
        val shape = RoundedCornerShape(16.dp)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
              if (isExpanded) 2.dp else 1.5.dp,
              if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
              shape,
            )
            .clickable { expandedId = if (isExpanded) null else topic.id }
            .padding(14.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = topic.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = topic.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Icon(
              imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
            )
          }
          AnimatedVisibility(visible = isExpanded) {
            Column(
              modifier = Modifier.padding(top = 10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
              )
              topic.sections.forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = section.heading,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                  )
                  section.points.forEach { point ->
                    Text(
                      text = point,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

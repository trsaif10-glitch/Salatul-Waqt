package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.PrayerCategory
import com.example.domain.model.PrayerWindow
import com.example.domain.prayer.BanglaDateFormatter
import com.example.domain.prayer.PrayerKnowledgeBase
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PrayerDetailSheet(
  window: PrayerWindow,
  isReminderEnabled: Boolean,
  use24HourFormat: Boolean,
  timeZoneId: String,
  onToggleReminder: () -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val detail = PrayerKnowledgeBase.getPrayerDetail(window.type, strings.language)

  val badgeContainer = when (window.category) {
    PrayerCategory.FARD -> MaterialTheme.colorScheme.primaryContainer
    PrayerCategory.NAFL -> MaterialTheme.colorScheme.tertiaryContainer
    PrayerCategory.WAJIB -> MaterialTheme.colorScheme.secondaryContainer
    PrayerCategory.FORBIDDEN -> MaterialTheme.colorScheme.errorContainer
  }
  val badgeContent = when (window.category) {
    PrayerCategory.FARD -> MaterialTheme.colorScheme.onPrimaryContainer
    PrayerCategory.NAFL -> MaterialTheme.colorScheme.onTertiaryContainer
    PrayerCategory.WAJIB -> MaterialTheme.colorScheme.onSecondaryContainer
    PrayerCategory.FORBIDDEN -> MaterialTheme.colorScheme.onErrorContainer
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("prayer_detail_sheet"),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            color = badgeContainer,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.25.dp, badgeContent.copy(alpha = 0.35f)),
          ) {
            Text(
              text = detail.categoryLabel,
              style = MaterialTheme.typography.labelMedium,
              color = badgeContent,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = detail.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Text(
          text = detail.arabicTitle,
          fontFamily = AmiriFontFamily,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(top = 6.dp),
        )
      }

      val timeShape = MaterialTheme.shapes.medium
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant, timeShape)
          .border(1.5.dp, MaterialTheme.colorScheme.outline, timeShape)
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = strings.startEndTimeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${BanglaDateFormatter.formatClockWithPeriod(window.startMillis, use24HourFormat, timeZoneId, strings)} — ${BanglaDateFormatter.formatClockWithPeriod(window.endMillis, use24HourFormat, timeZoneId, strings)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
          )
        }
        Box(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
            .border(1.25.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
          Text(
            text = BanglaDateFormatter.formatDuration(window.startMillis, window.endMillis, strings),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "${strings.rakaatBreakdownPrefix} (${detail.totalRakaat})",
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          detail.rakaatBreakdown.forEach { part ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f))
                .border(1.25.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = part.count,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = part.type,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }
      }

      HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
      )

      DetailInfoBlock(
        icon = Icons.Filled.AccessTime,
        title = strings.timeWindowRulesTitle,
        body = detail.timeRule,
      )
      DetailInfoBlock(
        icon = Icons.Filled.Info,
        title = strings.optimalMakruhTitle,
        body = detail.optimalAndMakruh,
      )

      val niyyahShape = MaterialTheme.shapes.medium
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant, niyyahShape)
          .border(1.5.dp, MaterialTheme.colorScheme.outline, niyyahShape)
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Text(
          text = strings.intentionNiyyahTitle,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = detail.niyyahPronunciation,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = "${strings.meaningPrefix} ${detail.niyyahMeaning}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      DetailInfoBlock(
        icon = Icons.Filled.Book,
        title = strings.virtuesHadithTitle,
        body = detail.virtues,
      )
      DetailInfoBlock(
        icon = Icons.Filled.AutoAwesome,
        title = strings.recommendedSunnahTitle,
        body = detail.specialAmal,
      )

      if (window.isReminderSupported) {
        if (isReminderEnabled) {
          OutlinedButton(
            onClick = onToggleReminder,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("detail_reminder_toggle"),
          ) {
            Icon(
              imageVector = Icons.Outlined.NotificationsOff,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.disableReminderBtn)
          }
        } else {
          Button(
            onClick = onToggleReminder,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("detail_reminder_toggle"),
          ) {
            Icon(
              imageVector = Icons.Filled.NotificationsActive,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.enableReminderBtn)
          }
        }
      }
    }
  }
}

@Composable
private fun DetailInfoBlock(
  icon: ImageVector,
  title: String,
  body: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .border(1.25.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
      )
    }
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

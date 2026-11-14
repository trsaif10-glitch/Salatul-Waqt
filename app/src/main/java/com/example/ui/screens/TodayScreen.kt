package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.localization.LocalAppStrings
import com.example.domain.prayer.BanglaDateFormatter
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.viewmodel.ActiveWaqtStatus
import com.example.ui.viewmodel.SalatulWaqtUiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun TodayScreen(
  state: SalatulWaqtUiState,
  activeWaqtFlow: StateFlow<ActiveWaqtStatus?>,
  nowMillisFlow: StateFlow<Long>,
  onOpenLocationPicker: () -> Unit,
  onOpenLanguagePicker: () -> Unit,
  onQuickDetectGps: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenPrayerSchedule: () -> Unit,
  onOpenQibla: () -> Unit,
  onOpenSehriIftar: () -> Unit,
  onOpenTasbih: () -> Unit,
  onOpenHijriCalendar: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 640.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Top Bar with Final Logo, Title, Location Pill, GPS & Settings
      TopHeaderRow(
        locationName = state.preferences.location.name,
        regionName = if (state.preferences.autoDetectLocation) {
          "${strings.autoLocationModeBadge} • ${state.preferences.location.region}"
        } else {
          state.preferences.location.region
        },
        isAutoLocation = state.preferences.autoDetectLocation,
        isDetecting = state.isDetectingLocation,
        onOpenLocationPicker = onOpenLocationPicker,
        onQuickDetectGps = onQuickDetectGps,
        onOpenSettings = onOpenSettings,
      )

      // Classic White Date Strip + Instant Language Selector Pill
      val dateShape = RoundedCornerShape(16.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface, dateShape)
          .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, dateShape)
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Text(
            text = state.hijriDateText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = state.gregorianDateText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val langPillShape = RoundedCornerShape(50)
        Row(
          modifier = Modifier
            .clip(langPillShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, langPillShape)
            .clickable(onClick = onOpenLanguagePicker)
            .minimumInteractiveComponentSize()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("home_language_button"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.Language,
            contentDescription = strings.cdChangeLanguage,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = state.preferences.language.nativeName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
          )
        }
      }

      // Classic White, Charcoal & Emerald Hero Card (ONLY Leaf Text ticks every 1s)
      HeroWaqtCard(
        activeWaqtFlow = activeWaqtFlow,
        nowMillisFlow = nowMillisFlow,
      )

      // Primary Action Button: Prayer Schedule
      val primaryShape = RoundedCornerShape(20.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(primaryShape)
          .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
          .border(1.75.dp, MaterialTheme.colorScheme.primary, primaryShape)
          .clickable(onClick = onOpenPrayerSchedule)
          .minimumInteractiveComponentSize()
          .padding(horizontal = 18.dp, vertical = 16.dp)
          .testTag("btn_prayer_schedule"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.weight(1f),
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondary)
              .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.Schedule,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.size(22.dp),
            )
          }
          Column {
            Text(
              text = strings.prayerScheduleTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
              text = strings.prayerScheduleSubtitle,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
        )
      }

      // Classic 2x2 Feature Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        FeatureTileButton(
          title = strings.qiblaCompassTitle,
          subtitle = strings.qiblaCompassSubtitle,
          icon = Icons.Filled.Explore,
          onClick = onOpenQibla,
          modifier = Modifier
            .weight(1f)
            .testTag("btn_qibla_compass"),
        )
        FeatureTileButton(
          title = strings.sehriIftarTitle,
          subtitle = state.todaySehriText.ifBlank { strings.dailyTimesFallback },
          icon = Icons.Filled.Brightness2,
          onClick = onOpenSehriIftar,
          modifier = Modifier
            .weight(1f)
            .testTag("btn_sehri_iftar"),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        FeatureTileButton(
          title = strings.tasbihTitle,
          subtitle = strings.tasbihSubtitle,
          icon = Icons.Filled.TouchApp,
          onClick = onOpenTasbih,
          modifier = Modifier
            .weight(1f)
            .testTag("btn_tasbih"),
        )
        FeatureTileButton(
          title = strings.hijriCalendarTitle,
          subtitle = strings.hijriCalendarSubtitle,
          icon = Icons.Filled.CalendarMonth,
          onClick = onOpenHijriCalendar,
          modifier = Modifier
            .weight(1f)
            .testTag("btn_hijri_calendar"),
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}

@Composable
private fun TopHeaderRow(
  locationName: String,
  regionName: String,
  isAutoLocation: Boolean,
  isDetecting: Boolean,
  onOpenLocationPicker: () -> Unit,
  onQuickDetectGps: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  val strings = LocalAppStrings.current

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_app_logo),
        contentDescription = stringResource(id = R.string.app_name),
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
      )
      Column {
        Text(
          text = stringResource(id = R.string.app_name),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = regionName,
          style = MaterialTheme.typography.labelSmall,
          color = if (isAutoLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = if (isAutoLocation) FontWeight.SemiBold else FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      val pillShape = RoundedCornerShape(50)
      Row(
        modifier = Modifier
          .clip(pillShape)
          .background(MaterialTheme.colorScheme.primaryContainer)
          .border(1.5.dp, MaterialTheme.colorScheme.primary, pillShape)
          .clickable(onClick = onOpenLocationPicker)
          .minimumInteractiveComponentSize()
          .padding(horizontal = 10.dp, vertical = 8.dp)
          .testTag("hero_location_button"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text = locationName,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Icon(
          imageVector = Icons.Filled.KeyboardArrowDown,
          contentDescription = strings.cdChangeCity,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )
      }

      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(
            if (isAutoLocation) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
          )
          .border(
            1.5.dp,
            if (isAutoLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            CircleShape,
          )
          .clickable(enabled = !isDetecting, onClick = onQuickDetectGps)
          .testTag("quick_gps_btn"),
        contentAlignment = Alignment.Center,
      ) {
        if (isDetecting) {
          CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else {
          Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = strings.cdDetectGps,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
          )
        }
      }

      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.secondaryContainer)
          .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
          .clickable(onClick = onOpenSettings)
          .testTag("home_settings_btn"),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Filled.Settings,
          contentDescription = strings.cdSettings,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(19.dp),
        )
      }
    }
  }
}

@Composable
private fun HeroWaqtCard(
  activeWaqtFlow: StateFlow<ActiveWaqtStatus?>,
  nowMillisFlow: StateFlow<Long>,
) {
  val strings = LocalAppStrings.current
  val activeStatus by activeWaqtFlow.collectAsStateWithLifecycle()
  val status = activeStatus ?: return
  val displayedWindow = status.currentWindow ?: status.nextWindow

  val cardShape = RoundedCornerShape(26.dp)
  val borderColor = if (status.isForbiddenNow) {
    MaterialTheme.colorScheme.error
  } else {
    MaterialTheme.colorScheme.outline
  }
  val softAccentCircle = if (status.isForbiddenNow) {
    MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
  } else {
    MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
  }
  val secondaryAccentCircle = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(cardShape)
      .background(MaterialTheme.colorScheme.surface)
      .border(2.dp, borderColor, cardShape),
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      drawCircle(
        color = softAccentCircle,
        radius = size.width * 0.42f,
        center = Offset(size.width * 0.94f, size.height * 0.14f),
      )
      drawCircle(
        color = secondaryAccentCircle,
        radius = size.width * 0.26f,
        center = Offset(size.width * 0.08f, size.height * 0.96f),
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier
            .background(
              color = if (status.isForbiddenNow) {
                MaterialTheme.colorScheme.errorContainer
              } else {
                MaterialTheme.colorScheme.primaryContainer
              },
              shape = RoundedCornerShape(50),
            )
            .border(
              1.25.dp,
              if (status.isForbiddenNow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
              RoundedCornerShape(50),
            )
            .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
          Text(
            text = when {
              status.isForbiddenNow -> strings.forbiddenTimeBadge
              status.isRunningWindow -> strings.currentPrayerBadge
              else -> strings.nextPrayerBadge
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (status.isForbiddenNow) {
              MaterialTheme.colorScheme.error
            } else {
              MaterialTheme.colorScheme.primary
            },
          )
        }

        if (displayedWindow != null) {
          Text(
            text = displayedWindow.arabicTitle,
            fontFamily = AmiriFontFamily,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
      ) {
        Text(
          text = displayedWindow?.title ?: "",
          style = MaterialTheme.typography.headlineMedium,
          fontSize = 27.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(1f),
        )

        LiveCountdownLeaf(
          nowMillisFlow = nowMillisFlow,
          targetMillis = status.targetMillis,
          isRunningWindow = status.isRunningWindow,
          isForbidden = status.isForbiddenNow,
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
          .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = strings.startsLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = status.startTimeText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        Box(
          modifier = Modifier
            .width(1.5.dp)
            .height(28.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        )

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = strings.endsLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = status.endTimeText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      LinearProgressIndicator(
        progress = { status.progressFraction },
        color = if (status.isForbiddenNow) {
          MaterialTheme.colorScheme.error
        } else {
          MaterialTheme.colorScheme.primary
        },
        trackColor = MaterialTheme.colorScheme.primaryContainer,
        strokeCap = StrokeCap.Round,
        modifier = Modifier
          .fillMaxWidth()
          .height(7.dp),
      )

      status.nextWindow?.let { next ->
        if (next.type != displayedWindow?.type) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = "${strings.upNextPrefix} ${next.title}",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.Medium,
            )
            Text(
              text = status.nextStartText,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LiveCountdownLeaf(
  nowMillisFlow: StateFlow<Long>,
  targetMillis: Long,
  isRunningWindow: Boolean,
  isForbidden: Boolean,
) {
  val strings = LocalAppStrings.current
  val nowMillis by nowMillisFlow.collectAsStateWithLifecycle()
  val remaining = (targetMillis - nowMillis).coerceAtLeast(0L)

  Column(horizontalAlignment = Alignment.End) {
    Text(
      text = if (isRunningWindow) strings.timeRemaining else strings.startsIn,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
      Text(
        text = BanglaDateFormatter.formatCountdown(remaining),
        fontFamily = PlusJakartaSansFontFamily,
        fontSize = 31.sp,
        fontWeight = FontWeight.Bold,
        color = if (isForbidden) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("live_countdown_text"),
      )
    }
  }
}

@Composable
private fun FeatureTileButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val shape = RoundedCornerShape(20.dp)
  Column(
    modifier = modifier
      .clip(shape)
      .background(MaterialTheme.colorScheme.surface)
      .border(1.5.dp, MaterialTheme.colorScheme.outline, shape)
      .clickable(onClick = onClick)
      .minimumInteractiveComponentSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .border(1.25.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(22.dp),
      )
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

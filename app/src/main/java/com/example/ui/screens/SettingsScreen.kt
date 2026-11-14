package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.AsrJuristic
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerOffsets
import com.example.domain.model.ThemeMode
import com.example.ui.viewmodel.SalatulWaqtUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  state: SalatulWaqtUiState,
  hasNotificationPermission: Boolean,
  onOpenLanguagePicker: () -> Unit,
  onOpenLocationPicker: () -> Unit,
  onDetectGpsLocation: () -> Unit,
  onToggleAutoDetectLocation: (Boolean) -> Unit,
  onSelectAsrJuristic: (AsrJuristic) -> Unit,
  onSelectCalculationMethod: (CalculationMethod) -> Unit,
  onUpdateOffsets: (PrayerOffsets) -> Unit,
  onUpdateHijriOffset: (Int) -> Unit,
  onToggle24HourFormat: (Boolean) -> Unit,
  onSelectThemeMode: (ThemeMode) -> Unit,
  onRequestNotificationPermission: () -> Unit,
  onSendTestNotification: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  val strings = LocalAppStrings.current
  val prefs = state.preferences
  var showOffsetsDialog by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = strings.settingsTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("settings_back_btn"),
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        item {
          SettingsSectionCard(title = strings.languageSectionTitle) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${prefs.language.nativeName} (${prefs.language.englishName})",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                )
                Text(
                  text = strings.languageSectionSubtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = onOpenLanguagePicker,
              border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("settings_language_btn"),
            ) {
              Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(strings.changeLanguageBtn)
            }
          }
        }

        item {
          SettingsSectionCard(title = strings.locationSectionTitle) {
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
                Icon(
                  imageVector = Icons.Filled.LocationOn,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
                Column {
                  Text(
                    text = prefs.location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = prefs.location.region,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
              val badgeShape = RoundedCornerShape(50)
              Box(
                modifier = Modifier
                  .background(
                    if (prefs.autoDetectLocation) {
                      MaterialTheme.colorScheme.primaryContainer
                    } else {
                      MaterialTheme.colorScheme.secondaryContainer
                    },
                    badgeShape,
                  )
                  .border(
                    1.25.dp,
                    if (prefs.autoDetectLocation) {
                      MaterialTheme.colorScheme.primary
                    } else {
                      MaterialTheme.colorScheme.outlineVariant
                    },
                    badgeShape,
                  )
                  .padding(horizontal = 10.dp, vertical = 4.dp),
              ) {
                Text(
                  text = if (prefs.autoDetectLocation) {
                    strings.autoLocationModeBadge
                  } else {
                    strings.manualLocationModeBadge
                  },
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (prefs.autoDetectLocation) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.secondary
                  },
                )
              }
            }
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 10.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
            )
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleAutoDetectLocation(!prefs.autoDetectLocation) }
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(
                modifier = Modifier
                  .weight(1f)
                  .padding(end = 10.dp),
              ) {
                Text(
                  text = strings.autoDetectLocationTitle,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = strings.autoDetectLocationDesc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Switch(
                checked = prefs.autoDetectLocation,
                onCheckedChange = onToggleAutoDetectLocation,
                modifier = Modifier.testTag("settings_auto_location_switch"),
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              OutlinedButton(
                onClick = onOpenLocationPicker,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("settings_pick_city_btn"),
              ) {
                Text(strings.changeCityBtn)
              }
              Button(
                onClick = onDetectGpsLocation,
                enabled = !state.isDetectingLocation,
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("settings_gps_btn"),
              ) {
                Icon(
                  imageVector = Icons.Filled.MyLocation,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (state.isDetectingLocation) strings.locatingBtn else strings.useGpsBtn)
              }
            }
          }
        }

        item {
          SettingsSectionCard(title = strings.asrJuristicSectionTitle) {
            AsrJuristic.entries.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSelectAsrJuristic(item) }
                  .padding(vertical = 6.dp)
                  .testTag("asr_juristic_${item.name}"),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                RadioButton(
                  selected = prefs.asrJuristic == item,
                  onClick = { onSelectAsrJuristic(item) },
                )
                Column(modifier = Modifier.padding(start = 6.dp)) {
                  Text(
                    text = strings.asrTitle(item),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                  )
                  Text(
                    text = strings.asrDesc(item),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }
          }
        }

        item {
          SettingsSectionCard(title = strings.calcMethodSectionTitle) {
            CalculationMethod.entries.forEach { method ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSelectCalculationMethod(method) }
                  .padding(vertical = 6.dp)
                  .testTag("calc_method_${method.name}"),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                RadioButton(
                  selected = prefs.calculationMethod == method,
                  onClick = { onSelectCalculationMethod(method) },
                )
                Column(modifier = Modifier.padding(start = 6.dp)) {
                  Text(
                    text = strings.methodTitle(method),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                  )
                  Text(
                    text = strings.methodSubtitle(method),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
            )

            OutlinedButton(
              onClick = { showOffsetsDialog = true },
              border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("manual_offsets_btn"),
            ) {
              Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(strings.manualOffsetsBtn)
            }
          }
        }

        item {
          SettingsSectionCard(title = strings.dateClockThemeTitle) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = strings.hijriAdjustmentTitle,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = strings.hijriAdjustmentDesc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { onUpdateHijriOffset(prefs.hijriOffsetDays - 1) },
                  enabled = prefs.hijriOffsetDays > -2,
                  modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("hijri_minus_btn"),
                ) {
                  Icon(Icons.Filled.Remove, contentDescription = strings.decreaseDayCd)
                }
                Text(
                  text = prefs.hijriOffsetDays.toString(),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp),
                )
                IconButton(
                  onClick = { onUpdateHijriOffset(prefs.hijriOffsetDays + 1) },
                  enabled = prefs.hijriOffsetDays < 2,
                  modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("hijri_plus_btn"),
                ) {
                  Icon(Icons.Filled.Add, contentDescription = strings.increaseDayCd)
                }
              }
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = strings.clock24hTitle,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = strings.clock24hDesc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Switch(
                checked = prefs.use24HourFormat,
                onCheckedChange = onToggle24HourFormat,
                modifier = Modifier.testTag("time_24h_switch"),
              )
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
            )

            Text(
              text = strings.appThemeTitle,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
              ThemeMode.entries.forEachIndexed { idx, mode ->
                SegmentedButton(
                  selected = prefs.themeMode == mode,
                  onClick = { onSelectThemeMode(mode) },
                  shape = SegmentedButtonDefaults.itemShape(
                    index = idx,
                    count = ThemeMode.entries.size,
                  ),
                  modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("theme_mode_${mode.name}"),
                ) {
                  Text(strings.themeTitle(mode), maxLines = 1)
                }
              }
            }
          }
        }

        item {
          SettingsSectionCard(title = strings.notificationsSectionTitle) {
            Text(
              text = if (hasNotificationPermission) {
                strings.notifEnabledDesc
              } else {
                strings.notifDisabledDesc
              },
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (!hasNotificationPermission) {
              Button(
                onClick = onRequestNotificationPermission,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("request_notif_perm_btn"),
              ) {
                Icon(
                  imageVector = Icons.Filled.NotificationsActive,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.allowNotifBtn)
              }
            } else {
              OutlinedButton(
                onClick = onSendTestNotification,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("test_notif_btn"),
              ) {
                Icon(
                  imageVector = Icons.Filled.NotificationsActive,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.sendTestNotifBtn)
              }
            }
          }
        }
      }
    }
  }

  if (showOffsetsDialog) {
    ManualOffsetsDialog(
      initialOffsets = prefs.offsets,
      onSave = {
        onUpdateOffsets(it)
        showOffsetsDialog = false
      },
      onDismiss = { showOffsetsDialog = false },
    )
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  content: @Composable () -> Unit,
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

@Composable
private fun ManualOffsetsDialog(
  initialOffsets: PrayerOffsets,
  onSave: (PrayerOffsets) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  var temp by remember { mutableStateOf(initialOffsets) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(strings.offsetsDialogTitle) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        OffsetRow(strings.offsetFajrSehri, temp.fajrMin) {
          temp = temp.copy(fajrMin = it.coerceIn(-15, 15))
        }
        OffsetRow(strings.offsetSunrise, temp.sunriseMin) {
          temp = temp.copy(sunriseMin = it.coerceIn(-15, 15))
        }
        OffsetRow(strings.offsetDhuhr, temp.dhuhrMin) {
          temp = temp.copy(dhuhrMin = it.coerceIn(-15, 15))
        }
        OffsetRow(strings.offsetAsr, temp.asrMin) {
          temp = temp.copy(asrMin = it.coerceIn(-15, 15))
        }
        OffsetRow(strings.offsetMaghribIftar, temp.maghribMin) {
          temp = temp.copy(maghribMin = it.coerceIn(-15, 15))
        }
        OffsetRow(strings.offsetIsha, temp.ishaMin) {
          temp = temp.copy(ishaMin = it.coerceIn(-15, 15))
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text(strings.cancel) }
    },
    confirmButton = {
      Button(onClick = { onSave(temp) }) { Text(strings.save) }
    },
  )
}

@Composable
private fun OffsetRow(
  label: String,
  value: Int,
  onChange: (Int) -> Unit,
) {
  val strings = LocalAppStrings.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text = label, style = MaterialTheme.typography.bodyMedium)
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = { onChange(value - 1) },
        modifier = Modifier.minimumInteractiveComponentSize(),
      ) {
        Icon(Icons.Filled.Remove, contentDescription = strings.decreaseCd)
      }
      val prefix = if (value > 0) "+" else ""
      Text(
        text = "$prefix$value ${strings.minShort}",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
      )
      IconButton(
        onClick = { onChange(value + 1) },
        modifier = Modifier.minimumInteractiveComponentSize(),
      ) {
        Icon(Icons.Filled.Add, contentDescription = strings.increaseCd)
      }
    }
  }
}

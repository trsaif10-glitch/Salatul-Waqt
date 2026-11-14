package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.domain.model.PrayerLocation
import com.example.domain.prayer.CityCatalog
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
  currentLocation: PrayerLocation,
  autoDetectLocation: Boolean,
  isDetectingGps: Boolean,
  onToggleAutoDetect: (Boolean) -> Unit,
  onRequestGps: () -> Unit,
  onSelectLocation: (PrayerLocation) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var query by remember { mutableStateOf("") }

  val quickCityNames = remember {
    listOf("Makkah", "Madinah", "London", "New York", "Dubai", "Istanbul", "Cairo", "Kuala Lumpur", "Dhaka", "Toronto")
  }
  val quickCities = remember {
    CityCatalog.allLocations.filter { it.name in quickCityNames }
  }

  val filtered = remember(query) {
    val q = query.trim()
    if (q.isEmpty()) {
      CityCatalog.allLocations
    } else {
      CityCatalog.allLocations.filter {
        it.name.contains(q, ignoreCase = true) || it.region.contains(q, ignoreCase = true)
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("location_picker_sheet"),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.86f)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = strings.selectLocationTitle,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = "${strings.currentCoordsPrefix} ${currentLocation.name} (${String.format(Locale.US, "%.2f°, %.2f°", currentLocation.latitude, currentLocation.longitude)})",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
        )
      }

      val autoCardShape = RoundedCornerShape(14.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(autoCardShape)
          .background(
            if (autoDetectLocation) {
              MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
          )
          .border(
            1.5.dp,
            if (autoDetectLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            autoCardShape,
          )
          .clickable { onToggleAutoDetect(!autoDetectLocation) }
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(end = 10.dp),
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Text(
            text = strings.autoDetectLocationTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = strings.autoDetectLocationDesc,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Switch(
          checked = autoDetectLocation,
          onCheckedChange = onToggleAutoDetect,
          modifier = Modifier.testTag("picker_auto_location_switch"),
        )
      }

      Button(
        onClick = {
          onRequestGps()
          onDismiss()
        },
        enabled = !isDetectingGps,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("gps_detect_button"),
      ) {
        if (isDetectingGps) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary,
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(strings.detectingGps)
        } else {
          Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(strings.detectGpsBtn)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        quickCities.forEach { city ->
          val isSelected = city.name == currentLocation.name
          FilterChip(
            selected = isSelected,
            onClick = {
              onSelectLocation(city)
              onDismiss()
            },
            label = { Text(city.name) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
          )
        }
      }

      OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text(strings.searchCitiesPlaceholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("city_search_input"),
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
      ) {
        items(
          items = filtered,
          key = { "${it.name}_${it.region}" },
          contentType = { "city_row" },
        ) { city ->
          val isSelected = city.name == currentLocation.name
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectLocation(city)
                onDismiss()
              }
              .padding(vertical = 12.dp, horizontal = 4.dp)
              .testTag("city_item_${city.name}"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    if (isSelected) {
                      MaterialTheme.colorScheme.primaryContainer
                    } else {
                      MaterialTheme.colorScheme.surfaceVariant
                    },
                  )
                  .border(
                    1.5.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(10.dp),
                  ),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Filled.LocationOn,
                  contentDescription = null,
                  tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                  },
                  modifier = Modifier.size(20.dp),
                )
              }
              Column {
                Text(
                  text = city.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = city.region,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            if (isSelected) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = strings.selected,
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }
          HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
          )
        }
        item(key = "bottom_spacer", contentType = "spacer") {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import com.example.domain.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
  currentLanguage: AppLanguage,
  onSelectLanguage: (AppLanguage) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var query by remember { mutableStateOf("") }

  val filtered = remember(query) {
    val q = query.trim()
    if (q.isEmpty()) {
      AppLanguage.entries
    } else {
      AppLanguage.entries.filter { lang ->
        lang.nativeName.contains(q, ignoreCase = true) ||
          lang.englishName.contains(q, ignoreCase = true) ||
          lang.code.contains(q, ignoreCase = true)
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("language_picker_sheet"),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.82f)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = strings.selectLanguageTitle,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = strings.selectLanguageSubtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
        )
      }

      OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text(strings.searchLanguagePlaceholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("language_search_input"),
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(
          items = filtered,
          key = { it.code },
        ) { lang ->
          val isSelected = lang == currentLanguage
          val rowShape = RoundedCornerShape(14.dp)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(rowShape)
              .background(
                if (isSelected) {
                  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                } else {
                  MaterialTheme.colorScheme.surface
                },
              )
              .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = if (isSelected) {
                  MaterialTheme.colorScheme.primary
                } else {
                  MaterialTheme.colorScheme.outlineVariant
                },
                shape = rowShape,
              )
              .clickable {
                onSelectLanguage(lang)
                onDismiss()
              }
              .padding(horizontal = 14.dp, vertical = 12.dp)
              .testTag("lang_item_${lang.code}"),
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
                      MaterialTheme.colorScheme.primary
                    } else {
                      MaterialTheme.colorScheme.secondaryContainer
                    },
                  ),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Filled.Language,
                  contentDescription = null,
                  tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                  } else {
                    MaterialTheme.colorScheme.secondary
                  },
                  modifier = Modifier.size(20.dp),
                )
              }
              Column {
                Text(
                  text = lang.nativeName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = lang.englishName,
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
        }
        item(key = "lang_bottom_spacer") {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

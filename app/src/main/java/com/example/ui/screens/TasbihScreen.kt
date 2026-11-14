package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.localization.LocalAppStrings
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.viewmodel.SalatulWaqtUiState
import com.example.ui.viewmodel.tasbihPresets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
  state: SalatulWaqtUiState,
  onIncrement: () -> Unit,
  onReset: () -> Unit,
  onSelectPreset: (Int) -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  val strings = LocalAppStrings.current
  val presetIdx = state.preferences.tasbihPresetIndex.coerceIn(tasbihPresets.indices)
  val preset = tasbihPresets[presetIdx]
  val presetTitle = strings.tasbihPresetTitles.getOrElse(presetIdx) { preset.title }
  val count = state.preferences.tasbihCount
  val haptic = LocalHapticFeedback.current

  val progress = (count.toFloat() / preset.target.toFloat()).coerceIn(0f, 1f)
  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = spring(stiffness = Spring.StiffnessLow),
    label = "tasbih_ring",
  )

  val primaryColor = MaterialTheme.colorScheme.primary
  val trackColor = MaterialTheme.colorScheme.primaryContainer

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = strings.digitalTasbihTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("tasbih_back_btn"),
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
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          tasbihPresets.forEachIndexed { idx, item ->
            val chipTitle = strings.tasbihPresetTitles.getOrElse(idx) { item.title }
            FilterChip(
              selected = idx == presetIdx,
              onClick = { onSelectPreset(idx) },
              label = { Text("$chipTitle (${item.target})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("tasbih_preset_$idx"),
            )
          }
        }

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(horizontal = 20.dp),
        ) {
          Text(
            text = preset.arabicText,
            fontFamily = AmiriFontFamily,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
          )
          Text(
            text = presetTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
          )
        }

        Box(
          modifier = Modifier
            .size(236.dp)
            .clip(CircleShape)
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onIncrement()
            }
            .testTag("tasbih_tap_circle"),
          contentAlignment = Alignment.Center,
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            drawCircle(
              color = trackColor,
              radius = (size.minDimension - stroke) / 2f,
              style = Stroke(width = stroke),
            )
            drawArc(
              color = primaryColor,
              startAngle = -90f,
              sweepAngle = animatedProgress * 360f,
              useCenter = false,
              style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
          }

          Column(
            modifier = Modifier
              .size(186.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = count.toString(),
              style = MaterialTheme.typography.displayLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("tasbih_count_text"),
            )
            Text(
              text = "${strings.targetPrefix} ${preset.target}",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
              imageVector = Icons.Filled.TouchApp,
              contentDescription = strings.tapToCountCd,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
          }
        }

        OutlinedButton(
          onClick = onReset,
          border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("tasbih_reset_btn"),
        ) {
          Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(strings.resetZeroBtn)
        }
      }
    }
  }
}

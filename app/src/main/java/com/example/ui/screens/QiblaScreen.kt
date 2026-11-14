package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.localization.AppStrings
import com.example.domain.localization.LocalAppStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.SalatulWaqtUiState
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
  state: SalatulWaqtUiState,
  onStartCompass: () -> Unit,
  onStopCompass: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  DisposableEffect(Unit) {
    onStartCompass()
    onDispose { onStopCompass() }
  }

  val strings = LocalAppStrings.current
  val qiblaDeg = state.qiblaBearingDeg.toFloat()
  val relativeQibla = ((qiblaDeg - state.deviceAzimuthDeg + 360f) % 360f)
  val isAligned = state.hasCompassSensor && (relativeQibla <= 6f || relativeQibla >= 354f)

  val smoothRotation by animateFloatAsState(
    targetValue = if (state.hasCompassSensor) -state.deviceAzimuthDeg else 0f,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "compass_dial_rotation",
  )

  val primaryColor = MaterialTheme.colorScheme.primary
  val outlineColor = MaterialTheme.colorScheme.outline
  val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
  val onSurfaceColor = MaterialTheme.colorScheme.onSurface
  val errorColor = MaterialTheme.colorScheme.error
  val compassDesc = stringResource(id = R.string.cd_qibla_compass)
  val dirName = directionNameLocalized(qiblaDeg, strings)

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = strings.qiblaCompassTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "${strings.qiblaFromPrefix} ${state.preferences.location.name}",
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
              .testTag("qibla_back_btn"),
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
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        val statusShape = MaterialTheme.shapes.large
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              color = if (isAligned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
              shape = statusShape,
            )
            .border(
              1.75.dp,
              if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
              statusShape,
            )
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = if (isAligned) strings.facingQiblaMsg else "${strings.qiblaDirectionFromPrefix} ${state.preferences.location.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "${qiblaDeg.roundToInt()}° ${strings.fromNorthLabel} ($dirName)  •  ${strings.distanceLabel} ~${state.distanceToKaabaKm.roundToInt()} ${strings.kmUnit}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
        }

        Box(
          modifier = Modifier
            .fillMaxWidth(0.76f)
            .aspectRatio(1f)
            .semantics { contentDescription = compassDesc },
          contentAlignment = Alignment.Center,
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
              color = surfaceVariantColor,
              radius = radius * 0.94f,
              center = center,
              style = Stroke(width = 11.dp.toPx()),
            )
            drawCircle(
              color = if (isAligned) primaryColor else outlineColor.copy(alpha = 0.7f),
              radius = radius * 0.88f,
              center = center,
              style = Stroke(width = 2.5.dp.toPx()),
            )

            rotate(degrees = smoothRotation, pivot = center) {
              for (deg in 0 until 360 step 10) {
                val rad = Math.toRadians(deg.toDouble() - 90.0)
                val isMajor = deg % 90 == 0
                val startR = radius * if (isMajor) 0.72f else 0.80f
                val endR = radius * 0.88f
                val p1 = Offset(
                  center.x + (startR * cos(rad)).toFloat(),
                  center.y + (startR * sin(rad)).toFloat(),
                )
                val p2 = Offset(
                  center.x + (endR * cos(rad)).toFloat(),
                  center.y + (endR * sin(rad)).toFloat(),
                )
                drawLine(
                  color = if (deg == 0) errorColor else onSurfaceColor.copy(alpha = if (isMajor) 0.85f else 0.35f),
                  start = p1,
                  end = p2,
                  strokeWidth = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx(),
                  cap = StrokeCap.Round,
                )
              }

              rotate(degrees = qiblaDeg, pivot = center) {
                val needlePath = Path().apply {
                  moveTo(center.x, center.y - radius * 0.68f)
                  lineTo(center.x - 14.dp.toPx(), center.y + 10.dp.toPx())
                  lineTo(center.x, center.y)
                  lineTo(center.x + 14.dp.toPx(), center.y + 10.dp.toPx())
                  close()
                }
                drawPath(
                  path = needlePath,
                  color = if (isAligned) primaryColor else GoldAccent,
                )
                drawCircle(
                  color = primaryColor,
                  radius = 12.dp.toPx(),
                  center = Offset(center.x, center.y - radius * 0.74f),
                )
              }
            }

            drawCircle(
              color = primaryColor,
              radius = 8.dp.toPx(),
              center = center,
            )
          }

          Column(
            modifier = Modifier.padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Icon(
              imageVector = Icons.Filled.Explore,
              contentDescription = null,
              tint = GoldAccent,
              modifier = Modifier.size(22.dp),
            )
            Text(
              text = strings.qiblaNeedleLabel,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }

        if (!state.hasCompassSensor) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
              .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
              .padding(14.dp),
          ) {
            Text(
              text = String.format(
                strings.compassUnavailableFormat,
                qiblaDeg.roundToInt(),
                dirName,
              ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        } else {
          Text(
            text = strings.compassInstruction,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

private fun directionNameLocalized(deg: Float, strings: AppStrings): String {
  val dirs = strings.directions8
  if (dirs.size < 8) return ""
  val norm = ((deg % 360f) + 360f) % 360f
  return when {
    abs(norm - 0f) < 22.5f || norm > 337.5f -> dirs[0]
    norm < 67.5f -> dirs[1]
    norm < 112.5f -> dirs[2]
    norm < 157.5f -> dirs[3]
    norm < 202.5f -> dirs[4]
    norm < 247.5f -> dirs[5]
    norm < 292.5f -> dirs[6]
    else -> dirs[7]
  }
}

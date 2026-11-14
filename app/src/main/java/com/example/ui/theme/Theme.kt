package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldBright,
  onPrimary = Color.White,
  primaryContainer = EmeraldPrimary,
  onPrimaryContainer = Color.White,
  secondary = GoldLight,
  onSecondary = EmeraldDeep,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = EmeraldSoft,
  tertiary = NaflTealDark,
  onTertiary = EmeraldDeep,
  tertiaryContainer = NaflBgDark,
  onTertiaryContainer = NaflTealDark,
  background = EmeraldNightBg,
  onBackground = InkPrimaryDark,
  surface = EmeraldDarkSurface,
  onSurface = InkPrimaryDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = InkSecondaryDark,
  outline = Color(0xFF2D493E),
  outlineVariant = Color(0xFF1F362D),
  error = ForbiddenRedDark,
  onError = ForbiddenBgDark,
  errorContainer = ForbiddenBgDark,
  onErrorContainer = ForbiddenRedDark,
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = Color.White,
  primaryContainer = SoftMintContainer,
  onPrimaryContainer = EmeraldDeep,
  secondary = ClassicCharcoalSoft,
  onSecondary = Color.White,
  secondaryContainer = ClassicCharcoalTint,
  onSecondaryContainer = ClassicCharcoal,
  tertiary = NaflGreenLight,
  onTertiary = Color.White,
  tertiaryContainer = NaflBgLight,
  onTertiaryContainer = EmeraldDeep,
  background = PureWhite,
  onBackground = InkPrimaryLight,
  surface = PureWhite,
  onSurface = InkPrimaryLight,
  surfaceVariant = SoftMintSurface,
  onSurfaceVariant = InkSecondaryLight,
  outline = SoftMintOutline,
  outlineVariant = SoftMintBorder,
  error = ForbiddenRedLight,
  onError = Color.White,
  errorContainer = ForbiddenBgLight,
  onErrorContainer = ForbiddenRedLight,
)

val AppShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun SalatulWaqtTheme(
  themeMode: ThemeMode = ThemeMode.LIGHT,
  content: @Composable () -> Unit,
) {
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }

  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    shapes = AppShapes,
    content = content,
  )
}

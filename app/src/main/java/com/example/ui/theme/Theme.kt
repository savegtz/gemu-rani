package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BongoDarkColorScheme =
  darkColorScheme(
    primary = NeonGold,
    onPrimary = DarkBgMain,
    primaryContainer = BrightAmber,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = DarkBgMain,
    secondaryContainer = TanzaniteBlue,
    onSecondaryContainer = TextPrimary,
    tertiary = AfricanEmerald,
    onTertiary = DarkBgMain,
    background = DarkBgMain,
    onBackground = TextPrimary,
    surface = DarkBgCard,
    onSurface = TextPrimary,
    surfaceVariant = DarkBgCardElevated,
    onSurfaceVariant = TextSecondary,
    error = CrimsonFire,
    onError = TextPrimary,
    outline = DarkBorder,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = BongoDarkColorScheme,
    typography = Typography,
    content = content,
  )
}


package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AetherColorScheme = darkColorScheme(
    primary = AetherIndigo,
    onPrimary = Color.White,
    primaryContainer = ImmersivePurpleDeep,
    onPrimaryContainer = AetherIndigo,
    secondary = AetherCyan,
    onSecondary = Color.Black,
    secondaryContainer = ImmersivePurplePill,
    onSecondaryContainer = ImmersiveTextPrimary,
    tertiary = AetherPink,
    onTertiary = Color.White,
    background = ImmersiveBackground,
    onBackground = ImmersiveTextPrimary,
    surface = ImmersiveSurface,
    onSurface = ImmersiveTextPrimary,
    surfaceVariant = ImmersiveSurfaceVariant,
    onSurfaceVariant = ImmersiveTextSecondary,
    outline = ImmersiveCardBorder
)

@Composable
fun AetherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AetherColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun KubeFlowTheme(
    content: @Composable () -> Unit
) {
    AetherTheme(content = content)
}

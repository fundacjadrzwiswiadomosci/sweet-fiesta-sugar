package com.Tahpesa8.Curqer.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SweetColorScheme = lightColorScheme(
    primary = CandyPink,
    onPrimary = TextOnAccent,
    primaryContainer = SurfaceTint,
    onPrimaryContainer = TextPrimary,
    secondary = GrapePurple,
    onSecondary = TextOnAccent,
    secondaryContainer = PuffGrape,
    onSecondaryContainer = TextPrimary,
    tertiary = StarGold,
    onTertiary = TextPrimary,
    tertiaryContainer = PuffLemon,
    onTertiaryContainer = TextPrimary,
    background = BackgroundBase,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSunken,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceTint,
    outlineVariant = DisabledSurface,
    error = BerryRed,
    onError = TextOnAccent,
    errorContainer = PuffStrawberry,
    onErrorContainer = TextPrimary,
    scrim = SplashDeeper
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SweetColorScheme,
        typography = AppTypography,
        content = content
    )
}

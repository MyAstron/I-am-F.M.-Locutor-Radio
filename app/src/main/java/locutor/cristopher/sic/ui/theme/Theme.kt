package locutor.cristopher.sic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CockpitDarkColorScheme = darkColorScheme(
    primary = CockpitPrimary,
    onPrimary = CockpitOnPrimary,
    primaryContainer = CockpitPrimaryContainer,
    onPrimaryContainer = CockpitOnPrimaryContainer,
    secondary = CockpitSecondary,
    onSecondary = CockpitOnSecondary,
    secondaryContainer = CockpitSecondaryContainer,
    onSecondaryContainer = CockpitOnSecondaryContainer,
    tertiary = CockpitTertiary,
    onTertiary = CockpitOnTertiary,
    tertiaryContainer = CockpitTertiaryContainer,
    onTertiaryContainer = CockpitOnTertiaryContainer,
    error = CockpitError,
    onError = CockpitOnError,
    errorContainer = CockpitErrorContainer,
    onErrorContainer = CockpitOnErrorContainer,
    background = CockpitSurface,
    onBackground = CockpitOnSurface,
    surface = CockpitSurface,
    onSurface = CockpitOnSurface,
    surfaceVariant = CockpitSurfaceContainerHigh,
    onSurfaceVariant = CockpitOnSurfaceVariant,
    outline = CockpitOutline,
    outlineVariant = CockpitOutlineVariant
)

@Composable
fun IAmFmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CockpitDarkColorScheme,
        typography = CockpitTypography,
        content = content
    )
}

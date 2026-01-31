package com.napse.qcmjuridique.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BleuJuridique,
    onPrimary = Color.White,
    primaryContainer = BleuJuridiqueClair,
    onPrimaryContainer = Color.White,
    
    secondary = GrisNeutre,
    onSecondary = Color.White,
    secondaryContainer = GrisNeutreClair,
    onSecondaryContainer = Color.White,
    
    tertiary = VertValidation,
    onTertiary = Color.White,
    tertiaryContainer = VertValidationClair,
    onTertiaryContainer = Color.White,
    
    error = RougeErreur,
    onError = Color.White,
    errorContainer = RougeErreurClair,
    onErrorContainer = Color.White,
    
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

private val DarkColorScheme = darkColorScheme(
    primary = BleuJuridiqueSombre,
    onPrimary = BleuJuridiqueFonce,
    primaryContainer = BleuJuridique,
    onPrimaryContainer = Color.White,
    
    secondary = GrisNeutreClair,
    onSecondary = GrisNeutreFonce,
    secondaryContainer = GrisNeutre,
    onSecondaryContainer = Color.White,
    
    tertiary = VertValidationSombre,
    onTertiary = VertValidationFonce,
    tertiaryContainer = VertValidation,
    onTertiaryContainer = Color.White,
    
    error = RougeErreurSombre,
    onError = RougeErreurFonce,
    errorContainer = RougeErreur,
    onErrorContainer = Color.White,
    
    background = Color(0xFF10131C),
    onBackground = Color(0xFFE6E1E5),
    
    surface = SurfaceSombre,
    onSurface = OnSurfaceSombre,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

@Composable
fun QCMJuridiqueNapseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
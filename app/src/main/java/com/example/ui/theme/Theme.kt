package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6B1B36),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = NightSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E3A6D),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = NightTertiary,
    onTertiary = Color.Black,
    background = NightDark,
    onBackground = LightText,
    surface = NightSurface,
    onSurface = LightText,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = Color(0xFFDFE4EA)
)

private val LightColorScheme = lightColorScheme(
    primary = BubblegumPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDE5),
    onPrimaryContainer = Color(0xFF5A0025),
    secondary = SkyBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E6FF),
    onSecondaryContainer = Color(0xFF002F6C),
    tertiary = MintGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC7F8DA),
    onTertiaryContainer = Color(0xFF004018),
    background = SoftCream,
    onBackground = DarkText,
    surface = PlayfulSurface,
    onSurface = DarkText,
    surfaceVariant = Color(0xFFEDE8F5),
    onSurfaceVariant = Color(0xFF4A4B54)
)

val KidShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent joyful kid colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = KidShapes,
        typography = Typography,
        content = content
    )
}

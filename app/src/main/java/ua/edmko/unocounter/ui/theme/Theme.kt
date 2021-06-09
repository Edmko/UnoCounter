package ua.edmko.unocounter.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Color.Red,
    secondary = Color.Red,
    onSecondary = Color.Black,
    surface = Color.Black,
    onSurface = Color.White,
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Color.Red,
    secondary = Color.Red,
    onSecondary = Color.Black,
    surface = Color.Black,
    onSurface = Color.White
)

@Composable
fun UNOcounterTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
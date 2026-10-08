package com.example.gestionalmacenpda.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



private val Gris800 = Color(0xFF37474F)
private val Gris600 = Color(0xFF546E7A)
private val Gris100 = Color(0xFFECEFF1)
private val Blanco = Color(0xFFFFFFFF)
private val GrisSuperficie = Color(0xFFCFD8DC)
private val RojoError = Color(0xFFC62828)

private val EsquemaClaro = lightColorScheme(
    primary = Gris800,
    onPrimary = Blanco,
    primaryContainer = GrisSuperficie,
    onPrimaryContainer = Gris800,
    secondary = Gris600,
    onSecondary = Blanco,
    background = Gris100,
    onBackground = Color(0xFF212121),
    surface = Blanco,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E7EA),
    onSurfaceVariant = Color(0xFF37474F),
    error = RojoError,
    onError = Blanco,
    outline = Color(0xFF90A4AE)
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFB0BEC5),
    onPrimary = Color(0xFF1C2A30),
    primaryContainer = Color(0xFF37474F),
    onPrimaryContainer = Color(0xFFECEFF1),
    secondary = Color(0xFF90A4AE),
    onSecondary = Color(0xFF1C2A30),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF263238),
    onSurfaceVariant = Color(0xFFB0BEC5),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF7F0000),
    outline = Color(0xFF546E7A)
)

@Composable
fun GestionAlmacenPDATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography(
            titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            bodyMedium = TextStyle(fontSize = 14.sp),
            bodySmall = TextStyle(fontSize = 12.sp),
            labelLarge = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            labelSmall = TextStyle(fontSize = 11.sp)
        ),
        content = content
    )
}
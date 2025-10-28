package com.example.sucustore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de colores personalizada y explícita para SucuStore
private val SucuStoreColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // Verde SucuStore
    onPrimary = Color.White, // Texto sobre el verde
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    tertiary = Color(0xFFA5D6A7),
    background = Color.White, // Fondo general
    onBackground = Color.Black, // Texto sobre el fondo
    surface = Color.White, // Color de las "superficies" como Cards y TextFields
    onSurface = Color.Black, // ¡¡CLAVE!! Texto sobre esas superficies (el que se escribe)
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color.Black,
    outline = Color.Gray // Color del borde de los OutlinedTextFields
)

@Composable
fun SucuStoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SucuStoreColorScheme,
        typography = Typography, // Ahora sí, viene de Type.kt
        shapes = Shapes,       // Ahora sí, viene de Shape.kt
        content = content
    )
}

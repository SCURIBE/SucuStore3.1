package com.example.sucustore.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    navController: NavController,
    route: String // La ruta a la que se navegará después de la carga
) {
    var startAnimation by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2500), // La barra se llena en 2.5s
        label = "loading_progress"
    )

    // El efecto se lanza cuando la pantalla aparece
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000) // La pantalla dura 3 segundos en total
        navController.navigate(route) {
            popUpTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Login, // Un icono más apropiado
            contentDescription = "Iniciando Sesión",
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Iniciando Sesión...", 
            style = MaterialTheme.typography.headlineSmall, // ¡Mejor fuente!
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(32.dp))
        LinearProgressIndicator(
            progress = { progress }, // ¡Animación Progresiva!
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}
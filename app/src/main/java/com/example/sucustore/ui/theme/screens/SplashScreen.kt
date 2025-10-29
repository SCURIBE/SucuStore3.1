package com.example.sucustore.ui.theme.screens // Tu package se queda igual

// 1. AÑADIMOS LAS IMPORTACIONES NECESARIAS
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sucustore.R // Importa los recursos (drawable) de tu app
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    // 2. MODIFICAMOS EL PARÁMETRO: AHORA RECIBE UNA FUNCIÓN
    //    En lugar de todo el NavController, solo necesita saber qué hacer cuando termina.
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animaremos la transparencia (alpha) de 0f (invisible) a 1f (visible).
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000 // La animación de aparición durará 2 segundos
        ),
        label = "splash_alpha_anim" // Etiqueta para depuración
    )

    // LaunchedEffect se ejecuta una vez cuando la pantalla aparece.
    LaunchedEffect(key1 = true) {
        startAnimation = true       // Inicia la animación
        delay(3000)                 // La pantalla se queda un total de 3 segundos
        onSplashFinished()          // 3. LLAMA A LA FUNCIÓN PARA NAVEGAR. No decidimos el destino aquí.
    }

    // --- DISEÑO DE LA PANTALLA (TU LOGO) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .alpha(alphaAnim),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(

        painter = painterResource(id = R.drawable.logo),

            contentDescription = "Logo de SuccuStore",
            modifier = Modifier.size(120.dp) // Ajusta el tamaño como necesites
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SuccuStore",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

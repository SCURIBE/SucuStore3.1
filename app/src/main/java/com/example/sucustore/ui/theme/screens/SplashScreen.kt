package com.example.sucustore.ui.theme.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sucustore.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animación para la aparición gradual del contenido
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000
        ),
        label = "splash_alpha_anim"
    )

    // Efecto para iniciar la animación y el temporizador de navegación
    LaunchedEffect(key1 = true) {
        startAnimation = true // Inicia la animación de aparición
        delay(3000) // Duración total de la pantalla de bienvenida
        onSplashFinished() // Notifica que el splash ha terminado
    }

    // Contenido de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .alpha(alphaAnim), // Aplica la animación de aparición a todo el contenido
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de SuccuStore",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de la App
        Text(
            text = "SuccuStore",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Animación de puntos de carga
        LoadingDotsAnimation()
    }
}

/**
 * Un Composable que muestra tres puntos que se mueven verticalmente en un bucle infinito.
 */
@Composable
fun LoadingDotsAnimation(
    dotSize: Dp = 12.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 8.dp,
    travelDistance: Dp = 10.dp
) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            // Retraso para que cada punto empiece en un momento diferente, creando efecto de ola.
            delay(index * 150L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0f at 0 // Empieza abajo
                        1f at 600 // Sube a la mitad del tiempo
                        0f at 1200 // Vuelve a bajar al final
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // Dibuja los puntos en una fila
    Row(
        modifier = Modifier.padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    // --- LÍNEA CORREGIDA ---
                    // Multiplica los valores numéricos (Float) y convierte el resultado final a Dp.
                    .offset(y = (-animatable.value * travelDistance.value).dp)
                    .background(
                        color = dotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

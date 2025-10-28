package com.example.sucustore.ui.theme.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sucustore.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1500) // simula carga inicial
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido a SucuStore 🌿") },
                actions = {
                    TextButton(onClick = {
                        authViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Text("Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->

        Crossfade(targetState = isLoading, label = "homeCrossfade") { loading ->
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hola, ${currentUser?.name ?: "usuario"} 👋",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { /* navController.navigate("products") */ }, // TODO: Implementar pantalla de productos
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver productos 🌱")
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("cart") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ir al carrito 🛒")
                    }
                }
            }
        }
    }
}
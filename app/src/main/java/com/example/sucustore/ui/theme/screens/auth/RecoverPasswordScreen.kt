package com.example.sucustore.ui.theme.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sucustore.viewmodel.AuthViewModel

@Composable
fun RecoverPasswordScreen(
    authViewModel: AuthViewModel,
    onEmailVerified: (String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val error by authViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Recuperar contraseña", fontSize = 28.sp)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo registrado") },
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.verifyEmailForReset(email) { exists ->
                    if (exists) onEmailVerified(email)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Verificar correo")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}

package com.example.sucustore.ui.theme.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sucustore.viewmodel.AuthViewModel

@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel,
    email: String,
    onPasswordResetComplete: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val error by authViewModel.error.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear nueva contraseña", fontSize = 28.sp)

        Spacer(Modifier.height(24.dp))

        // CONTRASEÑA
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // REPETIR CONTRASEÑA
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Repetir contraseña") },
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirm = !showConfirm }) {
                    Icon(
                        imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.resetPassword(email, password, confirm) { success ->
                    if (success) onPasswordResetComplete()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar nueva contraseña")
        }
    }
}

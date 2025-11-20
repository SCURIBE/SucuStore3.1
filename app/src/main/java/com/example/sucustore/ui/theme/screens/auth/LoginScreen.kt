package com.example.sucustore.ui.theme.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sucustore.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit   // 👈 NUEVO
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val error by authViewModel.error.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión 🔐",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation =
                if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector =
                            if (passwordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // LOGIN BUTTON
        Button(
            onClick = {
                if (!email.contains("@")) {
                    authViewModel.setError("Correo inválido")
                    return@Button
                }
                if (password.isBlank()) {
                    authViewModel.setError("Debe ingresar su contraseña")
                    return@Button
                }
                authViewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Ingresar", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // REGISTRO
        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // OLVIDÉ MI CONTRASEÑA 👇🔥
        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

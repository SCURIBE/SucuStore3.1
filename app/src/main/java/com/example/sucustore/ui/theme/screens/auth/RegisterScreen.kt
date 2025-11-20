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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sucustore.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel(),
    onRegisterComplete: () -> Unit,
    onLoginClick: () -> Unit
) {
    val formState by authViewModel.formState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) onRegisterComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear Cuenta",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // NOMBRE
        OutlinedTextField(
            value = formState.name,
            onValueChange = { authViewModel.onNameChange(it) },
            isError = formState.nameError != null,
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        formState.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL
        OutlinedTextField(
            value = formState.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            isError = formState.emailError != null,
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        formState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(16.dp))

        // CONTRASEÑA
        OutlinedTextField(
            value = formState.password,
            onValueChange = { authViewModel.onPasswordChange(it) },
            isError = formState.passwordError != null,
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
        formState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(16.dp))

        // REPETIR CONTRASEÑA
        OutlinedTextField(
            value = formState.confirmPassword,
            onValueChange = { authViewModel.onConfirmPasswordChange(it) },
            isError = formState.confirmPasswordError != null,
            label = { Text("Repetir Contraseña") },
            visualTransformation =
                if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector =
                            if (confirmPasswordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        formState.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { authViewModel.register() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Registrarse", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onLoginClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

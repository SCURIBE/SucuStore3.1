package com.example.sucustore.ui.theme.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // ---> ¡ASEGÚRATE DE TENER ESTA IMPORTACIÓN!
import com.example.sucustore.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    // Inyectamos el ViewModel automáticamente.
    authViewModel: AuthViewModel = viewModel(),
    onRegisterComplete: () -> Unit,
    onLoginClick: () -> Unit
) {
    // 1. LA UI YA NO TIENE SUS PROPIOS ESTADOS. AHORA OBSERVA LOS DEL VIEWMODEL.
    val formState by authViewModel.formState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Este observador se queda igual. Navega cuando el registro es exitoso.
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onRegisterComplete()
        }
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(48.dp))

        // --- CAMPO NOMBRE ---
        OutlinedTextField(
            value = formState.name, // El valor viene del ViewModel
            onValueChange = { authViewModel.onNameChange(it) }, // La UI notifica al ViewModel del cambio
            label = { Text("Nombre Completo") },
            isError = formState.nameError != null, // Se pone rojo si el ViewModel dice que hay un error
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        // Muestra el mensaje de error que viene del ViewModel
        formState.nameError?.let { errorMsg ->
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO CORREO ---
        OutlinedTextField(
            value = formState.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            label = { Text("Correo Electrónico") },
            isError = formState.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(8.dp)
        )
        formState.emailError?.let { errorMsg ->
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO CONTRASEÑA ---
        OutlinedTextField(
            value = formState.password,
            onValueChange = { authViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            isError = formState.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp)
        )
        formState.passwordError?.let { errorMsg ->
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- BOTÓN DE REGISTRO ---
        Button(
            // 2. AHORA LA UI SÓLO LLAMA A `register()` SIN PASARLE NADA.
            onClick = { authViewModel.register() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Registrarse", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onLoginClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

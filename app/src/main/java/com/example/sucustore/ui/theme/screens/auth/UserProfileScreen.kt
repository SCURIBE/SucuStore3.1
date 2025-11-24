package com.example.sucustore.ui.theme.screens.auth

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sucustore.viewmodel.AuthViewModel
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onGoToOrders: () -> Unit,
    onGoToExternalApi: () -> Unit,   // 👈 NUEVO
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    // Si no hay usuario logueado, muestro solo un mensaje
    if (currentUser == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mi perfil") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay usuario autenticado")
            }
        }
        return
    }

    val context = LocalContext.current

    // ¿Tenemos permiso de cámara?
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // --------- ESTADOS ---------
    var name by remember { mutableStateOf(currentUser!!.name) }
    val email = currentUser!!.email
    var description by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var feedback by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    // Foto de perfil actual que se ve en pantalla
    var profileImageModel by remember { mutableStateOf<Any?>(null) }

    // Para la foto que se está eligiendo pero aún no se confirma
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var showConfirmPhotoDialog by remember { mutableStateOf(false) }

    // --------- LAUNCHERS PARA GALERÍA Y SELFIE ---------
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            pendingImageUri = uri
            showConfirmPhotoDialog = true
        }
    }

    val selfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            val uri = saveBitmapAndGetUri(context, bmp)
            pendingImageUri = uri
            showConfirmPhotoDialog = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            // Si el usuario acepta, abrimos la cámara
            selfieLauncher.launch(null)
        }
    }

    // --------- DIÁLOGO: SELECCIONAR ORIGEN DE FOTO ---------
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Actualizar foto de perfil") },
            text = { Text("Elige cómo quieres actualizar tu foto de perfil.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Desde galería")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        if (hasCameraPermission) {
                            // Ya tiene permiso → abre cámara
                            selfieLauncher.launch(null)
                        } else {
                            // No tiene permiso → lo pedimos
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Text("Tomar selfie")
                }
            }
        )
    }

    // --------- DIÁLOGO: CONFIRMAR NUEVA FOTO ---------
    if (showConfirmPhotoDialog && pendingImageUri != null) {
        AlertDialog(
            onDismissRequest = { showConfirmPhotoDialog = false },
            title = { Text("Confirmar foto") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = pendingImageUri,
                        contentDescription = "Vista previa foto",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("¿Quieres usar esta imagen como tu foto de perfil?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileImageModel = pendingImageUri
                        showConfirmPhotoDialog = false
                        pendingImageUri = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmPhotoDialog = false
                        pendingImageUri = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --------- PANTALLA PRINCIPAL ---------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // ---------- FOTO DE PERFIL ----------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    if (profileImageModel != null) {
                        AsyncImage(
                            model = profileImageModel,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Foto de perfil",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Foto de perfil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                OutlinedButton(onClick = { showSourceDialog = true }) {
                    Text("Cambiar foto de perfil")
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- DATOS DE LA CUENTA ----------
            Text(
                "Datos de la cuenta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                label = { Text("Correo") }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") },
                placeholder = { Text("Algo sobre ti o tus plantas") }
            )

            Spacer(Modifier.height(24.dp))

            // ---------- CAMBIO DE CONTRASEÑA ----------
            Text(
                "Cambiar contraseña (opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(8.dp))

            // Nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nueva contraseña") },
                visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showNewPassword = !showNewPassword }) {
                        Icon(
                            imageVector = if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // Repetir contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Repetir contraseña") },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ---------- GUARDAR CAMBIOS ----------
            Button(
                onClick = {
                    authViewModel.updateProfile(
                        newName = name,
                        newPassword = newPassword.ifBlank { null },
                        confirmPassword = confirmPassword.ifBlank { null }
                    ) { success, message ->
                        isError = !success
                        feedback = message
                        if (success) {
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            if (feedback != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = feedback!!,
                    color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---------- ACCIONES EXTRA ----------
            Button(
                onClick = onGoToOrders,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver historial de pedidos 🧾")
            }

            Spacer(Modifier.height(12.dp))

            // ⭐ BOTÓN API EXTERNA
            Button(
                onClick = onGoToExternalApi,
                modifier = Modifier.fillMaxWidth()
            ) {
                (Text("🌿 Tips y noticias de jardinería", style = MaterialTheme.typography.headlineSmall)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * Guarda un Bitmap en caché y devuelve su Uri.
 */
private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return Uri.fromFile(file)
}

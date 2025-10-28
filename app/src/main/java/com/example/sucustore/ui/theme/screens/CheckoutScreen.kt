package com.example.sucustore.ui.theme.screens


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    total: Double,
    onConfirm: suspend (address: String) -> Long, // devuelve orderId
    onDone: () -> Unit
) {
    var address by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* ignoramos el resultado para simplificar */ }
    )

    Column(Modifier.padding(16.dp)) {
        Text("Checkout", style = MaterialTheme.typography.headlineSmall)
        Text("Total a pagar: $${"%.0f".format(total)}")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección de envío") }, modifier = Modifier.fillMaxWidth(), isError = error != null)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (address.isBlank()) { error = "La dirección es obligatoria"; return@Button }
            error = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            scope.launch {
                val orderId = onConfirm(address)
                // Notificación se envía en MainActivity tras confirmar (para tener Context)
                onDone()
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Confirmar pedido") }
    }
}
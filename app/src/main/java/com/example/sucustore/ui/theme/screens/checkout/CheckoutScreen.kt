package com.example.sucustore.ui.theme.screens.checkout

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {

    // --------- ESTADOS DEL FORMULARIO ---------
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Errores
    var cardHolderError by remember { mutableStateOf<String?>(null) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var expiryDateError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }

    // Contexto para usar el Vibrator
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Pago") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al carrito"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // --------- NOMBRE DEL TITULAR ---------
            OutlinedTextField(
                value = cardHolder,
                onValueChange = {
                    cardHolder = it
                    cardHolderError = null
                },
                label = { Text("Titular de la tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = cardHolderError != null
            )
            cardHolderError?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --------- NÚMERO DE TARJETA ---------
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    // Solo dejamos dígitos y espacios
                    cardNumber = it.filter { ch -> ch.isDigit() || ch == ' ' }
                    cardNumberError = null
                },
                label = { Text("Número de tarjeta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = cardNumberError != null,
                placeholder = { Text("XXXX XXXX XXXX XXXX") }
            )
            cardNumberError?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                // --------- FECHA DE VENCIMIENTO ---------
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        expiryDate = it
                        expiryDateError = null
                    },
                    label = { Text("MM/AA") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    isError = expiryDateError != null,
                    placeholder = { Text("09/27") }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // --------- CVV ---------
                OutlinedTextField(
                    value = cvv,
                    onValueChange = {
                        // Solo dígitos
                        cvv = it.filter { ch -> ch.isDigit() }
                        cvvError = null
                    },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    isError = cvvError != null,
                    placeholder = { Text("***") }
                )
            }
            expiryDateError?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            cvvError?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --------- BOTÓN PAGAR (con validación + vibración) ---------
            Button(
                onClick = {
                    var valido = true

                    // Validar titular
                    cardHolderError = when {
                        cardHolder.isBlank() ->
                            "El nombre del titular es obligatorio"
                        cardHolder.any { it.isDigit() } ->
                            "El nombre no debe contener números"
                        cardHolder.length < 5 ->
                            "El nombre es demasiado corto"
                        else -> null
                    }
                    if (cardHolderError != null) valido = false

                    // Validar número de tarjeta
                    val digitsNumber = cardNumber.filter { it.isDigit() }
                    cardNumberError = when {
                        digitsNumber.isBlank() ->
                            "El número de tarjeta es obligatorio"
                        digitsNumber.length != 16 ->
                            "La tarjeta debe tener 16 dígitos"
                        else -> null
                    }
                    if (cardNumberError != null) valido = false

                    // Validar fecha de vencimiento (MM/AA)
                    expiryDateError = when {
                        expiryDate.isBlank() ->
                            "La fecha de vencimiento es obligatoria"
                        !Regex("^(0[1-9]|1[0-2])/\\d{2}$").matches(expiryDate) ->
                            "Formato inválido, usa MM/AA"
                        else -> null
                    }
                    if (expiryDateError != null) valido = false

                    // Validar CVV
                    cvvError = when {
                        cvv.isBlank() ->
                            "El CVV es obligatorio"
                        cvv.length !in 3..4 ->
                            "El CVV debe tener 3 o 4 dígitos"
                        else -> null
                    }
                    if (cvvError != null) valido = false

                    if (!valido) return@Button

                    // Si todo está OK → vibración y navegar
                    vibrateSuccess(context)

                    // Volver al catálogo (o donde quieras)
                    navController.navigate("products") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Pagar")
            }
        }
    }
}

// 👉 Helper reutilizable para vibración “pro”
private fun vibrateSuccess(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Patrón: espera 0ms, vibra 80ms, pausa 50ms, vibra 120ms
        val timings = longArrayOf(0, 80, 50, 120)
        val amplitudes = intArrayOf(
            0,
            VibrationEffect.DEFAULT_AMPLITUDE,
            0,
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
        vibrator.vibrate(effect)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(150L)
    }
}

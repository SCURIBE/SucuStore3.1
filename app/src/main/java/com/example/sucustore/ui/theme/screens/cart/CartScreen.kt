package com.example.sucustore.ui.theme.screens.cart

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sucustore.viewmodel.*
import com.example.sucustore.data.db.entity.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    factory: SucuStoreViewModelFactory,
    onBack: () -> Unit,
    onCheckoutComplete: () -> Unit
) {
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)
    val productViewModel: ProductViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val products by productViewModel.products.collectAsState()

    val context = LocalContext.current

    // Cargar carrito y productos al entrar a la pantalla
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            cartViewModel.loadCart(user.id)
            productViewModel.loadProducts()
        }
    }

    // Mapear productId → Producto
    val productMap = remember(products) { products.associateBy { it.id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu carrito 🛒") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (currentUser != null && cartItems.isNotEmpty()) {
                        TextButton(onClick = {
                            cartViewModel.clearCart(currentUser!!.id)
                        }) {
                            Text("Vaciar")
                        }
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
        ) {

            // Usuario no ha iniciado sesión
            if (currentUser == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Debes iniciar sesión para ver tu carrito.")
                }
                return@Column
            }

            // Carrito vacío
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío 🌵")
                }
                return@Column
            }

            // LISTA DE ÍTEMS
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { item ->
                    val product = productMap[item.productId]

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(product?.name ?: "Producto #${item.productId}")

                                if (product != null) {
                                    Text("Cantidad: ${item.quantity}")
                                    Text("Precio unitario: \$${"%.0f".format(product.price)}")
                                }
                            }

                            IconButton(onClick = { cartViewModel.removeFromCart(item) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Total
            val total = cartItems.sumOf { item ->
                val product = productMap[item.productId]
                (product?.price ?: 0.0) * item.quantity
            }

            Text(
                text = "Total: \$${"%.0f".format(total)}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            // Botón “Comprar ahora”
            Button(
                onClick = {
                    orderViewModel.createOrder(
                        userId = currentUser!!.id,
                        total = total,
                        details = "Pedido con ${cartItems.size} productos"
                    )

                    cartViewModel.clearCart(currentUser!!.id)

                    vibrateSuccess(context)

                    onCheckoutComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comprar ahora 🌿")
            }
        }
    }
}

private fun vibrateSuccess(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

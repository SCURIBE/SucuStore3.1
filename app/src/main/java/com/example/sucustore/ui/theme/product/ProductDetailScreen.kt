package com.example.sucustore.ui.theme.product

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.CartViewModel
import com.example.sucustore.viewmodel.OrderViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit = {},
    onGoToCart: () -> Unit = {},
    factory: SucuStoreViewModelFactory
) {
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()
    var addedToCart by remember { mutableStateOf(false) }

    var clicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (clicked) 1.1f else 1f,
        label = "buttonScale"
    )
    val scope = rememberCoroutineScope()

    // ✅ NUEVO: cantidad seleccionada
    var quantity by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // =================== INFO PRODUCTO ===================
            Column {
                Text(product.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text("💰 Precio: \$${product.price}")
                Spacer(Modifier.height(8.dp))
                Text("📦 Descripción: ${product.description}")

                Spacer(Modifier.height(24.dp))

                // =================== CANTIDAD ===================
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) quantity -= 1
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Restar"
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = {
                            quantity += 1
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Sumar"
                        )
                    }
                }
            }

            // =================== ACCIONES ===================
            Column {
                if (currentUser == null) {
                    Text(
                        "Debes iniciar sesión para comprar productos.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {

                    // ---------- AGREGAR AL CARRITO ----------
                    if (!addedToCart) {
                        Button(
                            onClick = {
                                clicked = true
                                scope.launch {
                                    delay(150)
                                    clicked = false
                                }
                                cartViewModel.addToCart(
                                    userId = currentUser!!.id,
                                    productId = product.id,
                                    quantity = quantity    // ✅ usa cantidad seleccionada
                                )
                                addedToCart = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale)
                        ) {
                            Text("Agregar $quantity al carrito 🛒")
                        }
                    } else {
                        Button(
                            onClick = onGoToCart,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver carrito 🛍️")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ---------- COMPRAR AHORA ----------
                    Button(
                        onClick = {
                            val total = product.price * quantity
                            orderViewModel.createOrder(
                                userId = currentUser!!.id,
                                total = total,
                                details = "$quantity x ${product.name}"   // ✅ detalle correcto
                            )
                            onGoToCart()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Comprar ahora 🌿")
                    }
                }
            }
        }
    }
}

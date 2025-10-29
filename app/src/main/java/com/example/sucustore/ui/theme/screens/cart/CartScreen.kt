package com.example.sucustore.ui.theme.screens.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sucustore.R
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.CartViewModel
import com.example.sucustore.viewmodel.OrderViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    factory: SucuStoreViewModelFactory,
    onBack: () -> Unit = {},
    onCheckoutComplete: () -> Unit
) {
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            cartViewModel.loadCart(user.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu carrito 🛒") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (currentUser != null && cartItems.isNotEmpty() && !showAnimation) {
                        TextButton(onClick = { cartViewModel.clearCart(currentUser!!.id) }) {
                            Text("Vaciar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (showAnimation) {
                PaymentAnimation(onAnimationFinished = {
                    showAnimation = false
                    onCheckoutComplete() // Navega a la pantalla de productos
                })
            } else {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (currentUser == null) {
                        Text("Debes iniciar sesión para ver tu carrito.")
                        return@Column
                    }

                    if (cartItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tu carrito está vacío 🌵")
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cartItems) { item ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Producto ID: ${item.productId}")
                                            Text("Cantidad: ${item.quantity}")
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

                        Spacer(Modifier.height(24.dp))

                        val total = cartItems.sumOf { it.quantity * 1000.0 } // Simula precio
                        AnimatedContent(
                            targetState = total,
                            transitionSpec = { slideInVertically() togetherWith slideOutVertically() },
                            label = "cartTotal"
                        ) { animatedTotal ->
                            Text(
                                text = "Total: \$${"%.0f".format(animatedTotal)}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                orderViewModel.createOrder(
                                    userId = currentUser!!.id,
                                    total = total,
                                    details = "Pedido con ${cartItems.size} productos"
                                )
                                cartViewModel.clearCart(currentUser!!.id)
                                showAnimation = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finalizar compra 🌿")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentAnimation(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.payment_successful))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        restartOnPlay = true,
        speed = 1.5f
    )
    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(progress) {
        if (progress >= 1f) {
            showSuccessMessage = true
            delay(1500) // Show success message for 1.5 seconds
            onAnimationFinished()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!showSuccessMessage) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Pago completado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Pago completado",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
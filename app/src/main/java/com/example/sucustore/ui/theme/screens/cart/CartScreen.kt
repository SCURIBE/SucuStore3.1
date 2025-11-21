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
import com.example.sucustore.viewmodel.*
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
    val productViewModel: ProductViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val products by productViewModel.products.collectAsState()

    var showAnimation by remember { mutableStateOf(false) }

    val productMap = remember(products) { products.associateBy { it.id } }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            cartViewModel.loadCart(user.id)
            productViewModel.loadProducts()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (showAnimation) {
                PaymentAnimation(
                    onAnimationFinished = {
                        showAnimation = false
                        onCheckoutComplete()
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (currentUser == null) {
                        Text("Debes iniciar sesión para ver tu carrito.")
                        return@Column
                    }

                    if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tu carrito está vacío 🌵")
                        }
                    } else {

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cartItems) { item ->
                                val product = productMap[item.productId]

                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(product?.name ?: "Producto #${item.productId}")
                                            Text("Cantidad: ${item.quantity}")
                                            if (product != null) {
                                                Text("Precio unitario: \$${"%.0f".format(product.price)}")
                                            }
                                        }

                                        IconButton(
                                            onClick = { cartViewModel.removeFromCart(item) }
                                        ) {
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

                        val total = cartItems.sumOf { item ->
                            val product = productMap[item.productId]
                            (product?.price ?: 0.0) * item.quantity
                        }

                        AnimatedContent(
                            targetState = total,
                            transitionSpec = {
                                slideInVertically() togetherWith slideOutVertically()
                            },
                            label = "cartTotal"
                        ) { animatedTotal ->
                            Text(
                                text = "Total: \$${"%.0f".format(animatedTotal)}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // BOTÓN COMPRAR AHORA
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
                            Text("Comprar ahora")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentAnimation(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.payment_successful)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        restartOnPlay = true,
        speed = 1.4f
    )

    var showFinalMessage by remember { mutableStateOf(false) }

    LaunchedEffect(progress) {
        if (progress >= 0.6f && !showFinalMessage) {
            showFinalMessage = true
        }
        if (progress >= 1f) {
            delay(1400)
            onAnimationFinished()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        if (!showFinalMessage) {
            // ✔ PRIMER TICKET + MENSAJE
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Compra lista",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(130.dp)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "Compra realizada exitosamente",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

        } else {

            // ✔ ANIMACIÓN FINAL
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Pago completado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "Pago completado",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

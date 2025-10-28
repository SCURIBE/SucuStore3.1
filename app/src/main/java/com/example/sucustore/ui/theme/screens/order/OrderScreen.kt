package com.example.sucustore.ui.theme.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sucustore.data.db.entity.Order
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.OrderViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    factory: SucuStoreViewModelFactory, // ¡AÑADIDO!
    onBack: () -> Unit = {}
) {
    // Obtenemos los ViewModels usando la fábrica
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()
    val orders by orderViewModel.orders.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let {
            orderViewModel.loadOrders(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de pedidos 🧾") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            if (currentUser == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Debes iniciar sesión para ver tus pedidos.")
                }
                return@Column
            }

            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes pedidos registrados 🌵")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🧾 Pedido #${order.id}", style = MaterialTheme.typography.titleMedium)
                                Text("Fecha: ${order.date}")
                                Text("Total: \$${"%.0f".format(order.total)}")
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(8.dp))
                                Text("Detalles: ${order.details}")
                            }
                        }
                    }
                }
            }
        }
    }
}
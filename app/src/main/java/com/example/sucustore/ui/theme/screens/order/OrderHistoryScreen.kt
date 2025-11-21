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
import com.example.sucustore.data.db.entity.Order
import com.example.sucustore.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    isAdmin: Boolean,
    userId: Int?,
    onBack: () -> Unit
) {
    // Elegimos qué flujo observar
    val ordersFlow = if (isAdmin) orderViewModel.allOrders else orderViewModel.orders
    val orders by ordersFlow.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(isAdmin, userId) {
        if (isAdmin) {
            orderViewModel.loadAllOrders()
        } else if (userId != null) {
            orderViewModel.loadOrders(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isAdmin) "Historial de ventas"
                        else "Historial de compras"
                    )
                },
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
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isAdmin)
                        "No hay ventas registradas."
                    else
                        "Aún no has realizado compras."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderItemCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun OrderItemCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Fecha: ${order.date}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: \$${"%.0f".format(order.total)}", style = MaterialTheme.typography.bodyMedium)
            Text("Detalle: ${order.details}", style = MaterialTheme.typography.bodySmall)
            Text("Estado: ${order.status}", style = MaterialTheme.typography.labelMedium)
        }
    }
}

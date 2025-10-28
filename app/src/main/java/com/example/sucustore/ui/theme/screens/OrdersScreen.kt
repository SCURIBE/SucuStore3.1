package com.example.sucustore.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sucustore.data.db.entity.Order
import kotlinx.coroutines.flow.Flow

@Composable
fun OrdersScreen(ordersFlow: Flow<List<Order>>) {
    val orders by ordersFlow.collectAsState(initial = emptyList())
    Column(Modifier.padding(12.dp)) {
        Text("Mis pedidos", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        orders.forEach { o ->
            Text("Orden #${o.id} — $${o.total} — ${o.status}")
        }
    }
}
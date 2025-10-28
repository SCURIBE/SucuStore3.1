package com.example.sucustore.ui.theme.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.ui.components.ProductCard
import kotlinx.coroutines.flow.Flow

@Composable
fun CatalogScreen(
    productsFlow: Flow<List<Product>>,
    onSearch: (String) -> Unit,
    onGoCart: () -> Unit,
    onAdmin: (() -> Unit)? = null,
    addToCart: (Product) -> Unit
) {
    val products by productsFlow.collectAsState(initial = emptyList())
    var q by remember { mutableStateOf("") }

    Column(Modifier.padding(12.dp).animateContentSize()) {
        Row {
            OutlinedTextField(value = q, onValueChange = {
                q = it; onSearch(it)
            }, modifier = Modifier.weight(1f), label = { Text("Buscar") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = onGoCart) { Text("Carrito") }
        }
        onAdmin?.let { Spacer(Modifier.height(8.dp)); Button(onClick = it) { Text("Admin") } }

        Spacer(Modifier.height(12.dp))
        products.forEach { p ->
            ProductCard(product = p, onAddToCart = { addToCart(p) })
            Spacer(Modifier.height(8.dp))
        }
    }
}
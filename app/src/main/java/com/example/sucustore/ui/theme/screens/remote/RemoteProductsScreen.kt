package com.example.sucustore.ui.theme.screens.remote

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
import com.example.sucustore.viewmodel.RemoteProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteProductsScreen(
    onBack: () -> Unit = {}
) {
    val vm: RemoteProductViewModel = viewModel()
    val products by vm.products.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos (Spring Boot)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error al cargar productos remotos.\n${error}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            products.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos remotos disponibles.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = p.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Precio: $${p.price}")
                                Text("Stock: ${p.stock}")
                                Text(p.description)
                            }
                        }
                    }
                }
            }
        }
    }
}

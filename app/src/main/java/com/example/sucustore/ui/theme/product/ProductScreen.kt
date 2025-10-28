package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.ProductViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    factory: SucuStoreViewModelFactory,
    authViewModel: AuthViewModel, // Recibimos el AuthViewModel directamente
    onProductClick: (Product) -> Unit,
    onAddNewProduct: () -> Unit,
    onBack: () -> Unit
) {
    val productViewModel: ProductViewModel = viewModel(factory = factory)
    val products by productViewModel.products.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val isAdmin = currentUser?.role?.name == "ADMIN"

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        // ¡¡TÍTULO DINÁMICO!!
                        text = if (isAdmin) "Gestionar Productos" else currentUser?.name?.let { "Bienvenido, $it" } ?: "Productos", 
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // ¡¡ICONO INTELIGENTE!!
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = if (isAdmin) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = if (isAdmin) "Volver" else "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddNewProduct) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { paddingValues ->
        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("  Cargando productos...", modifier = Modifier.padding(start = 60.dp))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(product = product, onProductClick = onProductClick)
                }
            }
        }
    }
}
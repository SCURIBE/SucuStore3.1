package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.ProductViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    factory: SucuStoreViewModelFactory,
    authViewModel: AuthViewModel,
    navController: NavController,
    onProductClick: (Product) -> Unit,
    onAddNewProduct: () -> Unit,
    onBack: () -> Unit
) {
    val productViewModel: ProductViewModel = viewModel(factory = factory)
    val products by productViewModel.products.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role?.name == "ADMIN"
    val searchText by productViewModel.searchText.collectAsState()

    var productToDelete by remember { mutableStateOf<Product?>(null) }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar \"${productToDelete!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productViewModel.deleteProduct(productToDelete!!)
                        productToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isAdmin) "Gestionar Productos"
                        else currentUser?.name?.let { "Bienvenido(a), $it" } ?: "Productos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = if (isAdmin)
                                Icons.AutoMirrored.Filled.ArrowBack
                            else Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onAddNewProduct) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                        }
                    }
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(Modifier.padding(paddingValues)) {

            OutlinedTextField(
                value = searchText,
                onValueChange = productViewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->

                    ProductCard(
                        product = product,
                        isAdmin = isAdmin,
                        onProductClick = { onProductClick(product) },
                        onEditClick = {
                            navController.navigate("edit_product/${product.id}")
                        }
                    )

                    if (isAdmin) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { productToDelete = product }
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}

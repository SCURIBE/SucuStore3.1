// ProductScreen.kt
package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.CartViewModel
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
    val cartViewModel: CartViewModel = viewModel(factory = factory)

    val products by productViewModel.products.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role?.name == "ADMIN"

    var searchText by remember { mutableStateOf("") }

    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAdmin) "Gestionar productos" else "Catálogo de plantas",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (isAdmin) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                actions = {
                    if (!isAdmin) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Ver carrito"
                            )
                        }

                        IconButton(onClick = { navController.navigate("orders") }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Mis pedidos"
                            )
                        }

                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Mi perfil"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddNewProduct) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Añadir producto"
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar planta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (isAdmin) {
                Button(
                    onClick = { navController.navigate("remote_products") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text("Ver productos del servidor")
                }
                Spacer(Modifier.height(8.dp))
            }

            val filteredProducts = products.filter { product ->
                searchText.isBlank() ||
                        product.name.contains(searchText, ignoreCase = true) ||
                        product.description.contains(searchText, ignoreCase = true)
            }

            if (filteredProducts.isEmpty()) {
                Text("No se encontraron productos con ese criterio.")
            } else {
                val gridState = rememberLazyGridState()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts) { product ->
                        if (isAdmin) {
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product) },
                                isAdmin = true,
                                onEditClick = { onProductClick(product) },
                                onDeleteClick = { p ->
                                    productToDelete = p
                                    showDeleteDialog = true
                                }
                            )
                        } else {
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product) },
                                onAddToCart = { p ->
                                    // Mantengo tu comportamiento actual (ir al detalle)
                                    onProductClick(p)

                                    // Si quieres agregar directo al carrito, después lo cambiamos a:
                                    // cartViewModel.addToCart(p, userId = currentUser?.id ?: return@ProductCard)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                productToDelete = null
            },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${productToDelete!!.name}\" del catálogo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productToDelete?.let { productViewModel.deleteProduct(it) }
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

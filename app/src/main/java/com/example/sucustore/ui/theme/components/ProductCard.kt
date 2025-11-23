package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sucustore.data.db.entity.Product

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCart: ((Product) -> Unit)? = null,   // Cliente
    isAdmin: Boolean = false,                   // Admin: mostrar menú
    onEditClick: ((Product) -> Unit)? = null,   // Admin
    onDeleteClick: ((Product) -> Unit)? = null  // Admin
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onProductClick(product) }
    ) {
        Column {

            AsyncImage(
                model = product.imageUri,
                contentDescription = product.name,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            )

            Column(Modifier.padding(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // SOLO ADMIN: menú ⋮
                    if (isAdmin) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Opciones")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {
                                        menuExpanded = false
                                        onEditClick?.invoke(product)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteClick?.invoke(product)
                                    }
                                )
                            }
                        }
                    }
                }

                Text("\$${product.price}")

                // SOLO CLIENTE → muestra botón
                if (!isAdmin && onAddToCart != null) {
                    Spacer(Modifier.height(8.dp))

                    Button(
                        // 🔥 CAMBIO IMPORTANTE:
                        // YA NO agregamos directo al carrito
                        // Ahora abrimos el detalle del producto
                        onClick = { onProductClick(product) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 🔥 Nuevo texto profesional:
                        Text("Ver detalle y añadir")
                    }
                }
            }
        }
    }
}

package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sucustore.data.db.entity.Product

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCart: ((Product) -> Unit)? = null // <-- OPCIONAL
) {
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
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("\$${product.price}")

                // SOLO MUESTRA EL BOTÓN SI SE ENVÍA onAddToCart
                if (onAddToCart != null) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar al carrito")
                    }
                }
            }
        }
    }
}

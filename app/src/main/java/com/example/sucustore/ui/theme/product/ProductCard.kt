package com.example.sucustore.ui.theme.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sucustore.data.db.entity.Product

@Composable
fun ProductCard(
    product: Product,
    isAdmin: Boolean = false,
    onProductClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit = {}
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
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("\$${product.price}")

                if (isAdmin) {
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEditClick(product) }
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}

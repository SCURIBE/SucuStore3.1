package com.example.sucustore.ui.theme.product


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    onProductClick: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onProductClick(product) }
    ) {
        Column {
            AsyncImage(
                model = product.imageUri, // Ahora recibe la URI completa y correcta
                contentDescription = product.name,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("\$${product.price}")
            }
        }
    }
}
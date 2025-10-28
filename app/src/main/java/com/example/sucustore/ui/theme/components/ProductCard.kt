package com.example.sucustore.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.sucustore.data.db.entity.Product

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            val painter = rememberAsyncImagePainter(model = product.imageUri)
            Image(
                painter = painter,
                contentDescription = product.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(product.description, style = MaterialTheme.typography.bodySmall)
                Text("$${product.price} | Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
            Column {
                if (onAddToCart != null) {
                    Button(onClick = onAddToCart) { Text("Agregar") }
                }
                if (onEdit != null) {
                    TextButton(onClick = onEdit) { Text("Editar") }
                }
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Eliminar") }
                }
            }
        }
    }
}
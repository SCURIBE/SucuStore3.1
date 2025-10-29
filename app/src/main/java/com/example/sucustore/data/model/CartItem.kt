package com.example.sucustore.data.model

import com.example.sucustore.data.db.entity.Product

/**
 * Una clase de datos que une un Producto con la cantidad en el carrito.
 * No es una tabla de la base de datos, es un modelo para la UI.
 */
data class CartItem(
    val id: Int, // id de la entrada en la tabla Cart
    val product: Product,
    val quantity: Int
)

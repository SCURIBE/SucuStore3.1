package com.example.sucustore.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sucustore.data.db.entity.Cart
import com.example.sucustore.data.db.entity.Product

data class CartItemWithProduct(
    @Embedded
    val cart: Cart,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)

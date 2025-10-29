package com.example.sucustore.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId", "productId"], unique = true)]
)
data class Cart(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0,
    val userId: Int,
    val productId: Int,
    var quantity: Int
)

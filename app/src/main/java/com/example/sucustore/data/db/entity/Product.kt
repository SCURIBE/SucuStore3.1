package com.example.sucustore.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val imageUri: String?, // ¡¡¡EL ANTÍDOTO!!! Ahora la imagen puede ser nula.
    val stock: Int = 0
)
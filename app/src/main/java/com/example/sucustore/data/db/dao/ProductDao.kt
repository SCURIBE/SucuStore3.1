package com.example.sucustore.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sucustore.data.db.entity.Product

@Dao
interface ProductDao {

    // ➕ Agregar producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    // 🔁 Obtener todos los productos
    @Query("SELECT * FROM products ORDER BY id DESC")
    suspend fun getAll(): List<Product>

    // ❌ Eliminar producto
    @Delete
    suspend fun delete(product: Product)

    @Update
    suspend fun update(product: Product)
}

package com.example.sucustore.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sucustore.data.db.entity.CartItem

@Dao
interface CartDao {

    // ➕ Agregar producto al carrito
    @Insert
    suspend fun insert(item: CartItem): Long

    // 🔁 Obtener carrito del usuario
    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCartByUser(userId: Int): List<CartItem>

    // ❌ Eliminar producto específico del carrito
    @Delete
    suspend fun delete(cartItem: CartItem)

    // 🧹 Vaciar carrito del usuario
    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
}

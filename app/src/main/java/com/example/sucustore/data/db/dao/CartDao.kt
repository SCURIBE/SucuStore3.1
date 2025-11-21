package com.example.sucustore.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sucustore.data.db.entity.CartItem

@Dao
interface CartDao {

    @Insert
    suspend fun insert(item: CartItem): Long

    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCartByUser(userId: Int): List<CartItem>

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
}

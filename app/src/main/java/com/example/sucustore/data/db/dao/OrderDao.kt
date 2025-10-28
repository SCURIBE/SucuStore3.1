package com.example.sucustore.data.db.dao

import androidx.room.*
import com.example.sucustore.data.db.entity.Order

@Dao
interface OrderDao {
    @Insert suspend fun insert(order: Order): Long
    @Query("SELECT * FROM orders WHERE userId = :userId") suspend fun getOrdersByUser(userId: Int): List<Order>
    @Query("UPDATE orders SET status = :status WHERE id = :orderId") suspend fun updateStatus(orderId: Int, status: String)
    @Query("DELETE FROM orders") suspend fun clearAll()
}
package com.example.sucustore.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sucustore.data.db.entity.Order

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: Order): Long

    // Historial de un usuario (ordenado del más reciente al más antiguo)
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY id DESC")
    suspend fun getOrdersByUser(userId: Int): List<Order>

    // Historial completo (para el administrador)
    @Query("SELECT * FROM orders ORDER BY id DESC")
    suspend fun getAllOrders(): List<Order>

    // Actualizar estado de la orden
    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: Int, status: String)

    // Borrar todas (por si quieres limpiar la tabla alguna vez)
    @Query("DELETE FROM orders")
    suspend fun clearAll()
}

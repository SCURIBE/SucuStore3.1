package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.OrderDao
import com.example.sucustore.data.db.entity.Order

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun addOrder(order: Order) = orderDao.insert(order)

    // Historial de un usuario
    suspend fun getOrders(userId: Int) = orderDao.getOrdersByUser(userId)

    // Historial completo (admin)
    suspend fun getAllOrders() = orderDao.getAllOrders()

    // Cambiar estado de una orden (ej: PENDING, COMPLETED, CANCELED)
    suspend fun updateStatus(orderId: Int, status: String) =
        orderDao.updateStatus(orderId, status)
}

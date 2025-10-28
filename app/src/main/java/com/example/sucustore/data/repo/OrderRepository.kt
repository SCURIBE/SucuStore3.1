package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.OrderDao
import com.example.sucustore.data.db.entity.Order

// ¡CORREGIDO! Ahora recibe el OrderDao directamente.
class OrderRepository(private val orderDao: OrderDao) {

    suspend fun addOrder(order: Order) = orderDao.insert(order)
    suspend fun getOrders(userId: Int) = orderDao.getOrdersByUser(userId)
}
package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.Order
import com.example.sucustore.data.repo.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    fun loadOrders(userId: Int) {
        viewModelScope.launch {
            _orders.value = repository.getOrders(userId)
        }
    }

    fun createOrder(userId: Int, total: Double, details: String) {
        viewModelScope.launch {
            // El estado por defecto es PENDING
            val order = Order(userId = userId, total = total, details = details, status = "PENDING")
            repository.addOrder(order)
            // Opcional: Recargar la lista de órdenes después de crear una nueva
            loadOrders(userId)
        }
    }
}
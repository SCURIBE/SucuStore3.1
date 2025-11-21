package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.Order
import com.example.sucustore.data.repo.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    // Historial del usuario (cliente)
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    // Historial completo (admin)
    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders = _allOrders.asStateFlow()

    // Cargar historial de un usuario
    fun loadOrders(userId: Int) {
        viewModelScope.launch {
            _orders.value = repository.getOrders(userId)
        }
    }

    // Cargar historial completo (para admin)
    fun loadAllOrders() {
        viewModelScope.launch {
            _allOrders.value = repository.getAllOrders()
        }
    }

    // Crear una nueva orden (lo usas desde el carrito)
    fun createOrder(userId: Int, total: Double, details: String) {
        viewModelScope.launch {
            val order = Order(
                userId = userId,
                total = total,
                details = details,
                status = "PENDING"
            )
            repository.addOrder(order)
            // Recargar el historial del usuario
            loadOrders(userId)
        }
    }

    // Cambiar estado de una orden (por si el admin quiere marcarla completada, etc.)
    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateStatus(orderId, status)
            // Si quisieras recargar listas, puedes hacerlo aquí:
            // loadAllOrders()
        }
    }
}

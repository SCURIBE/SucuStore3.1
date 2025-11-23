package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.CartItem
import com.example.sucustore.data.repo.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    // Estado interno del carrito
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // Cargar carrito de un usuario
    fun loadCart(userId: Int) {
        viewModelScope.launch {
            _cartItems.value = repository.getCartByUser(userId)
        }
    }

    // ✅ Agregar al carrito (sin duplicar filas, solo actualiza cantidad si ya existe)
    fun addToCart(userId: Int, productId: Int, quantity: Int) {
        viewModelScope.launch {
            // Buscamos si ya existe un item con ese userId + productId
            val existing = _cartItems.value.find { it.userId == userId && it.productId == productId }

            if (existing != null) {
                // Si ya existe, aumentamos la cantidad y usamos UPDATE
                val updated = existing.copy(quantity = existing.quantity + quantity)
                repository.updateCartItem(updated)
            } else {
                // Si no existe, lo insertamos nuevo
                repository.addToCart(
                    CartItem(
                        userId = userId,
                        productId = productId,
                        quantity = quantity
                    )
                )
            }

            // Recargamos el carrito desde la BD
            loadCart(userId)
        }
    }

    // Eliminar un item específico del carrito
    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(item)
            loadCart(item.userId)
        }
    }

    // Vaciar carrito de un usuario
    fun clearCart(userId: Int) {
        viewModelScope.launch {
            repository.clearCart(userId)
            _cartItems.value = emptyList()
        }
    }
}

package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.CartItem
import com.example.sucustore.data.repo.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun loadCart(userId: Int) {
        viewModelScope.launch {
            _cartItems.value = repository.getCartByUser(userId)
        }
    }

    fun addToCart(userId: Int, productId: Int, quantity: Int) {
        viewModelScope.launch {
            val existingItem = _cartItems.value.find { it.productId == productId && it.userId == userId }
            if (existingItem != null) {
                // Si el item ya existe, actualizamos la cantidad
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                repository.addToCart(updatedItem) // Room se encargará de reemplazarlo por el @Insert
            } else {
                // Si es un item nuevo, lo insertamos
                repository.addToCart(CartItem(userId = userId, productId = productId, quantity = quantity))
            }
            loadCart(userId) // Recargamos el carrito para reflejar los cambios
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(cartItem)
            loadCart(cartItem.userId)
        }
    }

    fun clearCart(userId: Int) {
        viewModelScope.launch {
            repository.clearCart(userId)
            _cartItems.value = emptyList()
        }
    }
}
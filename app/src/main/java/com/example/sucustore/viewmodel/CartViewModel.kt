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

            val existing = _cartItems.value.find {
                it.userId == userId && it.productId == productId
            }

            if (existing != null) {
                val updated = existing.copy(quantity = existing.quantity + quantity)
                repository.addToCart(updated)
            } else {
                repository.addToCart(
                    CartItem(
                        userId = userId,
                        productId = productId,
                        quantity = quantity
                    )
                )
            }

            loadCart(userId)
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            repository.removeFromCart(item)
            loadCart(item.userId)
        }
    }

    fun clearCart(userId: Int) {
        viewModelScope.launch {
            repository.clearCart(userId)
            _cartItems.value = emptyList()
        }
    }
}

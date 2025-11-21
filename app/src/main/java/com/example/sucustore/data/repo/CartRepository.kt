package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.CartDao
import com.example.sucustore.data.db.entity.CartItem

class CartRepository(private val cartDao: CartDao) {

    suspend fun addToCart(item: CartItem) {
        cartDao.insert(item)
    }

    suspend fun getCartByUser(userId: Int): List<CartItem> {
        return cartDao.getCartByUser(userId)
    }

    suspend fun removeFromCart(item: CartItem) {
        cartDao.delete(item)
    }

    suspend fun clearCart(userId: Int) {
        cartDao.clearCart(userId)
    }
}

package com.example.sucustore

import android.app.Application
import com.example.sucustore.data.db.AppDatabase
import com.example.sucustore.data.repo.CartRepository
import com.example.sucustore.data.repo.OrderRepository
import com.example.sucustore.data.repo.ProductRepository
import com.example.sucustore.data.repo.UserRepository
import com.example.sucustore.util.Seed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SucuStoreApp : Application() {

    // Base de datos singleton
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Repositorios singleton
    val userRepository by lazy { UserRepository(database.userDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val orderRepository by lazy { OrderRepository(database.orderDao()) }

    // 🔥 ESTE FALTABA
    val cartRepository by lazy { CartRepository(database.cartDao()) }

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            Seed.plant(this@SucuStoreApp)
        }
    }
}

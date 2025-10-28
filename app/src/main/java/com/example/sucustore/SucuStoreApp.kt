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

    // Se crea una ÚNICA instancia de la base de datos para toda la aplicación.
    // Usamos 'lazy' para que solo se cree cuando se necesite por primera vez.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Se crean los repositorios una sola vez, usando los DAOs de la base de datos única.
    val userRepository by lazy { UserRepository(database.userDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val orderRepository by lazy { OrderRepository(database.orderDao()) }
    val cartRepository by lazy { CartRepository(database.cartDao()) }

    override fun onCreate() {
        super.onCreate()
        // Al iniciar la app, se lanzan las semillas en un hilo secundario.
        CoroutineScope(Dispatchers.IO).launch {
            Seed.plant(this@SucuStoreApp)
        }
    }
}
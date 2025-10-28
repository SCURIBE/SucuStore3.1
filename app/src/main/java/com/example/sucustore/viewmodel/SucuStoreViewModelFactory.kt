package com.example.sucustore.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sucustore.SucuStoreApp
import com.example.sucustore.data.prefs.AppPreference

// ¡¡¡LA VERSIÓN FINAL, FINAL, FINALÍSIMA!!!
class SucuStoreViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Le pedimos al "Guardián" (SucuStoreApp) las herramientas que ya ha creado.
        val sucuStoreApp = application as SucuStoreApp
        val userRepository = sucuStoreApp.userRepository
        val productRepository = sucuStoreApp.productRepository
        val orderRepository = sucuStoreApp.orderRepository
        val cartRepository = sucuStoreApp.cartRepository
        val appPreference = AppPreference(application)

        // Creamos los ViewModels con las herramientas que nos ha dado el Guardián.
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, appPreference) as T
            }
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(productRepository) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(orderRepository) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
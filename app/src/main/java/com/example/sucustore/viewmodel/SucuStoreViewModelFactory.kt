package com.example.sucustore.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sucustore.SucuStoreApp
import com.example.sucustore.data.prefs.AppPreference

class SucuStoreViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Acceso a la App (SINGLETON)
        val app = application as SucuStoreApp

        // Repositorios creados en SucuStoreApp
        val userRepository = app.userRepository
        val productRepository = app.productRepository
        val orderRepository = app.orderRepository
        val cartRepository = app.cartRepository
        val appPreference = AppPreference(application)

        // ENTREGAMOS LOS VIEWMODELS
        return when {

            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(userRepository, appPreference) as T

            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(productRepository) as T

            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRepository) as T

            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(cartRepository) as T

            modelClass.isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(userRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

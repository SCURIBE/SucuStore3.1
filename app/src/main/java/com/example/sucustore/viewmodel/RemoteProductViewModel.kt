package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.remote.RemoteProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteProductViewModel(
    private val repository: RemoteProductRepository = RemoteProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getProducts()
                _products.value = result
            } catch (e: Exception) {
                _products.value = emptyList()
                _error.value = e.message ?: "Error al cargar productos remotos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

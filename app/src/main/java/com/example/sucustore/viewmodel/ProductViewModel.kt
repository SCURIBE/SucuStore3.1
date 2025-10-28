package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.repo.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // La carga inicial se hará desde la pantalla (Composable) para evitar condiciones de carrera.

    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _products.value = repository.getAllProducts()
        }
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertProduct(product)
            // Después de insertar, siempre recargamos la lista para que la UI se actualice.
            _products.value = repository.getAllProducts()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            // Después de borrar, siempre recargamos la lista.
            _products.value = repository.getAllProducts()
        }
    }
}
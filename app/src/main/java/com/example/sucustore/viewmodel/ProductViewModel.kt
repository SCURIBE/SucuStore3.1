package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.repo.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(val repository: ProductRepository) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAll()
            _allProducts.value = list
            _products.value = list
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text

        val filtered = if (text.isBlank()) {
            _allProducts.value
        } else {
            _allProducts.value.filter {
                it.name.contains(text, ignoreCase = true)
            }
        }

        _products.value = filtered
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertProduct(product)
            loadProducts()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProduct(product)
            loadProducts()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            loadProducts()
        }
    }

    suspend fun getById(id: Int): Product? {
        return repository.getById(id)
    }
}

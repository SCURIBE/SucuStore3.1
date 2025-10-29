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

    // Guarda la lista COMPLETA y original de productos.
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    // Guarda el texto que el usuario escribe para buscar.
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Guarda la lista FILTRADA que se mostrará en la pantalla.
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Carga todos los productos del repositorio y actualiza la lista visible.
     */
    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Obtiene la lista completa desde tu repositorio.
            val allProductsList = repository.getAll()
            _allProducts.value = allProductsList
            // 2. Al cargar, la lista visible es igual a la lista completa.
            _products.value = allProductsList
        }
    }

    /**
     * La pantalla llama a esta función cuando el usuario escribe.
     */
    fun onSearchTextChange(text: String) {
        _searchText.value = text

        // Filtra la lista en el momento.
        val filteredList = if (text.isBlank()) {
            _allProducts.value // Si no hay texto, muestra todo.
        } else {
            _allProducts.value.filter { product ->
                // Filtra por nombre, ignorando mayúsculas/minúsculas.
                product.name.contains(text, ignoreCase = true)
            }
        }
        // Actualiza la lista que la UI está viendo.
        _products.value = filteredList
    }

    /**
     * Inserta un producto y vuelve a cargar todo para mostrar el cambio.
     */
    fun insertProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertProduct(product)
            loadProducts()
        }
    }

    /**
     * Borra un producto y vuelve a cargar todo para mostrar el cambio.
     */
    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            loadProducts()
        }
    }
}

package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.ProductDao
import com.example.sucustore.data.db.entity.Product

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertProduct(product: Product) {
        productDao.insert(product)
    }

    // --- CORRECCIÓN ---
    // Ahora esta función se llama 'getAll' para que coincida con el Dao.
    suspend fun getAll(): List<Product> {
        return productDao.getAll()
    }

    suspend fun updateProduct(product: Product) {
        productDao.update(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
    }
}

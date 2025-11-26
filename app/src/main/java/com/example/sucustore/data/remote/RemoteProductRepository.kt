package com.example.sucustore.data.remote

import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.remote.api.ProductApi
import com.example.sucustore.data.remote.ApiClient

class RemoteProductRepository {

    private val api: ProductApi = ApiClient.productApi

    suspend fun getProducts(): List<Product> =
        api.getProducts()

    suspend fun createProduct(product: Product): Product =
        api.createProduct(product)

    suspend fun updateProduct(id: Long, product: Product): Product =
        api.updateProduct(id, product)

    suspend fun deleteProduct(id: Long) =
        api.deleteProduct(id)
}

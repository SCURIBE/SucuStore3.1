package com.example.sucustore.data.remote.api

import com.example.sucustore.data.db.entity.Product
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: Product
    ): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)
}

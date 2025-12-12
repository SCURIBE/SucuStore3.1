package com.example.sucustore.data.remote.api

import com.example.sucustore.data.remote.dto.OrderRequest
import com.example.sucustore.data.remote.dto.OrderResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {

    @POST("orders")
    suspend fun createOrder(@Body body: OrderRequest): OrderResponse

    @GET("orders")
    suspend fun getOrders(): List<OrderResponse>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): OrderResponse

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Int
}

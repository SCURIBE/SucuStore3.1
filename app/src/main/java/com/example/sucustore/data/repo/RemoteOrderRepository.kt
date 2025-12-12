package com.example.sucustore.data.remote.repo

import com.example.sucustore.data.remote.api.OrderApi
import com.example.sucustore.data.remote.dto.OrderRequest
import com.example.sucustore.data.remote.dto.OrderResponse

class RemoteOrderRepository(
    private val orderApi: OrderApi
) {

    suspend fun createOrder(
        customerName: String,
        customerEmail: String,
        address: String,
        totalAmount: Double
    ): OrderResponse {
        val body = OrderRequest(
            customerName = customerName,
            customerEmail = customerEmail,
            address = address,
            status = "CREATED",
            totalAmount = totalAmount
        )
        return orderApi.createOrder(body)
    }

    suspend fun getOrders(): List<OrderResponse> = orderApi.getOrders()

    suspend fun getOrder(id: Int): OrderResponse = orderApi.getOrder(id)

    suspend fun deleteOrder(id: Int): Int = orderApi.deleteOrder(id)
}

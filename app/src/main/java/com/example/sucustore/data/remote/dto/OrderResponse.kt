package com.example.sucustore.data.remote.dto

data class OrderResponse(
    val id: Int,
    val customerName: String,
    val customerEmail: String,
    val address: String,
    val status: String,
    val totalAmount: Double,
    val createdAt: String? = null
)

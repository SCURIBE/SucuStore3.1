package com.example.sucustore.data.remote.dto

data class OrderRequest(
    val customerName: String,
    val customerEmail: String,
    val address: String,
    val status: String,
    val totalAmount: Double
)

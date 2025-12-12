package com.example.sucustore.data.remote

import com.example.sucustore.data.remote.api.JsonPlaceholderApi
import com.example.sucustore.data.remote.api.OrderApi
import com.example.sucustore.data.remote.api.ProductApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Backend productos (Spring Boot puerto 8080)
    private const val BASE_URL_LOCAL = "http://10.0.2.2:8080/"

    // Backend órdenes (Order-API puerto 8081)
    private const val BASE_URL_ORDER = "http://10.0.2.2:8081/"

    // API externa (JsonPlaceholder)
    private const val BASE_URL_JSON = "https://jsonplaceholder.typicode.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofitLocal: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_LOCAL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitOrder: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_ORDER)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitJson: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_JSON)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productApi: ProductApi by lazy {
        retrofitLocal.create(ProductApi::class.java)
    }

    val orderApi: OrderApi by lazy {
        retrofitOrder.create(OrderApi::class.java)
    }

    val jsonPlaceholderApi: JsonPlaceholderApi by lazy {
        retrofitJson.create(JsonPlaceholderApi::class.java)
    }

    fun <T> createJson(service: Class<T>): T = retrofitJson.create(service)
}

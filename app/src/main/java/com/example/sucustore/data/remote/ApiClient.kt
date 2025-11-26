package com.example.sucustore.data.remote

import com.example.sucustore.data.remote.api.ProductApi
import com.example.sucustore.data.remote.api.JsonPlaceholderApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Backend Spring Boot (tu microservicio local)
    private const val BASE_URL_LOCAL = "http://10.0.2.2:8080/"   // ✔ CORRECTO

    // API externa (JsonPlaceholder)
    private const val BASE_URL_JSON = "https://jsonplaceholder.typicode.com/"

    // Logging para ver peticiones en Logcat
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Retrofit para tu backend (Spring Boot)
    private val retrofitLocal: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_LOCAL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit para JSONPlaceholder (API externa)
    private val retrofitJson: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_JSON)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // -----------------------------
    // ✔ API del backend (productos)
    // -----------------------------
    val productApi: ProductApi by lazy {
        retrofitLocal.create(ProductApi::class.java)
    }

    // -----------------------------------------
    // ✔ API externa JSONPlaceholder (posts/tips)
    // -----------------------------------------
    val jsonPlaceholderApi: JsonPlaceholderApi by lazy {
        retrofitJson.create(JsonPlaceholderApi::class.java)
    }

    // Utilidad opcional
    fun <T> createJson(service: Class<T>): T = retrofitJson.create(service)
}

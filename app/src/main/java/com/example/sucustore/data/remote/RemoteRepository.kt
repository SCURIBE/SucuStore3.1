package com.example.sucustore.data.remote

import com.example.sucustore.data.remote.api.JsonPlaceholderApi
import com.example.sucustore.data.remote.ApiClient

class RemoteRepository {

    // ✅ Usamos la propiedad que ya define ApiClient
    private val api: JsonPlaceholderApi = ApiClient.jsonPlaceholderApi

    // GET /posts desde JSONPlaceholder (tips externos)
    suspend fun getPosts() = api.getPosts()
}

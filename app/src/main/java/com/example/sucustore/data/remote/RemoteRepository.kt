package com.example.sucustore.data.remote


import com.example.sucustore.data.remote.api.JsonPlaceholderApi
import com.example.sucustore.data.remote.dto.PostDto

class RemoteRepository {

    private val api = ApiClient.create(JsonPlaceholderApi::class.java)

    suspend fun getPosts(): List<PostDto> {
        return api.getPosts()
    }
}

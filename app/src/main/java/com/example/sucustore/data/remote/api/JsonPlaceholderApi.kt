package com.example.sucustore.data.remote.api


import com.example.sucustore.data.remote.dto.PostDto
import retrofit2.http.GET

interface JsonPlaceholderApi {

    // GET https://jsonplaceholder.typicode.com/posts
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}

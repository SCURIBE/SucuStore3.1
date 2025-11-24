package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.remote.RemoteRepository
import com.example.sucustore.data.remote.dto.PostDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteViewModel : ViewModel() {

    private val repository = RemoteRepository()

    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts = _posts.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

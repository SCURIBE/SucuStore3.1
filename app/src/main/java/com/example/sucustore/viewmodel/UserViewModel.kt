package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import com.example.sucustore.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                _message.value = "Ya existe un usuario con ese correo."
            } else {
                // Por defecto, los usuarios se registran como CLIENT
                repository.registerUser(User(name = name, email = email, password = password, role = UserRole.CLIENT))
                _message.value = "Usuario registrado correctamente."
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val userFound = repository.getUserByEmail(email)
            if (userFound == null || userFound.password != password) {
                _message.value = "Correo o contraseña incorrectos."
            } else {
                _user.value = userFound
                _message.value = "Inicio de sesión exitoso."
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
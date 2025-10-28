package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import com.example.sucustore.data.prefs.AppPreference
import com.example.sucustore.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val appPreference: AppPreference
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            val userEmail = appPreference.getUserEmail().first()
            if (userEmail != null) {
                _currentUser.value = userRepository.getUserByEmail(userEmail)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                _currentUser.value = user
                appPreference.saveLoginState(true, user.email)
                _error.value = null
            } else {
                _error.value = "Credenciales incorrectas"
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                _error.value = "Todos los campos son obligatorios"
                return@launch
            }
            if (userRepository.getUserByEmail(email) != null) {
                _error.value = "El correo ya está registrado"
                return@launch
            }
            val newUser = User(name = name, email = email, password = password, role = UserRole.CLIENT)
            userRepository.registerUser(newUser)
            _error.value = null
            
            // ¡¡AUTO-LOGIN!! Después de registrar, se inicia sesión automáticamente.
            _currentUser.value = newUser
            appPreference.saveLoginState(true, newUser.email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            appPreference.saveLoginState(false, "")
        }
    }
}
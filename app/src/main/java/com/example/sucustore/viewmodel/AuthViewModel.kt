package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import com.example.sucustore.data.prefs.AppPreference
import com.example.sucustore.data.repo.UserRepository
// 1. AÑADIMOS LAS IMPORTACIONES NECESARIAS
import com.example.sucustore.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 2. AÑADIMOS ESTA "DATA CLASS" PARA EL ESTADO DEL FORMULARIO
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

// 3. REEMPLAZAMOS LA CLASE ENTERA
class AuthViewModel(
    private val userRepository: UserRepository,
    private val appPreference: AppPreference
) : ViewModel() {

    // --- Tu código original que se mantiene ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _error = MutableStateFlow<String?>(null) // Lo mantenemos para el login
    val error = _error.asStateFlow()
    // --- Fin del código que se mantiene ---


    // --- Código nuevo que añadimos ---
    private val _formState = MutableStateFlow(AuthFormState())
    val formState = _formState.asStateFlow()

    init { // Tu `init` se queda igual
        viewModelScope.launch {
            val userEmail = appPreference.getUserEmail().first()
            if (userEmail != null) {
                _currentUser.value = userRepository.getUserByEmail(userEmail)
            }
        }
    }

    // Funciones nuevas para que la UI actualice los datos del formulario
    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }
    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email, emailError = null) }
    }
    fun onPasswordChange(password: String) {
        _formState.update { it.copy(password = password, passwordError = null) }
    }

    // Tu función de `login` se queda igual
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

    // TU FUNCIÓN DE `register` ANTIGUA SE REEMPLAZA POR ESTA NUEVA VERSIÓN
    fun register() {
        val currentState = _formState.value

        val nameError = Validators.nonEmpty("Nombre", currentState.name)?.message
        val emailError = Validators.email(currentState.email)?.message
        val passwordError = Validators.password(currentState.password)?.message

        val hasError = listOfNotNull(nameError, emailError, passwordError).any()

        if (hasError) {
            _formState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            if (userRepository.getUserByEmail(currentState.email) != null) {
                _formState.update { it.copy(emailError = "El correo ya está registrado") }
                return@launch
            }

            val newUser = User(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password,
                role = UserRole.CLIENT
            )
            userRepository.registerUser(newUser)
            _currentUser.value = newUser
            appPreference.saveLoginState(true, newUser.email)
            _formState.value = AuthFormState() // Limpia el formulario
        }
    }

    // Tu función de `logout` se queda igual
    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            appPreference.saveLoginState(false, "")
        }
    }
}

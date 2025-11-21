package com.example.sucustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import com.example.sucustore.data.prefs.AppPreference
import com.example.sucustore.data.repo.UserRepository
import com.example.sucustore.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --------------------------------------
// ESTADO DEL FORMULARIO
// --------------------------------------
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

// --------------------------------------
// VIEWMODEL COMPLETO
// --------------------------------------
class AuthViewModel(
    private val userRepository: UserRepository,
    private val appPreference: AppPreference
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            val userEmail = appPreference.getUserEmail().first()
            if (userEmail != null) {
                _currentUser.value = userRepository.getUserByEmail(userEmail)
            }
        }
    }

    // ------------------------------
    // SET ERROR (para login y reset)
    // ------------------------------
    fun setError(msg: String) {
        _error.value = msg
    }

    fun clearError() {
        _error.value = null
    }

    // ------------------------------
    // ACTUALIZADORES UI → VM
    // ------------------------------
    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name.trimStart(), nameError = null) }
    }

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _formState.update { it.copy(password = password, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _formState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    // ------------------------------
    // LOGIN
    // ------------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {

            if (!email.contains("@")) {
                _error.value = "Correo inválido"
                return@launch
            }

            if (password.isBlank()) {
                _error.value = "Debe ingresar su contraseña"
                return@launch
            }

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

    // ------------------------------
    // VALIDACIONES AVANZADAS + REGISTRO
    // ------------------------------
    fun register() {
        val state = _formState.value

        // VALIDACIÓN NOMBRE
        var nameError = Validators.nonEmpty("Nombre", state.name)?.message

        if (nameError == null) {
            val name = state.name

            if (!Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$").matches(name)) {
                nameError = "El nombre no puede contener números ni símbolos"
            }

            if (name.trim().split(" ").size < 2) {
                nameError = "Debe ingresar nombre y apellido"
            }

            if (name.length < 5) {
                nameError = "El nombre es demasiado corto"
            }

            if ("  " in name) {
                nameError = "No puede contener espacios dobles"
            }

            if (name.length > 40) {
                nameError = "El nombre es demasiado largo"
            }

            if (Regex("(.)\\1{3,}").containsMatchIn(name)) {
                nameError = "El nombre contiene caracteres repetidos en exceso"
            }
        }

        // VALIDACIONES EMAIL / PASSWORD
        val emailError = Validators.email(state.email)?.message
        val passwordError = Validators.password(state.password)?.message

        val confirmPasswordError =
            if (state.confirmPassword.isBlank())
                "Debe repetir la contraseña"
            else if (state.confirmPassword != state.password)
                "Las contraseñas no coinciden"
            else null

        if (listOfNotNull(nameError, emailError, passwordError, confirmPasswordError).isNotEmpty()) {
            _formState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        // CAPITALIZAR NOMBRE
        val formattedName = formatName(state.name)

        // REGISTRO FINAL
        viewModelScope.launch {
            if (userRepository.getUserByEmail(state.email) != null) {
                _formState.update { it.copy(emailError = "El correo ya está registrado") }
                return@launch
            }

            val newUser = User(
                name = formattedName,
                email = state.email,
                password = state.password,
                role = UserRole.CLIENT
            )

            userRepository.registerUser(newUser)
            _currentUser.value = newUser
            appPreference.saveLoginState(true, newUser.email)

            _formState.value = AuthFormState()
        }
    }

    // -------------------------------------------------
    // 🔥 ACTUALIZAR PERFIL (NOMBRE + CONTRASEÑA OPCIONAL)
    // -------------------------------------------------
    /**
     * @param newName nombre actualizado
     * @param newPassword nueva contraseña (opcional, puede ser null o vacío)
     * @param confirmPassword confirmación de la nueva contraseña
     */
    fun updateProfile(
        newName: String,
        newPassword: String?,
        confirmPassword: String?,
        callback: (success: Boolean, message: String?) -> Unit
    ) {
        val user = currentUser.value
        if (user == null) {
            callback(false, "Usuario no autenticado")
            return
        }

        // Validar nombre
        val trimmedName = newName.trim()
        if (trimmedName.isBlank()) {
            callback(false, "El nombre no puede estar vacío")
            return
        }

        // Reutilizamos la misma lógica de formato de nombre
        val formattedName = formatName(trimmedName)

        // Validar contraseña solo si el usuario escribió algo
        var finalPassword: String? = null
        val pass = newPassword?.trim().orEmpty()
        val confirm = confirmPassword?.trim().orEmpty()

        if (pass.isNotEmpty() || confirm.isNotEmpty()) {
            if (pass.length < 6) {
                callback(false, "La nueva contraseña debe tener al menos 6 caracteres")
                return
            }
            if (pass != confirm) {
                callback(false, "Las contraseñas no coinciden")
                return
            }
            finalPassword = pass
        }

        viewModelScope.launch {
            val updatedUser = if (finalPassword != null) {
                user.copy(name = formattedName, password = finalPassword)
            } else {
                user.copy(name = formattedName)
            }

            userRepository.updateUser(updatedUser)
            _currentUser.value = updatedUser
            callback(true, "Perfil actualizado correctamente")
        }
    }

    // -------------------------------------------------
    // 🔥 RECUPERAR CONTRASEÑA – OPCIÓN B IMPLEMENTADA
    // -------------------------------------------------

    // 1) Verificar si el correo existe en la BD
    fun verifyEmailForReset(email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)

            if (user == null) {
                _error.value = "El correo no está registrado"
                callback(false)
            } else {
                _error.value = null
                callback(true)
            }
        }
    }

    // 2) Resetear contraseña
    fun resetPassword(email: String, pass: String, confirm: String, callback: (Boolean) -> Unit) {

        if (pass.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            callback(false)
            return
        }

        if (pass != confirm) {
            _error.value = "Las contraseñas no coinciden"
            callback(false)
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)

            if (user != null) {
                val updatedUser = user.copy(password = pass)
                userRepository.updateUser(updatedUser)
                _error.value = null
                callback(true)
            } else {
                _error.value = "Error inesperado"
                callback(false)
            }
        }
    }

    // LOGOUT
    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            appPreference.saveLoginState(false, "")
        }
    }

    // Helper para capitalizar nombres
    private fun formatName(raw: String): String {
        return raw
            .lowercase()
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }
}

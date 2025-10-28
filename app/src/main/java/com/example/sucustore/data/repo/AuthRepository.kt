package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.UserDao
import com.example.sucustore.data.db.entity.User

/**
 * Repositorio que centraliza el acceso a datos relacionados con autenticación.
 * Sirve como puente entre el ViewModel (AuthViewModel) y la base de datos (UserDao).
 */
class AuthRepository(private val userDao: UserDao) {

    /**
     * Registra un nuevo usuario si el correo no está en uso.
     * @return Long → id generado o -1 si ya existe el correo.
     */
    suspend fun registerUser(user: User): Long {
        val existingUser = userDao.findByEmail(user.email)
        return if (existingUser == null) {
            userDao.insert(user)
        } else {
            -1 // ya existe usuario con este correo
        }
    }

    /**
     * Intenta iniciar sesión con las credenciales.
     * @return El usuario autenticado o null si las credenciales son incorrectas.
     */
    suspend fun login(email: String, password: String): User? {
        val user = userDao.findByEmail(email)
        return if (user != null && user.password == password) user else null
    }

    /**
     * Busca un usuario por su correo.
     */
    suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)

    /**
     * Devuelve todos los usuarios registrados (uso administrativo o debug).
     */
    suspend fun getAllUsers(): List<User> = userDao.getAll()
}
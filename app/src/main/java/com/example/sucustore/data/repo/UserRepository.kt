package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.UserDao
import com.example.sucustore.data.db.entity.User

class UserRepository(private val userDao: UserDao) {

    // Registrar nuevo usuario
    suspend fun registerUser(user: User) {
        userDao.insert(user)
    }

    // Buscar por correo
    suspend fun getUserByEmail(email: String): User? {
        return userDao.findByEmail(email)
    }

    // 🔥 ACTUALIZAR USUARIO (NECESARIO PARA RESTABLECER CONTRASEÑA)
    suspend fun updateUser(user: User) {
        userDao.update(user)
    }
}

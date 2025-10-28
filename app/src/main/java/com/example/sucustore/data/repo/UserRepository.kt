package com.example.sucustore.data.repo

import com.example.sucustore.data.db.dao.UserDao
import com.example.sucustore.data.db.entity.User

// ¡CORREGIDO! Ahora recibe el UserDao directamente.
class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.findByEmail(email)
    }
}
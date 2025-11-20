package com.example.sucustore.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sucustore.data.db.entity.User

@Dao
interface UserDao {

    // ➕ Registrar nuevo usuario
    @Insert
    suspend fun insert(user: User): Long

    // 🔍 Buscar usuario por correo
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?

    // 🔁 Obtener todos (solo para debug)
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    // 📊 Contar todos los usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // 🔥 NECESARIO PARA ACTUALIZAR CONTRASEÑA
    @Update
    suspend fun update(user: User)
}

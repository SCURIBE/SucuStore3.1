package com.example.sucustore.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String, // En una app real, esto debería estar hasheado
    val role: UserRole
)

enum class UserRole { ADMIN, CLIENT }

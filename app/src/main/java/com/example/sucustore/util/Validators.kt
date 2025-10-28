package com.example.sucustore.util

data class FieldError(val message: String)

object Validators {
    fun email(value: String): FieldError? =
        if (value.isBlank() || !value.contains("@")) FieldError("Email inválido") else null

    fun password(value: String): FieldError? =
        if (value.length < 4) FieldError("Mínimo 4 caracteres") else null

    fun nonEmpty(label: String, value: String): FieldError? =
        if (value.isBlank()) FieldError("$label es obligatorio") else null

    fun price(value: String): FieldError? =
        value.toDoubleOrNull()?.let { if (it <= 0) FieldError("Precio > 0") else null }
            ?: FieldError("Precio numérico")

    fun stock(value: String): FieldError? =
        value.toIntOrNull()?.let { if (it < 0) FieldError("Stock >= 0") else null }
            ?: FieldError("Stock numérico")
}
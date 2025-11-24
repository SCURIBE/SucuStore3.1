package com.example.sucustore.util

import androidx.core.util.PatternsCompat

data class FieldError(val message: String)

object Validators {

    fun email(value: String): FieldError? {
        return if (!PatternsCompat.EMAIL_ADDRESS.matcher(value).matches()) {
            FieldError("Por favor, ingresa un correo válido")
        } else {
            null
        }
    }

    fun password(value: String): FieldError? {
        if (value.length < 8) {
            return FieldError("Mínimo 8 caracteres")
        }
        if (!value.any { it.isUpperCase() }) {
            return FieldError("Debe contener una mayúscula")
        }
        return null
    }

    fun nonEmpty(label: String, value: String): FieldError? =
        if (value.isBlank()) FieldError("$label es obligatorio") else null

    fun price(value: String): FieldError? =
        value.toDoubleOrNull()?.let { number ->
            if (number <= 0.0) FieldError("Precio > 0") else null
        } ?: FieldError("Precio numérico")

    fun stock(value: String): FieldError? =
        value.toIntOrNull()?.let { number ->
            if (number < 0) FieldError("Stock >= 0") else null
        } ?: FieldError("Stock numérico")
}

package com.example.sucustore

import com.example.sucustore.util.Validators
import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    // -------- EMAIL --------

    @Test
    fun email_invalido_retornaError() {
        val error = Validators.email("correo-invalido")
        assertNotNull(error)
    }

    @Test
    fun email_valido_noRetornaError() {
        val error = Validators.email("usuario@dominio.com")
        assertNull(error)
    }

    // -------- PASSWORD --------

    @Test
    fun password_corta_retornaError() {
        val error = Validators.password("abc")
        assertNotNull(error)
    }

    @Test
    fun password_suficientementeFuerte_noRetornaError() {
        val error = Validators.password("Password1")
        assertNull(error)
    }

    // -------- PRECIO (CASOS CON ERROR) --------

    @Test
    fun precio_noNumerico_retornaError() {
        val error = Validators.price("abc")
        assertNotNull(error)
        assertEquals("Precio numérico", error?.message)
    }

    @Test
    fun precio_ceroONegativo_retornaError() {
        val error = Validators.price("0")
        assertNotNull(error)
        assertEquals("Precio > 0", error?.message)
    }

    // -------- STOCK (CASOS CON ERROR) --------

    @Test
    fun stock_noNumerico_retornaError() {
        val error = Validators.stock("abc")
        assertNotNull(error)
        assertEquals("Stock numérico", error?.message)
    }

    @Test
    fun stock_negativo_retornaError() {
        val error = Validators.stock("-1")
        assertNotNull(error)
        assertEquals("Stock >= 0", error?.message)
    }
}

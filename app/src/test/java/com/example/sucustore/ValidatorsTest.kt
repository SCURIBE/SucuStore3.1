package com.example.sucustore.util

import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    // -------- EMAIL --------
    @Test
    fun `email valido no debe devolver error`() {
        val result = Validators.email("usuario@test.com")
        assertNull(result)
    }

    @Test
    fun `email invalido devuelve error`() {
        val result = Validators.email("correo-sin-arroba")
        assertNotNull(result)
        assertEquals("Por favor, ingresa un correo válido", result?.message)
    }

    // -------- PASSWORD --------
    @Test
    fun `password muy corta devuelve error`() {
        val result = Validators.password("Abc12")
        assertNotNull(result)
        assertEquals("Mínimo 8 caracteres", result?.message)
    }

    @Test
    fun `password sin mayuscula devuelve error`() {
        val result = Validators.password("contrase1a")
        assertNotNull(result)
        assertEquals("Debe contener una mayúscula", result?.message)
    }

    @Test
    fun `password valida no devuelve error`() {
        val result = Validators.password("ContraSegura1")
        assertNull(result)
    }

    // -------- NON EMPTY --------
    @Test
    fun `nonEmpty con string vacio devuelve error`() {
        val result = Validators.nonEmpty("Nombre", "")
        assertNotNull(result)
        assertEquals("Nombre es obligatorio", result?.message)
    }

    @Test
    fun `nonEmpty con texto valido no devuelve error`() {
        val result = Validators.nonEmpty("Nombre", "Suculenta linda")
        assertNull(result)
    }

    // -------- PRICE --------
    @Test
    fun `price con valor negativo devuelve error`() {
        val result = Validators.price("-10")
        assertNotNull(result)
        assertEquals("Precio > 0", result?.message)
    }

    @Test
    fun `price con texto no numerico devuelve error`() {
        val result = Validators.price("abc")
        assertNotNull(result)
        assertEquals("Precio numérico", result?.message)
    }

    @Test
    fun `price valido no devuelve error`() {
        val result = Validators.price("1500")
        assertNull(result)
    }

    // -------- STOCK --------
    @Test
    fun `stock negativo devuelve error`() {
        val result = Validators.stock("-1")
        assertNotNull(result)
        assertEquals("Stock >= 0", result?.message)
    }

    @Test
    fun `stock con texto no numerico devuelve error`() {
        val result = Validators.stock("no-numero")
        assertNotNull(result)
        assertEquals("Stock numérico", result?.message)
    }

    @Test
    fun `stock valido no devuelve error`() {
        val result = Validators.stock("20")
        assertNull(result)
    }
}

package com.example.sucustore

import com.example.sucustore.data.db.entity.CartItem
import com.example.sucustore.data.repo.CartRepository
import com.example.sucustore.viewmodel.CartViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cartRepository: CartRepository = mock()
    private val viewModel = CartViewModel(cartRepository)

    @Test
    fun `addToCart inserta nuevo item cuando no existe`() = runTest {
        // Ejecutar
        viewModel.addToCart(
            userId = 1,
            productId = 10,
            quantity = 2
        )

        // Dejamos que se ejecuten las corrutinas del viewModelScope
        runCurrent()

        // Verificar que se llamó al repositorio con el CartItem correcto
        verify(cartRepository).addToCart(
            CartItem(
                userId = 1,
                productId = 10,
                quantity = 2
            )
        )
    }

    @Test
    fun `clearCart llama al repositorio`() = runTest {
        // Ejecutar
        viewModel.clearCart(1)

        // Dejamos correr las corrutinas
        runCurrent()

        // Verificar que se llamó al repositorio
        verify(cartRepository).clearCart(1)
    }
}

package com.example.sucustore

import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.repo.ProductRepository
import com.example.sucustore.viewmodel.ProductViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    class ProductViewModelTest {

    }
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        repository = mock()
        viewModel = ProductViewModel(repository)
    }

    @Test
    fun `insertProduct llama al repositorio`() = runTest {
        val product = Product(
            id = 0,
            name = "Suculenta test",
            price = 1000.0,
            description = "Desc",
            stock = 5,
            imageUri = null
        )

        viewModel.insertProduct(product)

        // dejamos ejecutar la corrutina
        advanceUntilIdle()

        verify(repository).insertProduct(any())
    }

    @Test
    fun `deleteProduct llama al repositorio`() = runTest {
        val product = Product(
            id = 1,
            name = "Suculenta test",
            price = 1000.0,
            description = "Desc",
            stock = 5,
            imageUri = null
        )

        viewModel.deleteProduct(product)

        // dejamos ejecutar la corrutina
        advanceUntilIdle()

        verify(repository).deleteProduct(product)
    }
}

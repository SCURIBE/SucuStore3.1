package com.example.sucustore

import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import com.example.sucustore.data.prefs.AppPreference
import com.example.sucustore.data.repo.UserRepository
import com.example.sucustore.viewmodel.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mock()
    private val appPreference: AppPreference = mock()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() = runTest {
        // Evita el NPE del init {} del ViewModel
        whenever(appPreference.getUserEmail()).thenReturn(flowOf(null))
        viewModel = AuthViewModel(userRepository, appPreference)
    }

    @Test
    fun `login con credenciales correctas asigna currentUser`() = runTest {
        val user = User(
            id = 1,
            name = "Scarlett",
            email = "test@sucustore.com",
            password = "123456",
            role = UserRole.CLIENT
        )

        whenever(userRepository.getUserByEmail("test@sucustore.com")).thenReturn(user)

        viewModel.login("test@sucustore.com", "123456")
        advanceUntilIdle()

        assertEquals(user, viewModel.currentUser.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `login con contraseña incorrecta mantiene currentUser en null`() = runTest {
        // Usuario no existe o contraseña no coincide
        whenever(userRepository.getUserByEmail("test@sucustore.com")).thenReturn(null)

        viewModel.login("test@sucustore.com", "wrong-pass")
        advanceUntilIdle()

        assertNull(viewModel.currentUser.value)
        assertEquals("Credenciales incorrectas", viewModel.error.value)
    }
}

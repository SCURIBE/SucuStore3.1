package com.example.sucustore.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define el DataStore a nivel superior
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sucustore_prefs")

class AppPreference(private val context: Context) {

    // Claves para guardar los valores
    private companion object {
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    // Flujo para saber si el usuario está logueado
    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN_KEY] ?: false
    }

    // Flujo para obtener el email del usuario logueado
    fun getUserEmail(): Flow<String?> = context.dataStore.data.map {
        it[USER_EMAIL_KEY]
    }

    // Guardar el estado de la sesión y el email del usuario
    suspend fun saveLoginState(isLoggedIn: Boolean, email: String) {
        context.dataStore.edit {
            it[IS_LOGGED_IN_KEY] = isLoggedIn
            if (isLoggedIn) {
                it[USER_EMAIL_KEY] = email
            } else {
                it.remove(USER_EMAIL_KEY)
            }
        }
    }
}
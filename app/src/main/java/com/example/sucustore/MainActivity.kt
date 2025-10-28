package com.example.sucustore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.sucustore.ui.theme.SucuStoreTheme // ¡¡TEMA NUEVO IMPORTADO!!
import com.example.sucustore.ui.theme.navigation.AppNavHost
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels { 
        SucuStoreViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ¡¡TEMA NUEVO APLICADO!!
            SucuStoreTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
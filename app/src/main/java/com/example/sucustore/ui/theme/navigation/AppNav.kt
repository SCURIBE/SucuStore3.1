package com.example.sucustore.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.sucustore.viewmodel.AuthViewModel


@Composable
fun AppNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    AppNavHost(navController = navController, authViewModel = authViewModel)
}
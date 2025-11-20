package com.example.sucustore.ui.theme.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sucustore.ui.theme.product.ProductDetailScreen
import com.example.sucustore.ui.theme.product.ProductScreen
import com.example.sucustore.ui.theme.screens.*
import com.example.sucustore.ui.theme.screens.auth.LoginScreen
import com.example.sucustore.ui.theme.screens.auth.RegisterScreen
import com.example.sucustore.ui.theme.screens.auth.RecoverPasswordScreen
import com.example.sucustore.ui.theme.screens.auth.ResetPasswordScreen
import com.example.sucustore.ui.theme.screens.cart.CartScreen
import com.example.sucustore.ui.theme.screens.home.HomeScreen
import com.example.sucustore.ui.theme.screens.order.OrderScreen
import com.example.sucustore.viewmodel.AuthViewModel
import com.example.sucustore.viewmodel.ProductViewModel
import com.example.sucustore.viewmodel.SucuStoreViewModelFactory

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val factory = SucuStoreViewModelFactory(LocalContext.current.applicationContext as Application)

    NavHost(navController = navController, startDestination = "splash") {

        // -----------------------------------------------------------
        // ⚡ SPLASH SCREEN
        // -----------------------------------------------------------
        composable("splash") {
            val currentUser by authViewModel.currentUser.collectAsState()

            SplashScreen(
                onSplashFinished = {
                    val destination = when (currentUser?.role?.name) {
                        "ADMIN" -> "admin_dashboard"
                        "CLIENT" -> "products"
                        else -> "login"
                    }

                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ LOGIN
        // -----------------------------------------------------------
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("recover_password") },
                onLoginSuccess = {
                    val user = authViewModel.currentUser.value
                    val route = when (user?.role?.name) {
                        "ADMIN" -> "admin_dashboard"
                        "CLIENT" -> "products"
                        else -> "login"
                    }

                    navController.navigate("loading/$route") { popUpTo(0) }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ REGISTER
        // -----------------------------------------------------------
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onLoginClick = { navController.navigate("login") { popUpTo(0) } },
                onRegisterComplete = {
                    val user = authViewModel.currentUser.value
                    val route = when (user?.role?.name) {
                        "ADMIN" -> "admin_dashboard"
                        "CLIENT" -> "products"
                        else -> "login"
                    }

                    navController.navigate("loading/$route") { popUpTo(0) }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ RESET PASSWORD — ENTER EMAIL
        // -----------------------------------------------------------
        composable("recover_password") {
            RecoverPasswordScreen(
                authViewModel = authViewModel,
                onEmailVerified = { email ->
                    navController.navigate("reset_password/$email")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // -----------------------------------------------------------
        // ⚡ RESET PASSWORD — ENTER NEW PASSWORD
        // -----------------------------------------------------------
        composable(
            route = "reset_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            ResetPasswordScreen(
                authViewModel = authViewModel,
                email = email,
                onPasswordResetComplete = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ LOADING SCREEN
        // -----------------------------------------------------------
        composable(
            route = "loading/{route}",
            arguments = listOf(navArgument("route") { type = NavType.StringType })
        ) {
            val finalRoute = it.arguments?.getString("route") ?: "login"
            LoadingScreen(navController = navController, route = finalRoute)
        }

        // -----------------------------------------------------------
        // ⚡ HOME
        // -----------------------------------------------------------
        composable("home") {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }

        // -----------------------------------------------------------
        // ⚡ ADMIN DASHBOARD
        // -----------------------------------------------------------
        composable("admin_dashboard") {
            AdminDashboardScreen(
                navController = navController,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ ADD PRODUCT
        // -----------------------------------------------------------
        composable("add_product") {
            ProductFormScreen(
                productViewModel = viewModel(factory = factory),
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // -----------------------------------------------------------
        // ⚡ CART
        // -----------------------------------------------------------
        composable("cart") {
            CartScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onCheckoutComplete = {
                    navController.navigate("products") { popUpTo(0) }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ PRODUCT LIST
        // -----------------------------------------------------------
        composable("products") {
            val currentUser by authViewModel.currentUser.collectAsState()
            val isAdmin = currentUser?.role?.name == "ADMIN"

            ProductScreen(
                factory = factory,
                authViewModel = authViewModel,
                navController = navController,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onAddNewProduct = { navController.navigate("add_product") },
                onBack = {
                    if (isAdmin) {
                        navController.popBackStack()
                    } else {
                        authViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }
                }
            )
        }

        // -----------------------------------------------------------
        // ⚡ ORDERS
        // -----------------------------------------------------------
        composable("orders") {
            OrderScreen(
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        // -----------------------------------------------------------
        // ⚡ PRODUCT DETAIL
        // -----------------------------------------------------------
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            if (productId != null) {
                val productViewModel: ProductViewModel = viewModel(factory = factory)

                LaunchedEffect(Unit) {
                    productViewModel.loadProducts()
                }

                val products by productViewModel.products.collectAsState()
                val product = products.find { it.id == productId }

                if (product != null) {
                    ProductDetailScreen(
                        product = product,
                        factory = factory,
                        onBack = { navController.popBackStack() },
                        onGoToCart = { navController.navigate("cart") }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Producto no encontrado o cargando...")
                    }
                }
            }
        }
    }
}

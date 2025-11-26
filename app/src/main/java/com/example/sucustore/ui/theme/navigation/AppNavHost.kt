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
import com.example.sucustore.ui.theme.screens.auth.*
import com.example.sucustore.ui.theme.screens.cart.CartScreen
import com.example.sucustore.ui.theme.screens.order.OrderHistoryScreen
import com.example.sucustore.ui.theme.screens.remote.RemotePostsScreen
import com.example.sucustore.ui.theme.screens.checkout.CheckoutScreen   // 👈 IMPORT NUEVO
import com.example.sucustore.viewmodel.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val factory = SucuStoreViewModelFactory(
        LocalContext.current.applicationContext as Application
    )

    NavHost(navController = navController, startDestination = "splash") {

        // -------------------- SPLASH --------------------
        composable("splash") {
            val currentUser by authViewModel.currentUser.collectAsState()

            SplashScreen(onSplashFinished = {
                val destination = when (currentUser?.role?.name) {
                    "ADMIN" -> "admin_dashboard"
                    "CLIENT" -> "products"
                    else -> "login"
                }
                navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // -------------------- LOGIN --------------------
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
                    navController.navigate(route) { popUpTo(0) }
                }
            )
        }

        // -------------------- REGISTER --------------------
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onLoginClick = {
                    navController.navigate("login") { popUpTo(0) }
                },
                onRegisterComplete = {
                    val user = authViewModel.currentUser.value
                    val route =
                        if (user?.role?.name == "ADMIN") "admin_dashboard" else "products"

                    navController.navigate(route) { popUpTo(0) }
                }
            )
        }

        // -------------------- RECOVER / RESET PASSWORD --------------------
        composable("recover_password") {
            RecoverPasswordScreen(
                authViewModel = authViewModel,
                onEmailVerified = { email ->
                    navController.navigate("reset_password/$email")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "reset_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { entry ->
            ResetPasswordScreen(
                authViewModel = authViewModel,
                email = entry.arguments?.getString("email") ?: "",
                onPasswordResetComplete = {
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }

        // -------------------- ADMIN DASHBOARD --------------------
        composable("admin_dashboard") {
            AdminDashboardScreen(
                navController = navController,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }

        // -------------------- ADMIN ORDERS --------------------
        composable("admin_orders") {
            val orderViewModel: OrderViewModel = viewModel(factory = factory)

            OrderHistoryScreen(
                orderViewModel = orderViewModel,
                isAdmin = true,
                userId = 0,
                onBack = { navController.popBackStack() }
            )
        }

        // -------------------- PRODUCTS --------------------
        composable("products") {
            val currentUser by authViewModel.currentUser.collectAsState()
            val isAdmin = currentUser?.role?.name == "ADMIN"

            ProductScreen(
                factory = factory,
                authViewModel = authViewModel,
                navController = navController,
                onProductClick = { product ->
                    if (isAdmin) navController.navigate("edit_product/${product.id}")
                    else navController.navigate("product_detail/${product.id}")
                },
                onAddNewProduct = { navController.navigate("add_product") },
                onBack = {
                    if (isAdmin) navController.popBackStack()
                    else {
                        authViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }
                }
            )
        }

        // -------------------- ADD PRODUCT --------------------
        composable("add_product") {
            ProductFormScreen(
                productViewModel = viewModel(factory = factory),
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // -------------------- EDIT PRODUCT --------------------
        composable(
            route = "edit_product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->

            val productId = entry.arguments?.getInt("productId") ?: 0
            val productViewModel: ProductViewModel = viewModel(factory = factory)

            LaunchedEffect(productId) {
                productViewModel.loadProducts()
            }

            val products by productViewModel.products.collectAsState()
            val product = products.find { it.id == productId }

            if (product == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando producto...")
                }
            } else {
                ProductFormScreen(
                    existingProduct = product,
                    productViewModel = productViewModel,
                    onSave = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // -------------------- PRODUCT DETAIL --------------------
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            val productId = entry.arguments?.getInt("productId")
            val productViewModel: ProductViewModel = viewModel(factory = factory)

            LaunchedEffect(Unit) { productViewModel.loadProducts() }

            val products by productViewModel.products.collectAsState()
            val product = products.find { it.id == productId }

            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    factory = factory,
                    onBack = { navController.popBackStack() },
                    onGoToCart = { navController.navigate("cart") },
                    onGoToCheckout = { navController.navigate("checkout") }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Producto no encontrado.")
                }
            }
        }


        // -------------------- CART --------------------
        composable("cart") {
            CartScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onCheckoutComplete = {
                    // 👇 AHORA VA A CHECKOUT, NO A PRODUCTS
                    navController.navigate("checkout")
                }
            )
        }

        // -------------------- CHECKOUT --------------------
        composable("checkout") {
            CheckoutScreen(navController = navController)
        }

        // -------------------- ORDERS (CLIENT) --------------------
        composable("orders") {
            val currentUser by authViewModel.currentUser.collectAsState()
            val orderViewModel: OrderViewModel = viewModel(factory = factory)

            val isAdmin = currentUser?.role?.name == "ADMIN"

            if (currentUser == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Debes iniciar sesión para ver los pedidos.")
                }
            } else {
                OrderHistoryScreen(
                    orderViewModel = orderViewModel,
                    isAdmin = isAdmin,
                    userId = currentUser!!.id,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // -------------------- 🌐 REMOTE POSTS (API EXTERNA) --------------------
        composable("remote_posts") {
            RemotePostsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // -------------------- PROFILE --------------------
        composable("profile") {
            UserProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onGoToOrders = { navController.navigate("orders") },
                onGoToExternalApi = { navController.navigate("remote_posts") },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
    }
}

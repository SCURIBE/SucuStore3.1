package com.example.sucustore.controller.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Gallery : Route("gallery")
    data object ProductDetail : Route("product/{id}")
    data object Cart : Route("cart")
    data object Checkout : Route("checkout")
    data object Orders : Route("orders")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object Blog : Route("blog")
    data object AdminDashboard : Route("admin/dashboard")
    data object AdminProducts : Route("admin/products")
    data object AdminProductForm : Route("admin/product/form")
    data object AdminOrders : Route("admin/orders")
}

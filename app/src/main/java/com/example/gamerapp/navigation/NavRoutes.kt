package com.example.gamerapp.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Roblox : NavRoutes("roblox")
    object Weather : NavRoutes("weather")
    // Add other routes as needed
} 
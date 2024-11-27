package com.example.gamerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gamerapp.ui.screens.AuthScreen
import com.example.gamerapp.ui.screens.ChatRoomScreen
import com.example.gamerapp.ui.screens.HomeScreen
import com.example.gamerapp.ui.screens.AdminScreen
import androidx.compose.ui.Modifier

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { 
            HomeScreen(navController) 
        }
        composable("chat") { 
            ChatRoomScreen(navController = navController)
        }
        composable("admin") { 
            AdminScreen(navController) 
        }
    }
} 
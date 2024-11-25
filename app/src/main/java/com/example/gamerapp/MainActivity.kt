package com.example.gamerapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamerapp.ui.theme.GamerAppTheme
import com.example.gamerapp.discord.DiscordRPCManager
import com.example.gamerapp.ui.screens.ChatRoomScreen
import com.example.gamerapp.ui.screens.LoginScreen
import com.example.gamerapp.ui.screens.WeatherScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler

class MainActivity : ComponentActivity() {
    private val discordRPC = DiscordRPCManager()
    private val auth = Firebase.auth
    
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("MainActivity", "CoroutineException: ${exception.message}", exception)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Discord RPC
        lifecycleScope.launch(errorHandler) {
            try {
                discordRPC.initialize()
                discordRPC.updatePresence(
                    details = "Browsing the app",
                    state = "In main menu"
                )
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to initialize Discord RPC", e)
            }
        }

        setContent {
            GamerAppTheme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            listOf(
                                "Home" to Icons.Default.Home,
                                "Weather" to Icons.Default.WbSunny,
                                "Chat" to Icons.Default.Chat
                            ).forEachIndexed { index, (route, icon) ->
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = route) },
                                    label = { Text(route) },
                                    selected = navController.currentBackStackEntry?.destination?.route == route,
                                    onClick = {
                                        if (route == "Chat" && !isLoggedIn) {
                                            navController.navigate("login")
                                        } else {
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "Home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Home") { 
                            Greeting("Android")
                        }
                        composable("Weather") {
                            WeatherScreen()
                        }
                        composable("Chat") {
                            if (isLoggedIn) {
                                ChatRoomScreen()
                            } else {
                                navController.navigate("login")
                            }
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    isLoggedIn = true
                                    navController.navigate("Chat") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch(errorHandler) {
            discordRPC.shutdown()
        }
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GamerAppTheme {
        Greeting("Android")
    }
}
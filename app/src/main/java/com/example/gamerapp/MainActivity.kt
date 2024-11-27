package com.example.gamerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.gamerapp.navigation.NavGraph
import com.example.gamerapp.ui.theme.GamerAppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamerAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        NavGraph(navController = navController, modifier = Modifier.padding(padding))
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val items = listOf("Home", "Chat", "Admin")
    val icons = listOf(Icons.Default.Home, Icons.Default.Chat, Icons.Default.Settings)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = currentRoute == item.lowercase(),
                onClick = {
                    navController.navigate(item.lowercase()) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
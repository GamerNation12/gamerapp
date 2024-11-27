package com.example.gamerapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavHostController

@Composable
fun MaintenanceScreen(
    onAdminLogin: (String) -> Unit = {},
    navController: NavHostController
) {
    var maintenanceMessage by remember { mutableStateOf("") }
    var maintenanceTime by remember { mutableStateOf("") }
    var showAdminLogin by remember { mutableStateOf(false) }
    var adminUsername by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Admin credentials
    val adminCredentials = remember {
        mapOf(
            "username" to "GamerNation12",
            "password" to "Hunter2006!"
        )
    }

    // Listen for maintenance status
    LaunchedEffect(Unit) {
        val maintenanceRef = FirebaseDatabase.getInstance().getReference("maintenance")
        maintenanceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val maintenance = snapshot.value as? Map<*, *>
                if (maintenance != null) {
                    maintenanceMessage = maintenance["message"] as? String ?: "Under Maintenance"
                    val timestamp = maintenance["updatedAt"] as? Long
                    if (timestamp != null) {
                        val date = Date(timestamp)
                        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        maintenanceTime = "Started: ${formatter.format(date)}"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                maintenanceMessage = "System under maintenance"
            }
        })
    }

    // Remember if admin login was successful
    var adminLoginSuccess by remember { mutableStateOf(false) }

    // Effect to handle navigation after successful login
    LaunchedEffect(adminLoginSuccess) {
        if (adminLoginSuccess) {
            // Reset the flag
            adminLoginSuccess = false
            // Navigate and clear back stack
            navController.navigate("chat") {
                popUpTo("chat") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D2D2D)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🛠️",
                    style = MaterialTheme.typography.displayMedium
                )
                
                Text(
                    text = "Under Maintenance",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                
                Text(
                    text = maintenanceMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                if (maintenanceTime.isNotEmpty()) {
                    Text(
                        text = maintenanceTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Admin Login Button
                Button(
                    onClick = { showAdminLogin = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text("Admin Login")
                }
            }
        }
    }

    // Admin Login Dialog
    if (showAdminLogin) {
        AlertDialog(
            onDismissRequest = { showAdminLogin = false },
            title = { Text("Admin Login") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = adminUsername,
                        onValueChange = { adminUsername = it.trim() },
                        label = { Text("Username") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it.trim() },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminUsername == adminCredentials["username"] && 
                            adminPassword == adminCredentials["password"]) {
                            onAdminLogin("[ADMIN] $adminUsername")
                            showAdminLogin = false
                            adminLoginSuccess = true  // Set success flag
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid credentials. Please check username and password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminLogin = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 
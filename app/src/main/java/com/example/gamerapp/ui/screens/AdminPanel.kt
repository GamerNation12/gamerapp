package com.example.gamerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gamerapp.data.ChatRepository

@Composable
fun AdminPanel(
    username: String,
    chatRepository: ChatRepository,
    modifier: Modifier = Modifier
) {
    var showBanDialog by remember { mutableStateOf(false) }
    var showUnbanDialog by remember { mutableStateOf(false) }
    var showMaintenanceDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Admin Panel",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Button(
                onClick = {
                    if (confirm("Are you sure you want to clear all messages?")) {
                        chatRepository.clearAllMessages()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All Messages")
            }

            Button(
                onClick = { showBanDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ban User")
            }

            Button(
                onClick = { showUnbanDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unban User")
            }

            Button(
                onClick = { showMaintenanceDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Toggle Maintenance Mode")
            }
        }
    }

    if (showBanDialog) {
        BanUserDialog(
            onDismiss = { showBanDialog = false },
            onBan = { targetUser, reason ->
                chatRepository.banUser(targetUser, reason, username)
            }
        )
    }

    if (showUnbanDialog) {
        UnbanUserDialog(
            onDismiss = { showUnbanDialog = false },
            onUnban = { targetUser ->
                chatRepository.unbanUser(targetUser)
            }
        )
    }

    if (showMaintenanceDialog) {
        MaintenanceDialog(
            onDismiss = { showMaintenanceDialog = false },
            onToggle = { enabled, message ->
                chatRepository.toggleMaintenance(enabled, message, username)
            }
        )
    }
}

@Composable
private fun BanUserDialog(
    onDismiss: () -> Unit,
    onBan: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ban User") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isNotBlank() && reason.isNotBlank()) {
                        onBan(username, reason)
                        onDismiss()
                    }
                }
            ) {
                Text("Ban")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun UnbanUserDialog(
    onDismiss: () -> Unit,
    onUnban: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unban User") },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isNotBlank()) {
                        onUnban(username)
                        onDismiss()
                    }
                }
            ) {
                Text("Unban")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MaintenanceDialog(
    onDismiss: () -> Unit,
    onToggle: (Boolean, String) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Maintenance Mode") },
        text = {
            Column {
                Switch(
                    checked = enabled,
                    onCheckedChange = { enabled = it }
                )
                if (enabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Maintenance Message") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!enabled || message.isNotBlank()) {
                        onToggle(enabled, message)
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun confirm(message: String): Boolean {
    // In a real app, you'd want to show a proper confirmation dialog
    return true
} 
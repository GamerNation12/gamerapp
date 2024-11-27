package com.example.gamerapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gamerapp.data.ChatMessage
import com.example.gamerapp.data.ChatRepository
import com.example.gamerapp.data.UserPreferences
import com.example.gamerapp.viewmodels.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChatRoomScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chatRepository = remember { ChatRepository() }
    val isInMaintenance by chatRepository.isInMaintenance.collectAsState()
    var currentUser by remember { mutableStateOf<String?>(null) }
    var isAdmin by remember { mutableStateOf(false) }

    if (isInMaintenance && !isAdmin) {
        MaintenanceScreen(
            onAdminLogin = { adminUsername ->
                currentUser = adminUsername
                isAdmin = true
            },
            navController = navController
        )
    } else {
        val userPreferences = remember { UserPreferences(context) }
        val coroutineScope = rememberCoroutineScope()
        var username by remember { mutableStateOf(currentUser ?: "") }
        var hasJoinedChat by remember { mutableStateOf(false) }
        var chatViewModel: ChatViewModel? by remember { mutableStateOf(null) }
        var showChangeUsernameDialog by remember { mutableStateOf(false) }

        // Collect saved username
        LaunchedEffect(Unit) {
            userPreferences.username.collect { savedUsername ->
                if (!savedUsername.isNullOrBlank()) {
                    username = savedUsername
                    chatViewModel = ChatViewModel(ChatRepository(), savedUsername)
                    hasJoinedChat = true
                }
            }
        }

        if (showChangeUsernameDialog) {
            ChangeUsernameDialog(
                currentUsername = username,
                onUsernameChange = { newUsername ->
                    if (newUsername.isNotBlank()) {
                        username = newUsername
                        chatViewModel = ChatViewModel(ChatRepository(), newUsername)
                        // Save the new username using coroutineScope
                        coroutineScope.launch {
                            userPreferences.saveUsername(newUsername)
                        }
                    }
                },
                onDismiss = { showChangeUsernameDialog = false }
            )
        }

        if (!hasJoinedChat) {
            LoginScreen(
                username = username,
                onUsernameChange = { username = it },
                onJoinChat = {
                    chatViewModel = ChatViewModel(ChatRepository(), username)
                    hasJoinedChat = true
                    // Save the username using coroutineScope
                    coroutineScope.launch {
                        userPreferences.saveUsername(username)
                    }
                },
                modifier = modifier
            )
        } else {
            chatViewModel?.let { viewModel ->
                ChatContent(
                    navController = navController,
                    chatViewModel = viewModel,
                    currentUsername = username,
                    onChangeUsernameClick = { showChangeUsernameDialog = true },
                    modifier = modifier
                )
            }
        }

        if (username.startsWith("[ADMIN]")) {
            AdminPanel(
                username = username,
                chatRepository = chatRepository,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun LoginScreen(
    username: String,
    onUsernameChange: (String) -> Unit,
    onJoinChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D2D2D)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome to Chat",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Enter your username", color = Color.White) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onJoinChat,
                    enabled = username.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Join Chat",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeUsernameDialog(
    currentUsername: String,
    onUsernameChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newUsername by remember { mutableStateOf(currentUsername) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Username") },
        text = {
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("New Username") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onUsernameChange(newUsername)
                    onDismiss()
                },
                enabled = newUsername.isNotBlank() && newUsername != currentUsername
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

@Composable
private fun ChatContent(
    navController: NavHostController,
    chatViewModel: ChatViewModel,
    currentUsername: String,
    onChangeUsernameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            // Chat header
            Surface(
                color = Color(0xFF2D2D2D),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Chat Room",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(Modifier.weight(1f))
                    // Username with edit button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                currentUsername,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        IconButton(onClick = onChangeUsernameClick) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Change Username",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { message ->
                    MessageItem(
                        message = message,
                        isOwnMessage = message.sender == currentUsername
                    )
                }
            }

            // Message input
            Surface(
                color = Color(0xFF2D2D2D),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF333333),
                            unfocusedContainerColor = Color(0xFF333333)
                        ),
                        placeholder = { Text("Type a message...", color = Color.Gray) }
                    )
                    
                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val sent = chatViewModel.sendMessage(messageText)
                                if (sent) {
                                    messageText = ""
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Please wait a moment before sending another message",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send message",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: ChatMessage,
    isOwnMessage: Boolean
) {
    val alignment = if (isOwnMessage) Alignment.End else Alignment.Start
    val backgroundColor = if (isOwnMessage) 
        MaterialTheme.colorScheme.primary 
    else 
        Color(0xFF2D2D2D)
    
    val timeFormatter = remember { 
        SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwnMessage) 16.dp else 4.dp,
                bottomEnd = if (isOwnMessage) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.sender,
                        color = if (isOwnMessage) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Platform tag
                    Surface(
                        color = if (isOwnMessage) 
                            Color.White.copy(alpha = 0.3f) 
                        else 
                            Color(0xFFE0E0E0),  // Light gray
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = message.platform,  // Will be either "Mobile" or "Website"
                            color = Color.Black,  // Always black text
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.content,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeFormatter.format(Date(message.timestamp)),
                    color = if (isOwnMessage) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
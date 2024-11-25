package com.example.gamerapp.discord

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.time.Instant

class DiscordRPCManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val applicationId = "1234567890" // Replace with your Discord application ID

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("ws://127.0.0.1:6463/?v=1&client_id=$applicationId")
                    .build()

                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        Log.d("DiscordRPC", "Connected to Discord!")
                        identify()
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                        Log.e("DiscordRPC", "WebSocket failure: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                Log.e("DiscordRPC", "Failed to initialize Discord RPC", e)
            }
        }
    }

    private fun identify() {
        val payload = JSONObject().apply {
            put("cmd", "IDENTIFY")
            put("args", JSONObject().apply {
                put("version", "1")
                put("client_id", applicationId)
            })
        }
        webSocket?.send(payload.toString())
    }

    fun updatePresence(
        details: String,
        state: String,
        largeImageKey: String = "app_icon",
        largeImageText: String = "Gamer App"
    ) {
        try {
            val activity = JSONObject().apply {
                put("details", details)
                put("state", state)
                put("timestamps", JSONObject().put("start", Instant.now().epochSecond))
                put("assets", JSONObject().apply {
                    put("large_image", largeImageKey)
                    put("large_text", largeImageText)
                })
            }

            val payload = JSONObject().apply {
                put("cmd", "SET_ACTIVITY")
                put("args", JSONObject().apply {
                    put("pid", android.os.Process.myPid())
                    put("activity", activity)
                })
            }

            webSocket?.send(payload.toString())
        } catch (e: Exception) {
            Log.e("DiscordRPC", "Failed to update presence", e)
        }
    }

    fun shutdown() {
        try {
            webSocket?.close(1000, "Application closing")
            webSocket = null
        } catch (e: Exception) {
            Log.e("DiscordRPC", "Failed to shutdown Discord RPC", e)
        }
    }
} 
package com.example.gamerapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private val usernameKey = stringPreferencesKey("username")

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[usernameKey]
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[usernameKey] = username
        }
    }

    suspend fun clearUsername() {
        context.dataStore.edit { preferences ->
            preferences.remove(usernameKey)
        }
    }
} 
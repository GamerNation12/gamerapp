package com.example.gamerapp.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {
    private const val ALGORITHM = "AES"
    private const val KEY = "YourSecretKey123" // In production, use a secure key management system

    fun encryptMessage(message: String): String {
        val key = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decryptMessage(encryptedMessage: String): String {
        val key = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT))
        return String(decryptedBytes)
    }
} 
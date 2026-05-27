package com.example.data

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {
    private const val ALGORITHM = "AES"

    fun generateSessionKey(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..16)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun encrypt(plainText: String, rawKey: String): String {
        if (plainText.isEmpty()) return ""
        return try {
            // Keep keys standard 16 bytes (128 bits)
            val keyBytes = rawKey.padEnd(16, '0').substring(0, 16).toByteArray(Charsets.UTF_8)
            val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP).trim()
        } catch (e: Exception) {
            // Fallback to simple Base64 obfuscation if crypt fails
            Base64.encodeToString(plainText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP).trim()
        }
    }

    fun decrypt(encryptedText: String, rawKey: String): String {
        if (encryptedText.isEmpty()) return ""
        return try {
            val keyBytes = rawKey.padEnd(16, '0').substring(0, 16).toByteArray(Charsets.UTF_8)
            val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            try {
                // Fallback to decode base64
                val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
                String(decodedBytes, Charsets.UTF_8)
            } catch (ex: Exception) {
                encryptedText
            }
        }
    }
}

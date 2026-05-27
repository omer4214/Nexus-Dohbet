package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getTroubleshootingAnswer(userQuery: String, appStateString: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Hata: AI Studio Secrets panelinden geçerli bir GEMINI_API_KEY tanımlanmalı. \n\n🤖 [Simüle Destek]: Sistem şifrelemesi (%100 Uçtan Uca) aktif görünüyor. Lütfen İnternet Senkronizasyonu ve Şifreleme Anahtarınızı Ayarlar menüsünden inceleyiniz! Sormak istediğiniz işlem: '$userQuery'"
        }

        // System instructions to align behavior
        val systemMessage = """
            Sen Nexus Sohbet uygulamasının entegre şifreli yapay zeka destek asistanısın. Kullanıcıya teknik destek sağla.
            Uygulamanın özellikleri: 
            1. %100 Uçtan Uca AES (Symmetric Cryptography) şifreleme.
            2. Çevrimdışı yerel veritabanı (Room), bulut senkronizasyonu simülasyonu.
            3. Şikayet, engelleme ve arama modülleri.
            4. Karşı taraftan bot yanıtlarının gecikmeli / gerçekçi mütalaa edilmesi ("Bot Rolü" / "Otomatik Sohbet Simülatörü" ayarı).
            
            Kullanıcının mevcut uygulama durumu: $appStateString
            Cevaplarını her zaman cana yakın, kısa ve Türkçe olarak sağla. Çözüm odaklı yönerge ver.
        """.trimIndent()

        try {
            // Build the body JSON manually without reflection
            val jsonPayload = JSONObject().apply {
                // systemInstruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemMessage)
                        })
                    })
                })
                // contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userQuery)
                            })
                        })
                    })
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val httpRequest = Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build()

            client.newCall(httpRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Destek asistanı sunucu hatası (Kod ${response.code}): ${response.message}. Lütfen internetinizi veya şifreleme anahtarınızı kontrol edin."
                }
                val bodyString = response.body?.string() ?: return@withContext "Boş yanıt alındı."
                val rootJson = JSONObject(bodyString)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val contentObj = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "Cevap metni bulunamadı.")
                    }
                }
                "Destek asistanı şu anda cevap üretemedi. Lütfen daha sonra tekrar deneyin."
            }
        } catch (e: Exception) {
            "Hata oluştu: ${e.localizedMessage}. \nLütfen internet bağlantınızı kontrol edip tekrar deneyin."
        }
    }
}

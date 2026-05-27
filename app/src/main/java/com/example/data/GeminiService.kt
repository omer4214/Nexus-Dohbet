package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Moshi Models for Gemini ---
class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

class GeminiContent(
    val parts: List<GeminiPart>
)

class GeminiPart(
    val text: String
)

class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

object GeminiHelper {
    suspend fun getTroubleshootingAnswer(userQuery: String, appStateString: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Hata: AI Studio Secrets panelinden geçerli bir GEMINI_API_KEY tanımlanmalı. \n\n🤖 [Simüle Destek]: Sistem şifrelemesi (%100 Uçtan Uca) aktif görünüyor. Lütfen İnternet Senkronizasyonu ve Şifreleme Anahtarınızı Ayarlar menüsünden inceleyiniz! Sormak istediğiniz işlem: '$userQuery'"
        }

        // Crafting the helper assistant system instructions
        val systemMessage = """
            Sen Nexus Sohbet uygulamasının entegre destek asistanısın. Kullanıcıya teknik destek sağla.
            Uygulamanın özellikleri: 
            1. %100 Uçtan Uca AES (Symmetric Cryptography) şifreleme.
            2. Çevrimdışı yerel veritabanı (Room).
            3. Sunucu senkronizasyon simülasyonu.
            4. Şikayet ve engelleme sistemi.
            5. Gelişmiş sesli/görüntülü taklit aramalar, ses kayıtları ve resim gönderme özellikleri.
            
            Kullanıcının mevcut uygulama durumu: $appStateString
            Cevaplarını her zaman anlaşılır, cana yakın ve Türkçe olarak sağla. Çözüm sunmaya odaklan.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = userQuery)))
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemMessage)))
        )

        try {
            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Destek asistanı şu anda cevap üretemedi. Lütfen daha sonra tekrar deneyin."
        } catch (e: Exception) {
            "Hata oluştu: ${e.localizedMessage}. \nLütfen internet bağlantınızı kontrol edin veya Ayarlar menüsünden Şifreleme Anahtarınızı yeniden oluşturun."
        }
    }
}

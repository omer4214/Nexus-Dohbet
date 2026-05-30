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
        // Highly intelligent and specific local fallback responses for Nexus App questions
        val query = userQuery.lowercase(java.util.Locale("tr", "TR"))
        val localResponse = when {
            query.contains("ekle") || query.contains("arkadaş") || query.contains("eposta") || query.contains("e-posta") || query.contains("hesap") -> {
                "🛡️ **Gelişmiş Arkadaş Ekleme Desteği:**\nKişi ekleme sistemi tamamen yenilendi! Artık arkadaşınızın e-postası kayıtlı olmasa bile sistem o e-postayı otomatik olarak siber ağ geçidinde tanımlayıp arkadaş listenize ekleyecektir.\n\n**Nasıl Arkadaş Eklenir?**\n1. Sol alttaki **Sohbetler** ya da **Kişiler** sayfasına gidin.\n2. Sağ alttaki mavi renkli yuvarlak **Arkadaş Ekle** (+ kişi) butonuna dokunun.\n3. Arkadaşınızın adını, e-postasını ve numarasını girip onaylayın.\n4. Sistem arkadaşınızı anında profilinize bağlayacaktır! İletişim tamamen uçtan uca şifrelenir."
            }
            query.contains("mesaj") || query.contains("bot") || query.contains("sohbet") || query.contains("gönder") || query.contains("ileti") || query.contains("yazış") -> {
                "💬 **Güvenli Sohbet & Bot Özellikleri:**\nGeri bildirimleriniz doğrultusunda **Yapay Zeka Otomatik Yanıt (Bot) sistemi tamamen kaldırılmıştır**. Artık tüm sohbet kartlarınız tamamen temiz, sade ve parazitsiz siber iletişim sunar.\n\n- **Mesaj Durumları:** Karşı taraf çevrimiçiyse mesaj gönderdiğinizde sırasıyla ✔ (Gönderildi), ✔✔ (İletildi) ve ✔✔ (Okundu) bildirimleri animasyonlu olarak gösterilir.\n- **Medya Gönderme:** Sohbet içinde '+' ikonuna basarak şifreli fotoğraf veya ses kaydı simüle edebilirsiniz."
            }
            query.contains("şifre") || query.contains("aes") || query.contains("rsa") || query.contains("kripto") || query.contains("güvenlik") || query.contains("key") -> {
                "🔒 **Askeri Düzey Kriptoloji Protokolü:**\nNexus Sohbet, sistem genelinde veri sızmasını önlemek amacıyla üst düzey kriptoloji standartları barındırır:\n- **Gelişmiş AES-256:** Mesajlar veritabanına kaydedilmeden önce simetrik AES anahtarı ile şifrelenir. Veritabanı dosyası deşifre edilmeden okunamaz.\n- **Asimetrik RSA Anahtarları:** Her profil için otomatik RSA anahtar çifti oluşturulur.\n- **Parametreleri İnceleme:** Kendi özel AES ve RSA kriptoloji anahtarlarınızı **Ayarlar** sekmesi altındaki 'Askeri Kriptolojik Parametreler' alanında canlı olarak görebilirsiniz."
            }
            query.contains("profil") || query.contains("ayar") || query.contains("isim") || query.contains("biyografi") || query.contains("bio") || query.contains("avatar") || query.contains("durum") -> {
                "👤 **Profil ve Görünüm Özelleştirme:**\nProfilinizi saniyeler içinde zenginleştirebilirsiniz:\n1. Ekranın altındaki **Ayarlar** (dişli) tabına geçin.\n2. En üstteki profil kartınızın hemen altındaki **Kullanıcı Profilini Düzenle** alanından 'Kalem' butonuna basın.\n3. Buradan profil adınızı, biyografinizi, telefon numaranızı değiştirebilir, 6 heyecan verici renk paletinden dilediğinizi seçebilir ve 4 farklı özel avatar ikonundan birini belirleyebilirsiniz.\n4. Değişiklikleri doğrulamak için **Bilgileri Kaydet** butonuna basın!"
            }
            else -> {
                "🤖 **Nexus Entegre Siber Yapay Zeka Destek:**\nMerhaba! Nexus şifreli sohbet ve askeri kriptolojik ağ geçidi rehberine hoş geldiniz. Size nasıl yardımcı olabilirim?\n\n**Size Nasıl Yardımcı Olabilirim?**\n- **Kişi Ekleme:** Eklemek istediğiniz e-posta bulunamadı hatası giderildi, artık her girdi otomatik simüle edilerek eklenir.\n- **Bot Sohbetleri:** İstekleriniz üzerine tüm otomatik bot yanıtları temizlendi.\n- **Profil Ayarları:** Sağ alttaki Ayarlar menüsünden adınızı, hakkınızda (biyografi) yazısını, durum iletinizi ve avatar temasını güncelleyebilirsiniz.\n- **E2EE Anahtarları:** Şifreleme parametrelerini Ayarlar sayfasından inceleyebilirsiniz.\n\nLütfen sormak istediğiniz konuyu (örn: 'arkadaş ekle', 'profil ayarları', 'kriptoloji') girip bana iletin!"
            }
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext localResponse
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
                    return@withContext localResponse
                }
                val bodyString = response.body?.string() ?: return@withContext localResponse
                val rootJson = JSONObject(bodyString)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val contentObj = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", localResponse)
                    }
                }
                localResponse
            }
        } catch (e: Exception) {
            localResponse
        }
    }
}

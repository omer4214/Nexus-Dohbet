package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NexusRepository

    init {
        val db = NexusDatabase.getDatabase(application)
        repository = NexusRepository(db)

        // Prepopulate db and load user profile
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
            val existingProfile = repository.getProfileDirect()
            if (existingProfile == null) {
                // We'll let them complete sign up, or auto-log on demo
                _isLoggedIn.value = false
            } else {
                _currentUser.value = existingProfile
                _isLoggedIn.value = true
                _selectedTheme.value = existingProfile.themeChoice
            }
        }
    }

    // --- State Management ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<ProfileEntity?>(null)
    val currentUser: StateFlow<ProfileEntity?> = _currentUser.asStateFlow()

    private val _selectedTheme = MutableStateFlow("Dark") // default is attractive dark
    val selectedTheme: StateFlow<String> = _selectedTheme.asStateFlow()

    // Active screen navigation inside Home
    // Tabs: 0 -> Sohbetler, 1 -> Arkadaşlar, 2 -> Topluluklar, 3 -> Geçmiş, 4 -> Ayarlar & Destek
    val currentTab = mutableStateOf(0)

    // Conversations lists & queries
    val contacts: StateFlow<List<ContactEntity>> = repository.allContactsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communities: StateFlow<List<CommunityEntity>> = repository.allCommunitiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callHistory: StateFlow<List<CallHistoryEntity>> = repository.callHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat Session
    private val _activeChatContact = MutableStateFlow<ContactEntity?>(null)
    val activeChatContact: StateFlow<ContactEntity?> = _activeChatContact.asStateFlow()

    // Get messages flow for currently active chat
    val activeChatMessages: StateFlow<List<MessageEntity>> = _activeChatContact
        .flatMapLatest { contact ->
            if (contact != null) {
                repository.getMessagesForChatFlow(contact.emailOrPhone)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Calls ---
    private val _activeCallState = MutableStateFlow<CallSession?>(null)
    val activeCallState: StateFlow<CallSession?> = _activeCallState.asStateFlow()

    data class CallSession(
        val contactName: String,
        val isVideo: Boolean,
        val durationSeconds: Int = 0
    )

    // --- Cloud Syncing State ---
    private val _syncState = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    sealed interface SyncStatus {
        object Idle : SyncStatus
        object Syncing : SyncStatus
        class Completed(val message: String) : SyncStatus
        class Failed(val error: String) : SyncStatus
    }

    // --- Gemini Support Assistant State ---
    private val _geminiSupportHistory = MutableStateFlow<List<SupportMessage>>(listOf(
        SupportMessage("asistan", "Merhaba! Nexus Sohbet Destek Asistanına hoş geldiniz. 🤖 Uygulama içindeki şifreleme, entegrasyon, yedekler veya ayarlar ile ilgili çözemediğiniz her şeyi bana danışabilirsiniz!")
    ))
    val geminiSupportHistory: StateFlow<List<SupportMessage>> = _geminiSupportHistory.asStateFlow()

    private val _geminiLoading = MutableStateFlow(false)
    val geminiLoading: StateFlow<Boolean> = _geminiLoading.asStateFlow()

    data class SupportMessage(val sender: String, val message: String, val timestamp: Long = System.currentTimeMillis())

    // --- Authentication Actions ---
    fun registerAndLogin(email: String, fullName: String, phone: String, passwordConfirm: String) {
        viewModelScope.launch {
            val key = CryptoHelper.generateSessionKey()
            val newProfile = ProfileEntity(
                email = email,
                fullName = fullName,
                phoneNumber = phone,
                passwordHash = passwordConfirm, 
                themeChoice = _selectedTheme.value,
                rsaPrivateKey = "PRV_KEY_" + CryptoHelper.generateSessionKey().substring(0, 8),
                rsaPublicKey = "PUB_KEY_" + CryptoHelper.generateSessionKey().substring(0, 8),
                aesSessionKey = key
            )
            repository.insertProfile(newProfile)
            _currentUser.value = newProfile
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        // Keep DB but remove session pointer
    }

    // --- Profile & Theme Management ---
    fun updateProfileDetails(name: String, phone: String) {
        val email = _currentUser.value?.email ?: return
        viewModelScope.launch {
            repository.updateDetails(email, name, phone)
            val updated = repository.getProfileDirect()
            _currentUser.value = updated
        }
    }

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
        val email = _currentUser.value?.email ?: return
        viewModelScope.launch {
            repository.updateTheme(email, theme)
        }
    }

    // --- Contact & Chat Management ---
    fun selectChat(contact: ContactEntity?) {
        _activeChatContact.value = contact
    }

    fun addFriend(name: String, emailOrPhone: String, phone: String) {
        viewModelScope.launch {
            val newFriend = ContactEntity(
                emailOrPhone = emailOrPhone,
                name = name,
                phoneNumber = phone,
                statusText = "Bağlantı isteği onay bekliyor... ⏳",
                isOnline = true,
                isBlocked = false,
                isReported = false,
                profileColorHex = listOf("#E91E63", "#4CAF50", "#2196F3", "#9C27B0", "#FFC107").random(),
                lastSeenTime = "Çevrimiçi",
                isPendingApproval = true
            )
            repository.insertContact(newFriend)
        }
    }

    fun acceptFriendRequest(contactId: String, customizedName: String) {
        viewModelScope.launch {
            val contact = contacts.value.find { it.emailOrPhone == contactId } ?: return@launch
            val updatedContact = contact.copy(
                name = customizedName,
                isPendingApproval = false,
                statusText = "Nexus ile şifreli hat aktif 7/24 🛡️"
            )
            repository.insertContact(updatedContact)
            if (_activeChatContact.value?.emailOrPhone == contactId) {
                _activeChatContact.value = updatedContact
            }
            // Insert a greeting system message in chat
            val aesKey = _currentUser.value?.aesSessionKey ?: "NexusSharedDemoKey"
            val welcomeMsg = MessageEntity(
                chatId = contactId,
                senderEmail = "system",
                recipientEmail = "self",
                plainContent = "✅ Güvenli bağlantı onaylandı! Arkadaşınızla asimetrik RSA & AES-128 anahtarları başarıyla değiş tokuş edildi. Adı '$customizedName' olarak ayarlandı.",
                encryptedPayload = CryptoHelper.encrypt("System Welcome Connection Approval Message", aesKey),
                status = "Read",
                contentType = "text"
            )
            repository.insertMessage(welcomeMsg)
        }
    }

    fun rejectFriendRequest(contactId: String) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
            if (_activeChatContact.value?.emailOrPhone == contactId) {
                _activeChatContact.value = null
            }
        }
    }

    fun blockContact(contactId: String, blocked: Boolean) {
        viewModelScope.launch {
            repository.updateBlockedStatus(contactId, blocked)
            // Update active cached contact if blocked
            val active = _activeChatContact.value
            if (active?.emailOrPhone == contactId) {
                _activeChatContact.value = active.copy(isBlocked = blocked)
            }
        }
    }

    fun clearChat(chatId: String) {
        viewModelScope.launch {
            repository.clearChat(chatId)
        }
    }

    fun clearCallHistory() {
        viewModelScope.launch {
            repository.clearCallHistory()
        }
    }

    fun reportContact(contactId: String, reason: String) {
        viewModelScope.launch {
            repository.updateReportedStatus(contactId, true)
            // Insert systematic alert message in chat
            val aesKey = _currentUser.value?.aesSessionKey ?: "NexusSharedDemoKey"
            val noticeMsg = MessageEntity(
                chatId = contactId,
                senderEmail = "system",
                recipientEmail = "self",
                plainContent = "⚠️ Bu kullanıcıyı raporladınız. Şikayet Gerekçesi: '$reason'. Güvenlik ekibimiz uçtan uca şifreli meta verileri inceliyor.",
                encryptedPayload = CryptoHelper.encrypt("System Block Warning Notice", aesKey),
                status = "Read",
                contentType = "text"
            )
            repository.insertMessage(noticeMsg)

            val active = _activeChatContact.value
            if (active?.emailOrPhone == contactId) {
                _activeChatContact.value = active.copy(isReported = true)
            }
        }
    }

    // --- Messaging Actions (AES E2EE encryption) ---
    fun sendEncryptedMessage(content: String, type: String = "text", mediaUri: String? = null, duration: String? = null) {
        val active = _activeChatContact.value ?: return
        if (active.isBlocked) return

        val user = _currentUser.value
        val sender = user?.email ?: "ben@nexus.com"
        val aesKey = user?.aesSessionKey ?: "NexusSharedDemoKey"

        // Encrypt content
        val ciphertext = CryptoHelper.encrypt(content, aesKey)

        val newMessage = MessageEntity(
            chatId = active.emailOrPhone,
            senderEmail = sender,
            recipientEmail = active.emailOrPhone,
            plainContent = content,
            encryptedPayload = ciphertext,
            status = "Sent",
            contentType = type,
            mediaUri = mediaUri,
            mediaDuration = duration
        )

        viewModelScope.launch {
            val insertedId = repository.insertMessage(newMessage)
            val savedMessage = newMessage.copy(id = insertedId)
            
            // Simulating ticking checks (Sent -> Delivered -> Read) on the SAME message ID
            delay(500)
            repository.insertMessage(savedMessage.copy(status = "Delivered"))
            delay(500)
            repository.insertMessage(savedMessage.copy(status = "Read"))

            // Trigger beautiful simulated automated reply from contact to make the app feel interactive
            delay(1000)
            triggerSimulatedReply(active, content)
        }
    }

    private suspend fun triggerSimulatedReply(fromContact: ContactEntity, userPrompt: String) {
        val user = _currentUser.value
        val aesKey = user?.aesSessionKey ?: "NexusSharedDemoKey"

        val replies = listOf(
            "Anladım! 🔒 Mesajını başarıyla aldım ve uçtan uca deşifre ettim.",
            "Nexus üzerinden yazışmak harika, sıfır dinleme sıfır gecikme! 🚀 Dediğin konuyu detaylandıralım.",
            "Mesajın şifreli geldi: (${CryptoHelper.encrypt(userPrompt.take(10), aesKey).take(12)}...) fakat anında çözüldü! Harika.",
            "Harika bir özellik! Resim, ses kaydı ve dosyalar sorunsuz çalışıyor. 👍",
            "Kesinlikle! Sunucu senkronizasyonunu başlattın mı? Verilerimiz güvende kalsın.",
            "Nexus Sohbet çok hızlı! Sıfır kasma, sıfır donma."
        )

        val responseContent = when {
            fromContact.isPendingApproval -> "Görünüşe göre güvenli bağlantımız henüz onaylanmamış! Mesajını aldım fakat şifreli el sıkışmayı (handshake) tamamlamak için yukarıdaki 'Arkadaşlığı Kabul Et' butonuna tıklamalısın. Kabul edince bana isim verebilirsin! 😊"
            userPrompt.contains("merhaba", ignoreCase = true) -> "Merhaba! Nexus şifreli hattımız 7/24 aktif."
            userPrompt.contains("resim", ignoreCase = true) || userPrompt.contains("foto", ignoreCase = true) -> "Resim sorunsuz aktarıldı, şifreli galerime kaydedildi! 📱"
            userPrompt.contains("ses", ignoreCase = true) -> "Gönderdiğin ses kaydını pürüzsüz dinledim, mükemmel netlik! 🔊"
            else -> replies.random()
        }

        val replyMsg = MessageEntity(
            chatId = fromContact.emailOrPhone,
            senderEmail = fromContact.emailOrPhone,
            recipientEmail = user?.email ?: "ben@nexus.com",
            plainContent = responseContent,
            encryptedPayload = CryptoHelper.encrypt(responseContent, aesKey),
            status = "Read",
            contentType = "text"
        )
        repository.insertMessage(replyMsg)
    }

    // --- Media simulation helpers ---
    fun simulatePhotoSend() {
        val visualUri = "https://images.unsplash.com/photo-1549488344-1f9b8d2bd1f3?auto=format&fit=crop&w=400&q=80"
        sendEncryptedMessage("📷 [Resim Gönderildi]", "image", visualUri)
    }

    fun simulateVoiceSend() {
        sendEncryptedMessage("🎤 [Ses Kaydı]", "audio", "simulated_voice_recording.mp3", "0:08")
    }

    fun simulateFileSend() {
        sendEncryptedMessage("📄 nexus_sohbet_e2ee_dokuman.pdf", "file", "documents/nexus_sohbet_e2ee_dokuman.pdf")
    }

    // --- Call Simulation Actions ---
    fun startCall(isVideo: Boolean) {
        val active = _activeChatContact.value ?: return
        _activeCallState.value = CallSession(contactName = active.name, isVideo = isVideo, durationSeconds = 0)
        
        // Start counting active seconds
        viewModelScope.launch {
            while (_activeCallState.value != null) {
                delay(1000)
                val current = _activeCallState.value ?: break
                _activeCallState.value = current.copy(durationSeconds = current.durationSeconds + 1)
            }
        }
    }

    fun endCall() {
        val currentCall = _activeCallState.value ?: return
        viewModelScope.launch {
            // Write to call history
            repository.insertCall(
                CallHistoryEntity(
                    contactName = currentCall.contactName,
                    phoneNumber = "+90 (555) Nexus",
                    callType = if (currentCall.isVideo) "Görüntülü" else "Sesli",
                    isMissed = false,
                    isOutgoing = true
                )
            )
            _activeCallState.value = null
        }
    }

    // --- Cloud Synchronization Simulation ---
    fun triggerCloudSync() {
        viewModelScope.launch {
            _syncState.value = SyncStatus.Syncing
            // Simulate authentic cloud encryption uploading
            delay(1200)
            val user = _currentUser.value
            val secureAesHash = user?.aesSessionKey?.hashCode() ?: "7f8fa901".hashCode()
            delay(1000)
            _syncState.value = SyncStatus.Completed(
                "Bulut eşitlemesi ve yedekleme başarılı! Sharding ve AES key anahtarı '$secureAesHash' ile veriler güvenli bir şekilde senkronize edildi."
            )
            delay(4000)
            _syncState.value = SyncStatus.Idle
        }
    }

    // --- Gemini Support Assistant AI Interactions ---
    fun sendSupportPrompt(prompt: String) {
        if (prompt.trim().isEmpty()) return
        
        val userMsg = SupportMessage("kullanıcı", prompt)
        _geminiSupportHistory.value = _geminiSupportHistory.value + userMsg
        
        _geminiLoading.value = true
        
        viewModelScope.launch {
            val appState = """
                User: ${_currentUser.value?.fullName} / ${_currentUser.value?.email}
                Active Key: ${_currentUser.value?.aesSessionKey ?: "Default"}
                ContactsCount: ${contacts.value.size}
                Theme: ${_selectedTheme.value}
            """.trimIndent()
            
            val answer = GeminiHelper.getTroubleshootingAnswer(prompt, appState)
            
            _geminiSupportHistory.value = _geminiSupportHistory.value + SupportMessage("asistan", answer)
            _geminiLoading.value = false
        }
    }

    fun clearSupportChat() {
        _geminiSupportHistory.value = listOf(
            SupportMessage("asistan", "Merhaba! Nexus Destek asistanı sıfırlandı. Çözemediğiniz herhangi bir problemi buradan bana yazarak çözebilirsiniz! 🛡️")
        )
    }
}

package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NexusRepository(private val db: NexusDatabase) {

    private val userDao = db.userDao()
    private val contactDao = db.contactDao()
    private val messageDao = db.messageDao()
    private val communityDao = db.communityDao()
    private val callHistoryDao = db.callHistoryDao()
    private val accountDao = db.accountDao()

    // --- Firebase Accounts Simulation ---
    suspend fun getAccountByEmail(email: String): AccountEntity? = withContext(Dispatchers.IO) {
        accountDao.getAccountByEmail(email)
    }

    suspend fun authenticate(email: String, password: String): AccountEntity? = withContext(Dispatchers.IO) {
        accountDao.authenticate(email, password)
    }

    suspend fun insertAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
    }

    // --- User Profile ---
    val profileFlow: Flow<ProfileEntity?> = userDao.getProfileFlow()
    
    suspend fun getProfileDirect(): ProfileEntity? = withContext(Dispatchers.IO) {
        userDao.getProfileDirect()
    }

    suspend fun insertProfile(profile: ProfileEntity) = withContext(Dispatchers.IO) {
        userDao.insertProfile(profile)
    }

    suspend fun updateTheme(email: String, theme: String) = withContext(Dispatchers.IO) {
        userDao.updateTheme(email, theme)
    }

    suspend fun updateDetails(email: String, name: String, phone: String) = withContext(Dispatchers.IO) {
        userDao.updateDetails(email, name, phone)
    }

    suspend fun updateDetailsFull(email: String, name: String, phone: String, bio: String, statusMsg: String, avatarColor: String, avatarIndex: Int) = withContext(Dispatchers.IO) {
        userDao.updateDetailsFull(email, name, phone, bio, statusMsg, avatarColor, avatarIndex)
    }

    // --- Contacts ---
    fun getAllContactsFlow(ownerEmail: String): Flow<List<ContactEntity>> = contactDao.getAllContactsFlow(ownerEmail)

    suspend fun insertContact(contact: ContactEntity) = withContext(Dispatchers.IO) {
        contactDao.insertContact(contact)
    }

    suspend fun updateBlockedStatus(ownerEmail: String, id: String, blocked: Boolean) = withContext(Dispatchers.IO) {
        contactDao.updateBlockedStatus(ownerEmail, id, blocked)
    }

    suspend fun updateReportedStatus(ownerEmail: String, id: String, reported: Boolean) = withContext(Dispatchers.IO) {
        contactDao.updateReportedStatus(ownerEmail, id, reported)
    }

    suspend fun deleteContact(ownerEmail: String, id: String) = withContext(Dispatchers.IO) {
        contactDao.deleteContact(ownerEmail, id)
    }

    // --- Messages ---
    fun getMessagesForChatFlow(userEmail: String, contactEmail: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChatFlow(userEmail, contactEmail)

    suspend fun insertMessage(message: MessageEntity): Long = withContext(Dispatchers.IO) {
        messageDao.insertMessage(message)
    }

    suspend fun deleteMessage(messageId: Long) = withContext(Dispatchers.IO) {
        messageDao.deleteMessage(messageId)
    }

    suspend fun clearChat(chatId: String) = withContext(Dispatchers.IO) {
        messageDao.clearChat(chatId)
    }

    // --- Communities ---
    val allCommunitiesFlow: Flow<List<CommunityEntity>> = communityDao.getAllCommunitiesFlow()

    suspend fun insertCommunity(community: CommunityEntity) = withContext(Dispatchers.IO) {
        communityDao.insertCommunity(community)
    }

    // --- Call History ---
    val callHistoryFlow: Flow<List<CallHistoryEntity>> = callHistoryDao.getCallHistoryFlow()

    suspend fun insertCall(call: CallHistoryEntity) = withContext(Dispatchers.IO) {
        callHistoryDao.insertCall(call)
    }

    suspend fun clearCallHistory() = withContext(Dispatchers.IO) {
        callHistoryDao.clearHistory()
    }

    // --- Dynamic Seeding / Prepopulate ---
    suspend fun prepopulateIfNeeded() = withContext(Dispatchers.IO) {
        // Core mock registered accounts directory (Simulated Firebase Users Directory)
        val defaultAccounts = listOf(
            AccountEntity("56celikomer@gmail.com", "Ömer Çelik", "+90 (555) 777 56 56", "admin1212"),
            AccountEntity("ayse@nexus.com", "Ayşe Yılmaz", "+90 (532) 111 22 33", "sifre123"),
            AccountEntity("mehmet@nexus.com", "Mehmet Yıldız", "+90 (544) 444 55 66", "sifre123"),
            AccountEntity("elif@nexus.com", "Elif Kaya", "+90 (505) 555 66 77", "sifre123"),
            AccountEntity("can@nexus.com", "Can Demir", "+90 (533) 999 88 77", "sifre123")
        )
        
        for (acc in defaultAccounts) {
            accountDao.insertAccount(acc)
        }

        // Seed a pending incoming friend request from Mehmet to show off approval flows for the default demo profile
        val existing = db.contactDao().getAllContactsFlow("56celikomer@gmail.com").firstOrNull() ?: emptyList()
        if (existing.isEmpty()) {
            val pendingFromMehmet = ContactEntity(
                ownerEmail = "56celikomer@gmail.com",
                emailOrPhone = "mehmet@nexus.com",
                name = "Mehmet Yıldız",
                phoneNumber = "+90 (544) 444 55 66",
                statusText = "Uçtan uca şifreli bağlantı kurmak istiyor... ⏳",
                isOnline = true, // Shows online when approved
                isBlocked = false,
                isReported = false,
                profileColorHex = "#9C27B0", // purple
                lastSeenTime = "Çevrimiçi",
                isPendingApproval = true // Yes, this is an incoming pending request!
            )
            db.contactDao().insertContact(pendingFromMehmet)

            // Seed a default community
            val communityHub = CommunityEntity(
                id = "cyber_sec_union",
                name = "Siber Güvenlik Birliği 📡",
                desc = "Uçtan uca şifreli siber ağ operasyonları, veri gizliliği ve güvenlik protokolleri bilgi paylaşım kanalı.",
                membersCount = 143,
                adminName = "Siber Savunma Merkezi",
                iconColorHex = "#00A884"
            )
            db.communityDao().insertCommunity(communityHub)
        }
    }
}

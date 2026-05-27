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

    // --- Contacts ---
    val allContactsFlow: Flow<List<ContactEntity>> = contactDao.getAllContactsFlow()

    suspend fun insertContact(contact: ContactEntity) = withContext(Dispatchers.IO) {
        contactDao.insertContact(contact)
    }

    suspend fun updateBlockedStatus(id: String, blocked: Boolean) = withContext(Dispatchers.IO) {
        contactDao.updateBlockedStatus(id, blocked)
    }

    suspend fun updateReportedStatus(id: String, reported: Boolean) = withContext(Dispatchers.IO) {
        contactDao.updateReportedStatus(id, reported)
    }

    suspend fun deleteContact(id: String) = withContext(Dispatchers.IO) {
        contactDao.deleteContact(id)
    }

    // --- Messages ---
    fun getMessagesForChatFlow(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChatFlow(chatId)

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
        // No seed data added. Starting with a 100% clean, empty workspace for the user.
    }
}

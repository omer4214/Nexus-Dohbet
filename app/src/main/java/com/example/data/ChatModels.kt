package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- 1. USER PROFILE ENTITY ---
@Entity(tableName = "user_profile")
data class ProfileEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val phoneNumber: String,
    val passwordHash: String,
    val themeChoice: String, // "Light", "Dark", "System"
    val rsaPrivateKey: String,
    val rsaPublicKey: String,
    val aesSessionKey: String,
    val bio: String = "Siber güvenlik uzmanı, Nexus korumalı.",
    val statusMessage: String = "Müsait",
    val avatarColorHex: String = "#00A884",
    val avatarIndex: Int = 0
)

// --- 2. CONTACT / FRIEND ENTITY ---
@Entity(tableName = "contacts", primaryKeys = ["ownerEmail", "emailOrPhone"])
data class ContactEntity(
    val ownerEmail: String,
    val emailOrPhone: String,
    val name: String,
    val phoneNumber: String,
    val statusText: String,
    val isOnline: Boolean,
    val isBlocked: Boolean,
    val isReported: Boolean,
    val profileColorHex: String,
    val lastSeenTime: String,
    val isPendingApproval: Boolean = false
)

// --- 3. MESSAGE ENTITY (E2EE) ---
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: String, // Email or phone of the conversation
    val senderEmail: String,
    val recipientEmail: String,
    val plainContent: String, // Plaintext shown to user after decryption
    val encryptedPayload: String, // Base64 encrypted presentation for security demonstration
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "Sent", "Delivered", "Read"
    val contentType: String, // "text", "image", "file", "audio"
    val mediaUri: String? = null, // Mock local file/image URL or base64 visual
    val mediaDuration: String? = null // Duration for audio files
)

// --- 4. COMMUNITY / GROUP ENTITY ---
@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val desc: String,
    val membersCount: Int,
    val adminName: String,
    val iconColorHex: String
)

// --- 5. CALL HISTORY ENTITY ---
@Entity(tableName = "call_history")
data class CallHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val callType: String, // "Sesli", "Görüntülü"
    val timestamp: Long = System.currentTimeMillis(),
    val isMissed: Boolean,
    val isOutgoing: Boolean
)

// --- DAOS ---

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getProfileFlow(): Flow<ProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getProfileDirect(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("UPDATE user_profile SET themeChoice = :theme WHERE email = :email")
    suspend fun updateTheme(email: String, theme: String)

    @Query("UPDATE user_profile SET fullName = :name, phoneNumber = :phone WHERE email = :email")
    suspend fun updateDetails(email: String, name: String, phone: String)

    @Query("UPDATE user_profile SET fullName = :name, phoneNumber = :phone, bio = :bio, statusMessage = :statusMsg, avatarColorHex = :avatarColor, avatarIndex = :avatarIndex WHERE email = :email")
    suspend fun updateDetailsFull(email: String, name: String, phone: String, bio: String, statusMsg: String, avatarColor: String, avatarIndex: Int)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE ownerEmail = :ownerEmail ORDER BY isOnline DESC, name ASC")
    fun getAllContactsFlow(ownerEmail: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("UPDATE contacts SET isBlocked = :blocked WHERE ownerEmail = :ownerEmail AND emailOrPhone = :id")
    suspend fun updateBlockedStatus(ownerEmail: String, id: String, blocked: Boolean)

    @Query("UPDATE contacts SET isReported = :reported WHERE ownerEmail = :ownerEmail AND emailOrPhone = :id")
    suspend fun updateReportedStatus(ownerEmail: String, id: String, reported: Boolean)

    @Query("DELETE FROM contacts WHERE ownerEmail = :ownerEmail AND emailOrPhone = :id")
    suspend fun deleteContact(ownerEmail: String, id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE (senderEmail = :userEmail AND recipientEmail = :contactEmail) OR (senderEmail = :contactEmail AND recipientEmail = :userEmail) ORDER BY timestamp ASC")
    fun getMessagesForChatFlow(userEmail: String, contactEmail: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChat(chatId: String)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM communities ORDER BY membersCount DESC")
    fun getAllCommunitiesFlow(): Flow<List<CommunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: CommunityEntity)
}

@Dao
interface CallHistoryDao {
    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun getCallHistoryFlow(): Flow<List<CallHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: CallHistoryEntity)

    @Query("DELETE FROM call_history")
    suspend fun clearHistory()
}

// --- 6. REGISTERED ACCOUNT DIRECTORY (SIMULATED CLOUD / FIREBASE DB) ---
@Entity(tableName = "nexus_accounts")
data class AccountEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val phoneNumber: String,
    val passwordHash: String
)

@Dao
interface AccountDao {
    @Query("SELECT * FROM nexus_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAccountByEmail(email: String): AccountEntity?

    @Query("SELECT * FROM nexus_accounts WHERE LOWER(email) = LOWER(:email) AND passwordHash = :password LIMIT 1")
    suspend fun authenticate(email: String, password: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Query("SELECT * FROM nexus_accounts")
    suspend fun getAllAccounts(): List<AccountEntity>
}

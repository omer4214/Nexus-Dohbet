package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallHistoryEntity
import com.example.data.CommunityEntity
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.ui.NexusViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- 1. MAIN NAVIGATION CONTAINER ---
@Composable
fun NexusMainContainer(viewModel: NexusViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(viewModel = viewModel)
    } else {
        val activeContact by viewModel.activeChatContact.collectAsState()
        if (activeContact != null) {
            ChatConversationScreen(viewModel = viewModel, contact = activeContact!!)
        } else {
            HomeScreen(viewModel = viewModel)
        }
    }
}

// --- 2. SIGN IN / UP ONBOARDING SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: NexusViewModel) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                // Secure E2EE Logo Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Shield",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "NEXUS SOHBET",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.SansSerif
                )
                
                Text(
                    text = "Uçtan Uca Şifreli Askeri Düzey Güvenlik",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Input Fields Form Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Güvenli Hesap Oluşturun",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-posta Adresi") },
                        placeholder = { Text("ornek@nexus.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Adınız Soyadınız") },
                        placeholder = { Text("Ömer Çelik") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefon Numarası (İsteğe Bağlı)") },
                        placeholder = { Text("+90 5XX XXX XX XX") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Güvenli Şifre") },
                        leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && name.isNotEmpty()) {
                                keyboardController?.hide()
                                viewModel.registerAndLogin(email, name, phone, password)
                            }
                        },
                        enabled = email.isNotEmpty() && name.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Güvenli Ağ Geçidiyle Kaydol", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Quick Bypass Demo Setup Option
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Geliştirici veya hızlı test modu mu?",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.registerAndLogin(
                            "56celikomer@gmail.com",
                            "Ömer Çelik",
                            "+90 (555) 777 56 56",
                            "admin1212"
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.FlashOn, modifier = Modifier.size(16.dp), contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hızlı Test Girişi (Demo Profili)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 3. HOMEPAGE TABBED DASHBOARD ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: NexusViewModel) {
    val tabIndex = viewModel.currentTab.value
    val currentUser by viewModel.currentUser.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    
    var showAddFriendDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nexus Sohbet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    // Sync to Cloud Trigger
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        enabled = syncState is NexusViewModel.SyncStatus.Idle
                    ) {
                        if (syncState is NexusViewModel.SyncStatus.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Default.CloudQueue, contentDescription = "Bulut Eşitle", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Logout Button
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = tabIndex == 0,
                    onClick = { viewModel.currentTab.value = 0 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Sohbetler") },
                    label = { Text("Sohbetler", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = tabIndex == 1,
                    onClick = { viewModel.currentTab.value = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Arkadaşlar") },
                    label = { Text("Arkadaşlar", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = tabIndex == 2,
                    onClick = { viewModel.currentTab.value = 2 },
                    icon = { Icon(Icons.Default.GroupWork, contentDescription = "Topluluklar") },
                    label = { Text("Topluluklar", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = tabIndex == 3,
                    onClick = { viewModel.currentTab.value = 3 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Geçmiş") },
                    label = { Text("Aramalar", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = tabIndex == 4,
                    onClick = { viewModel.currentTab.value = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Destek/Ayarlar") },
                    label = { Text("Ayarlar", fontSize = 11.sp) }
                )
            }
        },
        floatingActionButton = {
            if (tabIndex == 0 || tabIndex == 1) {
                FloatingActionButton(
                    onClick = { showAddFriendDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Arkadaş Ekle")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background Visual Warning overlay for Sync states
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = syncState is NexusViewModel.SyncStatus.Completed) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = (syncState as? NexusViewModel.SyncStatus.Completed)?.message ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                when (tabIndex) {
                    0 -> ChatsTabScreen(viewModel = viewModel)
                    1 -> FriendsTabScreen(viewModel = viewModel)
                    2 -> CommunitiesTabScreen(viewModel = viewModel)
                    3 -> CallHistoryTabScreen(viewModel = viewModel)
                    4 -> SettingsAndSupportScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Add Friend Dialog Modal
    if (showAddFriendDialog) {
        var friendName by remember { mutableStateOf("") }
        var friendEmail by remember { mutableStateOf("") }
        var friendPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text("Yeni Şifreli Kişi Ekle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Eklemek istediğiniz kişinin bilgilerini girin. Güvenlik anahtarı asimetrik olarak üretilecektir.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        label = { Text("Kişi İsmi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = friendEmail,
                        onValueChange = { friendEmail = it },
                        label = { Text("E-posta ID") },
                        placeholder = { Text("arkadas@nexus.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = friendPhone,
                        onValueChange = { friendPhone = it },
                        label = { Text("Telefon Numarası") },
                        placeholder = { Text("+90 532...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (friendName.isNotEmpty() && friendEmail.isNotEmpty()) {
                            viewModel.addFriend(friendName, friendEmail, friendPhone)
                            showAddFriendDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Arka Plan Şifreli Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

// --- 3A. SOHBETLER TAB SCREEN (WhatsApp List Layout) ---
@Composable
fun ChatsTabScreen(viewModel: NexusViewModel) {
    val contacts by viewModel.contacts.collectAsState()

    if (contacts.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.ChatBubbleOutline,
            title = "Henüz Sohbetiniz Yok",
            desc = "Eklemek istediğiniz arkadaşlarınızla anında uçtan uca şifreli sohbet kanalı kurabilirsiniz."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Mesajlarınız askeri düzey AES-128 şifreleme ile yerel Room veritabanında korunur.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            items(contacts) { contact ->
                ContactListItem(contact = contact, onClick = { viewModel.selectChat(contact) })
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                    modifier = Modifier.padding(start = 76.dp)
                )
            }
        }
    }
}

@Composable
fun ContactListItem(contact: ContactEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Avatar with color fallback
        val avatarColor = remember(contact.profileColorHex) {
            try { Color(android.graphics.Color.parseColor(contact.profileColorHex)) } catch(e: Exception) { PrimaryTeal }
        }
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(avatarColor)
        ) {
            Text(
                text = contact.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            // Online green badge
            if (contact.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (contact.isBlocked) "${contact.name} (Engellendi)" else contact.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (contact.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contact.lastSeenTime,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = contact.statusText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- 3B. ARKADAŞLAR TAB SCREEN ---
@Composable
fun FriendsTabScreen(viewModel: NexusViewModel) {
    val contacts by viewModel.contacts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.trim().isEmpty()) contacts
        else contacts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.phoneNumber.contains(searchQuery) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Arkadaş adı veya tel no ara...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (filteredContacts.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "Sonuç Bulunamadı",
                desc = "Eklemek istediğiniz şifreli arkadaş numarasını kontrol edin."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredContacts) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectChat(contact) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarColor = remember(contact.profileColorHex) {
                            try { Color(android.graphics.Color.parseColor(contact.profileColorHex)) } catch(e: Exception) { PrimaryTeal }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(avatarColor)
                        ) {
                            Text(contact.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text(contact.phoneNumber, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(onClick = { viewModel.selectChat(contact) }) {
                            Icon(Icons.Default.Chat, contentDescription = "Mesaj Gönder", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                }
            }
        }
    }
}

// --- 3C. TOPLULUKLAR TAB SCREEN ---
@Composable
fun CommunitiesTabScreen(viewModel: NexusViewModel) {
    val communities by viewModel.communities.collectAsState()
    var selectedCommunity by remember { mutableStateOf<CommunityEntity?>(null) }

    if (communities.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Hub,
            title = "Topluluk Yok",
            desc = "Yönetici şifreli gruplarına katılarak kriptografi haberlerini takip edin."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            item {
                Text(
                    text = "Açık ve Güvenli Topluluk Grupları",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(communities) { community ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCommunity = community }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colorHex = remember(community.iconColorHex) {
                        try { Color(android.graphics.Color.parseColor(community.iconColorHex)) } catch(e: Exception) { PrimaryTeal }
                    }
                    // Community shape representing global node
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colorHex)
                    ) {
                        Icon(Icons.Default.GroupWork, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(community.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("${community.membersCount} Güvenli Abone", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(community.desc, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            }
        }
    }

    if (selectedCommunity != null) {
        val group = selectedCommunity!!
        AlertDialog(
            onDismissRequest = { selectedCommunity = null },
            title = { Text(group.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Topluluk Açıklaması: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(group.desc, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Topluluk Yöneticisi: ${group.adminName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Üye Sayısı: ${group.membersCount} doğrulanmış cihaz", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Topluluk mesajları, asimetrik grup anahtarları ile uçtan uca kapatılır.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedCommunity = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Kapat")
                }
            }
        )
    }
}

// --- 3D. ARAMALAR / GEÇMİŞ TAB SCREEN ---
@Composable
fun CallHistoryTabScreen(viewModel: NexusViewModel) {
    val callHistory by viewModel.callHistory.collectAsState()

    if (callHistory.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.History,
            title = "Arama Kaydı Yok",
            desc = "Arkadaşlarınızla sesli ve görüntülü arama başlattığınızda kayıtları burada listelenir."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Yakın Zamanki Şifreli Aramalar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { viewModel.clearCallHistory() }) {
                        Text("Temizle", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            items(callHistory) { call ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Call Status Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (call.isMissed) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                    ) {
                        Icon(
                            imageVector = if (call.callType == "Görüntülü") Icons.Default.Videocam else Icons.Default.Call,
                            contentDescription = null,
                            tint = if (call.isMissed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(call.contactName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (call.isOutgoing) Icons.Default.ArrowOutward else Icons.Default.SouthWest,
                                contentDescription = null,
                                tint = if (call.isMissed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (call.isOutgoing) "Giden Arama" else if (call.isMissed) "Cevapsız Arama" else "Gelen Arama",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(call.timestamp)),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            }
        }
    }
}

// --- 3E. SETTINGS AND THE INTEGRATED GEMINI SUPPORTER ---
@Composable
fun SettingsAndSupportScreen(viewModel: NexusViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    var showSupportTab by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Headers inside Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showSupportTab = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showSupportTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (!showSupportTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Profil & Ayarlar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showSupportTab = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showSupportTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (showSupportTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("gemini_support_tab"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gemini Destek")
            }
        }

        if (showSupportTab) {
            SupportScreen(viewModel = viewModel)
        } else {
            // PROFILE & GENERAL OPTIONS SETTINGS SCREEN
            if (currentUser != null) {
                UserProfileOptions(user = currentUser!!, selectedTheme = selectedTheme, viewModel = viewModel)
            } else {
                Text(
                    text = "Lütfen önce giriş yapın.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun UserProfileOptions(user: com.example.data.ProfileEntity, selectedTheme: String, viewModel: NexusViewModel) {
    var editName by remember { mutableStateOf(user.fullName) }
    var editPhone by remember { mutableStateOf(user.phoneNumber) }
    
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User primary credential card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text(user.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Symmetric AES Anahtarı (Uçtan Uca):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user.aesSessionKey, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SignalGold)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("E2EE Public Key Model:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user.rsaPublicKey, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Profile details edits containing Phone and Email details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Bilgilerinizi Güncelleyin", fontWeight = FontWeight.Bold)

                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Yeni İsim") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Yeni Telefon Numarası") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { isEditing = false }) {
                                Text("İptal")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.updateProfileDetails(editName, editPhone)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Kaydet")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Telefon Numarası", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(user.phoneNumber.ifEmpty { "Eklenmedi" }, fontWeight = FontWeight.Medium)
                            }

                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Dark/Light Theme Manager
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Arayüz Tema Seçenekleri", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Dark", "Light", "System").forEach { t ->
                            val isSelected = selectedTheme == t
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.setTheme(t) }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = if (t == "Dark") "Karanlık" else if (t == "Light") "Aydınlık" else "Sistem",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Additional specifications metadata info panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔒 Nexus Kriptoloji Protokolü", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Metinler gönderilmeden önce telefonda rastgele AES-128 simetrik parolaları ile kapatılır. Şifrelenen paketler yalnızca alıcının cihazındaki RSA-2048 gizli anahtarı ile çözülebilir. Sunucularımızda şifresiz mesaj asla yer almaz.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- 4. TERMINAL-STYLE GEMINI TROUBLESHOOTER SCREEN ---
@Composable
fun SupportScreen(viewModel: NexusViewModel) {
    val supportHistory by viewModel.geminiSupportHistory.collectAsState()
    val isLoading by viewModel.geminiLoading.collectAsState()

    var supportInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to latest responses automatically on new content
    LaunchedEffect(supportHistory.size) {
        if (supportHistory.isNotEmpty()) {
            listState.animateScrollToItem(supportHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quick suggest troubleshoot options row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = listOf("Şifreleme nasıl çalışır?", "Yedeklerim güvende mi?")
            suggestions.forEach { suggest ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.sendSupportPrompt(suggest) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(suggest, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Conversational list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(supportHistory) { currentHistory ->
                val isAI = currentHistory.sender == "asistan"
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAI) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isAI) 2.dp else 16.dp,
                            bottomEnd = if (isAI) 16.dp else 2.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isAI) "🤖 Gemini Destek" else "Ben",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isAI) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentHistory.message,
                                fontSize = 14.sp,
                                color = if (isAI) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Gemini analizi yapıyor...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Bottom command panel input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = supportInput,
                onValueChange = { supportInput = it },
                placeholder = { Text("Asistana teknik soru sorun...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("support_input"),
                singleLine = true,
                trailingIcon = {
                    if (supportInput.isNotEmpty()) {
                        IconButton(onClick = { supportInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = {
                    if (supportInput.trim().isNotEmpty()) {
                        viewModel.sendSupportPrompt(supportInput)
                        supportInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("support_send_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Soru Sor", tint = Color.White)
            }
        }
    }
}

// --- 5. PRIVATE CHAT CONVERSATION SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationScreen(viewModel: NexusViewModel, contact: ContactEntity) {
    val messages by viewModel.activeChatMessages.collectAsState()
    val currentCallState by viewModel.activeCallState.collectAsState()
    val isBlocked = contact.isBlocked
    
    var inputText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    var showDropdownMenu by remember { mutableStateOf(false) }
    var expandedPayloadMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectChat(null) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDropdownMenu = true }
                    ) {
                        val avatarColor = remember(contact.profileColorHex) {
                            try { Color(android.graphics.Color.parseColor(contact.profileColorHex)) } catch(e: Exception) { PrimaryTeal }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(avatarColor)
                        ) {
                            Text(contact.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                text = if (isBlocked) "Engellendi" else if (contact.isOnline) "Çevrimiçi" else contact.lastSeenTime,
                                fontSize = 11.sp,
                                color = if (contact.isOnline && !isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.startCall(isVideo = false) }, enabled = !isBlocked) {
                        Icon(Icons.Default.Call, contentDescription = "Sesli Ara")
                    }
                    IconButton(onClick = { viewModel.startCall(isVideo = true) }, enabled = !isBlocked) {
                        Icon(Icons.Default.Videocam, contentDescription = "Görüntülü Ara")
                    }

                    Box {
                        IconButton(onClick = { showDropdownMenu = !showDropdownMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler")
                        }

                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("E2EE Güvenlik Detayı") },
                                onClick = {
                                    showDropdownMenu = false
                                    // Expand first message for security visual or notify
                                    if (messages.isNotEmpty()) expandedPayloadMessage = messages.last()
                                },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBlocked) "Engeli Kaldır" else "Kullanıcıyı Engelle") },
                                onClick = {
                                    showDropdownMenu = false
                                    viewModel.blockContact(contact.emailOrPhone, !isBlocked)
                                },
                                leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            )
                            DropdownMenuItem(
                                text = { Text("Şikayet Et & Raporla") },
                                onClick = {
                                    showDropdownMenu = false
                                    showReportDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = SignalGold) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sohbeti Temizle") },
                                onClick = {
                                    showDropdownMenu = false
                                    viewModel.clearChat(contact.emailOrPhone)
                                },
                                leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Main body containing list of messages
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                ) {
                    item {
                        // System E2E Shield Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mesajlar ve aramalar uçtan uca AES ile şifrelidir. Anahtar telefona özeldir.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(messages) { msg ->
                        val isSelf = msg.senderEmail != contact.emailOrPhone && msg.senderEmail != "system"
                        val isSystem = msg.senderEmail == "system"

                        if (isSystem) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = msg.plainContent,
                                        modifier = Modifier.padding(10.dp),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelf) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isSelf) 16.dp else 2.dp,
                                        bottomEnd = if (isSelf) 2.dp else 16.dp
                                    ),
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clickable { expandedPayloadMessage = msg }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        // Handle specific content types
                                        when (msg.contentType) {
                                            "image" -> {
                                                // Simulated image attachment card
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(140.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.Gray.copy(alpha = 0.3f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text("Uçtan Uca Şifreli Medya", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }
                                            "audio" -> {
                                                // Simulated voice recorder playback UI
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Audio", tint = MaterialTheme.colorScheme.primary)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    // Audio simulated wave
                                                    Box(modifier = Modifier.height(14.dp).weight(1f)) {
                                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                                            drawRect(color = Color.LightGray.copy(alpha = 0.5f))
                                                            // draw a couple of vertical spikes to simulate soundwaves
                                                            var start = 0f
                                                            while (start < size.width) {
                                                                val spikeHeight = (5..14).random().toFloat()
                                                                drawRect(
                                                                    color = Color(0xFF00A884),
                                                                    topLeft = androidx.compose.ui.geometry.Offset(start, (size.height - spikeHeight) / 2),
                                                                    size = androidx.compose.ui.geometry.Size(4f, spikeHeight)
                                                                )
                                                                start += 10f
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(msg.mediaDuration ?: "0:00", fontSize = 11.sp)
                                                }
                                            }
                                            "file" -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                                                        .padding(8.dp)
                                                ) {
                                                    Icon(Icons.Default.AttachFile, contentDescription = "Belge", tint = MaterialTheme.colorScheme.primary)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = msg.plainContent,
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }

                                        // Print body text (unless handled globally like files)
                                        if (msg.contentType != "file") {
                                            Text(
                                                text = msg.plainContent,
                                                fontSize = 14.sp,
                                                color = if (isSelf) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(
                                            modifier = Modifier.align(Alignment.End),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                                                fontSize = 9.sp,
                                                color = if (isSelf) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (isSelf) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = when (msg.status) {
                                                        "Sent" -> Icons.Default.Check
                                                        else -> Icons.Default.DoneAll
                                                    },
                                                    contentDescription = null,
                                                    tint = if (msg.status == "Read") TickBlue else Color.White.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Interaction inputs
                if (isBlocked) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Bu kullanıcı engellendi. Mesaj göndermek için şifreli kilidi tekrar açmanız gerekir.",
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Action Attachment Trigger Menu
                        var showAttachmentSheet by remember { mutableStateOf(false) }
                        IconButton(onClick = { showAttachmentSheet = !showAttachmentSheet }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Eklenti Ekle", tint = MaterialTheme.colorScheme.primary)
                        }

                        // Attachment quick indicators sheet
                        DropdownMenu(
                            expanded = showAttachmentSheet,
                            onDismissRequest = { showAttachmentSheet = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Galeri (Resim Detayı)") },
                                onClick = {
                                    showAttachmentSheet = false
                                    viewModel.simulatePhotoSend()
                                },
                                leadingIcon = { Icon(Icons.Default.Photo, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Belge ve PDF Ekle") },
                                onClick = {
                                    showAttachmentSheet = false
                                    viewModel.simulateFileSend()
                                },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Ses Kaydı Gönder") },
                                onClick = {
                                    showAttachmentSheet = false
                                    viewModel.simulateVoiceSend()
                                },
                                leadingIcon = { Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                        }

                        // Text input field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Şifreli Mesaj Yazın...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input"),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Mic / Send toggle action
                        IconButton(
                            onClick = {
                                if (inputText.trim().isNotEmpty()) {
                                    viewModel.sendEncryptedMessage(inputText)
                                    inputText = ""
                                } else {
                                    viewModel.simulateVoiceSend() // Click bare mic triggers voice simulation!
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = if (inputText.isNotEmpty()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                                contentDescription = "Gönder",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // --- Ongoing Active Sim Call Overlay HUD ---
            if (currentCallState != null) {
                ActiveCallOverlayHUD(call = currentCallState!!, onHangup = { viewModel.endCall() })
            }
        }
    }

    // --- Decryption details interactive bottom card ---
    if (expandedPayloadMessage != null) {
        val originalMsg = expandedPayloadMessage!!
        AlertDialog(
            onDismissRequest = { expandedPayloadMessage = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uçtan Uca Kriptografik Paket")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sinyalin geçtiği veri blokları şunlardır:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Text("📦 Ham Şifreli Ciphertext Payload (Base64):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Text(
                        text = originalMsg.encryptedPayload,
                        fontFamily = FontFamily.Monospace,
                        color = SignalGold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("🔓 Çözülmüş Gerçek Metin:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Text(
                        text = originalMsg.plainContent,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Algoritma", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("AES-128-ECB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Güvenlik Durumu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Güvenli (Sıfır Sızıntı)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Gecikme Süresi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("0.02 milisaniye", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { expandedPayloadMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Tamam")
                }
            }
        )
    }

    // --- Report user modal Dialog ---
    if (showReportDialog) {
        var reportReason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Kullanıcıyı Bildir / Rapor Et") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Güvenli topluluk ilkeleri gereği şüpheli kişileri şikayet edebilirsiniz. Detayları giriniz. Şifreli konuşma meta verisi incelenmeye alınacaktır.")
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Spam, taciz, uygunsuz içerik vb...") },
                        label = { Text("Şikayet Sebebi") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reportReason.trim().isNotEmpty()) {
                            viewModel.reportContact(contact.emailOrPhone, reportReason)
                            showReportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yönetime Bildir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

// --- 6. SIMULATED ONGOING VOICE / VIDEO CALL OVERLAY ---
@Composable
fun ActiveCallOverlayHUD(call: NexusViewModel.CallSession, onHangup: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Header call encryption warning
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Şifreli Nexus Görüşmesi Aktif", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text(call.contactName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                
                Spacer(modifier = Modifier.height(8.dp))

                val minutes = call.durationSeconds / 60
                val seconds = call.durationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Visualizer animation or generic avatar placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(PrimaryTeal.copy(alpha = 0.15f))
            ) {
                // Displaying a pulsing animation using standard Compose scale
                val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1100),
                        repeatMode = RepeatMode.Reverse
                    ), label = "pulseScale"
                )

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(PrimaryTeal.copy(alpha = 0.25f))
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal)
                ) {
                    Icon(
                        imageVector = if (call.isVideo) Icons.Default.Videocam else Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            // Footer actions buttons panel
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                // Mute microphone
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.MicOff, contentDescription = "Sesi Kapat", tint = Color.White)
                }

                // HANG UP Red Button
                IconButton(
                    onClick = onHangup,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .testTag("hang_up_button")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(28.dp))
                }

                // Speaker phone toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Hoparlör", tint = Color.White)
                }
            }
        }
    }
}

// --- 7. REUSABLE EMPTY STATE VIEWS ---
@Composable
fun EmptyStateView(icon: ImageVector, title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = desc,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

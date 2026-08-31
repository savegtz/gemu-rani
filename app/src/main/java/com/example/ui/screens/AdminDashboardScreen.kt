package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.game.*
import com.example.ui.theme.*

enum class AdminTab(val title: String, val icon: String) {
    LIVE_OPS("Seva & Live", "⚡"),
    PLAYERS("Wachezaji", "👥"),
    ECONOMY("Uchumi & Ads", "💰"),
    MIKOA("Mikoa & Ligi", "🏆")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    profile: PlayerProfileEntity,
    onGrantCurrency: (coins: Long, gems: Int) -> Unit,
    onSetBalance: (coins: Long, gems: Int) -> Unit,
    onUnlockAll: () -> Unit,
    onResetData: () -> Unit,
    onUpdateStats: (highScore: Long, level: Int, trophies: Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.LIVE_OPS) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    
    // Live ops state
    var announcementText by remember { mutableStateOf(AdminLiveConfig.globalAnnouncement) }
    var maintenanceText by remember { mutableStateOf(AdminLiveConfig.maintenanceMessage) }
    var activeSeasonText by remember { mutableStateOf(AdminLiveConfig.activeSeasonName) }

    // Player state custom inputs
    var customCoinsInput by remember { mutableStateOf(profile.coins.toString()) }
    var customGemsInput by remember { mutableStateOf(profile.gems.toString()) }
    var customHighScoreInput by remember { mutableStateOf(profile.bestScore.toString()) }
    var customLevelInput by remember { mutableStateOf(profile.level.toString()) }
    var customTrophiesInput by remember { mutableStateOf(profile.trophies.toString()) }
    
    // Player registry search & filters
    var playerSearchQuery by remember { mutableStateOf("") }
    
    // New promo code input
    var newPromoCode by remember { mutableStateOf("") }
    var newPromoReward by remember { mutableStateOf("") }
    var showAddPromoDialog by remember { mutableStateOf(false) }

    // New Sponsor campaign dialog
    var showAddSponsorDialog by remember { mutableStateOf(false) }
    var newSponsorBrand by remember { mutableStateOf("") }
    var newSponsorHeadline by remember { mutableStateOf("") }
    var newSponsorUrl by remember { mutableStateOf("") }
    var newSponsorEmoji by remember { mutableStateOf("📱") }

    val scrollState = rememberScrollState()

    fun showFeedback(msg: String) {
        actionMessage = msg
        SoundEngine.playPowerUp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_dashboard_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar with Admin Badge
            Surface(
                color = DarkBgCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = {
                                SoundEngine.playMenuClick()
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Rudi Nyuma",
                                tint = TextPrimary
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "PANELI YA UTAWALA",
                                    color = NeonGold,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CrimsonFire.copy(alpha = 0.25f),
                                    border = ButtonDefaults.outlinedButtonBorder(true)
                                ) {
                                    Text(
                                        text = "SUPER ADMIN",
                                        color = CrimsonFire,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Bongo Runner Control Center & Live Ops",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Quick Refresh / Status Indicator
                    Surface(
                        shape = CircleShape,
                        color = AfricanEmerald.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AfricanEmerald, CircleShape)
                            )
                            Text(
                                text = "ONLINE",
                                color = AfricanEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Feedback Action Toast
            AnimatedVisibility(
                visible = actionMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                actionMessage?.let { msg ->
                    Surface(
                        color = AfricanEmerald.copy(alpha = 0.25f),
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AfricanEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = msg,
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = { actionMessage = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Funga",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBgCardElevated)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AdminTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonGold else Color.Transparent)
                            .clickable {
                                SoundEngine.playMenuClick()
                                selectedTab = tab
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = tab.icon, fontSize = 13.sp)
                            Text(
                                text = tab.title,
                                color = if (isSelected) DarkBgMain else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. Tab Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    AdminTab.LIVE_OPS -> {
                        // TAB 1: Live Operations, Announcements, Server Health & Weather
                        LiveOpsTabContent(
                            announcementText = announcementText,
                            onAnnouncementTextChange = { announcementText = it },
                            onSaveAnnouncement = {
                                AdminLiveConfig.globalAnnouncement = announcementText
                                showFeedback("Tangazo la utawala limehifadhiwa na linasomwa na wachezaji wote!")
                            },
                            maintenanceText = maintenanceText,
                            onMaintenanceTextChange = { maintenanceText = it },
                            onToggleMaintenance = {
                                AdminLiveConfig.isMaintenanceMode = !AdminLiveConfig.isMaintenanceMode
                                AdminLiveConfig.maintenanceMessage = maintenanceText
                                showFeedback(if (AdminLiveConfig.isMaintenanceMode) "Hali ya matengenezo (Maintenance Mode) IMEWASHWA!" else "Hali ya matengenezo IMEZIMWA!")
                            },
                            onWeatherChange = { weather ->
                                AdminLiveConfig.forcedWeatherOverride = weather
                                showFeedback("Hali ya hewa imewekwa: $weather")
                            },
                            onSpeedChange = { speed ->
                                AdminLiveConfig.globalGameSpeedMultiplier = speed
                                showFeedback("Kasi ya mchezo imewekwa mara ${speed}x")
                            }
                        )
                    }

                    AdminTab.PLAYERS -> {
                        // TAB 2: Player Accounts, Current Profile Mods, Stats & Simulated Registry
                        PlayersTabContent(
                            profile = profile,
                            customCoins = customCoinsInput,
                            onCustomCoinsChange = { customCoinsInput = it },
                            customGems = customGemsInput,
                            onCustomGemsChange = { customGemsInput = it },
                            customHighScore = customHighScoreInput,
                            onCustomHighScoreChange = { customHighScoreInput = it },
                            customLevel = customLevelInput,
                            onCustomLevelChange = { customLevelInput = it },
                            customTrophies = customTrophiesInput,
                            onCustomTrophiesChange = { customTrophiesInput = it },
                            onApplyBalance = {
                                val coins = customCoinsInput.toLongOrNull() ?: profile.coins
                                val gems = customGemsInput.toIntOrNull() ?: profile.gems
                                onSetBalance(coins, gems)
                                showFeedback("Salio jipya limewekwa: 🪙 $coins | 💎 $gems")
                            },
                            onApplyStats = {
                                val hs = customHighScoreInput.toLongOrNull() ?: profile.bestScore
                                val lvl = customLevelInput.toIntOrNull() ?: profile.level
                                val tr = customTrophiesInput.toIntOrNull() ?: profile.trophies
                                onUpdateStats(hs, lvl, tr)
                                showFeedback("Takwimu zimesasishwa: HighScore: $hs | Lvl: $lvl | Trophies: $tr")
                            },
                            onQuickGrant = { coins, gems ->
                                onGrantCurrency(coins, gems)
                                customCoinsInput = (profile.coins + coins).toString()
                                customGemsInput = (profile.gems + gems).toString()
                                showFeedback("Imeongezwa: +$coins Shilingi & +$gems Vito!")
                            },
                            onUnlockAll = {
                                onUnlockAll()
                                showFeedback("Wahusika, Hoverboards na Miji yote imefunguliwa bure!")
                            },
                            onResetData = {
                                onResetData()
                                showFeedback("Data za mchezaji zimerudishwa mwanzo (Reset Completed)!")
                            },
                            searchQuery = playerSearchQuery,
                            onSearchQueryChange = { playerSearchQuery = it },
                            onToggleBan = { playerId ->
                                AdminLiveConfig.togglePlayerBan(playerId)
                                showFeedback("Hali ya marufuku (Ban Status) imebadilishwa!")
                            },
                            onGiftPlayer = { playerId, c, g ->
                                AdminLiveConfig.rewardPlayer(playerId, c, g)
                                showFeedback("Zawadi ya 🪙 $c & 💎 $g imetumwa kwa mchezaji!")
                            }
                        )
                    }

                    AdminTab.ECONOMY -> {
                        // TAB 3: Economy Multipliers, Shop Discounts, Sponsor Ads & Gift Codes
                        EconomyTabContent(
                            onCoinMultiplierChange = { mult ->
                                AdminLiveConfig.globalCoinMultiplier = mult
                                showFeedback("Kizidishi cha sarafu kimewekwa: ${mult}x")
                            },
                            onToggleDoubleRewards = {
                                AdminLiveConfig.doubleDailyRewardsActive = !AdminLiveConfig.doubleDailyRewardsActive
                                showFeedback(if (AdminLiveConfig.doubleDailyRewardsActive) "Zawadi za kila siku mara 2 ZIMEWASHWA!" else "Zawadi mara 2 zimezimwa.")
                            },
                            onShopDiscountChange = { discount ->
                                AdminLiveConfig.shopDiscountPercent = discount
                                showFeedback("Punguzo la duka (Shop Discount) limewekwa: $discount%")
                            },
                            onOpenAddSponsor = { showAddSponsorDialog = true },
                            onOpenAddPromo = { showAddPromoDialog = true }
                        )
                    }

                    AdminTab.MIKOA -> {
                        // TAB 4: Regional Season & Mkoa Championship Standing manager
                        MikoaTabContent(
                            activeSeason = activeSeasonText,
                            onActiveSeasonChange = { activeSeasonText = it },
                            onSaveSeason = {
                                AdminLiveConfig.activeSeasonName = activeSeasonText
                                showFeedback("Jina la msimu wa mashindano limehifadhiwa!")
                            },
                            onBoostMkoa = { mkoaName, points ->
                                showFeedback("Pointi +$points zimeongezwa kwa Mkoa wa $mkoaName!")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dialog: Add Sponsor Campaign
        if (showAddSponsorDialog) {
            AlertDialog(
                onDismissRequest = { showAddSponsorDialog = false },
                title = { Text("Ongeza Bango Jipya la Mteja", color = NeonGold, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newSponsorBrand,
                            onValueChange = { newSponsorBrand = it },
                            label = { Text("Jina la Kampuni / App") },
                            placeholder = { Text("Mfano: Airtel Money") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newSponsorHeadline,
                            onValueChange = { newSponsorHeadline = it },
                            label = { Text("Kichwa cha Tangazo") },
                            placeholder = { Text("Pata bando na dakika za bure") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newSponsorUrl,
                            onValueChange = { newSponsorUrl = it },
                            label = { Text("Website au PlayStore Link") },
                            placeholder = { Text("https://...") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newSponsorEmoji,
                            onValueChange = { newSponsorEmoji = it },
                            label = { Text("Nembo ya Emoji") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSponsorBrand.isNotEmpty()) {
                                SponsorAdsManager.campaigns.add(
                                    SponsorCampaign(
                                        id = "custom_${System.currentTimeMillis()}",
                                        brandName = newSponsorBrand,
                                        tagLine = "Huduma Bora Tanzania",
                                        headline = newSponsorHeadline.ifEmpty { "Furahia huduma bora msimu huu!" },
                                        description = "Pakua au tembelea tovuti rasmi kupata ofa maalum.",
                                        ctaText = "TEMBELEA SASA",
                                        targetUrl = if (newSponsorUrl.startsWith("http")) newSponsorUrl else "https://google.com",
                                        iconEmoji = newSponsorEmoji.ifEmpty { "🚀" },
                                        badgeText = "SPONSOR MPYA",
                                        themeColor = AfricanEmerald,
                                        category = SponsorCategory.MOBILE_APP,
                                        rewardCoins = 500,
                                        rewardGems = 10
                                    )
                                )
                                showFeedback("Kampeni mpya ya $newSponsorBrand imeongezwa!")
                                showAddSponsorDialog = false
                                newSponsorBrand = ""
                                newSponsorHeadline = ""
                                newSponsorUrl = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald)
                    ) {
                        Text("HIFADHI", color = DarkBgMain, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddSponsorDialog = false }) {
                        Text("GHAIRI", color = TextSecondary)
                    }
                }
            )
        }

        // Dialog: Add Promo Code
        if (showAddPromoDialog) {
            AlertDialog(
                onDismissRequest = { showAddPromoDialog = false },
                title = { Text("Tengeneza Gift Code Mpya", color = NeonGold, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newPromoCode,
                            onValueChange = { newPromoCode = it.uppercase() },
                            label = { Text("Nambari ya Vocha (Kodi)") },
                            placeholder = { Text("Mfano: TUSONGE2026") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newPromoReward,
                            onValueChange = { newPromoReward = it },
                            label = { Text("Zawadi ya Vocha") },
                            placeholder = { Text("Mfano: 🪙 10,000 Coins + 50 Gems") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPromoCode.isNotEmpty()) {
                                AdminLiveConfig.promoCodes.add(newPromoCode to newPromoReward.ifEmpty { "🪙 5,000 Coins" })
                                showFeedback("Kodi ya vocha '$newPromoCode' imetengenezwa!")
                                showAddPromoDialog = false
                                newPromoCode = ""
                                newPromoReward = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                    ) {
                        Text("ONGEZA", color = DarkBgMain, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddPromoDialog = false }) {
                        Text("GHAIRI", color = TextSecondary)
                    }
                }
            )
        }
    }
}

// ----------------------------------------------------
// TAB 1: LIVE OPS, ANNOUNCEMENTS & SERVER CLUSTER
// ----------------------------------------------------
@Composable
private fun LiveOpsTabContent(
    announcementText: String,
    onAnnouncementTextChange: (String) -> Unit,
    onSaveAnnouncement: () -> Unit,
    maintenanceText: String,
    onMaintenanceTextChange: (String) -> Unit,
    onToggleMaintenance: () -> Unit,
    onWeatherChange: (String) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    val metrics = remember { AdminMetric() }

    // 1. Live Server Telemetry
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HALI YA SEVA (SERVER CLUSTER)",
                    color = NeonGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Edge: Tanzania / EAC 🌍",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AdminStatRow("Server Edge Location", metrics.serverRegion, ElectricCyan)
            AdminStatRow("Live Tick Rate", "${metrics.tickRateHz} Hz (Sub-15ms sync)", AfricanEmerald)
            AdminStatRow("Anti-Cheat Shield", AdminLiveConfig.antiCheatSensitivity, AfricanEmerald)
            AdminStatRow("Wachezaji Waliojiandikisha", "142,850 Sprinters", TextPrimary)
            AdminStatRow("Wanaocheza Sasa (Concurrent)", "3,120 Live Duels", NeonGold)
            AdminStatRow("Shilingi Zilizopo Kwenye Mchezo", "184.5M 🪙", BrightAmber)
        }
    }

    // 2. Global Live Announcement
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TANGAZO KWA WACHEZAJI WOTE 📢",
                    color = NeonGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Switch(
                    checked = AdminLiveConfig.isAnnouncementActive,
                    onCheckedChange = { AdminLiveConfig.isAnnouncementActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonGold,
                        checkedTrackColor = DarkBgCardElevated
                    )
                )
            }

            OutlinedTextField(
                value = announcementText,
                onValueChange = onAnnouncementTextChange,
                label = { Text("Maandishi ya Tangazo (Home Banner)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Button(
                onClick = onSaveAnnouncement,
                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💾 CHAPISHA TANGAZO LIVE",
                    color = DarkBgMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    // 3. Maintenance Mode Control
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (AdminLiveConfig.isMaintenanceMode) CrimsonFire.copy(alpha = 0.15f) else DarkBgCard
        ),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HALI YA MATENGENEZO (MAINTENANCE) 🛠️",
                        color = if (AdminLiveConfig.isMaintenanceMode) CrimsonFire else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (AdminLiveConfig.isMaintenanceMode) "Mchezo umefungwa kwa matengenezo" else "Seva inafanya kazi kawaida",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = AdminLiveConfig.isMaintenanceMode,
                    onCheckedChange = { onToggleMaintenance() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CrimsonFire,
                        checkedTrackColor = CrimsonFire.copy(alpha = 0.3f)
                    )
                )
            }

            if (AdminLiveConfig.isMaintenanceMode) {
                OutlinedTextField(
                    value = maintenanceText,
                    onValueChange = onMaintenanceTextChange,
                    label = { Text("Sababu / Ujumbe wa Matengenezo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }

    // 4. Weather & Game Speed Override
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "HALI YA HEWA & KASI YA MCHEZO 🌤️",
                color = ElectricCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            // Weather buttons
            Text(text = "Chagua Hali ya Hewa Barabarani:", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val weathers = listOf("AUTO" to "🔄 Auto", "SUNNY" to "☀️ Jua", "RAINY" to "🌧️ Mvua", "NIGHT" to "🌙 Usiku")
                weathers.forEach { (key, label) ->
                    val isCur = AdminLiveConfig.forcedWeatherOverride == key
                    Button(
                        onClick = { onWeatherChange(key) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCur) ZanzibarTurquoise else DarkBgCardElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isCur) DarkBgMain else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Speed multiplier
            Text(text = "Kasi ya Mkimbiaji (Global Speed):", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val speeds = listOf(0.8f to "0.8x Pole", 1.0f to "1.0x Kawaida", 1.25f to "1.25x Haraka", 1.5f to "1.5x Turbo")
                speeds.forEach { (spd, label) ->
                    val isCur = AdminLiveConfig.globalGameSpeedMultiplier == spd
                    Button(
                        onClick = { onSpeedChange(spd) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCur) NeonGold else DarkBgCardElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isCur) DarkBgMain else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: PLAYERS & ACCOUNTS MANAGEMENT
// ----------------------------------------------------
@Composable
private fun PlayersTabContent(
    profile: PlayerProfileEntity,
    customCoins: String,
    onCustomCoinsChange: (String) -> Unit,
    customGems: String,
    onCustomGemsChange: (String) -> Unit,
    customHighScore: String,
    onCustomHighScoreChange: (String) -> Unit,
    customLevel: String,
    onCustomLevelChange: (String) -> Unit,
    customTrophies: String,
    onCustomTrophiesChange: (String) -> Unit,
    onApplyBalance: () -> Unit,
    onApplyStats: () -> Unit,
    onQuickGrant: (Long, Int) -> Unit,
    onUnlockAll: () -> Unit,
    onResetData: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleBan: (String) -> Unit,
    onGiftPlayer: (String, Long, Int) -> Unit
) {
    // 1. Current Active Player Profile Details
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "AKAUNTI YAKO YA SASA (ACTIVE RUNNER)",
                color = NeonGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            AdminStatRow("Jina Kamili", profile.fullName.ifEmpty { profile.username }, TextPrimary)
            AdminStatRow("Mkoa / Eneo", profile.selectedMkoa, SerengetiYellow)
            AdminStatRow("Simu / Email", profile.emailOrPhone.ifEmpty { "Guest (Haijaunganishwa)" }, ElectricCyan)
            AdminStatRow("Hali ya Akaunti", if (profile.isLoggedIn) "Imesajiliwa Rasmi ✅" else "Mgeni ⚡", AfricanEmerald)
            AdminStatRow("Sarafu (Coins)", "${profile.coins} 🪙", BrightAmber)
            AdminStatRow("Vito (Gems)", "${profile.gems} 💎", ZanzibarTurquoise)
            AdminStatRow("Level & HighScore", "Lvl ${profile.level} | HS: ${profile.bestScore}", TextPrimary)
        }
    }

    // 2. Custom Balance & Stats Modifier
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "BADILI SALIO & TAKWIMU ZA MCHEZAJI",
                color = TextAccentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = customCoins,
                    onValueChange = onCustomCoinsChange,
                    label = { Text("Sarafu (Coins 🪙)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = customGems,
                    onValueChange = onCustomGemsChange,
                    label = { Text("Vito (Gems 💎)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = onApplyBalance,
                colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "💾 HIFADHI SALIO JIPYA", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customHighScore,
                    onValueChange = onCustomHighScoreChange,
                    label = { Text("HighScore") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = customLevel,
                    onValueChange = onCustomLevelChange,
                    label = { Text("Level") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = customTrophies,
                    onValueChange = onCustomTrophiesChange,
                    label = { Text("Trophies") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Button(
                onClick = onApplyStats,
                colors = ButtonDefaults.buttonColors(containerColor = ZanzibarTurquoise),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "⚡ SASISHA LEVEL NA HIGHSCORE", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }

    // 3. Quick Action Power Tools
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "VITUFE VYA HARAKA (QUICK TOOLS)",
                color = NeonGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onQuickGrant(10000, 100) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "+10k 🪙 / +100 💎", color = DarkBgMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onQuickGrant(50000, 500) },
                    colors = ButtonDefaults.buttonColors(containerColor = TanzaniteBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "+50k 🪙 / +500 💎", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onUnlockAll,
                colors = ButtonDefaults.buttonColors(containerColor = SerengetiYellow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🎁 FUNGUA WAHUSIKA & BOARDS ZOTE BURE", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = onResetData,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonFire.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "⚠️ RESET DATA ZA MCHEZO (RUDI MWANZO)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 4. Community Registered Players Directory & Moderation Table
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORODHA YA WACHEZAJI (COMMUNITY REGISTRY)",
                    color = NeonGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${AdminLiveConfig.registeredPlayers.size} Wachezaji",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Tafuta Mchezaji kwa Jina au Mkoa") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            val filteredPlayers = AdminLiveConfig.registeredPlayers.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.mkoa.contains(searchQuery, ignoreCase = true)
            }

            filteredPlayers.forEach { player ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (player.isBanned) CrimsonFire.copy(alpha = 0.15f) else DarkBgCardElevated,
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = player.fullName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "(@${player.username})",
                                    color = ElectricCyan,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (player.isBanned) CrimsonFire else AfricanEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (player.isBanned) "BANNED 🚫" else "ACTIVE ✅",
                                    color = if (player.isBanned) Color.White else AfricanEmerald,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "📍 ${player.mkoa} | 📞 ${player.emailOrPhone}", color = TextSecondary, fontSize = 10.sp)
                            Text(text = "Lvl ${player.level} | 🪙 ${player.coins}", color = BrightAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Moderation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onGiftPlayer(player.id, 5000, 25) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "🎁 Tuma +5k Coins", color = BrightAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onToggleBan(player.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (player.isBanned) AfricanEmerald else CrimsonFire
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (player.isBanned) "Fungua (Unban)" else "Piga Marufuku (Ban)",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: ECONOMY, SHOP DISCOUNTS & SPONSOR ADS
// ----------------------------------------------------
@Composable
private fun EconomyTabContent(
    onCoinMultiplierChange: (Float) -> Unit,
    onToggleDoubleRewards: () -> Unit,
    onShopDiscountChange: (Int) -> Unit,
    onOpenAddSponsor: () -> Unit,
    onOpenAddPromo: () -> Unit
) {
    // 1. Currency Multipliers & Shop Sales
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "UCHUMI WA MCHEZO (GAME ECONOMY BOOSTERS)",
                color = NeonGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            // Global Coin Multiplier
            Text(text = "Kizidishi cha Sarafu Barabarani (Coin Multiplier):", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val mults = listOf(1.0f to "1x", 2.0f to "2x ⚡", 3.0f to "3x 🚀", 5.0f to "5x 🔥", 10.0f to "10x 🌟")
                mults.forEach { (m, label) ->
                    val isCur = AdminLiveConfig.globalCoinMultiplier == m
                    Button(
                        onClick = { onCoinMultiplierChange(m) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCur) NeonGold else DarkBgCardElevated
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isCur) DarkBgMain else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Double Daily Rewards Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Zawadi Maradufu (2x Daily Rewards)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Wachezaji wanapokea 1,000 Coins + 20 Gems kila siku", color = TextSecondary, fontSize = 10.sp)
                }
                Switch(
                    checked = AdminLiveConfig.doubleDailyRewardsActive,
                    onCheckedChange = { onToggleDoubleRewards() },
                    colors = SwitchDefaults.colors(checkedThumbColor = AfricanEmerald)
                )
            }

            // Global Shop Discount
            Text(text = "Punguzo la Duka (Global Shop Sale Discount):", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val discounts = listOf(0 to "Hakuna (0%)", 20 to "20% OFF", 50 to "50% OFA", 75 to "75% MEGA")
                discounts.forEach { (d, label) ->
                    val isCur = AdminLiveConfig.shopDiscountPercent == d
                    Button(
                        onClick = { onShopDiscountChange(d) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCur) CrimsonFire else DarkBgCardElevated
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            color = if (isCur) Color.White else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // 2. Client Sponsor Ads Manager
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BANGO LA WATEJA (SPONSOR ADS)",
                    color = NeonGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onOpenAddSponsor,
                    colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "+ ONGEZA BANGO", color = DarkBgMain, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }

            SponsorAdsManager.campaigns.forEach { campaign ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkBgCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = campaign.iconEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = campaign.brandName,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = campaign.headline,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${SponsorAdsManager.getImpressions(campaign.id)} Views",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${SponsorAdsManager.getClicks(campaign.id)} Clicks",
                                color = NeonGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // 3. Promo Codes & Gift Vouchers
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KODI ZA VOCHA (PROMO CODES)",
                    color = NeonGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onOpenAddPromo,
                    colors = ButtonDefaults.buttonColors(containerColor = TanzaniteBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "+ TENGENEZA KODI", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            AdminLiveConfig.promoCodes.forEach { (code, reward) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkBgCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = code, color = NeonGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text(text = reward, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 4: MIKOA & REGIONAL CHAMPIONSHIPS
// ----------------------------------------------------
@Composable
private fun MikoaTabContent(
    activeSeason: String,
    onActiveSeasonChange: (String) -> Unit,
    onSaveSeason: () -> Unit,
    onBoostMkoa: (String, Long) -> Unit
) {
    // 1. Active Safari Tournament Season
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MSIMU WA MASHINDANO YA MIKOA 🏆",
                color = NeonGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            OutlinedTextField(
                value = activeSeason,
                onValueChange = onActiveSeasonChange,
                label = { Text("Jina la Msimu (Season Name)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = onSaveSeason,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "💾 HIFADHI MSIMU MPYA", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }

    // 2. Mkoa Regional Standings & Points Booster
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SIMAMIA POINTI ZA MIKOA TANZANIA",
                color = SerengetiYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )

            MkoaLeaderboardCatalog.mikoa.forEach { mkoa ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkBgCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "#${mkoa.rank} ${mkoa.flagEmoji}", fontSize = 16.sp)
                            Column {
                                Text(text = mkoa.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = "${mkoa.totalScore} Pts | ${mkoa.activeRunners} Runners", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { onBoostMkoa(mkoa.name, 50000) },
                            colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "+50k Pts 🚀", color = AfricanEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

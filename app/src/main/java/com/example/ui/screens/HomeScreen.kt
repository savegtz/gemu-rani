package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.game.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: PlayerProfileEntity,
    onStartGame: (mode: GameMode, worldId: String, characterId: String) -> Unit,
    onOpenCharacters: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenBattleLobby: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdmin: () -> Unit,
    onClaimDailyReward: () -> Unit,
    dailyRewardAvailable: Boolean
) {
    val scrollState = rememberScrollState()
    val character = CharacterCatalog.getById(profile.selectedCharacterId)
    val world = WorldCatalog.getById(profile.selectedWorldId)

    var showWorldPicker by remember { mutableStateOf(false) }
    var showModePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header: User Profile & Currencies
            TopProfileBar(
                profile = profile,
                onProfileClick = onOpenProfile,
                onSettingsClick = onOpenSettings,
                onAdminClick = onOpenAdmin
            )

            // 2. Featured Safari Event Hero Banner
            FeaturedSafariBanner(
                worldName = world.name,
                onExplore = { showWorldPicker = true }
            )

            // 3. Daily Reward Banner (if available or claimed)
            DailyRewardCard(
                available = dailyRewardAvailable,
                onClaim = onClaimDailyReward
            )

            // 3. Central Character & Active City Card
            HeroCharacterCard(
                profile = profile,
                character = character,
                world = world,
                onChangeCharacter = onOpenCharacters,
                onChangeWorld = { showWorldPicker = true }
            )

            // 4. Primary Game Launch Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SOLO RUN BUTTON
                Button(
                    onClick = {
                        SoundEngine.playPowerUp()
                        showModePicker = true
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightAmber
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .testTag("solo_run_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = DarkBgMain,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "SOLO RUN",
                                color = DarkBgMain,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Endless & Safari",
                                color = DarkBgMain.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // MULTIPLAYER BATTLE BUTTON
                Button(
                    onClick = {
                        SoundEngine.playPowerUp()
                        onOpenBattleLobby()
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TanzaniteBlue
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .testTag("battle_mode_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚔️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "BATTLE",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "1v1 & 4P Grand Prix",
                                color = TextAccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 5. Quick Navigation Grid
            Text(
                text = "GAME HUBS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubButton(
                    icon = Icons.Default.Person,
                    title = "Characters",
                    subtitle = "Upgrades",
                    color = NeonGold,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCharacters,
                    tag = "hub_characters"
                )
                HubButton(
                    icon = Icons.Default.CheckCircle,
                    title = "Missions",
                    subtitle = "Earn Gems",
                    color = AfricanEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenMissions,
                    tag = "hub_missions"
                )
                HubButton(
                    icon = Icons.Default.ShoppingCart,
                    title = "Shop",
                    subtitle = "Outfits",
                    color = ZanzibarTurquoise,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenShop,
                    tag = "hub_shop"
                )
                HubButton(
                    icon = Icons.Default.EmojiEvents,
                    title = "Ranks",
                    subtitle = "Leaderboard",
                    color = ElectricCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenLeaderboard,
                    tag = "hub_ranks"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // World Selection Modal Sheet
        if (showWorldPicker) {
            WorldSelectionDialog(
                currentWorldId = profile.selectedWorldId,
                coins = profile.coins,
                onSelectWorld = { worldId ->
                    onStartGame(GameMode.ENDLESS, worldId, profile.selectedCharacterId)
                    showWorldPicker = false
                },
                onDismiss = { showWorldPicker = false }
            )
        }

        // Game Mode Picker Modal
        if (showModePicker) {
            GameModeDialog(
                onSelectMode = { mode ->
                    showModePicker = false
                    onStartGame(mode, profile.selectedWorldId, profile.selectedCharacterId)
                },
                onDismiss = { showModePicker = false }
            )
        }
    }
}

@Composable
private fun TopProfileBar(
    profile: PlayerProfileEntity,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBgCard, shape = RoundedCornerShape(20.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Avatar + User Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clickable { onProfileClick() }
                .testTag("profile_pill")
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BrightAmber, shape = CircleShape)
                    .border(2.dp, NeonGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = profile.countryFlag, fontSize = 20.sp)
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = profile.username,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "LVL ${profile.level}",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "• ${profile.trophies} 🏆",
                        color = TextAccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Currency Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coins
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkBgCardElevated,
                border = ButtonDefaults.outlinedButtonBorder(true)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "🪙", fontSize = 13.sp)
                    Text(
                        text = "${profile.coins}",
                        color = TextAccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Gems
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkBgCardElevated
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "💎", fontSize = 13.sp)
                    Text(
                        text = "${profile.gems}",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Language Toggle
            val lang = LocalizationManager.currentLanguage.value
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AfricanEmerald.copy(alpha = 0.25f),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier
                    .clickable { LocalizationManager.toggleLanguage() }
                    .testTag("toggle_language_button")
            ) {
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "🇹🇿 SW" else "🇬🇧 EN",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            // Settings & Admin buttons
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(32.dp).testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.size(32.dp).testTag("admin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Admin",
                    tint = NeonGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyRewardCard(
    available: Boolean,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (available) listOf(Color(0xFF451A03), Color(0xFF78350F))
                    else listOf(DarkBgCard, DarkBgCardElevated)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                if (available) NeonGold else DarkBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = if (available) "🎁" else "✅", fontSize = 28.sp)
            Column {
                Text(
                    text = if (available) "Daily Safari Gift Ready!" else "Daily Reward Claimed",
                    color = if (available) TextAccentGold else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (available) "+500 Coins & +10 Tanzanite Gems" else "Next gift unlocks in 24 hours",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        if (available) {
            Button(
                onClick = {
                    SoundEngine.playPowerUp()
                    onClaim()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("claim_daily_button")
            ) {
                Text(
                    text = "CLAIM",
                    color = DarkBgMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun HeroCharacterCard(
    profile: PlayerProfileEntity,
    character: com.example.game.CharacterDef,
    world: com.example.game.WorldTheme,
    onChangeCharacter: () -> Unit,
    onChangeWorld: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorderGold, DarkBorder))),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_character_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Location Badge & Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .background(GlassSurfaceLight, RoundedCornerShape(12.dp))
                        .clickable { onChangeWorld() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("select_world_button")
                ) {
                    Text(text = world.iconEmoji, fontSize = 14.sp)
                    Text(
                        text = world.name.uppercase(),
                        color = TextAccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = TextAccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkBgCardElevated
                ) {
                    Text(
                        text = "BEST: ${profile.bestScore} PTS",
                        color = TextAccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Character Visual Avatar & Ability
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Character Pose Preview Box
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(character.outfitColor.copy(alpha = 0.35f), DarkBgCardElevated)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(2.dp, character.accentColor, RoundedCornerShape(20.dp))
                        .clickable { onChangeCharacter() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🏃‍♂️", fontSize = 42.sp)
                        Text(
                            text = character.name,
                            color = character.outfitColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Character Lore & Special Power
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = character.name,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(text = character.countryFlag, fontSize = 14.sp)
                    }

                    Text(
                        text = character.title,
                        color = character.outfitColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBgMain.copy(alpha = 0.6f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "⚡", fontSize = 11.sp)
                            Text(
                                text = character.specialAbilityName,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Character Stats Bar Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatMiniBar("SPD", character.baseSpeed, NeonGold)
                StatMiniBar("JMP", character.baseJump, ElectricCyan)
                StatMiniBar("MAG", character.baseMagnet, AfricanEmerald)
                StatMiniBar("SHD", character.baseShield, ZanzibarTurquoise)
            }
        }
    }
}

@Composable
private fun StatMiniBar(label: String, value: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(5.dp)
                .background(DarkBgMain, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(((value - 0.8f) / 0.8f).coerceIn(0.2f, 1f))
                    .height(5.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
private fun HubButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorder, Color.Transparent))),
        modifier = modifier
            .clickable {
                SoundEngine.playLaneSwitch()
                onClick()
            }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WorldSelectionDialog(
    currentWorldId: String,
    coins: Long,
    onSelectWorld: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "SELECT AFRICAN WORLD",
                color = TextAccentGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WorldCatalog.allWorlds.forEach { w ->
                    val isSelected = w.id == currentWorldId
                    val isUnlocked = w.unlockedByDefault || coins >= w.unlockCostCoins

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) DarkBgCardElevated else DarkBgCard,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) NeonGold else DarkBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                SoundEngine.playPowerUp()
                                onSelectWorld(w.id)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = w.iconEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = w.name,
                                    color = if (isSelected) NeonGold else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = w.subtitle,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Text(text = "ACTIVE", color = AfricanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE", color = TextAccentGold, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkBgMain
    )
}

@Composable
fun GameModeDialog(
    onSelectMode: (GameMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (LocalizationManager.currentLanguage.value == AppLanguage.SWAHILI) "CHAGUA MFUMO WA MCHEZO" else "CHOOSE RUN MODE",
                color = NeonGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(GameMode.ENDLESS, GameMode.TOURNAMENT, GameMode.TIME_ATTACK, GameMode.DAILY_CHALLENGE).forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBgCard, RoundedCornerShape(14.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                SoundEngine.playPowerUp()
                                onSelectMode(mode)
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = when (mode) {
                                GameMode.ENDLESS -> "🏃"
                                GameMode.TOURNAMENT -> "🏆"
                                GameMode.TIME_ATTACK -> "⏱️"
                                else -> "🦁"
                            },
                            fontSize = 26.sp
                        )
                        Column {
                            Text(
                                text = if (mode == GameMode.TOURNAMENT && LocalizationManager.currentLanguage.value == AppLanguage.SWAHILI) "Kombe la Afrika (Tournament)" else mode.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = mode.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CANCEL", color = TextSecondary)
            }
        },
        containerColor = DarkBgMain
    )
}

@Composable
private fun FeaturedSafariBanner(
    worldName: String,
    onExplore: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(NeonGold, TanzaniteBlue))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExplore() }
            .testTag("featured_safari_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_safari_1788142626630),
                contentDescription = "Safari Cityscape",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient scrim for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xEE090D16),
                                Color(0xCC090D16),
                                Color(0x66090D16)
                            )
                        )
                    )
            )

            // Banner content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BrightAmber,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "MSIMU WA BONGO • SAFARI SEASON",
                            color = DarkBgMain,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "⚡ 2X COINS",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Kimbia Afrika: $worldName",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Endless sprint across 6 African iconic cities",
                            color = TextAccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        color = TanzaniteBlue,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Explore",
                            tint = TextPrimary,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


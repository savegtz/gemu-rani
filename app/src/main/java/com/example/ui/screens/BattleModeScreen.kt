package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.game.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BattleModeScreen(
    profile: PlayerProfileEntity,
    onStartBattle: (GameMode, String) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(GameMode.BATTLE_1V1) }
    val currentRoom by MultiplayerService.currentRoom.collectAsState()
    val isSearching by MultiplayerService.isSearchingMatch.collectAsState()
    var roomCodeInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("battle_mode_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column {
                    Text(
                        text = "MULTIPLAYER BATTLE",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time African Highway Sprint Races",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModeTab(
                    title = "1 vs 1 DUEL",
                    subtitle = "Fast Highway Duel",
                    selected = selectedMode == GameMode.BATTLE_1V1,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedMode = GameMode.BATTLE_1V1 }
                )
                ModeTab(
                    title = "4P GRAND PRIX",
                    subtitle = "Continental Clash",
                    selected = selectedMode == GameMode.BATTLE_4P,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedMode = GameMode.BATTLE_4P }
                )
            }

            if (currentRoom == null) {
                // Quick Match Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "QUICK MATCHMAKING",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Find active runners in East, West, Central & Southern Africa instantly.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                SoundEngine.playPowerUp()
                                coroutineScope.launch {
                                    MultiplayerService.quickMatchmaking(
                                        username = profile.username,
                                        country = profile.country,
                                        countryFlag = profile.countryFlag,
                                        avatarId = profile.selectedCharacterId,
                                        mode = selectedMode,
                                        worldId = profile.selectedWorldId,
                                        onMatched = { room ->
                                            onStartBattle(selectedMode, profile.selectedWorldId)
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("quick_match_button"),
                            enabled = !isSearching
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    color = DarkBgMain,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "SEARCHING RUNNERS...", color = DarkBgMain, fontWeight = FontWeight.Bold)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = DarkBgMain)
                                    Text(text = "FIND MATCH", color = DarkBgMain, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Custom Room Code Section
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "PRIVATE DERBY ROOM",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = roomCodeInput,
                            onValueChange = { roomCodeInput = it.take(8) },
                            placeholder = { Text(text = "Enter Room Code (e.g. BONGO-77)", color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    SoundEngine.playPowerUp()
                                    MultiplayerService.createRoom(
                                        hostUsername = profile.username,
                                        hostCountry = profile.country,
                                        hostFlag = profile.countryFlag,
                                        hostAvatar = profile.selectedCharacterId,
                                        mode = selectedMode,
                                        worldId = profile.selectedWorldId
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TanzaniteBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "CREATE ROOM", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (roomCodeInput.isNotBlank()) {
                                        SoundEngine.playPowerUp()
                                        MultiplayerService.joinRoomWithCode(
                                            code = roomCodeInput,
                                            username = profile.username,
                                            country = profile.country,
                                            countryFlag = profile.countryFlag,
                                            avatarId = profile.selectedCharacterId
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                modifier = Modifier.weight(1f),
                                enabled = roomCodeInput.isNotBlank()
                            ) {
                                Text(text = "JOIN CODE", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Room Lobby View
                val room = currentRoom!!
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = room.roomName, color = TextAccentGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text(text = "CODE: ${room.roomCode}", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkBgCardElevated
                            ) {
                                Text(
                                    text = "${room.players.size}/${room.maxPlayers} PLAYERS",
                                    color = TextAccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Players List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(180.dp)
                        ) {
                            items(room.players) { player ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkBgCardElevated, RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = player.countryFlag, fontSize = 20.sp)
                                        Column {
                                            Text(
                                                text = player.username + if (player.isCurrentUser) " (YOU)" else "",
                                                color = if (player.isCurrentUser) NeonGold else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = "${player.trophies} 🏆", color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    Text(
                                        text = if (player.isReady) "READY" else "WAITING",
                                        color = if (player.isReady) AfricanEmerald else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { MultiplayerService.leaveRoom() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "LEAVE", color = CrimsonFire)
                            }

                            Button(
                                onClick = {
                                    SoundEngine.playPowerUp()
                                    onStartBattle(room.mode, room.worldId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "START RACE", color = DarkBgMain, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTab(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) DarkBgCardElevated else DarkBgCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(if (selected) NeonGold else DarkBorder, Color.Transparent)
            )
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (selected) NeonGold else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.game.CrewCatalog
import com.example.game.RunnerCrew
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    profile: PlayerProfileEntity,
    onUpdateProfile: (name: String, country: String, flag: String) -> Unit,
    onSelectCrew: (crewId: String) -> Unit = {},
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var usernameInput by remember { mutableStateOf(profile.username) }
    var selectedFlag by remember { mutableStateOf(profile.countryFlag) }
    var selectedCountry by remember { mutableStateOf(profile.country) }
    var isEditing by remember { mutableStateOf(false) }

    val activeCrew = remember(profile.selectedCrewId) {
        CrewCatalog.getById(profile.selectedCrewId)
    }

    val countryList = listOf(
        Pair("Tanzania", "🇹🇿"),
        Pair("Kenya", "🇰🇪"),
        Pair("Uganda", "🇺🇬"),
        Pair("Rwanda", "🇷🇼"),
        Pair("South Africa", "🇿🇦"),
        Pair("Nigeria", "🇳🇬"),
        Pair("Ghana", "🇬🇭"),
        Pair("Ethiopia", "🇪🇹"),
        Pair("Egypt", "🇪🇬")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                Text(
                    text = "RUNNER PASSPORT & CLAN",
                    color = TextAccentGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Profile Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(BrightAmber, CircleShape)
                            .border(2.dp, NeonGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedFlag, fontSize = 34.sp)
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it.take(16) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            label = { Text("Runner Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Select Country Representation", color = TextSecondary, fontSize = 12.sp)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(countryList) { (c, f) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedFlag == f) NeonGold else DarkBgCardElevated,
                                    modifier = Modifier.clickable {
                                        selectedCountry = c
                                        selectedFlag = f
                                    }
                                ) {
                                    Text(
                                        text = "$f $c",
                                        color = if (selectedFlag == f) DarkBgMain else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                SoundEngine.playPowerUp()
                                onUpdateProfile(usernameInput, selectedCountry, selectedFlag)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "SAVE PASSPORT", color = DarkBgMain, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = profile.username, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text(text = "${profile.country} ${profile.countryFlag}", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            // Active Clan Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = activeCrew.badgeColor.copy(alpha = 0.2f),
                                border = ButtonDefaults.outlinedButtonBorder(true)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = activeCrew.flagEmoji, fontSize = 14.sp)
                                    Text(
                                        text = "CLAN: ${activeCrew.name.uppercase()}",
                                        color = activeCrew.badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { isEditing = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = TextAccentGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "EDIT PROFILE", color = TextAccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // African Runner Crews & Clans Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AFRICAN RUNNER CLANS & CREWS",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Weekly Turf War",
                    color = BrightAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CrewCatalog.allCrews.forEach { crew ->
                    val isSelected = crew.id == profile.selectedCrewId
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) crew.badgeColor.copy(alpha = 0.18f) else DarkBgCard
                        ),
                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder(true) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                SoundEngine.playPowerUp()
                                onSelectCrew(crew.id)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Crew Crest
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(crew.badgeColor.copy(alpha = 0.25f), CircleShape)
                                    .border(1.5.dp, crew.badgeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = crew.flagEmoji, fontSize = 22.sp)
                            }

                            // Crew Info
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = crew.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "• ${crew.city}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "\"${crew.motto}\"",
                                    color = SerengetiYellow,
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "⚡ Perk: ${crew.bonusPerk}",
                                    color = crew.badgeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Selection Status
                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = crew.badgeColor
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DarkBgMain,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "JOINED",
                                            color = DarkBgMain,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        SoundEngine.playPowerUp()
                                        onSelectCrew(crew.id)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "JOIN",
                                        color = TextAccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Career Stats Section
            Text(
                text = "LIFETIME CAREER METRICS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

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
                    StatRow("High Score", "${profile.bestScore} PTS", NeonGold)
                    StatRow("Total Distance Run", "${(profile.totalDistanceMeters / 1000f).toInt()} KM", ElectricCyan)
                    StatRow("Multiplayer Trophies", "${profile.trophies} 🏆", BrightAmber)
                    StatRow("Continental Battles Won", "${profile.wins} WINS", AfricanEmerald)
                    StatRow("Runner Level", "Level ${profile.level}", ZanzibarTurquoise)
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.example.data.MissionEntity
import com.example.game.StorySafariCatalog
import com.example.game.StorySafariChapter
import com.example.ui.theme.*

@Composable
fun MissionsScreen(
    missions: List<MissionEntity>,
    onClaimReward: (String) -> Unit,
    onStartSafariChapter: (StorySafariChapter) -> Unit = {},
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Missions") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("missions_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "BONGO SAFARI & MISSIONS",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Safari Story Mode & Daily Challenges",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBgCard, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf("Safari Story 🗺️", "Majaribio ya Kila Siku 🎯").forEach { tab ->
                    val isSel = (tab.startsWith("Safari") && activeTab == "Safari") || (tab.startsWith("Majaribio") && activeTab == "Missions")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSel) NeonGold else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                activeTab = if (tab.startsWith("Safari")) "Safari" else "Missions"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSel) DarkBgMain else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (activeTab == "Safari") {
                // Safari Story Mode Chapters
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(com.example.game.StorySafariCatalog.chapters) { chapter ->
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = chapter.landmarkEmoji, fontSize = 26.sp)
                                        Column {
                                            Text(
                                                text = chapter.swahiliTitle,
                                                color = NeonGold,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "${chapter.targetDistance.toInt()}m Lengo • ${chapter.title}",
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "🪙 +${chapter.rewardCoins}", color = TextAccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "💎 +${chapter.rewardGems}", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = chapter.subtitle,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )

                                Button(
                                    onClick = {
                                        SoundEngine.playPowerUp()
                                        onStartSafariChapter(chapter)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                ) {
                                    Text(text = "ANZA SURA HII (SAFARI)", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            } else {
                // Missions List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(missions) { mission ->
                        val progress = (mission.currentAmount.toFloat() / mission.targetAmount.toFloat()).coerceIn(0f, 1f)

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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mission.title,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = mission.description,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "🪙 +${mission.rewardCoins}", color = TextAccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "💎 +${mission.rewardGems}", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Progress Bar
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Progress",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "${mission.currentAmount.coerceAtMost(mission.targetAmount)} / ${mission.targetAmount}",
                                            color = TextAccentCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .background(DarkBgMain, RoundedCornerShape(3.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progress)
                                                .height(6.dp)
                                                .background(
                                                    if (mission.isCompleted) AfricanEmerald else NeonGold,
                                                    RoundedCornerShape(3.dp)
                                                )
                                        )
                                    }
                                }

                                // Claim Button
                                if (mission.isCompleted && !mission.isClaimed) {
                                    Button(
                                        onClick = {
                                            SoundEngine.playPowerUp()
                                            onClaimReward(mission.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Text(text = "CLAIM REWARD", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                } else if (mission.isClaimed) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AfricanEmerald, modifier = Modifier.size(16.dp))
                                        Text(text = "COMPLETED", color = AfricanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.MissionEntity
import com.example.ui.theme.*

@Composable
fun MissionsScreen(
    missions: List<MissionEntity>,
    onClaimReward: (String) -> Unit,
    onBack: () -> Unit
) {
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
                        text = "AFRICAN SAFARI MISSIONS",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Complete challenges to earn Tanzanite Gems & Coins",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

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

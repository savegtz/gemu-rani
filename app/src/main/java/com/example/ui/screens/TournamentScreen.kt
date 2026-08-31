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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.game.*
import com.example.ui.theme.*

@Composable
fun TournamentScreen(
    profile: PlayerProfileEntity,
    onStartTournamentMatch: (TournamentMatch) -> Unit,
    onBack: () -> Unit
) {
    val isSwahili = LocalizationManager.currentLanguage.value == AppLanguage.SWAHILI
    val matches = remember { TournamentCatalog.generateTournament() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("tournament_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
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
                            text = if (isSwahili) "KOMBE LA AFRIKA" else "AFRICAN CUP CHAMPIONSHIP",
                            color = NeonGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (isSwahili) "Mchuano wa Mtoano Bara Zima" else "Continental Knockout Bracket",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonGold.copy(alpha = 0.2f),
                    border = ButtonDefaults.outlinedButtonBorder(true)
                ) {
                    Text(
                        text = "🏆 GRAND FINALS",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tournament Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonGold, AfricanEmerald))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(BrightAmber.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, NeonGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSwahili) "Zawadi Kuu ya Bingwa" else "Championship Prize Pool",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🪙 10,000 Coins • 💎 100 Tanzanite Gems",
                            color = NeonGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (isSwahili) "Shinda hatua 3 mfululizo kutwaa taji!" else "Win 3 consecutive knockout stages!",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Text(
                text = if (isSwahili) "HATUA ZA MCHUANO" else "TOURNAMENT BRACKET STAGES",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // Match Stages List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches) { match ->
                    TournamentStageCard(
                        match = match,
                        isSwahili = isSwahili,
                        onPlayMatch = {
                            SoundEngine.playPowerUp()
                            onStartTournamentMatch(match)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentStageCard(
    match: TournamentMatch,
    isSwahili: Boolean,
    onPlayMatch: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (match.stage) {
                            TournamentStage.QUARTER_FINAL -> "🥇"
                            TournamentStage.SEMI_FINAL -> "🥈"
                            TournamentStage.GRAND_FINAL -> "👑"
                        },
                        fontSize = 20.sp
                    )
                    Column {
                        Text(
                            text = if (isSwahili) match.stage.swahiliName else match.stage.stageName,
                            color = NeonGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "🏁 ${match.stage.targetDistance.toInt()}m Sprint Race",
                            color = TextAccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkBgCardElevated
                ) {
                    Text(
                        text = "+${match.stage.rewardCoins} 🪙  +${match.stage.rewardGems} 💎",
                        color = TextAccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Opponent Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = match.rivalFlag, fontSize = 16.sp)
                    Column {
                        Text(
                            text = match.rivalName,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = match.rivalCountry,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrightAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "RIVAL",
                        color = NeonGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Start Race Button
            Button(
                onClick = onPlayMatch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("play_tournament_${match.stage.name.lowercase()}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DarkBgMain,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isSwahili) "ANZA MECHI YA ${match.stage.swahiliName.uppercase()}" else "ENTER ${match.stage.stageName.uppercase()}",
                        color = DarkBgMain,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

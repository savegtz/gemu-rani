package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.data.PlayerProfileEntity
import com.example.game.LeaderboardEntry
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    profile: PlayerProfileEntity,
    onBack: () -> Unit
) {
    val regions = listOf("Continental 🌍", "Tanzania 🇹🇿", "Zanzibar 🏝️", "Kenya 🇰🇪", "Uganda 🇺🇬", "Rwanda 🇷🇼", "South Africa 🇿🇦")
    var selectedRegion by remember { mutableStateOf(regions[0]) }

    val entries = remember(profile.bestScore) {
        listOf(
            LeaderboardEntry(1, "Bakari_Kigamboni", "Tanzania", "🇹🇿", "juma", 184520L, 2450, 142),
            LeaderboardEntry(2, "Amina_SpiceCoast", "Zanzibar", "🇹🇿", "zainabu", 162900L, 2210, 118),
            LeaderboardEntry(3, "Mwangi_SpeedDemon", "Kenya", "🇰🇪", "asha", 149300L, 1980, 95),
            LeaderboardEntry(4, profile.username, profile.country, profile.countryFlag, profile.selectedCharacterId, profile.bestScore.coerceAtLeast(42500L), profile.trophies, profile.wins, isUser = true),
            LeaderboardEntry(5, "Ochieng_Safari", "Uganda", "🇺🇬", "kassim", 38900L, 890, 42),
            LeaderboardEntry(6, "Kagame_Sprint", "Rwanda", "🇷🇼", "juma", 34200L, 760, 36),
            LeaderboardEntry(7, "Sipho_Joburg", "South Africa", "🇿🇦", "asha", 29800L, 620, 28)
        ).sortedByDescending { it.score }
            .mapIndexed { index, item -> item.copy(rank = index + 1) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("leaderboard_screen")
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
                        text = "AFRICAN RANKS & TROPHIES",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Top Sprinters across Africa",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Region Tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(regions) { r ->
                    val isSel = r == selectedRegion
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) NeonGold else DarkBgCard,
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier.clickable { selectedRegion = r }
                    ) {
                        Text(
                            text = r,
                            color = if (isSel) DarkBgMain else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Top 3 Podium
            val top3 = entries.take(3)
            if (top3.size >= 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place
                    PodiumCard(top3[1], height = 120.dp, medal = "🥈", color = Color(0xFF94A3B8), modifier = Modifier.weight(1f))
                    // 1st Place
                    PodiumCard(top3[0], height = 145.dp, medal = "🥇", color = NeonGold, modifier = Modifier.weight(1f))
                    // 3rd Place
                    PodiumCard(top3[2], height = 105.dp, medal = "🥉", color = BrightAmber, modifier = Modifier.weight(1f))
                }
            }

            // Full Leaderboard list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(entries) { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (entry.isUser) DarkBgCardElevated else DarkBgCard,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                if (entry.isUser) NeonGold else DarkBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                color = if (entry.rank <= 3) NeonGold else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(text = entry.countryFlag, fontSize = 20.sp)
                            Column {
                                Text(
                                    text = entry.username + if (entry.isUser) " (YOU)" else "",
                                    color = if (entry.isUser) NeonGold else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${entry.trophies} 🏆 • ${entry.wins} WINS",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entry.score}",
                                color = TextAccentGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(text = "PTS", color = TextMuted, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(
    entry: LeaderboardEntry,
    height: androidx.compose.ui.unit.Dp,
    medal: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(color, DarkBorder))),
        modifier = modifier.height(height)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = medal, fontSize = 22.sp)
            Text(text = entry.username, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = "${entry.score}", color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

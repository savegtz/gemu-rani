package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.game.SponsorAdsManager
import com.example.game.SponsorCampaign
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class AdRewardGoal {
    CURRENCY_BONUS,
    REVIVE_RUNNER,
    DOUBLE_RUN_COINS
}

@Composable
fun SponsorVideoAdModal(
    campaign: SponsorCampaign = SponsorAdsManager.activeCampaign,
    goal: AdRewardGoal = AdRewardGoal.CURRENCY_BONUS,
    onRewardEarned: (coins: Long, gems: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var secondsLeft by remember { mutableIntStateOf(campaign.videoDurationSec) }
    var isCompleted by remember { mutableStateOf(false) }
    var hasSkipped by remember { mutableStateOf(false) }
    val totalDuration = campaign.videoDurationSec

    // Countdown Timer Loop
    LaunchedEffect(Unit) {
        while (secondsLeft > 0 && !hasSkipped) {
            delay(1000L)
            secondsLeft -= 1
        }
        if (!hasSkipped) {
            isCompleted = true
            SoundEngine.playGem()
        }
    }

    // Video Canvas Ambient Pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "video_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.94f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("sponsor_video_ad_modal"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR: Sponsor Label, Countdown & Close/Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = campaign.themeColor.copy(alpha = 0.2f),
                    border = ButtonDefaults.outlinedButtonBorder(true)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = campaign.iconEmoji, fontSize = 14.sp)
                        Text(
                            text = campaign.badgeText,
                            color = campaign.themeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkBgCardElevated
                        ) {
                            Text(
                                text = "Zawadi ndani ya: ${secondsLeft}s",
                                color = TextAccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Close or Skip Button
                    val canSkip = (totalDuration - secondsLeft) >= 5 || isCompleted
                    IconButton(
                        onClick = {
                            if (canSkip) {
                                hasSkipped = true
                                if (isCompleted) {
                                    onRewardEarned(campaign.rewardCoins, campaign.rewardGems)
                                }
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (canSkip) DarkBgCardElevated else Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (canSkip) TextPrimary else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // CENTRAL VIDEO PLAYER SIMULATION FRAME
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(campaign.themeColor.copy(alpha = pulseGlow), DarkBorder)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Video Background Ambient Glow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        campaign.themeColor.copy(alpha = 0.35f),
                                        Color(0xFF070B12),
                                        Color.Black
                                    )
                                )
                            )
                    )

                    // Video Showcase Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Brand Crest & Header
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(campaign.themeColor.copy(alpha = 0.25f), CircleShape)
                                    .border(2.5.dp, campaign.themeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = campaign.iconEmoji, fontSize = 36.sp)
                            }

                            Text(
                                text = campaign.brandName,
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = campaign.tagLine,
                                color = campaign.themeColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            // Rating Stars & Downloads
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = NeonGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "${campaign.rating} • ${campaign.downloadsText}",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Headline & Features Ticker
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkBgMain.copy(alpha = 0.85f)),
                            border = ButtonDefaults.outlinedButtonBorder(true),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = campaign.headline,
                                    color = SerengetiYellow,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = campaign.description,
                                    color = TextPrimary.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (campaign.features.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    campaign.features.take(2).forEach { feat ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = AfricanEmerald,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = feat,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Reward Progress Bar at bottom of video player
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val progress = 1f - (secondsLeft.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isCompleted) AfricanEmerald else campaign.themeColor,
                                trackColor = DarkBgMain
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isCompleted) "✅ ZAWADI IMETHIBITISHWA!" else "Tazama hadi mwisho upate zawadi",
                                    color = if (isCompleted) AfricanEmerald else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when (goal) {
                                        AdRewardGoal.REVIVE_RUNNER -> "💖 Revive Runner"
                                        AdRewardGoal.DOUBLE_RUN_COINS -> "⚡ 2X Match Coins"
                                        AdRewardGoal.CURRENCY_BONUS -> "+${campaign.rewardCoins} 🪙 +${campaign.rewardGems} 💎"
                                    },
                                    color = TextAccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM ACTION BUTTONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Visit Client Website / Download App CTA Button
                Button(
                    onClick = {
                        SoundEngine.playPowerUp()
                        SponsorAdsManager.openCampaignUrl(context, campaign)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = campaign.themeColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("sponsor_cta_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = campaign.ctaText,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 2. Claim Reward & Continue (Enabled when video completes)
                if (isCompleted) {
                    Button(
                        onClick = {
                            SoundEngine.playVictory()
                            onRewardEarned(campaign.rewardCoins, campaign.rewardGems)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("claim_ad_reward_button")
                    ) {
                        Text(
                            text = when (goal) {
                                AdRewardGoal.REVIVE_RUNNER -> "💖 FUFUKA SASA NA HOVERBOARD"
                                AdRewardGoal.DOUBLE_RUN_COINS -> "🪙 POKEA SARAFU MARA 2"
                                AdRewardGoal.CURRENCY_BONUS -> "🎁 CHUKUA +${campaign.rewardCoins} COINS & +${campaign.rewardGems} GEMS"
                            },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

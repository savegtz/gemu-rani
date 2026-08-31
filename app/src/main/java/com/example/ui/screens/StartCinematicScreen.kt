package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.ui.theme.*

@Composable
fun StartCinematicScreen(
    onStartGame: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenAdmin: () -> Unit,
    onPlayAsGuest: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cinematic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0B0F19)
                    )
                )
            )
            .testTag("start_cinematic_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background aurora rings
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(pulseScale)
                .alpha(glowAlpha * 0.5f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonGold.copy(alpha = 0.35f),
                            ElectricCyan.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(30.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // African Flag & Heritage Crest
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(
                        color = GlassSurfaceLight,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(text = "🇹🇿", fontSize = 16.sp)
                Text(
                    text = "TANZANIA & EAST AFRICA",
                    color = TextAccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(text = "🌍", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main 3D Logo
            Text(
                text = "BONGO",
                color = NeonGold,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "RUNNER",
                color = ElectricCyan,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Slogan
            Text(
                text = "RUN THE STREETS. OWN AFRICA.",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. ANZA KUCHEZA (START GAME)
            Button(
                onClick = {
                    SoundEngine.playPowerUp()
                    onStartGame()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .scale(pulseScale)
                    .testTag("tap_to_start_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = DarkBgMain,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "ANZA KUKIMBIA (PLAY NOW)",
                        color = DarkBgMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. JISAJILI / INGIA (REGISTER / LOGIN)
            Button(
                onClick = {
                    SoundEngine.playMenuClick()
                    onOpenAuth()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AfricanEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_register_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📝", fontSize = 16.sp)
                    Text(
                        text = "JISAJILI / INGIA (REGISTER / LOGIN)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. ADMIN PANEL ACCESS
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkBgCardElevated,
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        SoundEngine.playMenuClick()
                        onOpenAdmin()
                    }
                    .testTag("start_admin_panel_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "👑", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PANELI YA ADMIN (ADMIN DASHBOARD)",
                        color = NeonGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Quick Guest Mode
            Text(
                text = "⚡ Au gusa hapa kucheza kama Mgeni (Play as Guest)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        SoundEngine.playMenuClick()
                        onPlayAsGuest()
                    }
                    .padding(8.dp)
                    .testTag("start_guest_play_link")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick feature tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "⚡ 3D ENDLESS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "•", color = TextMuted, fontSize = 10.sp)
                Text(text = "🏁 REALTIME BATTLES", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "•", color = TextMuted, fontSize = 10.sp)
                Text(text = "🛡️ 4 HEROES", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

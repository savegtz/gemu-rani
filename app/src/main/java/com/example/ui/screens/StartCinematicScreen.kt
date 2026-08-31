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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    onStartGame: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cinematic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
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

    val interactionSource = remember { MutableInteractionSource() }

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
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                SoundEngine.playPowerUp()
                onStartGame()
            }
            .testTag("start_cinematic_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background aurora rings
        Box(
            modifier = Modifier
                .size(320.dp)
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

            Spacer(modifier = Modifier.height(28.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle Slogan
            Text(
                text = "RUN THE STREETS. OWN AFRICA.",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Interactive "TAP TO RUN" Pill
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrightAmber,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .scale(pulseScale)
                    .testTag("tap_to_start_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = DarkBgMain,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "TAP TO START",
                        color = DarkBgMain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Quick feature tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "⚡ 3D ENDLESS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "•", color = TextMuted, fontSize = 11.sp)
                Text(text = "🏁 REALTIME BATTLES", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "•", color = TextMuted, fontSize = 11.sp)
                Text(text = "🛡️ 4 HEROES", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

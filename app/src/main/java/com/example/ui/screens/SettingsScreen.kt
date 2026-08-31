package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAdmin: () -> Unit = {}
) {
    var sfxEnabled by remember { mutableStateOf(SoundEngine.isSoundEnabled) }
    var musicEnabled by remember { mutableStateOf(SoundEngine.isMusicEnabled) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    val graphicQuality = "Ultra 60 FPS"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen")
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
                Text(
                    text = "SETTINGS & AUDIO",
                    color = TextAccentGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingToggle(
                        title = "African Drum Rhythm Beat",
                        subtitle = "Dynamic procedural percussion soundtrack",
                        checked = musicEnabled,
                        onCheckedChange = { isChecked ->
                            musicEnabled = isChecked
                            SoundEngine.isMusicEnabled = isChecked
                            if (!isChecked) SoundEngine.stopMusic()
                        }
                    )

                    HorizontalDivider(color = DarkBorder)

                    SettingToggle(
                        title = "Sound Effects & Audio FX",
                        subtitle = "Jump, slide, coin pickup & power-up cues",
                        checked = sfxEnabled,
                        onCheckedChange = { isChecked ->
                            sfxEnabled = isChecked
                            SoundEngine.isSoundEnabled = isChecked
                        }
                    )

                    HorizontalDivider(color = DarkBorder)

                    SettingToggle(
                        title = "Haptic Vibration",
                        subtitle = "Tactile bump on collision & lane switches",
                        checked = vibrationEnabled,
                        onCheckedChange = { isChecked -> vibrationEnabled = isChecked }
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "GRAPHICS ENGINE", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Rendering pipeline: Jetpack Compose 3D Horizon Canvas with dynamic shadows", color = TextMuted, fontSize = 11.sp)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkBgCardElevated
                    ) {
                        Text(
                            text = "STATUS: $graphicQuality • ACTIVE",
                            color = AfricanEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Dedicated Admin Dashboard Access Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkBgCardElevated,
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        SoundEngine.playMenuClick()
                        onOpenAdmin()
                    }
                    .testTag("settings_open_admin_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "👑", fontSize = 20.sp)
                        Column {
                            Text(
                                text = "Paneli ya Msimamizi (Admin Panel)",
                                color = NeonGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Live Ops, Sarafu, Matangazo, na Wachezaji",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Text(text = "FUNGUA →", color = NeonGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBgMain,
                checkedTrackColor = NeonGold,
                uncheckedTrackColor = DarkBgCardElevated
            )
        )
    }
}

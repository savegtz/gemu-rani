package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
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
import com.example.game.AdminMetric
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onGrantCurrency: (coins: Long, gems: Int) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val metrics = remember { AdminMetric() }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_dashboard_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
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
                        text = "BONGO LIVE OPERATIONS",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Server Cluster & Live Match Telemetry",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (actionMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AfricanEmerald.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionMessage!!,
                        color = AfricanEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Real-Time Server Telemetry
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
                    Text(
                        text = "CONTINENTAL CLOUD CLUSTER",
                        color = TextAccentGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    MetricItem("Server Edge Region", metrics.serverRegion, ElectricCyan)
                    MetricItem("Tick Rate", "${metrics.tickRateHz} Hz (Sub-15ms sync)", AfricanEmerald)
                    MetricItem("Anti-Cheat Shield", metrics.antiCheatStatus, AfricanEmerald)
                    MetricItem("Total Registered Players", "142,850 Sprinters", TextPrimary)
                    MetricItem("Daily Active Runners", "28,400 DAU", TextPrimary)
                    MetricItem("Concurrent Online Matches", "3,120 Live Duels", NeonGold)
                    MetricItem("Circulating Shillings", "184.5M 🪙", BrightAmber)
                }
            }

            // Developer Sandbox Controls
            Text(
                text = "SANDBOX TOOLS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

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
                    Button(
                        onClick = {
                            SoundEngine.playPowerUp()
                            onGrantCurrency(10000, 100)
                            actionMessage = "Granted +10,000 Coins & +100 Gems successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "🪙 GRANT +10,000 COINS & +100 GEMS", color = DarkBgMain, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = {
                            SoundEngine.playPowerUp()
                            onGrantCurrency(50000, 500)
                            actionMessage = "Granted Mega Tester Vault (50k Coins, 500 Gems)!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TanzaniteBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "💎 GRANT MEGA TESTER VAULT", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

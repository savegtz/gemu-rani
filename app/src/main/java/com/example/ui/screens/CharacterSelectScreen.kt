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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.PlayerProfileEntity
import com.example.data.UnlockedItemEntity
import com.example.game.CharacterCatalog
import com.example.game.CharacterDef
import com.example.ui.theme.*

@Composable
fun CharacterSelectScreen(
    profile: PlayerProfileEntity,
    unlockedItems: List<UnlockedItemEntity>,
    onSelectCharacter: (String) -> Unit,
    onBuyCharacter: (String, Int, Int) -> Unit,
    onUpgradeStat: (String) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var inspectedCharId by remember { mutableStateOf(profile.selectedCharacterId) }
    val inspected = CharacterCatalog.getById(inspectedCharId)
    val isInspectedUnlocked = inspected.unlockedByDefault || unlockedItems.any { it.itemId == inspected.id && it.itemType == "character" }
    val isInspectedEquipped = profile.selectedCharacterId == inspected.id

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("character_select_screen")
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
                    Text(
                        text = "HEROES & UPGRADES",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Coin & Gem Pill
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🪙 ${profile.coins}", color = TextAccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "💎 ${profile.gems}", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Character Carousel Horizontal Selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CharacterCatalog.allCharacters) { c ->
                    val isSelected = c.id == inspectedCharId
                    val isUnlocked = c.unlockedByDefault || unlockedItems.any { it.itemId == c.id }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkBgCardElevated else DarkBgCard
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(
                                listOf(if (isSelected) NeonGold else DarkBorder, Color.Transparent)
                            )
                        ),
                        modifier = Modifier
                            .width(110.dp)
                            .clickable {
                                SoundEngine.playLaneSwitch()
                                inspectedCharId = c.id
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(c.outfitColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.5.dp, c.accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏃‍♂️", fontSize = 24.sp)
                                if (!isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = NeonGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = c.name,
                                color = if (isSelected) NeonGold else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Inspected Character Showcase Details
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = inspected.name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                Text(text = inspected.countryFlag, fontSize = 16.sp)
                            }
                            Text(text = inspected.title, color = inspected.outfitColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (isInspectedEquipped) {
                            Surface(shape = RoundedCornerShape(8.dp), color = AfricanEmerald.copy(alpha = 0.2f)) {
                                Text(text = "EQUIPPED", color = AfricanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Text(text = inspected.description, color = TextSecondary, fontSize = 12.sp)

                    // Special Ability Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkBgCardElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "⚡ SPECIAL: ${inspected.specialAbilityName}", color = TextAccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = inspected.specialAbilityDesc, color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Equip or Buy Button
                    if (isInspectedUnlocked) {
                        if (!isInspectedEquipped) {
                            Button(
                                onClick = {
                                    SoundEngine.playPowerUp()
                                    onSelectCharacter(inspected.id)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text(text = "EQUIP RUNNER", color = DarkBgMain, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                SoundEngine.playPowerUp()
                                onBuyCharacter(inspected.id, inspected.unlockCostCoins, inspected.unlockCostGems)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            val priceStr = if (inspected.unlockCostCoins > 0) "🪙 ${inspected.unlockCostCoins} COINS" else "💎 ${inspected.unlockCostGems} GEMS"
                            Text(text = "UNLOCK FOR $priceStr", color = DarkBgMain, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Power-up Upgrades Station
            Text(
                text = "UPGRADE WORKSHOP (GLOBAL)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            UpgradeRow(
                title = "Turbo Speed Duration",
                subtitle = "Faster acceleration & longer turbo sprint",
                level = profile.speedUpgradeLevel,
                maxLevel = 10,
                color = CrimsonFire,
                coins = profile.coins,
                onUpgrade = { onUpgradeStat("speed") }
            )

            UpgradeRow(
                title = "Coin Magnet Strength",
                subtitle = "Wider pickup radius & magnetic duration",
                level = profile.magnetUpgradeLevel,
                maxLevel = 10,
                color = NeonGold,
                coins = profile.coins,
                onUpgrade = { onUpgradeStat("magnet") }
            )

            UpgradeRow(
                title = "Energy Shield Armor",
                subtitle = "Resilient protective shield duration",
                level = profile.shieldUpgradeLevel,
                maxLevel = 10,
                color = ElectricCyan,
                coins = profile.coins,
                onUpgrade = { onUpgradeStat("shield") }
            )

            UpgradeRow(
                title = "Spring Jump Boost",
                subtitle = "Higher vertical obstacle leap clearance",
                level = profile.jumpUpgradeLevel,
                maxLevel = 10,
                color = AfricanEmerald,
                coins = profile.coins,
                onUpgrade = { onUpgradeStat("jump") }
            )
        }
    }
}

@Composable
private fun UpgradeRow(
    title: String,
    subtitle: String,
    level: Int,
    maxLevel: Int,
    color: Color,
    coins: Long,
    onUpgrade: () -> Unit
) {
    val cost = level * 800
    val canAfford = coins >= cost && level < maxLevel

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.2f)) {
                        Text(
                            text = "LVL $level/$maxLevel",
                            color = color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(text = subtitle, color = TextMuted, fontSize = 10.sp)

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(4.dp)
                        .background(DarkBgMain, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(level / maxLevel.toFloat())
                            .height(4.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
            }

            if (level < maxLevel) {
                Button(
                    onClick = {
                        SoundEngine.playPowerUp()
                        onUpgrade()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                    shape = RoundedCornerShape(10.dp),
                    enabled = canAfford,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙 $cost", color = DarkBgMain, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            } else {
                Text(text = "MAX", color = AfricanEmerald, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

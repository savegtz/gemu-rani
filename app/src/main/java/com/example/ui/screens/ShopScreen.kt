package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.data.PlayerProfileEntity
import com.example.data.UnlockedItemEntity
import com.example.game.HoverboardCatalog
import com.example.game.LocalizationManager
import com.example.game.ShopItem
import com.example.ui.theme.*

@Composable
fun ShopScreen(
    profile: PlayerProfileEntity,
    unlockedItems: List<UnlockedItemEntity>,
    onBuyItem: (ShopItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Hoverboards", "Outfits & Skins", "African Trails")

    val catalogItems = remember {
        listOf(
            ShopItem("board_kilimanjaro", "Kilimanjaro Glider", "Float like snow with reduced gravity jumps", "board", priceCoins = 2500, iconEmoji = "🏔️", previewColor = AfricanEmerald),
            ShopItem("board_zanzibar", "Zanzibar Wave Rider", "Double coins collection aura on coastal runs", "board", priceCoins = 4000, iconEmoji = "🏄", previewColor = ZanzibarTurquoise),
            ShopItem("board_serengeti", "Serengeti Cheetah Deck", "+15% top speed turbo burst", "board", priceGems = 60, iconEmoji = "🐆", previewColor = BrightAmber),
            ShopItem("board_bongo_neon", "Bongo Neon Cruiser", "Neon night illumination & magnet trail", "board", priceGems = 80, iconEmoji = "🛹", previewColor = ElectricCyan),
            ShopItem("skin_juma_gold", "Serengeti Golden Juma", "Shimmering gold Kitenge hoodie", "skin", priceCoins = 3500, iconEmoji = "👑", previewColor = NeonGold),
            ShopItem("skin_asha_sunset", "Zanzibar Sunset Asha", "Vibrant crimson sunset athleisure", "skin", priceCoins = 3000, iconEmoji = "🌅", previewColor = CrimsonFire),
            ShopItem("skin_zainabu_royal", "Stone Town Royal Zainabu", "Turquoise embroidered Swahili silks", "skin", priceGems = 80, iconEmoji = "💎", previewColor = ZanzibarTurquoise),
            ShopItem("trail_neon_cyan", "Electric Cyan Trail", "Sparks of neon lightning behind runner", "trail", priceCoins = 2000, iconEmoji = "⚡", previewColor = ElectricCyan),
            ShopItem("trail_gold_sparkle", "Tanzanite Sparkles", "Floating blue gemstones and sparks", "trail", priceGems = 50, iconEmoji = "✨", previewColor = TanzaniteBlue)
        )
    }

    val filteredItems = remember(selectedCategory) {
        when (selectedCategory) {
            "Hoverboards" -> catalogItems.filter { it.type == "board" }
            "Outfits & Skins" -> catalogItems.filter { it.type == "skin" }
            "African Trails" -> catalogItems.filter { it.type == "trail" }
            else -> catalogItems
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("shop_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
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
                        text = "BONGO BAZAAR",
                        color = TextAccentGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🪙 ${profile.coins}", color = TextAccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "💎 ${profile.gems}", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Category Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) NeonGold else DarkBgCard,
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) DarkBgMain else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Products Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    val isOwned = unlockedItems.any { it.itemId == item.id }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(item.previewColor.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    .border(1.5.dp, item.previewColor, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.iconEmoji, fontSize = 32.sp)
                            }

                            Text(
                                text = item.title,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                text = item.subtitle,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 2,
                                minLines = 2
                            )

                            if (isOwned) {
                                Text(text = "OWNED", color = AfricanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            } else {
                                Button(
                                    onClick = {
                                        SoundEngine.playPowerUp()
                                        onBuyItem(item)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    val price = if (item.priceCoins > 0) "🪙 ${item.priceCoins}" else if (item.priceGems > 0) "💎 ${item.priceGems}" else "CLAIM"
                                    Text(text = price, color = DarkBgMain, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

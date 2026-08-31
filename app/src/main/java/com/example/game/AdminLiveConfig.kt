package com.example.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class RegisteredPlayerAccount(
    val id: String,
    val fullName: String,
    val username: String,
    val emailOrPhone: String,
    val mkoa: String,
    val coins: Long,
    val gems: Int,
    val highScore: Long,
    val level: Int,
    var isBanned: Boolean = false,
    val registrationDate: String = "2026-08-15"
)

object AdminLiveConfig {
    // 1. Live Announcements & Maintenance
    var globalAnnouncement by mutableStateOf("🏆 MSIMU MPYA: Kombe la Mikoa Tanzania linaendelea! Pata pointi maradufu Dar, Arusha na Mwanza!")
    var isAnnouncementActive by mutableStateOf(true)
    
    var isMaintenanceMode by mutableStateOf(false)
    var maintenanceMessage by mutableStateOf("Mchezo upo kwenye maboresho ya seva kwa dakika chache. Tafadhali subiri kidogo!")

    // 2. Global Game Economics & Multipliers
    var globalCoinMultiplier by mutableFloatStateOf(1.0f) // 1x, 2x, 3x, 5x, 10x
    var doubleDailyRewardsActive by mutableStateOf(false)
    var shopDiscountPercent by mutableIntStateOf(0) // 0%, 20%, 50%, 75%
    var activeSeasonName by mutableStateOf("Msimu wa 1: Mbio za Afrika Mashariki 🌍")

    // 3. Gameplay Adjustments & Overrides
    var forcedWeatherOverride by mutableStateOf("AUTO") // AUTO, SUNNY, RAINY, NIGHT
    var globalGameSpeedMultiplier by mutableFloatStateOf(1.0f) // 0.8f, 1.0f, 1.25f, 1.5f
    var antiCheatSensitivity by mutableStateOf("Imara (Strict - 99.8%)")

    // 4. Player Accounts Directory (Sample of community registered players for live moderation)
    val registeredPlayers = mutableStateListOf<RegisteredPlayerAccount>(
        RegisteredPlayerAccount(
            id = "user_001",
            fullName = "Juma Bakari",
            username = "Juma_Speed",
            emailOrPhone = "0712345678",
            mkoa = "Dar es Salaam",
            coins = 12500,
            gems = 45,
            highScore = 84500,
            level = 7,
            isBanned = false,
            registrationDate = "2026-08-01"
        ),
        RegisteredPlayerAccount(
            id = "user_002",
            fullName = "Asha Rashid",
            username = "Asha_Safari",
            emailOrPhone = "asha.rashid@gmail.com",
            mkoa = "Arusha",
            coins = 24000,
            gems = 120,
            highScore = 142000,
            level = 12,
            isBanned = false,
            registrationDate = "2026-08-04"
        ),
        RegisteredPlayerAccount(
            id = "user_003",
            fullName = "Emmanuel Mwamba",
            username = "RockCity_King",
            emailOrPhone = "0784556677",
            mkoa = "Mwanza",
            coins = 38900,
            gems = 180,
            highScore = 210500,
            level = 16,
            isBanned = false,
            registrationDate = "2026-08-10"
        ),
        RegisteredPlayerAccount(
            id = "user_004",
            fullName = "Ali Mohamed",
            username = "Ali_StoneTown",
            emailOrPhone = "ali.zanzibar@yahoo.com",
            mkoa = "Zanzibar",
            coins = 9800,
            gems = 30,
            highScore = 65000,
            level = 5,
            isBanned = false,
            registrationDate = "2026-08-18"
        ),
        RegisteredPlayerAccount(
            id = "user_005",
            fullName = "Neema Temu",
            username = "Kibo_Challenger",
            emailOrPhone = "0755998811",
            mkoa = "Kilimanjaro",
            coins = 16400,
            gems = 65,
            highScore = 98700,
            level = 9,
            isBanned = false,
            registrationDate = "2026-08-22"
        ),
        RegisteredPlayerAccount(
            id = "user_006",
            fullName = "Peter Mrema",
            username = "Speed_Hacker_99",
            emailOrPhone = "bot99@tempmail.com",
            mkoa = "Dodoma",
            coins = 999999,
            gems = 9999,
            highScore = 9999999,
            level = 99,
            isBanned = true,
            registrationDate = "2026-08-28"
        )
    )

    fun togglePlayerBan(playerId: String) {
        val index = registeredPlayers.indexOfFirst { it.id == playerId }
        if (index != -1) {
            val p = registeredPlayers[index]
            registeredPlayers[index] = p.copy(isBanned = !p.isBanned)
        }
    }

    fun rewardPlayer(playerId: String, coins: Long, gems: Int) {
        val index = registeredPlayers.indexOfFirst { it.id == playerId }
        if (index != -1) {
            val p = registeredPlayers[index]
            registeredPlayers[index] = p.copy(coins = p.coins + coins, gems = p.gems + gems)
        }
    }

    // 5. Gift Codes & Promos
    val promoCodes = mutableStateListOf<Pair<String, String>>(
        "BONGO2026" to "🪙 5,000 Coins + 50 Gems (Active)",
        "KARIAKOO" to "🪙 2,500 Coins (Active)",
        "ZANZIBAR" to "💎 30 Gems (Active)"
    )
}

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "Juma_Runner",
    val fullName: String = "Juma Bakari",
    val emailOrPhone: String = "",
    val passwordHash: String = "",
    val isLoggedIn: Boolean = false,
    val selectedMkoa: String = "Dar es Salaam",
    val country: String = "Tanzania",
    val countryFlag: String = "🇹🇿",
    val avatarId: String = "juma",
    val coins: Long = 1250,
    val gems: Int = 45,
    val trophies: Int = 340,
    val level: Int = 4,
    val xp: Int = 850,
    val xpToNextLevel: Int = 1500,
    val bestScore: Long = 18450,
    val totalDistanceMeters: Long = 42300,
    val wins: Int = 14,
    val losses: Int = 5,
    val selectedCharacterId: String = "juma",
    val selectedWorldId: String = "dar_es_salaam",
    val selectedSkinId: String = "default",
    val selectedTrailId: String = "gold_sparkle",
    val selectedCrewId: String = "crew_kariakoo",
    // Upgrade levels (1 to 10)
    val speedUpgradeLevel: Int = 2,
    val magnetUpgradeLevel: Int = 2,
    val shieldUpgradeLevel: Int = 1,
    val jumpUpgradeLevel: Int = 2,
    // Settings
    val soundFxEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val lastDailyRewardClaimTime: Long = 0L
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetAmount: Int,
    val currentAmount: Int = 0,
    val rewardCoins: Int,
    val rewardGems: Int,
    val rewardXp: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val worldRequirement: String? = null
)

@Entity(tableName = "unlocked_items")
data class UnlockedItemEntity(
    @PrimaryKey val itemId: String,
    val itemType: String, // "character", "skin", "trail", "world"
    val unlockedAt: Long = System.currentTimeMillis()
)

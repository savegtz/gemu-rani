package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val playerDao: PlayerDao) {

    val playerProfile: Flow<PlayerProfileEntity?> = playerDao.getPlayerProfile()
    val missions: Flow<List<MissionEntity>> = playerDao.getAllMissions()
    val unlockedItems: Flow<List<UnlockedItemEntity>> = playerDao.getUnlockedItems()

    suspend fun initializeDefaultsIfNeeded() {
        val existing = playerDao.getPlayerProfileSync()
        if (existing == null) {
            val defaultProfile = PlayerProfileEntity()
            playerDao.insertOrUpdateProfile(defaultProfile)

            // Seed default missions
            val initialMissions = listOf(
                MissionEntity(
                    id = "m_run_2km",
                    title = "Run 2,000 Meters",
                    description = "Sprint across any African city for 2 KM in total.",
                    targetAmount = 2000,
                    rewardCoins = 600,
                    rewardGems = 5,
                    rewardXp = 250
                ),
                MissionEntity(
                    id = "m_coins_300",
                    title = "Collect 300 Gold Coins",
                    description = "Grab shining Tanzanian Schilling coins on the streets.",
                    targetAmount = 300,
                    rewardCoins = 400,
                    rewardGems = 3,
                    rewardXp = 180
                ),
                MissionEntity(
                    id = "m_jump_30",
                    title = "Leap Over 30 Barriers",
                    description = "Perform 30 jumps over road hazards and fruit stalls.",
                    targetAmount = 30,
                    rewardCoins = 500,
                    rewardGems = 4,
                    rewardXp = 200
                ),
                MissionEntity(
                    id = "m_slide_20",
                    title = "Slide Under 20 Obstacles",
                    description = "Slide safely under low Zanzibar carved archways & barriers.",
                    targetAmount = 20,
                    rewardCoins = 450,
                    rewardGems = 4,
                    rewardXp = 190
                ),
                MissionEntity(
                    id = "m_battle_win",
                    title = "Win 2 Multiplayer Battles",
                    description = "Outrun African racers in 1v1 or 4-Player Grand Prix.",
                    targetAmount = 2,
                    rewardCoins = 1000,
                    rewardGems = 10,
                    rewardXp = 500
                ),
                MissionEntity(
                    id = "m_powerups_5",
                    title = "Activate 5 Power-Ups",
                    description = "Collect magnets, shields, turbo speed, or freeze attacks.",
                    targetAmount = 5,
                    rewardCoins = 550,
                    rewardGems = 5,
                    rewardXp = 220
                )
            )
            playerDao.insertMissions(initialMissions)

            // Seed default unlocked items
            playerDao.unlockItem(UnlockedItemEntity("juma", "character"))
            playerDao.unlockItem(UnlockedItemEntity("dar_es_salaam", "world"))
            playerDao.unlockItem(UnlockedItemEntity("zanzibar", "world"))
            playerDao.unlockItem(UnlockedItemEntity("default", "skin"))
            playerDao.unlockItem(UnlockedItemEntity("gold_sparkle", "trail"))
        }
    }

    suspend fun updateRunRewards(
        score: Long,
        coinsEarned: Int,
        gemsEarned: Int,
        distanceMeters: Float,
        isMultiplayerWin: Boolean = false
    ) {
        val current = playerDao.getPlayerProfileSync() ?: PlayerProfileEntity()
        val newCoins = current.coins + coinsEarned
        val newGems = current.gems + gemsEarned
        val newBestScore = if (score > current.bestScore) score else current.bestScore
        val newTotalDistance = current.totalDistanceMeters + distanceMeters.toLong()

        val gainedXp = (distanceMeters / 5f).toInt() + coinsEarned * 2 + (if (isMultiplayerWin) 300 else 50)
        var totalXp = current.xp + gainedXp
        var level = current.level
        var xpToNext = current.xpToNextLevel

        while (totalXp >= xpToNext) {
            totalXp -= xpToNext
            level += 1
            xpToNext = (xpToNext * 1.25f).toInt()
        }

        val updated = current.copy(
            coins = newCoins,
            gems = newGems,
            bestScore = newBestScore,
            totalDistanceMeters = newTotalDistance,
            xp = totalXp,
            level = level,
            xpToNextLevel = xpToNext,
            trophies = if (isMultiplayerWin) current.trophies + 25 else (current.trophies - 5).coerceAtLeast(0),
            wins = if (isMultiplayerWin) current.wins + 1 else current.wins,
            losses = if (!isMultiplayerWin && isMultiplayerWin) current.losses + 1 else current.losses
        )
        playerDao.updateProfile(updated)
    }

    suspend fun upgradeStat(statType: String): Boolean {
        val current = playerDao.getPlayerProfileSync() ?: return false
        val currentLevel = when (statType) {
            "speed" -> current.speedUpgradeLevel
            "magnet" -> current.magnetUpgradeLevel
            "shield" -> current.shieldUpgradeLevel
            "jump" -> current.jumpUpgradeLevel
            else -> 10
        }
        if (currentLevel >= 10) return false

        val cost = currentLevel * 800
        if (current.coins < cost) return false

        val updated = when (statType) {
            "speed" -> current.copy(coins = current.coins - cost, speedUpgradeLevel = currentLevel + 1)
            "magnet" -> current.copy(coins = current.coins - cost, magnetUpgradeLevel = currentLevel + 1)
            "shield" -> current.copy(coins = current.coins - cost, shieldUpgradeLevel = currentLevel + 1)
            "jump" -> current.copy(coins = current.coins - cost, jumpUpgradeLevel = currentLevel + 1)
            else -> current
        }
        playerDao.updateProfile(updated)
        return true
    }

    suspend fun buyAndEquipCharacter(characterId: String, costCoins: Int, costGems: Int): Boolean {
        val current = playerDao.getPlayerProfileSync() ?: return false
        if (current.coins < costCoins || current.gems < costGems) return false

        val updated = current.copy(
            coins = current.coins - costCoins,
            gems = current.gems - costGems,
            selectedCharacterId = characterId
        )
        playerDao.updateProfile(updated)
        playerDao.unlockItem(UnlockedItemEntity(characterId, "character"))
        return true
    }

    suspend fun buyAndUnlockWorld(worldId: String, costCoins: Int): Boolean {
        val current = playerDao.getPlayerProfileSync() ?: return false
        if (current.coins < costCoins) return false

        val updated = current.copy(
            coins = current.coins - costCoins,
            selectedWorldId = worldId
        )
        playerDao.updateProfile(updated)
        playerDao.unlockItem(UnlockedItemEntity(worldId, "world"))
        return true
    }

    suspend fun selectCharacter(characterId: String) {
        val current = playerDao.getPlayerProfileSync() ?: return
        playerDao.updateProfile(current.copy(selectedCharacterId = characterId))
    }

    suspend fun selectWorld(worldId: String) {
        val current = playerDao.getPlayerProfileSync() ?: return
        playerDao.updateProfile(current.copy(selectedWorldId = worldId))
    }

    suspend fun updateProfileInfo(name: String, country: String, flag: String) {
        val current = playerDao.getPlayerProfileSync() ?: return
        playerDao.updateProfile(current.copy(username = name, country = country, countryFlag = flag))
    }

    suspend fun recordMissionProgress(eventType: String, amount: Int) {
        val missionsList = playerDao.getAllMissions().firstOrNull() ?: return
        missionsList.forEach { m ->
            if (!m.isCompleted) {
                val shouldTrack = when (m.id) {
                    "m_run_2km" -> eventType == "distance"
                    "m_coins_300" -> eventType == "coin"
                    "m_jump_30" -> eventType == "jump"
                    "m_slide_20" -> eventType == "slide"
                    "m_battle_win" -> eventType == "battle_win"
                    "m_powerups_5" -> eventType == "powerup"
                    else -> false
                }
                if (shouldTrack) {
                    val newAmt = m.currentAmount + amount
                    val completed = newAmt >= m.targetAmount
                    playerDao.updateMissionProgress(m.id, newAmt, completed)
                }
            }
        }
    }

    suspend fun claimMissionReward(missionId: String): Boolean {
        val missionsList = playerDao.getAllMissions().firstOrNull() ?: return false
        val mission = missionsList.find { it.id == missionId } ?: return false
        if (!mission.isCompleted || mission.isClaimed) return false

        val current = playerDao.getPlayerProfileSync() ?: return false
        playerDao.claimMission(missionId)

        val updated = current.copy(
            coins = current.coins + mission.rewardCoins,
            gems = current.gems + mission.rewardGems,
            xp = current.xp + mission.rewardXp
        )
        playerDao.updateProfile(updated)
        return true
    }

    suspend fun claimDailyReward(): Boolean {
        val current = playerDao.getPlayerProfileSync() ?: return false
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        if (now - current.lastDailyRewardClaimTime < dayMillis) {
            return false // Already claimed today
        }
        val updated = current.copy(
            coins = current.coins + 500,
            gems = current.gems + 10,
            lastDailyRewardClaimTime = now
        )
        playerDao.updateProfile(updated)
        return true
    }

    suspend fun grantAdminCurrency(coins: Long, gems: Int) {
        val current = playerDao.getPlayerProfileSync() ?: return
        playerDao.updateProfile(current.copy(coins = current.coins + coins, gems = current.gems + gems))
    }

    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null

        fun getInstance(context: Context): GameRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = GameRepository(db.playerDao())
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.MissionEntity
import com.example.data.PlayerProfileEntity
import com.example.data.UnlockedItemEntity
import com.example.game.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    START_CINEMATIC,
    AUTH,
    HOME,
    GAMEPLAY,
    BATTLE_LOBBY,
    TOURNAMENT,
    CHARACTERS,
    MISSIONS,
    LEADERBOARD,
    SHOP,
    PROFILE,
    SETTINGS,
    ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository.getInstance(application)

    private val _currentScreen = MutableStateFlow(AppScreen.START_CINEMATIC)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _playerProfile = MutableStateFlow(PlayerProfileEntity())
    val playerProfile: StateFlow<PlayerProfileEntity> = _playerProfile.asStateFlow()

    private val _missions = MutableStateFlow<List<MissionEntity>>(emptyList())
    val missions: StateFlow<List<MissionEntity>> = _missions.asStateFlow()

    private val _unlockedItems = MutableStateFlow<List<UnlockedItemEntity>>(emptyList())
    val unlockedItems: StateFlow<List<UnlockedItemEntity>> = _unlockedItems.asStateFlow()

    var activeGameEngine: GameEngine? = null
        private set

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()

            launch {
                repository.playerProfile.collect { profile ->
                    if (profile != null) {
                        _playerProfile.value = profile
                    }
                }
            }

            launch {
                repository.missions.collect { list ->
                    _missions.value = list
                }
            }

            launch {
                repository.unlockedItems.collect { items ->
                    _unlockedItems.value = items
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun startRun(
        mode: GameMode = GameMode.ENDLESS,
        worldId: String = _playerProfile.value.selectedWorldId,
        characterId: String = _playerProfile.value.selectedCharacterId
    ) {
        val character = CharacterCatalog.getById(characterId)
        val world = WorldCatalog.getById(worldId)
        val profile = _playerProfile.value

        val upgradesMap = mapOf(
            "speed" to profile.speedUpgradeLevel,
            "magnet" to profile.magnetUpgradeLevel,
            "shield" to profile.shieldUpgradeLevel,
            "jump" to profile.jumpUpgradeLevel
        )

        activeGameEngine = GameEngine(
            selectedCharacter = character,
            selectedWorld = world,
            gameMode = mode,
            upgrades = upgradesMap,
            onGameOver = { score, coins, gems, distance ->
                viewModelScope.launch {
                    val isBattle = (mode == GameMode.BATTLE_1V1 || mode == GameMode.BATTLE_4P || mode == GameMode.TOURNAMENT)
                    val isWin = (activeGameEngine?.myCurrentRank ?: 2) == 1
                    repository.updateRunRewards(score, coins, gems, distance, isMultiplayerWin = isBattle && isWin)
                    if (isBattle && isWin) {
                        repository.recordMissionProgress("battle_win", 1)
                        if (mode == GameMode.TOURNAMENT) {
                            repository.grantAdminCurrency(1000, 10)
                        }
                    }
                }
            },
            onMissionEvent = { type, amount ->
                viewModelScope.launch {
                    repository.recordMissionProgress(type, amount)
                }
            }
        )

        _currentScreen.value = AppScreen.GAMEPLAY
    }

    fun startTournamentMatch(match: TournamentMatch) {
        val character = CharacterCatalog.getById(_playerProfile.value.selectedCharacterId)
        val world = WorldCatalog.getById(match.worldId)
        val profile = _playerProfile.value

        val upgradesMap = mapOf(
            "speed" to profile.speedUpgradeLevel,
            "magnet" to profile.magnetUpgradeLevel,
            "shield" to profile.shieldUpgradeLevel,
            "jump" to profile.jumpUpgradeLevel
        )

        activeGameEngine = GameEngine(
            selectedCharacter = character,
            selectedWorld = world,
            gameMode = GameMode.TOURNAMENT,
            targetRaceDistance = match.stage.targetDistance,
            upgrades = upgradesMap,
            onGameOver = { score, coins, gems, distance ->
                viewModelScope.launch {
                    val isWin = (activeGameEngine?.myCurrentRank ?: 2) == 1
                    val bonusCoins = if (isWin) match.stage.rewardCoins else 0
                    val bonusGems = if (isWin) match.stage.rewardGems else 0
                    repository.updateRunRewards(score, coins + bonusCoins, gems + bonusGems, distance, isMultiplayerWin = isWin)
                }
            },
            onMissionEvent = { type, amount ->
                viewModelScope.launch {
                    repository.recordMissionProgress(type, amount)
                }
            }
        )

        _currentScreen.value = AppScreen.GAMEPLAY
    }

    fun upgradeStat(stat: String) {
        viewModelScope.launch {
            repository.upgradeStat(stat)
        }
    }

    fun buyCharacter(characterId: String, costCoins: Int, costGems: Int) {
        viewModelScope.launch {
            repository.buyAndEquipCharacter(characterId, costCoins, costGems)
        }
    }

    fun selectCharacter(characterId: String) {
        viewModelScope.launch {
            repository.selectCharacter(characterId)
        }
    }

    fun selectWorld(worldId: String) {
        viewModelScope.launch {
            repository.selectWorld(worldId)
        }
    }

    fun claimMissionReward(missionId: String) {
        viewModelScope.launch {
            repository.claimMissionReward(missionId)
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            repository.claimDailyReward()
        }
    }

    fun claimSponsorAdReward(coins: Long = 500L, gems: Int = 10) {
        viewModelScope.launch {
            repository.grantAdminCurrency(coins, gems)
        }
    }

    fun selectCrew(crewId: String) {
        viewModelScope.launch {
            repository.selectCrew(crewId)
        }
    }

    fun updateProfile(name: String, country: String, flag: String) {
        viewModelScope.launch {
            repository.updateProfileInfo(name, country, flag)
        }
    }

    fun grantAdminCurrency(coins: Long, gems: Int) {
        viewModelScope.launch {
            repository.grantAdminCurrency(coins, gems)
        }
    }

    fun buyShopItem(item: ShopItem) {
        viewModelScope.launch {
            if (item.type == "bundle") {
                repository.grantAdminCurrency(10000, 100)
            } else {
                val current = _playerProfile.value
                if (current.coins >= item.priceCoins && current.gems >= item.priceGems) {
                    repository.buyAndUnlockWorld(item.id, item.priceCoins)
                }
            }
        }
    }

    fun isDailyRewardAvailable(): Boolean {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        return now - _playerProfile.value.lastDailyRewardClaimTime >= dayMillis
    }

    fun registerUser(
        fullName: String,
        username: String,
        emailOrPhone: String,
        password: String,
        mkoa: String
    ) {
        viewModelScope.launch {
            repository.registerUser(fullName, username, emailOrPhone, password, mkoa)
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun loginUser(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            repository.loginUser(emailOrPhone, password)
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logoutUser()
            _currentScreen.value = AppScreen.AUTH
        }
    }

    fun setPlayerBalance(coins: Long, gems: Int) {
        viewModelScope.launch {
            repository.setPlayerBalance(coins, gems)
        }
    }

    fun unlockAllContent() {
        viewModelScope.launch {
            repository.unlockAllContent()
        }
    }

    fun resetPlayerData() {
        viewModelScope.launch {
            repository.resetPlayerData()
        }
    }

    fun updatePlayerStats(highScore: Long, level: Int, trophies: Int) {
        viewModelScope.launch {
            repository.updatePlayerStats(highScore, level, trophies)
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.audio.SoundEngine
import com.example.game.GameMode
import com.example.ui.screens.*
import com.example.ui.theme.DarkBgMain
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SoundEngine.init(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBgMain
                ) {
                    BongoRunnerApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        SoundEngine.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEngine.release()
    }
}

@Composable
fun BongoRunnerApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val unlockedItems by viewModel.unlockedItems.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentScreen != AppScreen.START_CINEMATIC && currentScreen != AppScreen.HOME) {
        if (currentScreen == AppScreen.GAMEPLAY) {
            viewModel.navigateTo(AppScreen.HOME)
        } else {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            AppScreen.START_CINEMATIC -> {
                StartCinematicScreen(
                    onStartGame = {
                        viewModel.navigateTo(AppScreen.HOME)
                    }
                )
            }
            AppScreen.HOME -> {
                HomeScreen(
                    profile = profile,
                    onStartGame = { mode, worldId, charId ->
                        if (mode == GameMode.TOURNAMENT) {
                            viewModel.navigateTo(AppScreen.TOURNAMENT)
                        } else {
                            viewModel.startRun(mode, worldId, charId)
                        }
                    },
                    onOpenCharacters = { viewModel.navigateTo(AppScreen.CHARACTERS) },
                    onOpenMissions = { viewModel.navigateTo(AppScreen.MISSIONS) },
                    onOpenLeaderboard = { viewModel.navigateTo(AppScreen.LEADERBOARD) },
                    onOpenShop = { viewModel.navigateTo(AppScreen.SHOP) },
                    onOpenBattleLobby = { viewModel.navigateTo(AppScreen.BATTLE_LOBBY) },
                    onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                    onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onOpenAdmin = { viewModel.navigateTo(AppScreen.ADMIN) },
                    onClaimDailyReward = { viewModel.claimDailyReward() },
                    dailyRewardAvailable = viewModel.isDailyRewardAvailable(),
                    onClaimSponsorReward = { coins, gems -> viewModel.claimSponsorAdReward(coins, gems) }
                )
            }
            AppScreen.TOURNAMENT -> {
                TournamentScreen(
                    profile = profile,
                    onStartTournamentMatch = { match ->
                        viewModel.startTournamentMatch(match)
                    },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.GAMEPLAY -> {
                viewModel.activeGameEngine?.let { engine ->
                    GamePlayScreen(
                        engine = engine,
                        onReturnHome = {
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    )
                } ?: run {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            }
            AppScreen.BATTLE_LOBBY -> {
                BattleModeScreen(
                    profile = profile,
                    onStartBattle = { mode, worldId ->
                        viewModel.startRun(mode, worldId, profile.selectedCharacterId)
                    },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.CHARACTERS -> {
                CharacterSelectScreen(
                    profile = profile,
                    unlockedItems = unlockedItems,
                    onSelectCharacter = { charId -> viewModel.selectCharacter(charId) },
                    onBuyCharacter = { charId, coins, gems -> viewModel.buyCharacter(charId, coins, gems) },
                    onUpgradeStat = { stat -> viewModel.upgradeStat(stat) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.MISSIONS -> {
                MissionsScreen(
                    missions = missions,
                    onClaimReward = { missionId -> viewModel.claimMissionReward(missionId) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.LEADERBOARD -> {
                LeaderboardScreen(
                    profile = profile,
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.SHOP -> {
                ShopScreen(
                    profile = profile,
                    unlockedItems = unlockedItems,
                    onBuyItem = { item -> viewModel.buyShopItem(item) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.PROFILE -> {
                ProfileScreen(
                    profile = profile,
                    onUpdateProfile = { name, country, flag -> viewModel.updateProfile(name, country, flag) },
                    onSelectCrew = { crewId -> viewModel.selectCrew(crewId) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.ADMIN -> {
                AdminDashboardScreen(
                    onGrantCurrency = { coins, gems -> viewModel.grantAdminCurrency(coins, gems) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
        }
    }
}

